package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.rifatrichani.backendinmobiliaria.models.Lead;
//Por los momentos se tiene unicamente las consultas del Lead
public interface LeadRepository extends JpaRepository<Lead, Integer> {

    // Obtiene todos los leads ordenados del más reciente al más antiguo para el CRM
    @Query("SELECT l FROM Lead l JOIN FETCH l.usuario JOIN FETCH l.empleado JOIN FETCH l.estatusVenta ORDER BY l.fechaCreacion DESC")
    List<Lead> obtenerTodosConRelaciones();

    // Obtiene los leads asignados a un vendedor específico
    @Query("SELECT l FROM Lead l JOIN FETCH l.usuario JOIN FETCH l.empleado e JOIN FETCH l.estatusVenta WHERE e.id = :empleadoId ORDER BY l.fechaCreacion DESC")
    List<Lead> obtenerPorEmpleadoId(@Param("empleadoId") Integer empleadoId);
}