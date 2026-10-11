package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;


import com.rifatrichani.backendinmobiliaria.dto.response.EstatusInmuebleResponseDTO;


public interface  EstatusInmuebleService {
    public List<EstatusInmuebleResponseDTO> obtenerEstatusInmuebles();
}
