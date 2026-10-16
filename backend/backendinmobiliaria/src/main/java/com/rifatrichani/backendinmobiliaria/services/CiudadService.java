package com.rifatrichani.backendinmobiliaria.services;
import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.response.CiudadResponseDTO;
public interface CiudadService {
    public List<CiudadResponseDTO> obtenerCiudades();
    
}
