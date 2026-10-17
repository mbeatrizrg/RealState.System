package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.rifatrichani.backendinmobiliaria.models.Visto;

public interface VistoRepository extends JpaRepository<Visto, Long> {

    // Obtiene el historial completo de vistas para el CRM cargando inmueble y usuario en una sola consulta
    @Query("SELECT v FROM Visto v JOIN FETCH v.inmueble i LEFT JOIN FETCH v.usuario u ORDER BY v.fechaVista DESC")
    List<Visto> obtenerTodosParaCrm();

    // Filtra las vistas en el CRM de un inmueble específico
    @Query("SELECT v FROM Visto v JOIN FETCH v.inmueble i LEFT JOIN FETCH v.usuario u WHERE i.id = :inmuebleId ORDER BY v.fechaVista DESC")
    List<Visto> obtenerPorInmuebleParaCrm(@Param("inmuebleId") Integer inmuebleId);
}