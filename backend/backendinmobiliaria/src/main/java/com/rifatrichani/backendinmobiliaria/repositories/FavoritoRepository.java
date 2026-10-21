package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Favorito;

//La columna "favorito" indica si sigue activo: quitar un favorito lo desactiva en lugar de borrar la fila
public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {
    @EntityGraph(attributePaths = {"inmueble", "inmueble.ciudad"})
    List<Favorito> findByUsuarioIdAndFavoritoTrueOrderByIdDesc(Integer usuarioId);

    Optional<Favorito> findFirstByUsuarioIdAndInmuebleId(Integer usuarioId, Integer inmuebleId);

    boolean existsByUsuarioIdAndInmuebleIdAndFavoritoTrue(Integer usuarioId, Integer inmuebleId);

    //Ranking de inmuebles con mas favoritos activos
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(i.id AS long), i.titulo, i.ciudad.nombre, COUNT(f.id), CAST(i.precio AS bigdecimal)) " +
        "FROM Favorito f JOIN f.inmueble i WHERE f.favorito = true " +
        "GROUP BY i.id, i.titulo, i.ciudad.nombre, i.precio " +
        "ORDER BY COUNT(f.id) DESC")
    List<RankingItemResponseDTO> rankingInmueblesMasFavoritos(Pageable pageable);
}
