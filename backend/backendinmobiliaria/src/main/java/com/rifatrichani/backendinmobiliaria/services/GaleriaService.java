package com.rifatrichani.backendinmobiliaria.services;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;

public interface  GaleriaService {
    public void agregarImagen(GaleriaRequesDTO dto);
    public void eliminarImagen(Integer imagenId);
}