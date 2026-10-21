package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.response.UsuarioResponseDTO;

//El registro de usuarios esta en AuthServices (maneja la contraseña y la verificacion del correo)
public interface UsuarioService {
    UsuarioResponseDTO obtenerPerfil(Integer idUsuario);
    List<UsuarioResponseDTO> obtenerTodosLosUsuariosCrm();
    List<UsuarioResponseDTO> buscarUsuariosPorTerminoCrm(String termino);
}
