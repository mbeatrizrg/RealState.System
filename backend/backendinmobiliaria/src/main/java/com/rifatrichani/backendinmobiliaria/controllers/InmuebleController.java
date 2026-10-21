package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.InmuebleService;

import jakarta.validation.Valid;

// Inmuebles para el portal web (consulta publica) y para el CRM (gestion)
@RestController
@RequestMapping("/api/inmueble")
public class InmuebleController {
    private final InmuebleService inmuebleService;
    public InmuebleController(InmuebleService is) {
        this.inmuebleService = is;
    }

    /* 1. PÚBLICO: busqueda del portal con los filtros de las listas desplegables. Todos son opcionales y nunca devuelve vendidos.
        Ejemplo: GET /api/inmueble/buscar?ciudadId=1&tipoInmuebleId=2&isAlquiler=false&minPrecio=15000&maxPrecio=60000 */
    @GetMapping("/buscar")
    public ResponseEntity<List<InmuebleListResponseDTO>> buscar(
            @RequestParam(required = false) Integer ciudadId,
            @RequestParam(required = false) Integer tipoInmuebleId,
            @RequestParam(required = false) Boolean isAlquiler,
            @RequestParam(required = false) Integer minPrecio,
            @RequestParam(required = false) Integer maxPrecio) {
        return ResponseEntity.ok(inmuebleService.buscarPorFiltros(ciudadId, tipoInmuebleId, isAlquiler, minPrecio, maxPrecio));
    }

    // 2. PÚBLICO: detalle de un inmueble. Si no existe el GlobalExceptionHandler responde 404
    @GetMapping("/{id}")
    public ResponseEntity<InmuebleDetalleResponseDTO> obtenerDetalle(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(inmuebleService.obtenerDetalleInmueble(id));
    }

    // 3. CRM: todos los inmuebles (incluidos los vendidos). Con ?soloMios=true solo los del vendedor autenticado
    @GetMapping("/crm")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<InmuebleListResponseDTO>> obtenerTodosCRM(@RequestParam(defaultValue = "false") boolean soloMios) {
        return ResponseEntity.ok(inmuebleService.obtenerTodosCRM(soloMios ? SesionActual.idEmpleado() : null));
    }

    // 4. CRM: registra un inmueble a cargo del empleado autenticado (un admin puede indicar otro vendedor en empleadoId)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> registrarInmueble(@Valid @RequestBody InmuebleRequestDTO dto) {
        Integer idGenerado = inmuebleService.registrarInmueble(dto, SesionActual.empleado());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", idGenerado, "mensaje", "Inmueble registrado exitosamente."));
    }

    // 5. CRM: actualiza un inmueble (solo su vendedor encargado o un admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> actualizarInmueble(@PathVariable("id") Integer id, @Valid @RequestBody InmuebleRequestDTO dto) {
        inmuebleService.actualizarInmueble(id, dto, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Inmueble actualizado exitosamente."));
    }

    // 6. CRM: elimina un inmueble (solo su vendedor encargado o un admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> eliminarInmueble(@PathVariable("id") Integer id) {
        inmuebleService.eliminarInmueble(id, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Inmueble eliminado exitosamente."));
    }
}
