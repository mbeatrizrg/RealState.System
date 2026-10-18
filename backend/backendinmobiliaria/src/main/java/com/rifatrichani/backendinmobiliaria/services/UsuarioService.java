package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioCorreoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.UsuarioResponseDTO;

public interface UsuarioService {
    void registrarUsuario(UsuarioRegistroDTO dto);
    void actualizarCorreo(Long idObjetivo, Long idUsuario, UsuarioCorreoRequestDTO dto);
    List<UsuarioResponseDTO> obtenerTodosLosUsuariosCrm();
    List<UsuarioResponseDTO> buscarUsuariosPorTerminoCrm(String termino);
}