package com.rifatrichani.backendinmobiliaria.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.VentaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.EstatusInmueble;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Venta;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusInmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.VentaRepository;
import com.rifatrichani.backendinmobiliaria.services.VentaService;

import jakarta.transaction.Transactional;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final InmuebleRepository inmuebleRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstatusInmuebleRepository estatusInmuebleRepository;

    public VentaServiceImpl(VentaRepository vr, InmuebleRepository ir, UsuarioRepository ur, EstatusInmuebleRepository eir) {
        this.ventaRepository = vr;
        this.inmuebleRepository = ir;
        this.usuarioRepository = ur;
        this.estatusInmuebleRepository = eir;
    }

    // Registro de venta por el Vendedor cambiando automáticamente el estatus del inmueble a "Vendido"
    @Transactional
    @Override
    public void registrarVenta(VentaRequestDTO dto) {
        if (ventaRepository.existsByInmuebleId(dto.getInmueble())) {
            throw new IllegalArgumentException("Este inmueble ya tiene una venta registrada.");
        }
        Inmueble inmueble = inmuebleRepository.findById(dto.getInmueble())
                .orElseThrow(() -> new ResourceNotFoundException("El inmueble no existe en el sistema."));

        Venta venta = new Venta();
        venta.setInmueble(inmueble);
        venta.setUsuario(usuarioRepository.getReferenceById(dto.getUsuario()));
        venta.setMontoFinal(dto.getMontoFinal());
        ventaRepository.save(venta); // La comisión queda null inicialmente hasta que el Admin la asigne

        EstatusInmueble estatusVendido = estatusInmuebleRepository.findByNombre("Vendido")
                .orElseThrow(() -> new IllegalArgumentException("Error interno: Estatus 'Vendido' no configurado en los catálogos."));
        inmueble.setEstatusInmueble(estatusVendido);
        inmuebleRepository.save(inmueble);
    }

    // Exclusivo del Administrador: asigna o actualiza la comisión de una venta registrada
    @Transactional
    @Override
    public void asignarComisionAdmin(Long ventaId, BigDecimal comision) {
        if (comision == null || comision.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La comisión debe ser un monto válido mayor o igual a cero.");
        }
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + ventaId));
        venta.setComision(comision);
        ventaRepository.save(venta);
    }

    // Obtiene el historial completo de ventas para el CRM
    @Override
    public List<VentaResponseDTO> obtenerTodasLasVentas() {
        return ventaRepository.obtenerTodasConRelaciones().stream()
                .map(v -> {
                    VentaResponseDTO dto = new VentaResponseDTO();
                    dto.setId(v.getId());
                    dto.setInmuebleId(v.getInmueble().getId());
                    dto.setTituloInmueble(v.getInmueble().getTitulo());
                    dto.setRrbrInmueble(v.getInmueble().getRrbr());
                    dto.setNombreVendedor(v.getInmueble().getEmpleado().getNombre() + " " + v.getInmueble().getEmpleado().getApellido());
                    dto.setUsuarioId(v.getUsuario().getId());
                    dto.setNombreComprador(v.getUsuario().getNombre() + " " + v.getUsuario().getApellido());
                    dto.setMontoFinal(v.getMontoFinal());
                    dto.setComision(v.getComision());
                    dto.setFechaVenta(v.getFechaVenta());
                    return dto;
                }).collect(Collectors.toList());
    }
}