package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.RankingService;
//En el trabajo me dijeron que si queria hacer un ranking deberia de hacerlo con un unico endpoint y que el tipo de ranking se pasara como 
// parametro, asi que hice este controlador para centralizar todos los rankings del CRM en un solo endpoint
@RestController
@RequestMapping("/api/ranking")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class RankingController {
    private final RankingService rankingService;
    public RankingController(RankingService rs) {
        this.rankingService = rs;
    }
    /* Endpoint único y centralizado para todos los rankings del CRM
    Ejemplos de consumo desde el Frontend:
        - GET /api/ranking?tipo=INMUEBLES_MAS_VISTOS&limite=10
        - GET /api/ranking?tipo=VENDEDORES_MAS_VENTAS&limite=5
        - GET /api/ranking?tipo=CIUDADES_MAS_BUSCADAS
        - GET /api/ranking?tipo=TIPOS_MAS_BUSCADOS
        - GET /api/ranking?tipo=INMUEBLES_MAS_FAVORITOS
    */
    @GetMapping
    public ResponseEntity<List<RankingItemResponseDTO>> obtenerRanking(
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "limite", defaultValue = "10") int limite) {
        return ResponseEntity.ok(rankingService.obtenerRankingPorTipo(tipo, limite));
    }
}