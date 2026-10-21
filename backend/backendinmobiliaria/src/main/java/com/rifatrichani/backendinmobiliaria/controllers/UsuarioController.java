package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.UsuarioResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;
import com.rifatrichani.backendinmobiliaria.services.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthServices authServices;

    public UsuarioController(UsuarioService us, AuthServices as) {
        this.usuarioService = us;
        this.authServices = as;
    }

    // 1. PÚBLICO (Portal web): registro de un nuevo cliente. Responde un texto que el frontend muestra tal cual
    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authServices.registrarUsuario(usuario));
    }

    // 2. USUARIO con sesión: sus propios datos. El id se toma del token, no de la URL
    @GetMapping("/perfil")
    @PreAuthorize("hasAuthority('ROLE_USUARIO')")
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil() {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(SesionActual.idUsuario()));
    }

    // 3. PRIVADO CRM (Vendedor y Admin): listar todos los clientes registrados para crear un Lead
    @GetMapping("/crm")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosParaCrm() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuariosCrm());
    }

    // 4. PRIVADO CRM (Vendedor y Admin): buscar clientes por nombre, apellido o correo. Ej: GET /api/usuario/crm/buscar?termino=carlos
    @GetMapping("/crm/buscar")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarClientesCrm(
            @RequestParam(value = "termino", required = false) String termino) {
        return ResponseEntity.ok(usuarioService.buscarUsuariosPorTerminoCrm(termino));
    }
}
