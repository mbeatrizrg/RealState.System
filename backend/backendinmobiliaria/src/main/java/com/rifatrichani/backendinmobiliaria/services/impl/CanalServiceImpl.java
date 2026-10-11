package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.CanalResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Canal;
import com.rifatrichani.backendinmobiliaria.repositories.CanalRepository;
import com.rifatrichani.backendinmobiliaria.services.CanalService;
import java.util.List;
import jakarta.transaction.Transactional;
@Service 
public class CanalServiceImpl implements  CanalService{
    private final CanalRepository canalRepository;
    public CanalServiceImpl(CanalRepository CR){
        this.canalRepository = CR;
    }
    @Transactional 
    @Override 
    public List<CanalResponseDTO> obtenerTodos(){
        List<Canal> canales = canalRepository.findAll();  
        return canales.stream().map(canal -> {
            CanalResponseDTO dto = new CanalResponseDTO();
            dto.setId(canal.getId());
            dto.setNombre(canal.getNombre());
            return dto;
        }).collect(Collectors.toList());
    } 
}
