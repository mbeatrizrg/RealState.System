package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.CambioContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.ConfirmarCuentaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioCorreoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LoginResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;

import jakarta.validation.Valid;

/* Inicio y cierre de sesion. El registro de usuarios del portal esta en POST /api/usuario/registro.
   Los empleados del CRM NO se registran solos: los crea un administrador en POST /api/empleado/registro */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServices authServices;

    public AuthController(AuthServices authServices) {
        this.authServices = authServices;
    }

    // PÚBLICO (CRM): login de vendedores y administradores
    @PostMapping("/login/empleado")
    public ResponseEntity<LoginResponseDTO> loginEmpleado(@Valid @RequestBody UsuarioLogeoDTO dto) {
        return ResponseEntity.ok(authServices.loginEmpleadoCrm(dto));
    }

    // PÚBLICO (Portal web): login de clientes con el correo ya confirmado
    @PostMapping("/login/usuario")
    public ResponseEntity<LoginResponseDTO> loginUsuario(@Valid @RequestBody UsuarioLogeoDTO dto) {
        return ResponseEntity.ok(authServices.loginUsuarioWeb(dto));
    }

    // PÚBLICO (Portal web): confirma la cuenta con el código de 6 dígitos enviado al correo
    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, String>> confirmarCuenta(@Valid @RequestBody ConfirmarCuentaDTO dto) {
        authServices.confirmarCuenta(dto);
        return ResponseEntity.ok(Map.of("mensaje", "Cuenta confirmada. Ya puede iniciar sesión."));
    }

    // PÚBLICO (Portal web): reenvía el código. Responde lo mismo exista o no la cuenta
    @PostMapping("/reenviar-codigo")
    public ResponseEntity<Map<String, String>> reenviarCodigo(@Valid @RequestBody UsuarioCorreoRequestDTO dto) {
        authServices.reenviarCodigo(dto.getCorreo());
        return ResponseEntity.ok(Map.of("mensaje", "Si la cuenta existe y no está confirmada, se envió un nuevo código."));
    }

    // AUTENTICADO: datos de la sesión actual (el frontend lo usa para saber quién está conectado y con qué rol)
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> sesionActual() {
        CuentaAutenticada cuenta = SesionActual.cuenta();
        return ResponseEntity.ok(Map.of("id", cuenta.id(), "tipo", cuenta.tipo().name(), "rol", cuenta.rol()));
    }

    // AUTENTICADO (CRM): cierra la sesión; el token deja de ser válido aunque no haya expirado
    @PostMapping("/logout/empleado")
    public ResponseEntity<Map<String, String>> logoutEmpleado() {
        authServices.logoutEmpleadoCrm(SesionActual.idEmpleado());
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente."));
    }

    // AUTENTICADO (CRM): cambio de contraseña propia. Cierra todas las sesiones abiertas del empleado
    @PutMapping("/empleado/contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(@Valid @RequestBody CambioContrasenaDTO dto) {
        authServices.cambiarContrasenaEmpleado(SesionActual.idEmpleado(), dto);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada. Inicie sesión nuevamente."));
    }
}
