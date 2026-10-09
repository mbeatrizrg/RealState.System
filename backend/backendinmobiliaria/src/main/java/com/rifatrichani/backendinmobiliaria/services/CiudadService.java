package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.CiudadResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Ciudad;
import com.rifatrichani.backendinmobiliaria.repositories.CiudadRepository;

@Service 
public class CiudadService {
    private final CiudadRepository ciudadRepositorio;
    public CiudadService(CiudadRepository cR){
        this.ciudadRepositorio = cR;
    }
    public List<CiudadResponseDTO> getAll(){
        List<Ciudad> ciudadesDB = ciudadRepositorio.findAll();
        return ciudadesDB.stream().map(ciudad -> {
            CiudadResponseDTO dto = new CiudadResponseDTO();
            dto.setId(ciudad.getId());
            dto.setNombre(ciudad.getNombre());
            return dto;
        }).collect(Collectors.toList());
    }
    
}
