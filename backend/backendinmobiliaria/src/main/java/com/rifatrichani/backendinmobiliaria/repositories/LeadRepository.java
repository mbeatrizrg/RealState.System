package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.Collection;
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

    //Contactos: leads de un usuario, el mas reciente primero
    @Query("SELECT l FROM Lead l JOIN FETCH l.inmueble i JOIN FETCH i.empleado WHERE l.usuario.id = :usuarioId ORDER BY l.id DESC")
    List<Lead> obtenerPorUsuarioId(@Param("usuarioId") Integer usuarioId);

    //Contactos: [usuarioId, estatus] del lead mas reciente de cada usuario (el estatus del contacto)
    @Query("SELECT l.usuario.id, l.estatus FROM Lead l WHERE l.id IN " +
           "(SELECT MAX(l2.id) FROM Lead l2 WHERE l2.usuario.id IN :usuarioIds GROUP BY l2.usuario.id)")
    List<Object[]> estatusMasRecientePorUsuario(@Param("usuarioIds") Collection<Integer> usuarioIds);

    //Contactos: [usuarioId, cantidad de leads]
    @Query("SELECT l.usuario.id, COUNT(l.id) FROM Lead l WHERE l.usuario.id IN :usuarioIds GROUP BY l.usuario.id")
    List<Object[]> contarPorUsuario(@Param("usuarioIds") Collection<Integer> usuarioIds);
}
