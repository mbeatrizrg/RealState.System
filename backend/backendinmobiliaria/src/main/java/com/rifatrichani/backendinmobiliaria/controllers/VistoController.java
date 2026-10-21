package com.rifatrichani.backendinmobiliaria.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.VistoDTO;
import com.rifatrichani.backendinmobiliaria.services.VistoService;

import jakarta.validation.Valid;

//Telemetria del portal: el frontend registra una vista cada vez que se abre el detalle de un inmueble
@RestController
@RequestMapping ("/api/visto") //El CORS de este controlador lo maneja config/CorsConfig
public class VistoController {
    private final VistoService vistoService;
    public VistoController(VistoService vs){
        this.vistoService = vs;
    }
    @PostMapping
    public ResponseEntity<Void> registrarVista(@Valid @RequestBody VistoDTO dto){
        //Hasta tener JWT no se confia en el usuario que envia el cliente, se registra como visitante anonimo
        dto.setUsuario(null);
        vistoService.registrarVista(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
