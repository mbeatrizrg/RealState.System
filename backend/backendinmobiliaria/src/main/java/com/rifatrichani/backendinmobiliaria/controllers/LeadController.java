package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.LeadEstatusRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.LeadInteresRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.LeadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LeadResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.LeadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lead")
public class LeadController {
    private final LeadService leadService;
    public LeadController(LeadService ls) {
        this.leadService = ls;
    }

    // PORTAL (usuario con sesión): botón "Contáctanos" del detalle. Crea el lead para el vendedor del inmueble
    @PostMapping("/interes")
    @PreAuthorize("hasAuthority('ROLE_USUARIO')")
    public ResponseEntity<Map<String, String>> registrarInteres(@Valid @RequestBody LeadInteresRequestDTO dto) {
        boolean nuevo = leadService.registrarInteres(dto.getInmuebleId(), SesionActual.idUsuario());
        return nuevo
                ? ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "¡Gracias! Un asesor se comunicará con usted pronto."))
                : ResponseEntity.ok(Map.of("mensaje", "Ya registramos su interés en este inmueble. Un asesor se comunicará con usted."));
    }

    // CRM: registrar un lead manualmente (solo para inmuebles del vendedor, o cualquiera si es admin)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> registrarLead(@Valid @RequestBody LeadRequestDTO dto) {
        Integer id = leadService.registrarLead(dto, SesionActual.empleado());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id, "mensaje", "Lead registrado exitosamente en el CRM."));
    }

    // CRM: admin ve todos los leads; vendedor solo los de sus inmuebles
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<LeadResponseDTO>> obtenerLeads() {
        return ResponseEntity.ok(leadService.obtenerLeads(SesionActual.empleado()));
    }

    // CRM: leads de un vendedor específico (admin cualquiera, vendedor solo el suyo)
    @GetMapping("/vendedor/{empleadoId}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<LeadResponseDTO>> obtenerPorVendedor(@PathVariable("empleadoId") Integer empleadoId) {
        return ResponseEntity.ok(leadService.obtenerLeadsPorVendedor(empleadoId, SesionActual.empleado()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<LeadResponseDTO> obtenerLead(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(leadService.obtenerLead(id, SesionActual.empleado()));
    }

    // CRM: avanzar el lead en el embudo de venta
    @PatchMapping("/{id}/estatus")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> actualizarEstatus(@PathVariable("id") Integer id, @Valid @RequestBody LeadEstatusRequestDTO dto) {
        leadService.actualizarEstatus(id, dto.getEstatus(), SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Estatus del lead actualizado correctamente."));
    }
}
