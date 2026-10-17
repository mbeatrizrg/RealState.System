package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.request.FavoritoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.FavoritoListResponseDTO;

public interface FavoritoService {
    void guardarFavorito(FavoritoRequestDTO dto);
    void eliminarFavortio(Integer favoritoId);
    List<FavoritoListResponseDTO> obtenerFavoritosPorUsuario(Long usuarioId);
}