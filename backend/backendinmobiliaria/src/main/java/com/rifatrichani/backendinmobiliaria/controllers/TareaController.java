package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroTareaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.TareaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.TareaResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.ColumnaTarea;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.TareaService;

import jakarta.validation.Valid;

// CRM (vendedor y administrador): modulo de Tareas
@RestController
@RequestMapping("/api/tareas")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class TareaController {
    private final TareaService tareaService;
    public TareaController(TareaService ts) {
        this.tareaService = ts;
    }

    /* 1. Tabla de tareas con filtros y paginacion (ver FiltroTareaDTO). Ej:
          GET /api/tareas?vista=mias_pendientes&estados=vencida&prioridades=alta&orden=fechaProgramada */
    @GetMapping
    public ResponseEntity<PaginaResponseDTO<TareaResponseDTO>> buscar(@Valid @ModelAttribute FiltroTareaDTO filtro) {
        return ResponseEntity.ok(tareaService.buscar(filtro, SesionActual.empleado()));
    }

    // 2. Cantidad de cada pestaña: todas, mis pendientes y completadas / vencidas
    @GetMapping("/conteos")
    public ResponseEntity<ConteoPestanasDTO> contar(@Valid @ModelAttribute FiltroTareaDTO filtro) {
        return ResponseEntity.ok(tareaService.contar(filtro, SesionActual.empleado()));
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@Valid @ModelAttribute FiltroTareaDTO filtro,
                                           @RequestParam(defaultValue = "xlsx") String formato,
                                           @RequestParam(required = false) List<String> columnas,
                                           @RequestParam(defaultValue = "todos") String alcance) {
        return ContactoController.descarga(tareaService.exportar(filtro, ColumnaTarea.desdeClaves(columnas),
                FormatoExportacion.desde(formato), ContactoController.soloPagina(alcance), SesionActual.empleado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaResponseDTO> obtener(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(tareaService.obtener(id, SesionActual.empleado()));
    }

    @PostMapping
    public ResponseEntity<TareaResponseDTO> crear(@Valid @RequestBody TareaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaService.crear(dto, SesionActual.empleado()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaResponseDTO> actualizar(@PathVariable("id") Integer id, @Valid @RequestBody TareaRequestDTO dto) {
        return ResponseEntity.ok(tareaService.actualizar(id, dto, SesionActual.empleado()));
    }

    // 3. Cambiar el estado. Body: {"estado": "en_proceso"}
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TareaResponseDTO> cambiarEstado(@PathVariable("id") Integer id, @RequestBody Map<String, String> cuerpo) {
        return ResponseEntity.ok(tareaService.cambiarEstado(id, cuerpo.get("estado"), SesionActual.empleado()));
    }

    // 4. Completar y registrar el resultado de la gestion. Body: {"resultado": "El cliente confirmó la visita"}
    @PostMapping("/{id}/completar")
    public ResponseEntity<TareaResponseDTO> completar(@PathVariable("id") Integer id, @RequestBody(required = false) Map<String, String> cuerpo) {
        return ResponseEntity.ok(tareaService.completar(id, cuerpo != null ? cuerpo.get("resultado") : null, SesionActual.empleado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("id") Integer id) {
        tareaService.eliminar(id, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Tarea eliminada."));
    }
}
