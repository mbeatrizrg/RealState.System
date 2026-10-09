package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.CanalResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Canal;
import com.rifatrichani.backendinmobiliaria.repositories.CanalRepository;

@Service 
public class CanalService {
    private final CanalRepository canalRepository;
    public CanalService(CanalRepository CR){
        this.canalRepository = CR;
    }
    public List<CanalResponseDTO> getAll(){
        List<Canal> canales = canalRepository.findAll();  
        return canales.stream().map(canal -> {
            CanalResponseDTO dto = new CanalResponseDTO();
            dto.setId(canal.getId());
            dto.setNombre(canal.getNombre());
            return dto;
        }).collect(Collectors.toList());
    } 
}
