package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Favorito;

public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {
    // Lista todos los favoritos guardados por un cliente específico
    List<Favorito> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // Verifica si un usuario ya tiene marcado un inmueble como favorito usado en VistosResponseCrmDTO :)
    boolean existsByUsuarioIdAndInmuebleId(Long usuarioId, Integer inmuebleId);
    // 5. Ranking de Inmuebles con más Favoritos guardados por los clientes
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(i.id AS long), i.titulo, i.ciudad.nombre, COUNT(f.id), i.precio) " +
        "FROM Favorito f JOIN f.inmueble i " +
        "GROUP BY i.id, i.titulo, i.ciudad.nombre, i.precio " +
        "ORDER BY COUNT(f.id) DESC")
    List<RankingItemResponseDTO> rankingInmueblesMasFavoritos(org.springframework.data.domain.Pageable pageable);
}