package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Negocio;

public interface NegocioRepository extends JpaRepository<Negocio, Integer>, JpaSpecificationExecutor<Negocio> {
    //Tabla y tablero de negocios (los filtros se arman en NegocioEspecificaciones)
    @Override
    @EntityGraph(attributePaths = {"inmueble", "inmueble.tipoInmueble", "contacto", "etapa", "propietario"})
    Page<Negocio> findAll(Specification<Negocio> spec, Pageable pageable);

    @Query("SELECT n FROM Negocio n JOIN FETCH n.inmueble i JOIN FETCH i.tipoInmueble JOIN FETCH n.contacto "
            + "JOIN FETCH n.etapa LEFT JOIN FETCH n.propietario WHERE n.id = :id")
    Optional<Negocio> obtenerConRelaciones(@Param("id") Integer id);

    //Ficha del contacto y del inmueble: sus negocios, el mas reciente primero
    @EntityGraph(attributePaths = {"inmueble", "inmueble.tipoInmueble", "contacto", "etapa", "propietario"})
    List<Negocio> findByContactoIdOrderByIdDesc(Integer contactoId);

    @EntityGraph(attributePaths = {"inmueble", "inmueble.tipoInmueble", "contacto", "etapa", "propietario"})
    List<Negocio> findByInmuebleIdOrderByIdDesc(Integer inmuebleId);

    //Inmuebles: [inmuebleId, cantidad de negocios abiertos]
    @Query("SELECT n.inmueble.id, COUNT(n.id) FROM Negocio n WHERE n.inmueble.id IN :ids AND n.etapa.abierto = true GROUP BY n.inmueble.id")
    List<Object[]> contarAbiertosPorInmueble(@Param("ids") Collection<Integer> ids);

    //Estadisticas: todos los negocios de varios inmuebles
    @EntityGraph(attributePaths = {"inmueble", "contacto", "etapa", "propietario"})
    List<Negocio> findByInmuebleIdIn(Collection<Integer> inmuebleIds);
}
