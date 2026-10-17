package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.EmpleadoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EmpleadoAdminResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.TipoCedulaRepository;
import com.rifatrichani.backendinmobiliaria.services.EmpleadoService;
import jakarta.transaction.Transactional;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final TipoCedulaRepository tipoCedulaRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository,TipoCedulaRepository tipoCedulaRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.tipoCedulaRepository = tipoCedulaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registro de nuevo vendedor o administrador con contraseña hasheada en BCrypt
    @Transactional
    @Override
    public void registrarEmpleado(EmpleadoRequestDTO dto) {
        if (empleadoRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está asignado a otro empleado.");
        }
        if (empleadoRepository.existsByCedula(dto.getCedula())) {
            throw new IllegalArgumentException("La cédula ya se encuentra registrada.");
        }
        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setCedula(dto.getCedula());
        empleado.setCorreo(dto.getCorreo());
        empleado.setEsAdmin(dto.getEsAdmin());
        empleado.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        empleado.setTipoCedula(tipoCedulaRepository.getReferenceById(dto.getTipoCedulaId()));
        empleadoRepository.save(empleado);
    }

    // Panel Admin: Visualiza todos los empleados con la cantidad de clientes (leads), comunicaciones y último logeo
    @Override
    public List<EmpleadoAdminResponseDTO> obtenerTodosConMetricasAdmin() {
        return empleadoRepository.findAll().stream()
                .map(this::mapearAEmpleadoAdminDTO)
                .collect(Collectors.toList());
    }

    // Panel Admin: Visualiza únicamente los vendedores/empleados activos actualmente en el sistema
    @Override
    public List<EmpleadoAdminResponseDTO> obtenerEmpleadosActivosAdmin() {
        return empleadoRepository.obtenerEmpleadosActivosEnSistema().stream()
                .map(this::mapearAEmpleadoAdminDTO)
                .collect(Collectors.toList());
    }

    private EmpleadoAdminResponseDTO mapearAEmpleadoAdminDTO(Empleado emp) {
        EmpleadoAdminResponseDTO dto = new EmpleadoAdminResponseDTO();
        dto.setId(emp.getId());
        dto.setNombreCompleto(emp.getNombre() + " " + emp.getApellido());
        dto.setTipoCedula(emp.getTipoCedula() != null ? emp.getTipoCedula().getNombre() : null);
        dto.setCedula(emp.getCedula());
        dto.setCorreo(emp.getCorreo());
        dto.setEsAdmin(emp.getEsAdmin());
        dto.setUltimaEntrada(emp.getFechaEntrada());
        dto.setUltimaSalida(emp.getSalidaSistema());

        // Está activo si ya inició sesión al menos una vez y no ha salido (o su última entrada es posterior a su última salida)
        boolean activo = emp.getFechaEntrada() != null &&
                (emp.getSalidaSistema() == null || emp.getFechaEntrada().isAfter(emp.getSalidaSistema()));
        dto.setActivoEnSistema(activo);

        // Métricas en tiempo real desde PostgreSQL
        dto.setCantidadClientesLeads(empleadoRepository.contarLeadsPorEmpleado(emp.getId()));
        dto.setCantidadComunicaciones(empleadoRepository.contarComunicacionesPorEmpleado(emp.getId()));
        return dto;
    }
}