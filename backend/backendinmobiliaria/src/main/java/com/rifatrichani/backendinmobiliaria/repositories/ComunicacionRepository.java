package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Comunicacion;

public interface ComunicacionRepository extends JpaRepository<Comunicacion, Integer> {
    // Historial de comunicaciones de un Lead específico
    @Query("SELECT c FROM Comunicacion c JOIN FETCH c.canal JOIN FETCH c.lead l JOIN FETCH l.usuario JOIN FETCH l.inmueble i JOIN FETCH i.empleado " +
           "WHERE l.id = :leadId ORDER BY c.fechaCreacion DESC")
    List<Comunicacion> obtenerPorLeadId(@Param("leadId") Integer leadId);

    // Comunicaciones de los leads de un vendedor (el vendedor del lead es el encargado del inmueble)
    @Query("SELECT c FROM Comunicacion c JOIN FETCH c.canal JOIN FETCH c.lead l JOIN FETCH l.usuario JOIN FETCH l.inmueble i JOIN FETCH i.empleado e " +
           "WHERE e.id = :empleadoId ORDER BY c.fechaCreacion DESC")
    List<Comunicacion> obtenerPorEmpleadoId(@Param("empleadoId") Integer empleadoId);
}
