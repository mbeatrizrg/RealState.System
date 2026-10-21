package com.rifatrichani.backendinmobiliaria.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.EmpleadoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EmpleadoAdminResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.services.EmpleadoService;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registro de nuevo vendedor o administrador con contraseña hasheada en BCrypt
    @Override
    @Transactional
    public Integer registrarEmpleado(EmpleadoRequestDTO dto) {
        String correo = dto.getCorreo().trim().toLowerCase();
        if (empleadoRepository.existsByCorreoIgnoreCase(correo)) {
            throw new IllegalArgumentException("El correo ya está asignado a otro empleado.");
        }
        if (empleadoRepository.existsByCedula(dto.getCedula())) {
            throw new IllegalArgumentException("La cédula ya se encuentra registrada.");
        }
        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre().trim());
        empleado.setApellido(dto.getApellido().trim());
        empleado.setCedula(dto.getCedula());
        empleado.setCorreo(correo);
        empleado.setEsAdmin(dto.getEsAdmin());
        empleado.setActivo(true);
        empleado.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        //En Supabase las fechas son obligatorias: se inicializan iguales (sin sesion abierta)
        LocalDateTime ahora = LocalDateTime.now();
        empleado.setFechaEntrada(ahora);
        empleado.setSalidaSistema(ahora);
        return empleadoRepository.save(empleado).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoAdminResponseDTO> obtenerTodosConMetricasAdmin() {
        return empleadoRepository.findAll().stream()
                .map(this::mapearAEmpleadoAdminDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoAdminResponseDTO> obtenerEmpleadosConSesionAbierta() {
        return empleadoRepository.obtenerEmpleadosActivosEnSistema().stream()
                .map(this::mapearAEmpleadoAdminDTO)
                .collect(Collectors.toList());
    }

    //Desactivar corta el acceso de inmediato: el JwtAuthenticationFilter rechaza los tokens de empleados inactivos
    @Override
    @Transactional
    public void cambiarEstadoActivo(Integer empleadoId, boolean activo, Integer idAdminAutenticado) {
        if (!activo && empleadoId.equals(idAdminAutenticado)) {
            throw new IllegalArgumentException("No puede desactivar su propia cuenta.");
        }
        Empleado empleado = buscarEmpleado(empleadoId);
        empleado.setActivo(activo);
        empleadoRepository.save(empleado);
    }

    //El administrador asigna una contraseña provisional; se cierran las sesiones abiertas de ese empleado
    @Override
    @Transactional
    public void restablecerContrasena(Integer empleadoId, String contrasenaProvisional) {
        Empleado empleado = buscarEmpleado(empleadoId);
        empleado.setContrasena(passwordEncoder.encode(contrasenaProvisional));
        empleado.setSalidaSistema(LocalDateTime.now());
        empleadoRepository.save(empleado);
    }

    private Empleado buscarEmpleado(Integer empleadoId) {
        return empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + empleadoId));
    }

    private EmpleadoAdminResponseDTO mapearAEmpleadoAdminDTO(Empleado emp) {
        EmpleadoAdminResponseDTO dto = new EmpleadoAdminResponseDTO();
        dto.setId(emp.getId());
        dto.setNombreCompleto(emp.getNombre() + (emp.getApellido() != null ? " " + emp.getApellido() : ""));
        dto.setCedula(emp.getCedula());
        dto.setCorreo(emp.getCorreo());
        dto.setEsAdmin(emp.getEsAdmin());
        dto.setActivo(emp.getActivo());
        dto.setTieneContrasena(emp.getContrasena() != null);
        dto.setUltimaEntrada(emp.getFechaEntrada());
        dto.setUltimaSalida(emp.getSalidaSistema());
        dto.setSesionAbierta(emp.getFechaEntrada() != null
                && (emp.getSalidaSistema() == null || emp.getFechaEntrada().isAfter(emp.getSalidaSistema())));
        dto.setCantidadClientesLeads(empleadoRepository.contarLeadsPorEmpleado(emp.getId()));
        dto.setCantidadComunicaciones(empleadoRepository.contarComunicacionesPorEmpleado(emp.getId()));
        return dto;
    }
}
