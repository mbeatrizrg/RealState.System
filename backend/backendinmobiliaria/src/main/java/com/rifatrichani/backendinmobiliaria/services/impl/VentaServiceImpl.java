package com.rifatrichani.backendinmobiliaria.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.VentaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Venta;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.VentaRepository;
import com.rifatrichani.backendinmobiliaria.security.ControlAcceso;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.VentaService;

@Service
public class VentaServiceImpl implements VentaService {
    private final VentaRepository ventaRepository;
    private final InmuebleRepository inmuebleRepository;

    public VentaServiceImpl(VentaRepository vr, InmuebleRepository ir) {
        this.ventaRepository = vr;
        this.inmuebleRepository = ir;
    }

    //Registra la venta y marca el inmueble como vendido (deja de aparecer en el portal). La comision inicia en 0
    @Override
    @Transactional
    public Integer registrarVenta(VentaRequestDTO dto, CuentaAutenticada empleado) {
        Inmueble inmueble = inmuebleRepository.findById(dto.getInmuebleId())
                .orElseThrow(() -> new ResourceNotFoundException("El inmueble no existe en el sistema."));
        ControlAcceso.verificarEncargado(inmueble, empleado);
        if (Boolean.TRUE.equals(inmueble.getVendido()) || ventaRepository.existsByInmuebleId(inmueble.getId())) {
            throw new IllegalArgumentException("Este inmueble ya figura como vendido en el sistema.");
        }
        Venta venta = new Venta();
        venta.setInmueble(inmueble);
        venta.setMontoFinal(dto.getMontoFinal());
        venta.setComprobante(dto.getComprobante().trim());
        venta.setComision(BigDecimal.ZERO);
        Integer id = ventaRepository.save(venta).getId();
        //Tambien marca isVendido (ver Inmueble.cambiarEstado)
        inmueble.cambiarEstado(Boolean.TRUE.equals(inmueble.getAlquiler()) ? "alquilado" : "vendido");
        inmuebleRepository.save(inmueble);
        return id;
    }

    @Override
    @Transactional
    public void asignarComisionAdmin(Integer ventaId, BigDecimal comision) {
        if (comision == null || comision.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La comisión debe ser un monto válido mayor o igual a cero.");
        }
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + ventaId));
        if (comision.compareTo(venta.getMontoFinal()) > 0) {
            throw new IllegalArgumentException("La comisión no puede ser mayor al monto de la venta.");
        }
        venta.setComision(comision);
        ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> obtenerVentas(CuentaAutenticada empleado) {
        List<Venta> ventas = empleado.esAdmin()
                ? ventaRepository.obtenerTodasConRelaciones()
                : ventaRepository.obtenerPorEmpleadoId(empleado.id());
        return ventas.stream().map(this::mapearAVentaDTO).collect(Collectors.toList());
    }

    private VentaResponseDTO mapearAVentaDTO(Venta v) {
        VentaResponseDTO dto = new VentaResponseDTO();
        Empleado vendedor = v.getInmueble().getEmpleado();
        dto.setId(v.getId());
        dto.setInmuebleId(v.getInmueble().getId());
        dto.setTituloInmueble(v.getInmueble().getTitulo());
        dto.setRrbrInmueble(v.getInmueble().getRrbr());
        dto.setEmpleadoId(vendedor.getId());
        dto.setNombreVendedor(vendedor.getNombre() + (vendedor.getApellido() != null ? " " + vendedor.getApellido() : ""));
        dto.setMontoFinal(v.getMontoFinal());
        dto.setComision(v.getComision());
        dto.setComprobante(v.getComprobante());
        dto.setFechaVenta(v.getFechaVenta());
        return dto;
    }
}
