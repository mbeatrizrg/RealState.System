package com.rifatrichani.backendinmobiliaria.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;
import com.rifatrichani.backendinmobiliaria.services.GaleriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/galeria")
public class GaleriaController {
    private final GaleriaService galeriaService;
    public GaleriaController(GaleriaService gs) {
        this.galeriaService = gs;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> agregarImagen(@Valid @RequestBody GaleriaRequesDTO dto) {
        galeriaService.agregarImagen(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Imagen agregada a la galería del inmueble.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> eliminarImagen(@PathVariable Integer id) {
        galeriaService.eliminarImagen(id);
        return ResponseEntity.ok("Imagen eliminada de la galería.");
    }
}