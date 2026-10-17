package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.EmpleadoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EmpleadoAdminResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.EmpleadoService;

import jakarta.validation.Valid;

// Controlador exclusivo para el Panel del Administrador del CRM
@RestController
@RequestMapping("/api/empleado")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EmpleadoController {
    private final EmpleadoService empleadoService;
    public EmpleadoController(EmpleadoService es) {
        this.empleadoService = es;
    }
    // 1. Registrar un nuevo vendedor o administrador en el CRM
    @PostMapping("/registro")
    public ResponseEntity<String> registrarEmpleado(@Valid @RequestBody EmpleadoRequestDTO dto) {
        empleadoService.registrarEmpleado(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Empleado registrado exitosamente.");
    }
    // 2. Visualizar todos los empleados junto con cantidad de clientes (leads), comunicaciones manejadas y último logeo
    @GetMapping
    public ResponseEntity<List<EmpleadoAdminResponseDTO>> obtenerTodosConMetricas() {
        return ResponseEntity.ok(empleadoService.obtenerTodosConMetricasAdmin());
    }
    // 3. Visualizar únicamente los vendedores/empleados activos actualmente en el sistema
    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoAdminResponseDTO>> obtenerEmpleadosActivos() {
        return ResponseEntity.ok(empleadoService.obtenerEmpleadosActivosAdmin());
    }
}