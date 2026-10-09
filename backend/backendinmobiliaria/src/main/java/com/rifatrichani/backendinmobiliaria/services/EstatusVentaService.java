package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.response.EstatusVentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.EstatusVenta;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusVentaRepository;

@Service 
public class EstatusVentaService {
    private final EstatusVentaRepository estatusVentaRepository;
    public EstatusVentaService(EstatusVentaRepository EVR){
        this.estatusVentaRepository = EVR;
    }
    public List<EstatusVentaResponseDTO> getAll(){
        List<EstatusVenta> estatusVentas = estatusVentaRepository.findAll();
        return estatusVentas.stream().map(estatus -> {
            EstatusVentaResponseDTO dto = new EstatusVentaResponseDTO();
            dto.setId(estatus.getId());
            dto.setNombre(estatus.getNombre());
            return dto;
        }).collect(Collectors.toList());
    }
}
