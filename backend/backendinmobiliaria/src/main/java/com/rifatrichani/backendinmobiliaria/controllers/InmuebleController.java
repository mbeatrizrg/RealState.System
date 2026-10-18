package com.rifatrichani.backendinmobiliaria.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleEstatusRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.InmuebleService;

import jakarta.validation.Valid;

// Controlador REST que maneja todas las peticiones de inmuebles tanto para el Sitio Web (Hostinger) como para el CRM
@RestController
@RequestMapping("/api/inmueble")
public class InmuebleController {
    private final InmuebleService inmuebleService;
    public InmuebleController(InmuebleService is) {
        this.inmuebleService = is;
    }
    /* 1. PÚBLICO (Sitio Web Hostinger): Barra de búsqueda multicriterio por listas seleccionables (dropdowns).
        No recibe texto libre, sino los IDs seleccionados en los menús desplegables (Ciudad, Tipo de Propiedad,
        Estatus/Operación como Compra o Alquiler, y rango de precios). Todos los parámetros son opcionales (required = false):
        si el usuario no selecciona alguno en la barra, llega como null y la consulta en PostgreSQL ignora ese filtro.
        Ejemplo de llamada: GET /api/inmueble/buscar?ciudadId=1&tipoInmuebleId=2&estatusInmuebleId=1&minPrecio=15000&maxPrecio=60000
    */
    @GetMapping("/buscar")
    public ResponseEntity<List<InmuebleListResponseDTO>> buscarPorBarraMulticriterio(
            @RequestParam(required = false) Integer ciudadId,
            @RequestParam(required = false) Integer tipoInmuebleId,
            @RequestParam(required = false) Integer estatusInmuebleId,
            @RequestParam(required = false) Boolean isAlquiler,
            @RequestParam(required = false) BigDecimal minPrecio,
            @RequestParam(required = false) BigDecimal maxPrecio) {
        return ResponseEntity.ok(
                inmuebleService.buscarPorFiltros(ciudadId, tipoInmuebleId, estatusInmuebleId, isAlquiler, minPrecio, maxPrecio)
        );
    }

    /* 2. PÚBLICO (Sitio Web y CRM): Obtiene la vista detallada de un inmueble específico al hacer clic sobre él.
        Devuelve InmuebleDetalleResponseDTO con toda su descripción, datos del vendedor, lista de extras y URLs de Galería.
        Ejemplo de llamada: GET /api/inmueble/5
    */
    @GetMapping("/{id}")
    public ResponseEntity<InmuebleDetalleResponseDTO> obtenerDetalleInmueble(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(inmuebleService.obtenerDetalleInmueble(id));
    }

    /* 3. PRIVADO CRM (Vendedor y Admin): Obtiene el listado completo de inmuebles registrados en el sistema
        (incluyendo los que ya tienen estatus "Vendido") para la tabla de gestión del CRM.
        Ejemplo de llamada: GET /api/inmueble/crm
    */
    @GetMapping("/crm")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<InmuebleListResponseDTO>> obtenerTodosCRM() {
        return ResponseEntity.ok(inmuebleService.obtenerTodosCRM());
    }

    /* 4. PRIVADO CRM (Vendedor y Admin): Registra un nuevo inmueble en la base de datos.
        Extrae el ID del empleado autenticado directamente desde el SecurityContextHolder (inyectado por el JwtAuthenticationFilter).
        Ejemplo de llamada: POST /api/inmueble
    */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> registrarInmueble(@Valid @RequestBody InmuebleRequestDTO dto) {
        Integer idEmpleadoAutenticado = obtenerIdEmpleadoDelContexto();
        Integer idGenerado = inmuebleService.registrarInmueble(dto, idEmpleadoAutenticado);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Inmueble registrado exitosamente con ID: " + idGenerado);
    }

    /* 5. PRIVADO CRM (Vendedor y Admin): Actualiza toda la información de un inmueble existente.Ejemplo de llamada: PUT /api/inmueble/5
    */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> actualizarInmueble(
            @PathVariable("id") Integer id,
            @Valid @RequestBody InmuebleRequestDTO dto) {
        Integer idEmpleadoAutenticado = obtenerIdEmpleadoDelContexto();
        inmuebleService.actualizarInmueble(id, dto, idEmpleadoAutenticado);
        return ResponseEntity.ok("Inmueble actualizado exitosamente.");
    }

    /* 6. PRIVADO CRM (Vendedor y Admin): Cambia únicamente el estatus de un inmueble (ej. Disponible, En Alquiler, Reservado, Vendido).
        Ejemplo de llamada: PATCH /api/inmueble/5/estatus
    */
    @PatchMapping("/{id}/estatus")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> cambiarEstatusInmueble(
            @PathVariable("id") Integer id,
            @Valid @RequestBody InmuebleEstatusRequestDTO dto) {
        Integer idEmpleadoAutenticado = obtenerIdEmpleadoDelContexto();
        inmuebleService.cambiarEstatus(id, dto, idEmpleadoAutenticado);
        return ResponseEntity.ok("Estatus del inmueble actualizado correctamente.");
    }

    /* 7. PRIVADO CRM (Vendedor y Admin): Elimina un inmueble del sistema por su ID.
        Ejemplo de llamada: DELETE /api/inmueble/5
    */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> eliminarInmueble(@PathVariable("id") Integer id) {
        Integer idEmpleadoAutenticado = obtenerIdEmpleadoDelContexto();
        inmuebleService.eliminarInmueble(id, idEmpleadoAutenticado);
        return ResponseEntity.ok("Inmueble eliminado exitosamente.");
    }

    // Método auxiliar privado para no repetir la extracción del ID del empleado desde el token JWT en cada endpoint
    private Integer obtenerIdEmpleadoDelContexto() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Integer.parseInt(auth.getPrincipal().toString());
    }
}