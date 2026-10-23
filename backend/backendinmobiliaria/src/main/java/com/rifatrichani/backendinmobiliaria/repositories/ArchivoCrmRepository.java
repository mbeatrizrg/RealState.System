package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.ArchivoCrm;

public interface ArchivoCrmRepository extends JpaRepository<ArchivoCrm, Integer> {
    //Adjuntos de varias actividades en una sola consulta
    @Query("SELECT a FROM ArchivoCrm a WHERE a.actividad.id IN :ids ORDER BY a.id")
    List<ArchivoCrm> obtenerPorActividades(@Param("ids") Collection<Integer> actividadIds);

    List<ArchivoCrm> findByActividadId(Integer actividadId);

    //Documentos legales de un inmueble
    List<ArchivoCrm> findByInmuebleIdOrderByIdDesc(Integer inmuebleId);

    //Adjuntos de todas las actividades de un contacto, el mas reciente primero
    @Query("SELECT a FROM ArchivoCrm a JOIN FETCH a.actividad act WHERE act.contacto.id = :contactoId ORDER BY a.id DESC")
    List<ArchivoCrm> obtenerPorContacto(@Param("contactoId") Integer contactoId);
}
