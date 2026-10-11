package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.response.EstatusInmuebleResponseDTO;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusInmuebleRepository;
import com.rifatrichani.backendinmobiliaria.services.EstatusInmuebleService;
import com.rifatrichani.backendinmobiliaria.models.EstatusInmueble;

@Service 
public class EstatusInmuebleServiceImpl implements EstatusInmuebleService{
    private final EstatusInmuebleRepository estatusInmuebleRepository;
    public EstatusInmuebleServiceImpl(EstatusInmuebleRepository EIR){
        this.estatusInmuebleRepository = EIR;
    }
    
    //Obtener estatus de Empleados
    @Override 
    public List<EstatusInmuebleResponseDTO> obtenerTodos(){
        List<EstatusInmueble> estatusInmuebles = estatusInmuebleRepository.findAll();
        return estatusInmuebles.stream().map(estatus -> {
            EstatusInmuebleResponseDTO dto = new EstatusInmuebleResponseDTO();
            dto.setId(estatus.getId());
            dto.setNombre(estatus.getNombre());
            return dto;
        }).collect(Collectors.toList());
    }
}
