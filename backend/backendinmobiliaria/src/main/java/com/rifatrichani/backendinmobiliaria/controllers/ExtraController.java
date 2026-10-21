package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.response.ExtraResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.ExtraService;

@RestController 
@RequestMapping ("/api/extras")
public class ExtraController {
    private final ExtraService extraService;
    public ExtraController(ExtraService es){
        this.extraService = es;
    }
    @GetMapping 
    public List<ExtraResponseDTO> obtenerTodos(){
        return extraService.obtenerExtras();
    }
}
