package com.rifatrichani.backendinmobiliaria.services;

import com.rifatrichani.backendinmobiliaria.dto.request.CambioContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.ConfirmarCuentaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LoginResponseDTO;

public interface AuthServices {
    LoginResponseDTO loginEmpleadoCrm(UsuarioLogeoDTO dto);
    LoginResponseDTO loginUsuarioWeb(UsuarioLogeoDTO dto);
    void logoutEmpleadoCrm(Integer idEmpleado);
    void cambiarContrasenaEmpleado(Integer idEmpleado, CambioContrasenaDTO dto);
    //Devuelve el mensaje que se le muestra al usuario
    String registrarUsuario(UsuarioRegistroDTO dto);
    void confirmarCuenta(ConfirmarCuentaDTO dto);
    void reenviarCodigo(String correo);
}
