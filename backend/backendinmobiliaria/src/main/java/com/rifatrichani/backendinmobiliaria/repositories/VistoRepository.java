package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Visto;

public interface VistoRepository extends JpaRepository<Visto, Integer> {
    // Historial completo de vistas (admin)
    @Query("SELECT v FROM Visto v JOIN FETCH v.inmueble i JOIN FETCH v.usuario u ORDER BY v.fechaVista DESC")
    List<Visto> obtenerTodosParaCrm();

    // Vistas de los inmuebles de un vendedor
    @Query("SELECT v FROM Visto v JOIN FETCH v.inmueble i JOIN FETCH v.usuario u WHERE i.empleado.id = :empleadoId ORDER BY v.fechaVista DESC")
    List<Visto> obtenerPorEmpleadoParaCrm(@Param("empleadoId") Integer empleadoId);

    // Vistas de un inmueble específico
    @Query("SELECT v FROM Visto v JOIN FETCH v.inmueble i JOIN FETCH v.usuario u WHERE i.id = :inmuebleId ORDER BY v.fechaVista DESC")
    List<Visto> obtenerPorInmuebleParaCrm(@Param("inmuebleId") Integer inmuebleId);

    // Últimas vistas de un contacto (usuario)
    @Query("SELECT v FROM Visto v JOIN FETCH v.inmueble i WHERE v.usuario.id = :usuarioId ORDER BY v.fechaVista DESC")
    List<Visto> obtenerRecientesPorUsuario(@Param("usuarioId") Integer usuarioId, Pageable pageable);

    // Ranking de inmuebles más vistos
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(i.id AS long), i.titulo, i.ciudad.nombre, COUNT(v.id), CAST(i.precio AS bigdecimal)) " +
        "FROM Visto v JOIN v.inmueble i " +
        "GROUP BY i.id, i.titulo, i.ciudad.nombre, i.precio " +
        "ORDER BY COUNT(v.id) DESC")
    List<RankingItemResponseDTO> rankingInmueblesMasVistos(Pageable pageable);

    // Ranking de ciudades más buscadas (según las vistas de inmuebles de cada ciudad)
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(c.id AS long), c.nombre, 'Ciudad', COUNT(v.id), CAST(0 AS bigdecimal)) " +
        "FROM Visto v JOIN v.inmueble i JOIN i.ciudad c " +
        "GROUP BY c.id, c.nombre " +
        "ORDER BY COUNT(v.id) DESC")
    List<RankingItemResponseDTO> rankingCiudadesMasBuscadas(Pageable pageable);

    // Ranking de tipos de inmueble más buscados (Casa, Apartamento, etc.)
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(t.id AS long), t.nombre, 'Tipo de Propiedad', COUNT(v.id), CAST(0 AS bigdecimal)) " +
        "FROM Visto v JOIN v.inmueble i JOIN i.tipoInmueble t " +
        "GROUP BY t.id, t.nombre " +
        "ORDER BY COUNT(v.id) DESC")
    List<RankingItemResponseDTO> rankingTiposInmuebleMasBuscados(Pageable pageable);
}
