package com.rifatrichani.backendinmobiliaria.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.VentaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Venta;
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

    public VentaServiceImpl(VentaRepository vr, InmuebleRepository ir, UsuarioRepository ur) {
        this.ventaRepository = vr;
        this.inmuebleRepository = ir;
        this.usuarioRepository = ur;
    }

    // Registro de venta cambiando automáticamente el booleano isVendido del inmueble a true
    @Transactional
    @Override
    public void registrarVenta(VentaRequestDTO dto) {
        Inmueble inmueble = inmuebleRepository.findById(dto.getInmueble()).orElseThrow(() -> new ResourceNotFoundException("El inmueble no existe en el sistema."));
        if (Boolean.TRUE.equals(inmueble.getIsVendido()) || ventaRepository.existsByInmuebleId(dto.getInmueble())) {
            throw new IllegalArgumentException("Este inmueble ya figura como vendido en el sistema.");
        }
        Venta venta = new Venta();
        venta.setInmueble(inmueble);
        venta.setUsuario(usuarioRepository.getReferenceById(dto.getUsuario()));
        venta.setMontoFinal(dto.getMontoFinal());
        ventaRepository.save(venta);
        inmueble.setIsVendido(true);
        inmuebleRepository.save(inmueble);
    }
    
    //Asigna una comisión a la venta, validando que sea un monto válido y actualizando la venta correspondiente en la base de datos
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

    //Optiene todas las ventas registradas en el sistema, incluyendo detalles del inmueble y del comprador para el CRM, mapeando a DTO de response
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