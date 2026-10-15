package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.response.TipoInmuebleResponseDTO;

public interface TipoInmuebleService {
    public List<TipoInmuebleResponseDTO> obtenerTiposInmuebles();
}
