package com.rifatrichani.backendinmobiliaria.services.impl;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.VentaRequestDTO;
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
public class VentaServiceImpl implements VentaService{

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

    //Registro de venta en el sistema cambiando el estatus del inmueble
    @Transactional
    @Override 
    public void registrarVenta(VentaRequestDTO dto) {
        Inmueble inmueble = inmuebleRepository.findById(dto.getInmueble()).orElseThrow(() -> new IllegalArgumentException("El inmueble no existe en el sistema."));
        Venta venta = new Venta();
        venta.setInmueble(inmuebleRepository.getReferenceById(dto.getInmueble())); 
        venta.setUsuario(usuarioRepository.getReferenceById(dto.getUsuario()));     
        venta.setMontoFinal(dto.getMontoFinal());
        ventaRepository.save(venta);  // La comisión puede ser nula inicialmente hasta que el admin lo decida        
        EstatusInmueble estatusVendido = estatusInmuebleRepository.findByNombre("Vendido").orElseThrow(() -> new IllegalArgumentException("Error interno: Estatus 'Vendido' no configurado en los catálogos."));
        inmueble.setEstatusInmueble(estatusVendido);
        inmuebleRepository.save(inmueble);
    }
}
