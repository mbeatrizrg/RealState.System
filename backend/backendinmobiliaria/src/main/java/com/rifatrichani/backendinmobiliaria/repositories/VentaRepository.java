package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    boolean existsByInmuebleId(Integer inmuebleId);

    @Query("SELECT v FROM Venta v JOIN FETCH v.inmueble i JOIN FETCH i.empleado JOIN FETCH v.usuario ORDER BY v.fechaVenta DESC")
    List<Venta> obtenerTodasConRelaciones();
    //Ranking de Vendedores que venden más (ordenado por cantidad de ventas cerradas y suma de montoFinal)
    @Query("SELECT new com.rifatrichani.backendinmobiliaria.dto.response.RankingItemResponseDTO(" +
        "CAST(e.id AS long), CONCAT(e.nombre, ' ', e.apellido), e.correo, COUNT(v.id), SUM(v.montoFinal)) " +
        "FROM Venta v JOIN v.inmueble i JOIN i.empleado e " +
        "GROUP BY e.id, e.nombre, e.apellido, e.correo " +
        "ORDER BY COUNT(v.id) DESC, SUM(v.montoFinal) DESC")
    List<RankingItemResponseDTO> rankingVendedoresTopVentas(Pageable pageable);
}