package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.ComunicacionRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ComunicacionResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.ComunicacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comunicacion")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class ComunicacionController {
    private final ComunicacionService comunicacionService;
    public ComunicacionController(ComunicacionService cs) {
        this.comunicacionService = cs;
    }
    // Registrar una nueva comunicación de seguimiento con un Lead
    @PostMapping
    public ResponseEntity<String> registrarComunicacion(@Valid @RequestBody ComunicacionRequestDTO dto) {
        comunicacionService.registrarComunicacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comunicación registrada exitosamente.");
    }
    // Ver todas las comunicaciones asociadas a un Lead específico
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<ComunicacionResponseDTO>> obtenerPorLead(@PathVariable("leadId") Integer leadId) {
        return ResponseEntity.ok(comunicacionService.obtenerPorLead(leadId));
    }
    // Ver todas las comunicaciones realizadas por un Vendedor específico
    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<ComunicacionResponseDTO>> obtenerPorEmpleado(@PathVariable("empleadoId") Integer empleadoId) {
        return ResponseEntity.ok(comunicacionService.obtenerPorEmpleado(empleadoId));
    }
}