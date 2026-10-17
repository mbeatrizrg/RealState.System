package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rifatrichani.backendinmobiliaria.dto.request.FavoritoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.FavoritoListResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.FavoritoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/favorito")
public class FavoritoController {
    private final FavoritoService favoritoService;
    public FavoritoController(FavoritoService fs) {
        this.favoritoService = fs;
    }
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FavoritoListResponseDTO>> obtenerFavoritosUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(favoritoService.obtenerFavoritosPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<String> guardarFavorito(@Valid @RequestBody FavoritoRequestDTO dto) {
        favoritoService.guardarFavorito(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Inmueble añadido a favoritos.");
    }

    @DeleteMapping("/{favoritoId}")
    public ResponseEntity<String> eliminarFavorito(@PathVariable Integer favoritoId) {
        favoritoService.eliminarFavortio(favoritoId);
        return ResponseEntity.ok("Inmueble eliminado de favoritos.");
    }
}