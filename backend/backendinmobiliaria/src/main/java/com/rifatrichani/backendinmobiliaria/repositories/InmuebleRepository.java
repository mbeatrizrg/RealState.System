package com.rifatrichani.backendinmobiliaria.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Inmueble;

public interface InmuebleRepository extends JpaRepository<Inmueble, Integer> {

    boolean existsByRrbr(String rrbr);

    // Filtra para el sitio web público únicamente los inmuebles que NO están vendidos (i.isVendido = false)
        @Query("SELECT i FROM Inmueble i WHERE " +
            "(:ciudadId IS NULL OR i.ciudad.id = :ciudadId) AND " +
            "(:tipoInmuebleId IS NULL OR i.tipoInmueble.id = :tipoInmuebleId) AND " +
            "(:estatusInmuebleId IS NULL OR i.estatusInmueble.id = :estatusInmuebleId) AND " +
            "(:isAlquiler IS NULL OR i.isAlquiler = :isAlquiler) AND " +
            "(:minPrecio IS NULL OR i.precio >= :minPrecio) AND " +
            "(:maxPrecio IS NULL OR i.precio <= :maxPrecio) AND " +
            "i.isVendido = false")
    List<Inmueble> buscarPorFiltrosMultiples(
            @Param("ciudadId") Integer ciudadId,
            @Param("tipoInmuebleId") Integer tipoInmuebleId,
            @Param("estatusInmuebleId") Integer estatusInmuebleId,
            @Param("isAlquiler") Boolean isAlquiler,
            @Param("minPrecio") BigDecimal minPrecio,
            @Param("maxPrecio") BigDecimal maxPrecio
    );
}