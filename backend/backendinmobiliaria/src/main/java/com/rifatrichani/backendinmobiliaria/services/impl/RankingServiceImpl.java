package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;
import com.rifatrichani.backendinmobiliaria.repositories.FavoritoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.VentaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.VistoRepository;
import com.rifatrichani.backendinmobiliaria.services.RankingService;

@Service
public class RankingServiceImpl implements RankingService {
    private final VistoRepository vistoRepository;
    private final VentaRepository ventaRepository;
    private final FavoritoRepository favoritoRepository;
    public RankingServiceImpl(VistoRepository vistoRepo, VentaRepository ventaRepo, FavoritoRepository favRepo) {
        this.vistoRepository = vistoRepo;
        this.ventaRepository = ventaRepo;
        this.favoritoRepository = favRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingItemResponseDTO> obtenerRankingPorTipo(String tipo, int limite) {
        int topLimite = (limite <= 0 || limite > 100) ? 10 : limite;
        Pageable pageable = PageRequest.of(0, topLimite);
        return switch (tipo.toUpperCase()) {
            case "INMUEBLES_MAS_VISTOS" -> vistoRepository.rankingInmueblesMasVistos(pageable);
            case "VENDEDORES_MAS_VENTAS" -> ventaRepository.rankingVendedoresTopVentas(pageable);
            case "CIUDADES_MAS_BUSCADAS" -> vistoRepository.rankingCiudadesMasBuscadas(pageable);
            case "TIPOS_MAS_BUSCADOS" -> vistoRepository.rankingTiposInmuebleMasBuscados(pageable);
            case "INMUEBLES_MAS_FAVORITOS" -> favoritoRepository.rankingInmueblesMasFavoritos(pageable);
            default -> throw new IllegalArgumentException(
                    "Tipo de ranking no válido: '" + tipo + "'. Valores permitidos: " +
                    "INMUEBLES_MAS_VISTOS, VENDEDORES_MAS_VENTAS, CIUDADES_MAS_BUSCADAS, TIPOS_MAS_BUSCADOS, INMUEBLES_MAS_FAVORITOS."
            );
        };
    }
}