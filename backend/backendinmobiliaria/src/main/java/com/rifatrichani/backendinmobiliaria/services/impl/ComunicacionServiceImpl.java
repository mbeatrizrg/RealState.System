package com.rifatrichani.backendinmobiliaria.services.impl;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import com.rifatrichani.backendinmobiliaria.dto.request.ComunicacionRequestDTO;
import com.rifatrichani.backendinmobiliaria.models.Comunicacion;
import com.rifatrichani.backendinmobiliaria.repositories.CanalRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ComunicacionRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
import com.rifatrichani.backendinmobiliaria.services.ComunicacionService;

@Service 
public class ComunicacionServiceImpl implements ComunicacionService{
    private final ComunicacionRepository comunicacionRepository;
    private final CanalRepository canalRepository;
    private final LeadRepository leadRepository;
    public ComunicacionServiceImpl(ComunicacionRepository cr, CanalRepository canalRepo, LeadRepository lr) {
        this.comunicacionRepository = cr;
        this.canalRepository = canalRepo;
        this.leadRepository = lr;
    }
    //Registrar comunicacion con el cliente
    @Transactional
    @Override 
    public void registrarComunicacion(ComunicacionRequestDTO dto) {
        Comunicacion comunicacion = new Comunicacion();
        comunicacion.setDescripcion(dto.getDescripcion());        
        comunicacion.setCanal(canalRepository.getReferenceById(dto.getCanalId()));
        comunicacion.setLead(leadRepository.getReferenceById(dto.getLeadId()));
        comunicacionRepository.save(comunicacion);
    }
}
