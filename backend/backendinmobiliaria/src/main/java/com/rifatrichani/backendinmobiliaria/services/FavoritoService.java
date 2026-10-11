package com.rifatrichani.backendinmobiliaria.services;

import com.rifatrichani.backendinmobiliaria.dto.request.FavoritoRequestDTO;

public interface FavoritoService {
    public void guardarFavorito(FavoritoRequestDTO dto);
    public void eliminarFavortio(Integer favoritoId);

}
