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
        ventaRepository.save(venta);

        EstatusInmueble estatusVendido = estatusInmuebleRepository.findByNombre("Vendido")
                .orElseThrow(() -> new IllegalArgumentException("Error interno: Estatus 'Vendido' no configurado en los catálogos."));
        inmueble.setEstatusInmueble(estatusVendido);
        inmuebleRepository.save(inmueble);
    }

    // Exclusivo del Administrador: asigna o actualiza la comisión de una