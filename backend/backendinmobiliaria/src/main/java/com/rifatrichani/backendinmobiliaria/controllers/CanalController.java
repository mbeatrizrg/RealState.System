package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.response.CanalResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.CanalService;

@RestController 
@RequestMapping ("/api/canal")
public class CanalController {
    private final CanalService canalService;
    public CanalController(CanalService cs){
        this.canalService = cs;
    }
    @GetMapping 
    public List<CanalResponseDTO> obtenerTodos(){
        return canalService.obtenerTodos();
    }
}
