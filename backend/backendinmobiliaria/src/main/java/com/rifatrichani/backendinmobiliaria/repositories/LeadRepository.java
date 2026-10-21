package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Lead;

//El vendedor de un lead es el encargado de su inmueble (lead -> inmueble -> empleado)
public interface LeadRepository extends JpaRepository<Lead, Integer> {
    boolean existsByUsuarioIdAndInmuebleId(Integer usuarioId, Integer inmuebleId);

    @Query("SELECT l FROM Lead l JOIN FETCH l.usuario JOIN FETCH l.inmueble i JOIN FETCH i.empleado ORDER BY l.id DESC")
    List<Lead> obtenerTodosConRelaciones();

    @Query("SELECT l FROM Lead l JOIN FETCH l.usuario JOIN FETCH l.inmueble i JOIN FETCH i.empleado e WHERE e.id = :empleadoId ORDER BY l.id DESC")
    List<Lead> obtenerPorEmpleadoId(@Param("empleadoId") Integer empleadoId);

    @Query("SELECT l FROM Lead l JOIN FETCH l.usuario JOIN FETCH l.inmueble i JOIN FETCH i.empleado WHERE l.id = :id")
    Optional<Lead> obtenerConRelaciones(@Param("id") Integer id);
}
