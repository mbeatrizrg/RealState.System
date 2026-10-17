package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.rifatrichani.backendinmobiliaria.models.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    boolean existsByInmuebleId(Integer inmuebleId);

    @Query("SELECT v FROM Venta v JOIN FETCH v.inmueble i JOIN FETCH i.empleado JOIN FETCH v.usuario ORDER BY v.fechaVenta DESC")
    List<Venta> obtenerTodasConRelaciones();
}