package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    boolean existsByInmuebleId(Integer inmuebleId);

    @Query("SELECT v FROM Venta v JOIN FETCH v.inmueble i JOIN FETCH i.empleado ORDER BY v.fechaVenta DESC")
    List<Venta> obtenerTodasConRelaciones();

    @Query("SELECT v FROM Venta v JOIN FETCH v.inmueble i JOIN FETCH i.empleado e WHERE e.id = :empleadoId ORDER BY v.fechaVenta DESC")
    List<Venta> obtenerPorEmpleadoId(@Param("empleadoId") Integer empleadoId);

    @Query("SELECT v FROM Venta v JOIN FETCH v.inmueble i JOIN FETCH i.empleado WHERE v.id = :id")
    Optional<Venta> obtenerConRelaciones(@Param("id") Integer id);

    //Ranking de vendedores (cantidad de ventas cerradas y suma de montoFinal)
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(e.id AS long), CONCAT(e.nombre, ' ', COALESCE(e.apellido, '')), e.correo, COUNT(v.id), SUM(v.montoFinal)) " +
        "FROM Venta v JOIN v.inmueble i JOIN i.empleado e " +
        "GROUP BY e.id, e.nombre, e.apellido, e.correo " +
        "ORDER BY COUNT(v.id) DESC, SUM(v.montoFinal) DESC")
    List<RankingItemResponseDTO> rankingVendedoresTopVentas(Pageable pageable);
}
