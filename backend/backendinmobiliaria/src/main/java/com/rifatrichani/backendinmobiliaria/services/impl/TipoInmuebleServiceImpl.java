package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.TipoInmuebleResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.TipoInmueble;
import com.rifatrichani.backendinmobiliaria.repositories.TipoInmuebleRepository;
import com.rifatrichani.backendinmobiliaria.services.TipoInmuebleService;

@Service 
public class TipoInmuebleServiceImpl implements TipoInmuebleService{
    private final TipoInmuebleRepository tipoInmuebleRepository;
    public TipoInmuebleServiceImpl(TipoInmuebleRepository tir){
        this.tipoInmuebleRepository = tir;
    }
    //Obtener todos los tipos de inmuebles
    @Override 
    public List<TipoInmuebleResponseDTO> obtenerTiposInmuebles(){
        List<TipoInmueble> tipoInmuelbles = tipoInmuebleRepository.findAll();
        return tipoInmuelbles.stream().map(tipo -> {
            TipoInmuebleResponseDTO dto = new TipoInmuebleResponseDTO();
            dto.setId(tipo.getId());
            dto.setNombre(tipo.getNombre());
            return dto;
    }).collect(Collectors.toList());
    }
}
