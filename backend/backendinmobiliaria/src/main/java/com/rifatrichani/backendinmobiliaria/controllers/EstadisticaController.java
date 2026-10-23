package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroEstadisticaDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.InteraccionInmueble;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.EstadisticaService;

import jakarta.validation.Valid;

// CRM (vendedor y administrador): modulo de Estadisticas e Informes
@RestController
@RequestMapping("/api/estadisticas")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class EstadisticaController {
    private final EstadisticaService estadisticaService;
    public EstadisticaController(EstadisticaService es) {
        this.estadisticaService = es;
    }

    /* 1. Panel completo (tarjetas, graficas, tabla de inmuebles y rendimiento de ventas). Ej:
          GET /api/estadisticas?desde=2026-01-01&hasta=2026-10-31&tipoInmuebleIds=1&agrupacion=zona */
    @GetMapping
    public ResponseEntity<EstadisticasDTO> calcular(@Valid @ModelAttribute FiltroEstadisticaDTO filtro) {
        return ResponseEntity.ok(estadisticaService.calcular(filtro));
    }

    // 2. Vista detallada: contactos que interactuaron con el inmueble y que acciones hicieron (?nivel=VISITO opcional)
    @GetMapping("/inmuebles/{id}")
    public ResponseEntity<InteraccionInmueble> interaccionInmueble(@PathVariable("id") Integer id,
                                                                   @RequestParam(required = false) String nivel) {
        if (nivel != null && !nivel.isBlank() && !List.of("INFORMACION", "AGENDO", "VISITO", "OFERTA", "CERRO", "DESCARTO").contains(nivel)) {
            throw new IllegalArgumentException("Nivel de interacción inválido.");
        }
        return ResponseEntity.ok(estadisticaService.interaccionInmueble(id, nivel));
    }

    // 3. Informe Excel o PDF con las secciones visibles: secciones=resumen,pastel,serie,inmuebles,vendedores
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@Valid @ModelAttribute FiltroEstadisticaDTO filtro,
                                           @RequestParam(defaultValue = "xlsx") String formato,
                                           @RequestParam(required = false) List<String> secciones) {
        return ContactoController.descarga(estadisticaService.exportar(filtro, FormatoExportacion.desde(formato), secciones));
    }
}
