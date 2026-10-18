package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;

public interface RankingService {
    List<RankingItemResponseDTO> obtenerRankingPorTipo(String tipo, int limite);
}