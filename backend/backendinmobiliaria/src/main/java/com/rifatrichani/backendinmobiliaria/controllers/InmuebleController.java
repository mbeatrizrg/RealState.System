package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroInmuebleDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
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

    /* 1. PÚBLICO: busqueda del portal con texto libre, filtros y paginacion. Todos son opcionales y nunca devuelve vendidos.
        Ejemplo: GET /api/inmueble/buscar?q=mirador&ciudadId=1&tipoInmuebleId=2&isAlquiler=false&maxPrecio=60000&pagina=0&tamano=12&orden=precio_asc */
    @GetMapping("/buscar")
    public ResponseEntity<PaginaResponseDTO<InmuebleListResponseDTO>> buscar(@Valid @ModelAttribute FiltroInmuebleDTO filtro) {
        return ResponseEntity.ok(inmuebleService.buscarPublico(filtro));
    }

    // 2. PÚBLICO: detalle de un inmueble. Si no existe el GlobalExceptionHandler responde 404
    @GetMapping("/{id}")
    public ResponseEntity<InmuebleDetalleResponseDTO> obtenerDetalle(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(inmuebleService.obtenerDetalleInmueble(id));
    }

    /* 3. CRM: inventario completo (incluidos los vendidos) con los mismos filtros del portal, mas ?vendido=true|false.
        Con ?soloMios=true solo los del vendedor autenticado; ?empleadoId=N filtra por otro vendedor */
    @GetMapping("/crm")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<PaginaResponseDTO<InmuebleListResponseDTO>> buscarCrm(
            @Valid @ModelAttribute FiltroInmuebleDTO filtro,
            @RequestParam(defaultValue = "false") boolean soloMios) {
        if (soloMios) {
            filtro.setEmpleadoId(SesionActual.idEmpleado());
        }
        return ResponseEntity.ok(inmuebleService.buscarCrm(filtro));
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
