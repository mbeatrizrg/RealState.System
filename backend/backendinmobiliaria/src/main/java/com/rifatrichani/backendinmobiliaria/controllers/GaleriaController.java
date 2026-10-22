package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ImagenResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.GaleriaService;

import jakarta.validation.Valid;

//Fotos de los inmuebles (maximo 6 por inmueble). Las fotos se consultan publicamente en GET /api/inmueble/{id}
@RestController
@RequestMapping("/api/galeria")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class GaleriaController {
    private final GaleriaService galeriaService;
    public GaleriaController(GaleriaService gs) {
        this.galeriaService = gs;
    }

    //Agrega una foto que ya esta publicada en internet (URL http/https)
    @PostMapping
    public ResponseEntity<Map<String, Object>> agregarImagen(@Valid @RequestBody GaleriaRequesDTO dto) {
        Integer id = galeriaService.agregarImagen(dto, SesionActual.empleado());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id, "mensaje", "Imagen agregada a la galería del inmueble."));
    }

    /* Sube archivos de imagen (JPG, PNG o WEBP, max 5 MB c/u) como multipart/form-data con el campo "archivos" repetido.
       Ejemplo: POST /api/galeria/inmueble/7  (archivos=@foto1.jpg, archivos=@foto2.png) */
    @PostMapping(value = "/inmueble/{inmuebleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ImagenResponseDTO>> subirImagenes(@PathVariable("inmuebleId") Integer inmuebleId,
                                                                 @RequestParam("archivos") List<MultipartFile> archivos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(galeriaService.subirImagenes(inmuebleId, archivos, SesionActual.empleado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarImagen(@PathVariable("id") Integer id) {
        galeriaService.eliminarImagen(id, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Imagen eliminada de la galería."));
    }
}
