package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.response.EstatusVentaResponseDTO;

public interface  EstatusVentaService {
    public List<EstatusVentaResponseDTO> obtenerEstatusVenta();
}
