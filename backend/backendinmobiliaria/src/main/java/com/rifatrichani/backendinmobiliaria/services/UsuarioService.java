package com.rifatrichani.backendinmobiliaria.services;

import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioCorreoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;

public interface  UsuarioService {
    public void registrarUsuario(UsuarioRegistroDTO dto);
    public void actualizarCorreo(Long idUsuario, UsuarioCorreoRequestDTO dto);
}