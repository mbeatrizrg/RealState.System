package com.rifatrichani.backendinmobiliaria.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Inmueble;

public interface InmuebleRepository extends JpaRepository<Inmueble, Integer> {

    /* Barra de búsqueda multicriterio del menú inicial (solo listas seleccionables / dropdowns):
        ciudadId: ID seleccionado del dropdown de Ciudades (o null si seleccionó "Todas")
        tipoInmuebleId: ID seleccionado del dropdown de Tipo de Propiedad (o null si seleccionó "Todos")
        estatusInmuebleId: ID seleccionado del dropdown de Operación/Estatus, ej. Compra vs Alquiler (o null para ambos)
        minPrecio / maxPrecio: Rango de precios seleccionado (o null si no filtró por precio)
        Además filtra que el inmueble no esté "Vendido" para no mostrar propiedades ya cerradas en la web pública.
    */
    @Query("SELECT i FROM Inmueble i WHERE " +
            "(:ciudadId IS NULL OR i.ciudad.id = :ciudadId) AND " +
            "(:tipoInmuebleId IS NULL OR i.tipoInmueble.id = :tipoInmuebleId) AND " +
            "(:estatusInmuebleId IS NULL OR i.estatusInmueble.id = :estatusInmuebleId) AND " +
            "(:minPrecio IS NULL OR i.precio >= :minPrecio) AND " +
            "(:maxPrecio IS NULL OR i.precio <= :maxPrecio) AND " +
            "LOWER(i.estatusInmueble.nombre) <> 'vendido'")
    List<Inmueble> buscarPorFiltrosMultiples(
            @Param("ciudadId") Integer ciudadId,
            @Param("tipoInmuebleId") Integer tipoInmuebleId,
            @Param("estatusInmuebleId") Integer estatusInmuebleId,
            @Param("minPrecio") BigDecimal minPrecio,
            @Param("maxPrecio") BigDecimal maxPrecio
    );
}