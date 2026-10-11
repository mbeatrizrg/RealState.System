package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.response.ExtraResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.ExtraService;

@RestController 
@RequestMapping ("api/extras")
@CrossOrigin (origins = "*")
public class ExtraController {
    private final ExtraService extraService;
    public ExtraController(ExtraService es){
        this.extraService = es;
    }
    public List<ExtraResponseDTO> obtenerTodos(){
        return extraService.obtenerExtras();
    }
}
