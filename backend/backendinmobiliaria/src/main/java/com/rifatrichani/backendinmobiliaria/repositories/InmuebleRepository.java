package com.rifatrichani.backendinmobiliaria.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;

public interface InmuebleRepository extends JpaRepository<Inmueble,Integer>{
    @Query("SELECT inm FROM Inmueble inm WHERE(LOWER(inm.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) OR LOWER(inm.ciudad.nombre) LIKE LOWER (CONCAT('%', :titulo, '%'))) AND inm.precio <= :maxPrecio AND inm.precio >= :minPrecio")
    List<InmuebleListResponseDTO> getByFilter(@Param("titulo") String titulo, @Param("maxPrecio") BigDecimal maxPrecio,@Param ("minPrecio") BigDecimal minPrecio);
}
