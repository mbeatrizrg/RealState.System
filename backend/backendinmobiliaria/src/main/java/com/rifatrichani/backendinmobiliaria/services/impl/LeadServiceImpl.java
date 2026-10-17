package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.LeadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LeadResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusVentaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.services.LeadService;
import jakarta.transaction.Transactional;

@Service
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EstatusVentaRepository estatusVentaRepository;

    public LeadServiceImpl(LeadRepository lr, UsuarioRepository ur, EmpleadoRepository er, EstatusVentaRepository evr) {
        this.leadRepository = lr;
        this.usuarioRepository = ur;
        this.empleadoRepository = er;
        this.estatusVentaRepository = evr;
    }

    @Transactional
    @Override
    public void registrarLead(LeadRequestDTO dto) {
        Lead lead = new Lead();
        lead.setUsuario(usuarioRepository.getReferenceById(dto.getUsuarioId()));
        lead.setEmpleado(empleadoRepository.getReferenceById(dto.getEmpleadoId()));
        lead.setEstatusVenta(estatusVentaRepository.getReferenceById(dto.getEstatusVentaId()));
        lead.setFechaVisita(dto.getFechaVisita());
        lead.setEsVisitado(dto.getEsVisitado() != null ? dto.getEsVisitado() : false);
        leadRepository.save(lead);
    }

    @Override
    public List<LeadResponseDTO> obtenerTodosLosLeads() {
        return leadRepository.obtenerTodosConRelaciones().stream()
                .map(this::mapearALeadResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeadResponseDTO> obtenerLeadsPorVendedor(Integer empleadoId) {
        return leadRepository.obtenerPorEmpleadoId(empleadoId).stream()
                .map(this::mapearALeadResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void actualizarLead(Integer leadId, LeadRequestDTO dto) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con ID: " + leadId));

        lead.setEstatusVenta(estatusVentaRepository.getReferenceById(dto.getEstatusVentaId()));
        lead.setFechaVisita(dto.getFechaVisita());
        if (dto.getEsVisitado() != null) {
            lead.setEsVisitado(dto.getEsVisitado());
        }
        leadRepository.save(lead);
    }

    private LeadResponseDTO mapearALeadResponseDTO(Lead l) {
        LeadResponseDTO dto = new LeadResponseDTO();
        dto.setId(l.getId());
        dto.setUsuarioId(l.getUsuario().getId());
        dto.setNombreCliente(l.getUsuario().getNombre() + " " + l.getUsuario().getApellido());
        dto.setCorreoCliente(l.getUsuario().getCorreo());
        dto.setTelefonoCliente(l.getUsuario().getTelefono());
        dto.setEmpleadoId(l.getEmpleado().getId());
        dto.setNombreVendedor(l.getEmpleado().getNombre() + " " + l.getEmpleado().getApellido());
        dto.setEstatusVenta(l.getEstatusVenta().getNombre());
        dto.setFechaVisita(l.getFechaVisita());
        dto.setEsVisitado(l.getEsVisitado());
        dto.setFechaCreacion(l.getFechaCreacion());
        return dto;
    }
}