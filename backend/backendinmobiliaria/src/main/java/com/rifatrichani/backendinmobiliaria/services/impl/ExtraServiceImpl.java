package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.ExtraResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Extra;
import com.rifatrichani.backendinmobiliaria.repositories.ExtraRepository;
import com.rifatrichani.backendinmobiliaria.services.ExtraService;
import java.util.List;

@Service 
public class ExtraServiceImpl implements ExtraService{
    private final ExtraRepository extraRepository;
    public ExtraServiceImpl(ExtraRepository ER){
        this.extraRepository = ER;
    }
    @Override 
    public List<ExtraResponseDTO> obtenerExtras(){
        List<Extra> listaExtra = extraRepository.findAll();
        return listaExtra.stream().map(extra -> {
            ExtraResponseDTO dto = new ExtraResponseDTO();
            dto.setId(extra.getId());
            dto.setNombre(extra.getNombre());
            return dto;
        }).collect(Collectors.toList());
    }
}

