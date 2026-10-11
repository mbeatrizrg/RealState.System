package com.rifatrichani.backendinmobiliaria.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.LeadRequestDTO;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusVentaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EstatusVentaRepository estatusVentaRepository;
    public LeadService(LeadRepository lr, UsuarioRepository ur, EmpleadoRepository er, EstatusVentaRepository evr) {
        this.leadRepository = lr;
        this.usuarioRepository = ur;
        this.empleadoRepository = er;
        this.estatusVentaRepository = evr;
    }

    //Registra un nuevo lead
    @Transactional
    public void registrarLead(LeadRequestDTO dto) {
        Lead lead = new Lead();
        lead.setUsuario(usuarioRepository.getReferenceById(dto.getUsuarioId()));
        lead.setEmpleado(empleadoRepository.getReferenceById(dto.getEmpleadoId()));
        lead.setEstatusVenta(estatusVentaRepository.getReferenceById(dto.getEstatusVentaId()));
        lead.setFechaVisita(dto.getFechaVisita());
        lead.setEsVisitado(dto.getEsVisitado() != null ? dto.getEsVisitado() : false);
        leadRepository.save(lead);
    }
}