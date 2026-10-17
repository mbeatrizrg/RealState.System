package com.rifatrichani.backendinmobiliaria.controllers;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.VentaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/venta")
public class VentaController {
    private final VentaService ventaService;
    public VentaController(VentaService vs) {
        this.ventaService = vs;
    }
    // Vendedor o Admin registra la venta (cambia automáticamente el estatus del inmueble a "Vendido")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> registrarVenta(@Valid @RequestBody VentaRequestDTO dto) {
        ventaService.registrarVenta(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Venta registrada exitosamente y estatus del inmueble actualizado a 'Vendido'.");
    }
    // Listado histórico de ventas para el CRM
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentas() {
        return ResponseEntity.ok(ventaService.obtenerTodasLasVentas());
    }
    // Exclusivo del Administrador: asignar el monto de la comisión a una venta
    // Ejemplo de llamada: PATCH /api/venta/1/comision?comision=1500.00
    @PatchMapping("/{ventaId}/comision")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> asignarComision(
            @PathVariable("ventaId") Long ventaId,
            @RequestParam("comision") BigDecimal comision) {
        ventaService.asignarComisionAdmin(ventaId, comision);
        return ResponseEntity.ok("Comisión asignada correctamente a la venta.");
    }
}