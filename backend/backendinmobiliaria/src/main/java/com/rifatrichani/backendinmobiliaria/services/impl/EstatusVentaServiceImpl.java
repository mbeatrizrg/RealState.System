package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.response.EstatusVentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.EstatusVenta;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusVentaRepository;
import com.rifatrichani.backendinmobiliaria.services.EstatusVentaService;
@Service 
public class EstatusVentaServiceImpl implements EstatusVentaService{
    private final EstatusVentaRepository estatusVentaRepository;
    public EstatusVentaServiceImpl(EstatusVentaRepository EVR){
        this.estatusVentaRepository = EVR;
    }
    @Override 
    public List<EstatusVentaResponseDTO> obtenerEstatusVenta(){
        List<EstatusVenta> estatusVentas = estatusVentaRepository.findAll();
        return estatusVentas.stream().map(estatus -> {
            EstatusVentaResponseDTO dto = new EstatusVentaResponseDTO();
            dto.setId(estatus.getId());
            dto.setNombre(estatus.getNombre());
            return dto;
        }).collect(Collectors.toList());
    }
}
