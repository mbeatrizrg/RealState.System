package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.LeadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LeadResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.LeadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lead")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class LeadController {
    private final LeadService leadService;
    public LeadController(LeadService ls) {
        this.leadService = ls;
    }
    // Registrar un nuevo Lead en el CRM
    @PostMapping
    public ResponseEntity<String> registrarLead(@Valid @RequestBody LeadRequestDTO dto) {
        leadService.registrarLead(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Lead registrado exitosamente en el CRM.");
    }
    // Obtener todos los Leads del CRM
    @GetMapping
    public ResponseEntity<List<LeadResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(leadService.obtenerTodosLosLeads());
    }
    // Obtener los Leads asignados a un vendedor específico
    @GetMapping("/vendedor/{empleadoId}")
    public ResponseEntity<List<LeadResponseDTO>> obtenerPorVendedor(@PathVariable("empleadoId") Integer empleadoId) {
        return ResponseEntity.ok(leadService.obtenerLeadsPorVendedor(empleadoId));
    }
    // Actualizar seguimiento del Lead (fecha de visita, si fue visitado y estatus de venta)
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarLead(@PathVariable("id") Integer id, @Valid @RequestBody LeadRequestDTO dto) {
        leadService.actualizarLead(id, dto);
        return ResponseEntity.ok("Información del Lead actualizada correctamente.");
    }
}