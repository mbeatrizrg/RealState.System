package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.TipoCedulaResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.TipoCedula;
import com.rifatrichani.backendinmobiliaria.repositories.TipoCedulaRepository;
import com.rifatrichani.backendinmobiliaria.services.TipoCedulaService;

import java.util.List;

@Service 
public class TipoCedulaServiceImpl implements TipoCedulaService{
    private final TipoCedulaRepository tipoCedulaRepository;
    public TipoCedulaServiceImpl(TipoCedulaRepository TCR){
        this.tipoCedulaRepository = TCR;
    }
    //Obtener todos los tipos de cedulas :)
    public List<TipoCedulaResponseDTO> obtenerTiposCedula(){
        List<TipoCedula> tiposCedulas = tipoCedulaRepository.findAll();
        return tiposCedulas.stream().map(tipo -> {
            TipoCedulaResponseDTO dto = new TipoCedulaResponseDTO();
            dto.setId(tipo.getId());
            dto.setNombre(tipo.getNombre());
            return dto;
        }).collect(Collectors.toList());
            
    }
    
}
