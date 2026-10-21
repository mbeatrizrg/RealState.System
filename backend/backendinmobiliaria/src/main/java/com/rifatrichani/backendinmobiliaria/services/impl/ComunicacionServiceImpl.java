package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.ComunicacionRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ComunicacionResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Comunicacion;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.repositories.CanalRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ComunicacionRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
import com.rifatrichani.backendinmobiliaria.security.ControlAcceso;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ComunicacionService;

@Service
public class ComunicacionServiceImpl implements ComunicacionService {
    private final ComunicacionRepository comunicacionRepository;
    private final CanalRepository canalRepository;
    private final LeadRepository leadRepository;

    public ComunicacionServiceImpl(ComunicacionRepository cr, CanalRepository canalRepo, LeadRepository lr) {
        this.comunicacionRepository = cr;
        this.canalRepository = canalRepo;
        this.leadRepository = lr;
    }

    @Override
    @Transactional
    public void registrarComunicacion(ComunicacionRequestDTO dto, CuentaAutenticada empleado) {
        Lead lead = buscarLead(dto.getLeadId());
        ControlAcceso.verificarEncargado(lead, empleado);
        Comunicacion comunicacion = new Comunicacion();
        comunicacion.setDescripcion(dto.getDescripcion());
        comunicacion.setCanal(canalRepository.findById(dto.getCanalId())
                .orElseThrow(() -> new ResourceNotFoundException("Canal no encontrado con ID: " + dto.getCanalId())));
        comunicacion.setLead(lead);
        comunicacionRepository.save(comunicacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComunicacionResponseDTO> obtenerPorLead(Integer leadId, CuentaAutenticada empleado) {
        ControlAcceso.verificarEncargado(buscarLead(leadId), empleado);
        return comunicacionRepository.obtenerPorLeadId(leadId).stream()
                .map(this::mapearAComunicacionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComunicacionResponseDTO> obtenerPorEmpleado(Integer empleadoId, CuentaAutenticada empleado) {
        if (!empleado.esAdmin() && !empleado.id().equals(empleadoId)) {
            throw new SecurityException("Solo puede consultar sus propias comunicaciones.");
        }
        return comunicacionRepository.obtenerPorEmpleadoId(empleadoId).stream()
                .map(this::mapearAComunicacionDTO)
                .collect(Collectors.toList());
    }

    private Lead buscarLead(Integer leadId) {
        return leadRepository.obtenerConRelaciones(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con ID: " + leadId));
    }

    private ComunicacionResponseDTO mapearAComunicacionDTO(Comunicacion c) {
        ComunicacionResponseDTO dto = new ComunicacionResponseDTO();
        Empleado vendedor = c.getLead().getInmueble().getEmpleado();
        dto.setId(c.getId());
        dto.setLeadId(c.getLead().getId());
        dto.setNombreCliente(c.getLead().getUsuario().getNombre() + " " + c.getLead().getUsuario().getApellido());
        dto.setNombreVendedor(vendedor.getNombre() + (vendedor.getApellido() != null ? " " + vendedor.getApellido() : ""));
        dto.setCanal(c.getCanal().getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setFechaCreacion(c.getFechaCreacion());
        return dto;
    }
}
