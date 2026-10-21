package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.CambioContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.ConfirmarCuentaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.RecuperarContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.RestablecerContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioCorreoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LoginResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PerfilSesionDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;

import jakarta.validation.Valid;

/* Inicio y cierre de sesion, contraseñas y verificacion de cuentas.
   El registro de usuarios del portal esta en POST /api/usuario/registro.
   Los empleados del CRM NO se registran solos: los crea un administrador en POST /api/empleado/registro */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServices authServices;

    public AuthController(AuthServices authServices) {
        this.authServices = authServices;
    }

    // ===================== Públicos =====================

    // CRM: login de vendedores y administradores
    @PostMapping("/login/empleado")
    public ResponseEntity<LoginResponseDTO> loginEmpleado(@Valid @RequestBody UsuarioLogeoDTO dto) {
        return ResponseEntity.ok(authServices.loginEmpleadoCrm(dto));
    }

    // Portal web: login de clientes con el correo ya confirmado
    @PostMapping("/login/usuario")
    public ResponseEntity<LoginResponseDTO> loginUsuario(@Valid @RequestBody UsuarioLogeoDTO dto) {
        return ResponseEntity.ok(authServices.loginUsuarioWeb(dto));
    }

    // Portal web: confirma la cuenta con el código de 6 dígitos enviado al correo
    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, String>> confirmarCuenta(@Valid @RequestBody ConfirmarCuentaDTO dto) {
        authServices.confirmarCuenta(dto);
        return ResponseEntity.ok(Map.of("mensaje", "Cuenta confirmada. Ya puede iniciar sesión."));
    }

    // Portal web: reenvía el código de verificación. Responde lo mismo exista o no la cuenta
    @PostMapping("/reenviar-codigo")
    public ResponseEntity<Map<String, String>> reenviarCodigo(@Valid @RequestBody UsuarioCorreoRequestDTO dto) {
        authServices.reenviarCodigo(dto.getCorreo());
        return ResponseEntity.ok(Map.of("mensaje", "Si la cuenta existe y no está confirmada, se envió un nuevo código."));
    }

    // "Olvidé mi contraseña" (portal y CRM), paso 1: envía un código al correo. Responde lo mismo exista o no la cuenta
    @PostMapping("/recuperar")
    public ResponseEntity<Map<String, String>> solicitarRecuperacion(@Valid @RequestBody RecuperarContrasenaDTO dto) {
        authServices.solicitarRecuperacion(dto);
        return ResponseEntity.ok(Map.of("mensaje", "Si el correo está registrado, recibirá un código para restablecer su contraseña."));
    }

    // "Olvidé mi contraseña", paso 2: código + contraseña nueva. Cierra las sesiones abiertas de la cuenta
    @PostMapping("/recuperar/confirmar")
    public ResponseEntity<Map<String, String>> restablecerContrasena(@Valid @RequestBody RestablecerContrasenaDTO dto) {
        authServices.restablecerContrasena(dto);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada. Ya puede iniciar sesión."));
    }

    // ===================== Con sesión iniciada =====================

    // Datos de la cuenta conectada (el frontend lo usa para saber quién es y con qué rol)
    @GetMapping("/me")
    public ResponseEntity<PerfilSesionDTO> sesionActual() {
        return ResponseEntity.ok(authServices.obtenerPerfilSesion(SesionActual.cuenta()));
    }

    // Cierra la sesión de cualquier tipo de cuenta: el token deja de ser válido aunque no haya expirado
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        CuentaAutenticada cuenta = SesionActual.cuenta();
        if (cuenta.tipo() == CuentaAutenticada.TipoCuenta.EMPLEADO) {
            authServices.logoutEmpleadoCrm(cuenta.id());
        } else {
            authServices.logoutUsuarioWeb(cuenta.id());
        }
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente."));
    }

    // Se mantiene por compatibilidad con el frontend del CRM
    @PostMapping("/logout/empleado")
    public ResponseEntity<Map<String, String>> logoutEmpleado() {
        authServices.logoutEmpleadoCrm(SesionActual.idEmpleado());
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente."));
    }

    // CRM: cambio de contraseña propia. Cierra todas las sesiones abiertas del empleado
    @PutMapping("/empleado/contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasenaEmpleado(@Valid @RequestBody CambioContrasenaDTO dto) {
        authServices.cambiarContrasenaEmpleado(SesionActual.idEmpleado(), dto);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada. Inicie sesión nuevamente."));
    }

    // Portal: cambio de contraseña propia. Cierra todas las sesiones abiertas del usuario
    @PutMapping("/usuario/contrasena")
    @PreAuthorize("hasAuthority('ROLE_USUARIO')")
    public ResponseEntity<Map<String, String>> cambiarContrasenaUsuario(@Valid @RequestBody CambioContrasenaDTO dto) {
        authServices.cambiarContrasenaUsuario(SesionActual.idUsuario(), dto);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada. Inicie sesión nuevamente."));
    }
}
