package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.TipoCedulaResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.TipoCedula;
import com.rifatrichani.backendinmobiliaria.repositories.TipoCedulaRepository;

@Service 
public class TipoCedulaService {
    private final TipoCedulaRepository tipoCedulaRepository;
    public TipoCedulaService(TipoCedulaRepository TCR){
        this.tipoCedulaRepository = TCR;
    }
    public List<TipoCedulaResponseDTO> getAll(){
        List<TipoCedula> tiposCedulas = tipoCedulaRepository.findAll();
        return tiposCedulas.stream().map(tipo -> {
            TipoCedulaResponseDTO dto = new TipoCedulaResponseDTO();
            dto.setId(tipo.getId());
            dto.setNombre(tipo.getNombre());
            return dto;
        }).collect(Collectors.toList());
            
    }
}
