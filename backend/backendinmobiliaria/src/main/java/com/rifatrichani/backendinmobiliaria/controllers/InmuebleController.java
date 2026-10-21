package com.rifatrichani.backendinmobiliaria.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.services.InmuebleService;

@RestController
@RequestMapping ("/api/inmueble")
@CrossOrigin (origins = "*")
public class InmuebleController {
    private final InmuebleService inmuebleService;
    public InmuebleController(InmuebleService is){
        this.inmuebleService = is;
    }
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") //Solo los json web tokens con rol de admin pueden hacer el registro
    public ResponseEntity<?> registrarInmueble(@RequestBody InmuebleRequestDTO dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Integer idEmpleadoAutenticado = Integer.parseInt(auth.getPrincipal().toString());
            Integer idGenerado = inmuebleService.registrarInmueble(dto, idEmpleadoAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body("Inmueble registrado exitosamente con ID: " + idGenerado);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar: " + e.getMessage());
        }
    } 
}
