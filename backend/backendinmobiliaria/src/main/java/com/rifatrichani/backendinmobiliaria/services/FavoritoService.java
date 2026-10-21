package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.response.FavoritoListResponseDTO;

//Todas las operaciones reciben el id del usuario autenticado (tomado del token)
public interface FavoritoService {
    void guardarFavorito(Integer idUsuario, Integer inmuebleId);
    void quitarFavorito(Integer idUsuario, Integer inmuebleId);
    List<FavoritoListResponseDTO> obtenerFavoritosPorUsuario(Integer idUsuario);
}
