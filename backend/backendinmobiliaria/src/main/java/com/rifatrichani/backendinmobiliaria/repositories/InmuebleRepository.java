package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Inmueble;

public interface InmuebleRepository extends JpaRepository<Inmueble,Integer>{

    boolean existsByRrbr(String rrbr);
    boolean existsByRrbrAndIdNot(String rrbr, Integer id);

    //Busqueda del portal publico, todos los filtros son opcionales. Nunca muestra inmuebles vendidos
    @EntityGraph(attributePaths = {"ciudad", "tipoInmueble"})
    @Query("SELECT i FROM Inmueble i WHERE " +
            "(:ciudadId IS NULL OR i.ciudad.id = :ciudadId) AND " +
            "(:tipoInmuebleId IS NULL OR i.tipoInmueble.id = :tipoInmuebleId) AND " +
            "(:isAlquiler IS NULL OR i.alquiler = :isAlquiler) AND " +
            "(:minPrecio IS NULL OR i.precio >= :minPrecio) AND " +
            "(:maxPrecio IS NULL OR i.precio <= :maxPrecio) AND " +
            "i.vendido = false ORDER BY i.id DESC")
    List<Inmueble> buscarPorFiltrosMultiples(@Param("ciudadId") Integer ciudadId, @Param("tipoInmuebleId") Integer tipoInmuebleId,
            @Param("isAlquiler") Boolean isAlquiler,
            @Param("minPrecio") Integer minPrecio, @Param("maxPrecio") Integer maxPrecio);

    //CRM: todos los inmuebles (incluidos los vendidos)
    @EntityGraph(attributePaths = {"ciudad", "tipoInmueble"})
    @Query("SELECT i FROM Inmueble i ORDER BY i.id DESC")
    List<Inmueble> obtenerTodosCrm();

    //CRM: inmuebles a cargo de un vendedor
    @EntityGraph(attributePaths = {"ciudad", "tipoInmueble"})
    List<Inmueble> findByEmpleadoIdOrderByIdDesc(Integer empleadoId);
}
