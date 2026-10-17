package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import com.rifatrichani.backendinmobiliaria.dto.request.ComunicacionRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ComunicacionResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Comunicacion;
import com.rifatrichani.backendinmobiliaria.repositories.CanalRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ComunicacionRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
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

    @Transactional
    @Override
    public void registrarComunicacion(ComunicacionRequestDTO dto) {
        Comunicacion comunicacion = new Comunicacion();
        comunicacion.setDescripcion(dto.getDescripcion());
        comunicacion.setCanal(canalRepository.getReferenceById(dto.getCanalId()));
        comunicacion.setLead(leadRepository.getReferenceById(dto.getLeadId()));
        comunicacionRepository.save(comunicacion);
    }

    @Override
    public List<ComunicacionResponseDTO> obtenerPorLead(Integer leadId) {
        return comunicacionRepository.obtenerPorLeadId(leadId).stream()
                .map(this::mapearAComunicacionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComunicacionResponseDTO> obtenerPorEmpleado(Integer empleadoId) {
        return comunicacionRepository.obtenerPorEmpleadoId(empleadoId).stream()
                .map(this::mapearAComunicacionDTO)
                .collect(Collectors.toList());
    }

    private ComunicacionResponseDTO mapearAComunicacionDTO(Comunicacion c) {
        ComunicacionResponseDTO dto = new ComunicacionResponseDTO();
        dto.setId(c.getId());
        dto.setLeadId(c.getLead().getId());
        dto.setNombreCliente(c.getLead().getUsuario().getNombre() + " " + c.getLead().getUsuario().getApellido());
        dto.setNombreVendedor(c.getLead().getEmpleado().getNombre() + " " + c.getLead().getEmpleado().getApellido());
        dto.setCanal(c.getCanal().getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setFechaCreacion(c.getFechaCreacion());
        return dto;
    }
}