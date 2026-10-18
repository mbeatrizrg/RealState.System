package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioCorreoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.UsuarioResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService us) {
        this.usuarioService = us;
    }

    // 1. PÚBLICO (Sitio Web Hostinger): Registro de un nuevo cliente
    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO usuario) {
        usuarioService.registrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente.");
    }

    /* 2. PROTEGIDO (Cliente autenticado en la Web): Actualizar su propio correo electrónico.
        Extrae el ID del usuario directamente del token JWT en el SecurityContextHolder,
        impidiendo que un usuario "A" suplante el ID de un usuario "B" mediante cabeceras manuales.
    */
    @PutMapping("/{id}/correo")
    @PreAuthorize("hasAuthority('ROLE_USUARIO')")
    public ResponseEntity<String> editarCorreo(
            @PathVariable("id") Long idUsuarioObjetivo,
            @Valid @RequestBody UsuarioCorreoRequestDTO usuario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long idUsuarioAuth = Long.parseLong(auth.getPrincipal().toString());

        usuarioService.actualizarCorreo(idUsuarioObjetivo, idUsuarioAuth, usuario);
        return ResponseEntity.ok("Correo actualizado exitosamente.");
    }

    // 3. PRIVADO CRM (Vendedor y Admin): Listar todos los clientes registrados para crear un Lead
    @GetMapping("/crm")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosParaCrm() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuariosCrm());
    }

    // 4. PRIVADO CRM (Vendedor y Admin): Buscar clientes por nombre o correo para asignarlos a un Lead
    // Ejemplo de llamada: GET /api/usuario/crm/buscar?termino=carlos
    @GetMapping("/crm/buscar")
    @PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarClientesCrm(
            @RequestParam(value = "termino", required = false) String termino) {
        return ResponseEntity.ok(usuarioService.buscarUsuariosPorTerminoCrm(termino));
    }
}