package com.rifatrichani.backendinmobiliaria.controllers;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.VentaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/venta")
public class VentaController {
    private final VentaService ventaService;
    public VentaController(VentaService vs) {
        this.ventaService = vs;
    }

    // Vendedor (de sus inmuebles) o Admin registra la venta; el inmueble queda marcado como vendido
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> registrarVenta(@Valid @RequestBody VentaRequestDTO dto) {
        Integer id = ventaService.registrarVenta(dto, SesionActual.empleado());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", id, "mensaje", "Venta registrada exitosamente y el inmueble quedó marcado como vendido."));
    }

    // Historial de ventas: admin ve todas, vendedor solo las suyas
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentas() {
        return ResponseEntity.ok(ventaService.obtenerVentas(SesionActual.empleado()));
    }

    // Exclusivo del Administrador: asignar la comisión. Ejemplo: PATCH /api/venta/1/comision?comision=1500.00
    @PatchMapping("/{ventaId}/comision")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> asignarComision(@PathVariable("ventaId") Integer ventaId, @RequestParam("comision") BigDecimal comision) {
        ventaService.asignarComisionAdmin(ventaId, comision);
        return ResponseEntity.ok(Map.of("mensaje", "Comisión asignada correctamente a la venta."));
    }
}
