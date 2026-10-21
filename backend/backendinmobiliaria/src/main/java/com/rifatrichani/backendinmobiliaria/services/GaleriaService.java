package com.rifatrichani.backendinmobiliaria.services;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

public interface  GaleriaService {
    public Integer agregarImagen(GaleriaRequesDTO dto, CuentaAutenticada empleado);
    public void eliminarImagen(Integer imagenId, CuentaAutenticada empleado);
}
