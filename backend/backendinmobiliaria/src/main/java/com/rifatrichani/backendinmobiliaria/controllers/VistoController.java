package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.VistoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VistosResponseCrmDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.VistoService;

import jakarta.validation.Valid;

//Telemetria del portal: el frontend registra una vista cada vez que se abre el detalle de un inmueble
@RestController
@RequestMapping("/api/visto")
public class VistoController {
    private final VistoService vistoService;

    public VistoController(VistoService vs) {
        this.vistoService = vs;
    }

    /* PÚBLICO: se acepta siempre, pero solo se guarda si hay un usuario del portal con sesión
       (en Supabase la vista requiere usuario). Las visitas anónimas responden 204 sin guardar nada */
    @PostMapping
    public ResponseEntity<Void> registrarVista(@Valid @RequestBody VistoDTO dto) {
        CuentaAutenticada cuenta = SesionActual.cuentaOpcional();
        if (cuenta == null || cuenta.tipo() != CuentaAutenticada.TipoCuenta.USUARIO) {
            return ResponseEntity.noContent().build();
        }
        vistoService.registrarVista(dto.getInmueble(), cuenta.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // CRM: historial de vistas (admin todas, vendedor las de sus inmuebles) e indica si el usuario lo tiene en favoritos
    @GetMapping("/crm")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<VistosResponseCrmDTO>> obtenerTodosCrm() {
        return ResponseEntity.ok(vistoService.obtenerVistosParaCrm(SesionActual.empleado()));
    }

    // CRM: quiénes han visto un inmueble específico
    @GetMapping("/crm/inmueble/{inmuebleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<VistosResponseCrmDTO>> obtenerPorInmuebleCrm(@PathVariable("inmuebleId") Integer inmuebleId) {
        return ResponseEntity.ok(vistoService.obtenerVistosPorInmuebleCrm(inmuebleId, SesionActual.empleado()));
    }
}
