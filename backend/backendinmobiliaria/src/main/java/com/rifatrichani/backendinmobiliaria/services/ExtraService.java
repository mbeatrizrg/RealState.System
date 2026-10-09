package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.ExtraResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Extra;
import com.rifatrichani.backendinmobiliaria.repositories.ExtraRepository;

@Service 
public class ExtraService {
    private final ExtraRepository extraRepository;
    public ExtraService(ExtraRepository ER){
        this.extraRepository = ER;
    }
    public List<ExtraResponseDTO> getAll(){
        List<Extra> listaExtra = extraRepository.findAll();
        return listaExtra.stream().map(extra -> {
            ExtraResponseDTO dto = new ExtraResponseDTO();
            dto.setId(extra.getId());
            dto.setNombre(extra.getNombre());
            return dto;
        }).collect(Collectors.toList());
    }
}
