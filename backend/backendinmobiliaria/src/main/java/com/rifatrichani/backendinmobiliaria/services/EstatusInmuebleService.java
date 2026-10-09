package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.EstatusInmuebleResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.EstatusInmueble;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusInmuebleRepository;

@Service 
public class EstatusInmuebleService {
    private final EstatusInmuebleRepository estatusInmuebleRepository;
    public EstatusInmuebleService(EstatusInmuebleRepository EIR){
        this.estatusInmuebleRepository = EIR;
    }
    public List<EstatusInmuebleResponseDTO> getAll(){
        List<EstatusInmueble> estatusInmuebles = estatusInmuebleRepository.findAll();
        return estatusInmuebles.stream().map(estatus -> {
            EstatusInmuebleResponseDTO dto = new EstatusInmuebleResponseDTO();
            dto.setId(estatus.getId());
            dto.setNombre(estatus.getNombre());
            return dto;
        }).collect(Collectors.toList());
    }
}
