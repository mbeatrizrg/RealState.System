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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.EmpleadoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EmpleadoAdminResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.EmpleadoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Controlador exclusivo para el Panel del Administrador del CRM
@RestController
@RequestMapping("/api/empleado")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EmpleadoController {
    private final EmpleadoService empleadoService;
    public EmpleadoController(EmpleadoService es) {
        this.empleadoService = es;
    }

    public record ContrasenaProvisionalDTO(
            @NotBlank(message = "La contraseña provisional es obligatoria")
            @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String contrasena) {}

    // 1. Registrar un nuevo vendedor o administrador en el CRM
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrarEmpleado(@Valid @RequestBody EmpleadoRequestDTO dto) {
        Integer id = empleadoService.registrarEmpleado(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id, "mensaje", "Empleado registrado exitosamente."));
    }

    // 2. Todos los empleados con cantidad de leads, comunicaciones y último ingreso
    @GetMapping
    public ResponseEntity<List<EmpleadoAdminResponseDTO>> obtenerTodosConMetricas() {
        return ResponseEntity.ok(empleadoService.obtenerTodosConMetricasAdmin());
    }

    // 3. Empleados con sesión abierta en este momento
    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoAdminResponseDTO>> obtenerEmpleadosConSesionAbierta() {
        return ResponseEntity.ok(empleadoService.obtenerEmpleadosConSesionAbierta());
    }

    // 4. Activar o desactivar un empleado. Ejemplo: PATCH /api/empleado/3/activo?valor=false
    @PatchMapping("/{id}/activo")
    public ResponseEntity<Map<String, String>> cambiarEstadoActivo(@PathVariable("id") Integer id, @RequestParam("valor") boolean valor) {
        empleadoService.cambiarEstadoActivo(id, valor, SesionActual.idEmpleado());
        return ResponseEntity.ok(Map.of("mensaje", valor ? "Empleado activado." : "Empleado desactivado."));
    }

    // 5. Asignar una contraseña provisional (ej. si el empleado la olvidó)
    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Map<String, String>> restablecerContrasena(@PathVariable("id") Integer id, @Valid @RequestBody ContrasenaProvisionalDTO dto) {
        empleadoService.restablecerContrasena(id, dto.contrasena());
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña provisional asignada."));
    }
}
