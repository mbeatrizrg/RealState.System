package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.ActualizarPerfilDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.CambioCorreoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.UsuarioResponseDTO;

//El registro, login y contraseñas de usuarios estan en AuthServices
public interface UsuarioService {
    UsuarioResponseDTO obtenerPerfil(Integer idUsuario);
    UsuarioResponseDTO actualizarPerfil(Integer idUsuario, ActualizarPerfilDTO dto);
    //Envia un codigo al correo nuevo; el cambio se aplica al confirmarlo
    void solicitarCambioCorreo(Integer idUsuario, CambioCorreoDTO dto);
    void confirmarCambioCorreo(Integer idUsuario, String codigo);
    List<UsuarioResponseDTO> obtenerTodosLosUsuariosCrm();
    List<UsuarioResponseDTO> buscarUsuariosPorTerminoCrm(String termino);
}
