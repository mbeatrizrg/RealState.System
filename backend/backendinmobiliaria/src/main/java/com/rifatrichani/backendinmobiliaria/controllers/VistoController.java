package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.rifatrichani.backendinmobiliaria.dto.request.VistoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VistosResponseCrmDTO;
import com.rifatrichani.backendinmobiliaria.services.VistoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/visto")
public class VistoController {

    private final VistoService vistoService;

    public VistoController(VistoService vs) {
        this.vistoService = vs;
    }

    // Público (Sitio Web Hostinger): Registra cada clic que un usuario (logueado o anónimo) hace sobre un inmueble
    @PostMapping
    public ResponseEntity<String> registrarClicInmueble(@Valid @RequestBody VistoDTO dto) {
        vistoService.registrarVista(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Visualización registrada.");
    }

    // CRM (Vendedor y Admin): Obtiene el historial de vistas con datos del usuario y si lo tiene en favoritos
    @GetMapping("/crm")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<VistosResponseCrmDTO>> obtenerTodosCrm() {
        return ResponseEntity.ok(vistoService.obtenerVistosParaCrm());
    }

    // CRM (Vendedor y Admin): Filtra quiénes han visto un inmueble específico
    @GetMapping("/crm/inmueble/{inmuebleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<VistosResponseCrmDTO>> obtenerPorInmuebleCrm(@PathVariable Integer inmuebleId) {
        return ResponseEntity.ok(vistoService.obtenerVistosPorInmuebleCrm(inmuebleId));
    }
}