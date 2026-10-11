package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.response.CiudadResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Ciudad;
import com.rifatrichani.backendinmobiliaria.repositories.CiudadRepository;
import com.rifatrichani.backendinmobiliaria.services.CiudadService;

@Service 
public class CiudadServiceImpl implements CiudadService{
    private final CiudadRepository ciudadRepositorio;
    public CiudadServiceImpl(CiudadRepository cR){
        this.ciudadRepositorio = cR;
    }

    @Override 
    public List<CiudadResponseDTO> obtenerCiudades(){
        List<Ciudad> ciudadesDB = ciudadRepositorio.findAll();
        return ciudadesDB.stream().map(ciudad -> {
            CiudadResponseDTO dto = new CiudadResponseDTO();
            dto.setId(ciudad.getId());
            dto.setNombre(ciudad.getNombre());
            return dto;
        }).collect(Collectors.toList());
    }
}
