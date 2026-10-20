package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServices authServices;

    public AuthController(AuthServices authServices) {
        this.authServices = authServices;
    }
    
    @PostMapping("/login/usuario")
    public ResponseEntity<Map<String, Object>> loginUsuario(@Valid @RequestBody UsuarioLogeoDTO dto) {
        return ResponseEntity.ok(authServices.loginUsuarioWeb(dto));
    }

    @PostMapping("/login/empleado")
    public ResponseEntity<Map<String, Object>> loginEmpleado(@Valid @RequestBody UsuarioLogeoDTO dto) {
        return ResponseEntity.ok(authServices.loginEmpleadoCrm(dto));
    }

    @PostMapping("/logout/empleado")
    public ResponseEntity<String> logoutEmpleado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer idEmpleado = Integer.parseInt(auth.getPrincipal().toString());
        authServices.logoutEmpleadoCrm(idEmpleado);
        return ResponseEntity.ok("Sesión cerrada correctamente.");
    }

    // Endpoint 1: El usuario envía sus datos y se le envía el correo
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO dto) {
        return ResponseEntity.ok(authServices.registrarUsuario(dto));
    }

    // Endpoint 2: El frontend envía el código que el usuario escribió
    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarCuenta(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigo");
        return ResponseEntity.ok(authServices.confirmarCuenta(codigo));
    }
}