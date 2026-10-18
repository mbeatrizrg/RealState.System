package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Visto;

public interface VistoRepository extends JpaRepository<Visto, Long> {

    // Obtiene el historial completo de vistas para el CRM cargando inmueble y usuario en una sola consulta
    @Query("SELECT v FROM Visto v JOIN FETCH v.inmueble i LEFT JOIN FETCH v.usuario u ORDER BY v.fechaVista DESC")
    List<Visto> obtenerTodosParaCrm();

    //Filtra las vistas en el CRM de un inmueble específico
    @Query("SELECT v FROM Visto v JOIN FETCH v.inmueble i LEFT JOIN FETCH v.usuario u WHERE i.id = :inmuebleId ORDER BY v.fechaVista DESC")
    List<Visto> obtenerPorInmuebleParaCrm(@Param("inmuebleId") Integer inmuebleId);
    //Ranking de Inmuebles más vistos
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(i.id AS long), i.titulo, i.ciudad.nombre, COUNT(v.id), i.precio) " +
        "FROM Visto v JOIN v.inmueble i " +
        "GROUP BY i.id, i.titulo, i.ciudad.nombre, i.precio " +
        "ORDER BY COUNT(v.id) DESC")
    List<RankingItemResponseDTO> rankingInmueblesMasVistos(org.springframework.data.domain.Pageable pageable);

//Ranking de Ciudades más buscadas (según los clics en inmuebles de cada ciudad)
@Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(c.id AS long), c.nombre, 'Ciudad', COUNT(v.id), CAST(0 AS bigdecimal)) " +
        "FROM Visto v JOIN v.inmueble i JOIN i.ciudad c " +
        "GROUP BY c.id, c.nombre " +
        "ORDER BY COUNT(v.id) DESC")
    List<RankingItemResponseDTO> rankingCiudadesMasBuscadas(org.springframework.data.domain.Pageable pageable);

    //Ranking de Tipos de Inmueble más buscados (Casa, Apartamento, etc.)
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(t.id AS long), t.nombre, 'Tipo de Propiedad', COUNT(v.id), CAST(0 AS bigdecimal)) " +
        "FROM Visto v JOIN v.inmueble i JOIN i.tipoInmueble t " +
        "GROUP BY t.id, t.nombre " +
        "ORDER BY COUNT(v.id) DESC")
    List<RankingItemResponseDTO> rankingTiposInmuebleMasBuscados(org.springframework.data.domain.Pageable pageable);
}