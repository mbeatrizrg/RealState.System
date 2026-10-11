package com.rifatrichani.backendinmobiliaria.services;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;


public interface InmuebleService {
    public Integer registrarInmueble(InmuebleRequestDTO dto);
    public void eliminarInmueble(Integer inmuebleId);
}