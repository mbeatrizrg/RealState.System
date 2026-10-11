package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.response.TipoCedulaResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.TipoCedulaService;

@RestController 
@RequestMapping ("api/tipo_cedula")
@CrossOrigin (origins = "*")
public class TipoCedulaController {
    private final TipoCedulaService tipoCedulaService;
    public TipoCedulaController(TipoCedulaService tcs){
        this.tipoCedulaService = tcs;
    }
    public List<TipoCedulaResponseDTO> obtenerTodos(){
        return tipoCedulaService.obtenerTiposCedula();
    }
}
