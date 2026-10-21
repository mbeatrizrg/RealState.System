package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.response.EstatusInmuebleResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.EstatusInmuebleService;

@RestController 
@RequestMapping("/api/estatus_inmueble") 
public class EstatusInmuebleController {
    private final EstatusInmuebleService estatusInmuebleService;
    public EstatusInmuebleController(EstatusInmuebleService eis){
        this.estatusInmuebleService = eis;
    }
    @GetMapping 
    public List<EstatusInmuebleResponseDTO> obtenerTodos(){
        return estatusInmuebleService.obtenerEstatusInmuebles();
    }
}
