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
import com.rifatrichani.backendinmobiliaria.services.AuthServices;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServices authServices;
    public AuthController(AuthServices authServices) {
        this.authServices = authServices;
    }
    
    // Login para clientes desde el sitio web en Hostinger
    @PostMapping("/login/usuario")
    public ResponseEntity<Map<String, Object>> loginUsuario(@Valid @RequestBody UsuarioLogeoDTO dto) {
        return ResponseEntity.ok(authServices.loginUsuarioWeb(dto));
    }

    // Login para Vendedores y Administradores desde el CRM
    @PostMapping("/login/empleado")
    public ResponseEntity<Map<String, Object>> loginEmpleado(@Valid @RequestBody UsuarioLogeoDTO dto) {
        return ResponseEntity.ok(authServices.loginEmpleadoCrm(dto));
    }

    // Logout para registrar la salida_sistema del empleado en el CRM
    @PostMapping("/logout/empleado")
    public ResponseEntity<String> logoutEmpleado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer idEmpleado = Integer.parseInt(auth.getPrincipal().toString());
        authServices.logoutEmpleadoCrm(idEmpleado);
        return ResponseEntity.ok("Sesión cerrada correctamente.");
    }
}