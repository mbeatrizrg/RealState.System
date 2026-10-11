package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.response.TipoInmuebleResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.TipoInmuebleService;

@RestController 
@RequestMapping("api/tipo_inmueble") 
@CrossOrigin (origins = "*")
public class TipoInmuebleController {
    private final TipoInmuebleService tipoInmuebleService;
    public TipoInmuebleController(TipoInmuebleService tis){
        this.tipoInmuebleService = tis;
    }
    @GetMapping 
    public List<TipoInmuebleResponseDTO> obtenerTodos(){
        return  tipoInmuebleService.obtenerTiposInmuebles();
    }
}
