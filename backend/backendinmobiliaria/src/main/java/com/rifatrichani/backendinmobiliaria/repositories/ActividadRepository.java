package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Actividad;

public interface ActividadRepository extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {
    //Tabla de tareas (los filtros se arman en TareaEspecificaciones)
    @Override
    @EntityGraph(attributePaths = {"empleado", "contacto", "negocio", "inmueble", "negocio.inmueble"})
    Page<Actividad> findAll(Specification<Actividad> spec, Pageable pageable);

    //Historial del contacto (incluye las actividades de sus negocios), la mas reciente primero
    @EntityGraph(attributePaths = {"empleado", "negocio"})
    List<Actividad> findByContactoIdOrderByFechaCreacionDescIdDesc(Integer contactoId);

    @EntityGraph(attributePaths = {"empleado", "negocio"})
    List<Actividad> findByNegocioIdOrderByFechaCreacionDescIdDesc(Integer negocioId);

    //Al borrar un negocio sus actividades se conservan en el historial del contacto
    @Modifying
    @Query("UPDATE Actividad a SET a.negocio = null WHERE a.negocio.id = :negocioId")
    void desvincularNegocio(@Param("negocioId") Integer negocioId);

    //Estadisticas: visitas realizadas (tareas de tipo VISITA y reuniones completadas) y todas las tareas
    @Query("SELECT a FROM Actividad a LEFT JOIN FETCH a.negocio n LEFT JOIN FETCH a.inmueble LEFT JOIN FETCH a.contacto "
            + "WHERE a.completada = true AND ((a.tipo = com.rifatrichani.backendinmobiliaria.models.Actividad.Tipo.TAREA AND a.tipoTarea = 'VISITA') "
            + "OR a.tipo = com.rifatrichani.backendinmobiliaria.models.Actividad.Tipo.REUNION)")
    List<Actividad> obtenerVisitasRealizadas();

    @Query("SELECT a FROM Actividad a LEFT JOIN FETCH a.empleado WHERE a.tipo = com.rifatrichani.backendinmobiliaria.models.Actividad.Tipo.TAREA")
    List<Actividad> obtenerTareas();

    //Vista detallada de un inmueble: el historial de los negocios del inmueble
    @Query("SELECT a FROM Actividad a JOIN FETCH a.negocio n WHERE n.inmueble.id = :inmuebleId ORDER BY a.fechaCreacion")
    List<Actividad> obtenerPorNegociosDeInmueble(@Param("inmuebleId") Integer inmuebleId);
}
