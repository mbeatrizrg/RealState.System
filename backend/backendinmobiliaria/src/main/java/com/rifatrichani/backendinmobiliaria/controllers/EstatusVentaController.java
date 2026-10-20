package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.response.EstatusVentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.EstatusVentaService;

@RestController 
@RequestMapping ("/api/estatus_venta") 
@CrossOrigin (origins = "*")
public class EstatusVentaController {
    private final EstatusVentaService estatusVentaService;
    public EstatusVentaController(EstatusVentaService evs){
        this.estatusVentaService = evs;
    }
    @GetMapping 
    public List<EstatusVentaResponseDTO> obtenerTodos(){
        return estatusVentaService.obtenerEstatusVenta();
    }
}
