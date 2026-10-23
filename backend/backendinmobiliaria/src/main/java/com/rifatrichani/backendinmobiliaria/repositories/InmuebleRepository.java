package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Inmueble;

import jakarta.persistence.LockModeType;

public interface InmuebleRepository extends JpaRepository<Inmueble,Integer>, JpaSpecificationExecutor<Inmueble> {

    boolean existsByRrbr(String rrbr);
    boolean existsByRrbrAndIdNot(String rrbr, Integer id);

    //Listados paginados del portal y del CRM (InmuebleEspecificaciones y PropiedadEspecificaciones)
    @Override
    @EntityGraph(attributePaths = {"ciudad", "tipoInmueble", "empleado"})
    Page<Inmueble> findAll(Specification<Inmueble> spec, Pageable pageable);

    /* Bloquea la fila del inmueble hasta el fin de la transaccion. Se usa al agregar fotos: dos subidas simultaneas
       no pueden contar las fotos al mismo tiempo y pasarse del maximo permitido */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inmueble i WHERE i.id = :id")
    Optional<Inmueble> bloquearPorId(@Param("id") Integer id);
}
