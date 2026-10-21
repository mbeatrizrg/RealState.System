package com.rifatrichani.backendinmobiliaria.services;

import com.rifatrichani.backendinmobiliaria.dto.request.CambioContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.ConfirmarCuentaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.RecuperarContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.RestablecerContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LoginResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PerfilSesionDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

public interface AuthServices {
    // ----- CRM (empleados) -----
    LoginResponseDTO loginEmpleadoCrm(UsuarioLogeoDTO dto);
    void logoutEmpleadoCrm(Integer idEmpleado);
    void cambiarContrasenaEmpleado(Integer idEmpleado, CambioContrasenaDTO dto);

    // ----- Portal web (usuarios) -----
    LoginResponseDTO loginUsuarioWeb(UsuarioLogeoDTO dto);
    void logoutUsuarioWeb(Integer idUsuario);
    void cambiarContrasenaUsuario(Integer idUsuario, CambioContrasenaDTO dto);
    //Devuelve el mensaje que se le muestra al usuario
    String registrarUsuario(UsuarioRegistroDTO dto);
    void confirmarCuenta(ConfirmarCuentaDTO dto);
    void reenviarCodigo(String correo);

    // ----- Comunes -----
    //"Olvidé mi contraseña": envia un codigo si la cuenta existe (nunca revela si existe)
    void solicitarRecuperacion(RecuperarContrasenaDTO dto);
    void restablecerContrasena(RestablecerContrasenaDTO dto);
    PerfilSesionDTO obtenerPerfilSesion(CuentaAutenticada cuenta);
}
