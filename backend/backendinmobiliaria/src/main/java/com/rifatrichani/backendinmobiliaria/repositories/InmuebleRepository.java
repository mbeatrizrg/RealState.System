package com.rifatrichani.backendinmobiliaria.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Inmueble;

public interface InmuebleRepository extends JpaRepository<Inmueble,Integer>{
    @Query("SELECT i FROM Inmueble i WHERE " +"(:ciudadId IS NULL OR i.ciudad.id = :ciudadId) AND " +"(:tipoInmuebleId IS NULL OR i.tipoInmueble.id = :tipoInmuebleId) AND " + "(:minPrecio IS NULL OR i.precio >= :minPrecio) AND " +"(:maxPrecio IS NULL OR i.precio <= :maxPrecio)")
    List<Inmueble> buscarPorFiltrosMultiples(@Param("ciudadId") Integer ciudadId, @Param("tipoInmuebleId") Integer tipoInmuebleId, @Param("minPrecio") BigDecimal minPrecio, @Param("maxPrecio") BigDecimal maxPrecio);
}
