package com.rifatrichani.backendinmobiliaria.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {
    //Busqueda del CRM por coincidencia parcial en correo, nombre o apellido
    @Query("SELECT us FROM Usuario us WHERE LOWER(us.correo) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(us.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(us.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Usuario> buscarPorTermino(@Param("termino") String termino);
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByCedula(Integer cedula);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    //Listado de contactos del CRM (los filtros se arman en ContactoEspecificaciones)
    @Override
    @EntityGraph(attributePaths = {"propietario"})
    Page<Usuario> findAll(Specification<Usuario> spec, Pageable pageable);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.propietario WHERE u.id = :id")
    Optional<Usuario> obtenerConPropietario(@Param("id") Integer id);

    /* Marca la ultima actividad del contacto (lead, cambio de estatus, comunicacion, vista o favorito).
       La hora la pone Java, igual que @CreationTimestamp en las demas fechas, para que todas usen la misma zona horaria */
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimaActividad = :fecha WHERE u.id = :id")
    void registrarActividad(@Param("id") Integer id, @Param("fecha") LocalDateTime fecha);

    default void registrarActividad(Integer id) {
        registrarActividad(id, LocalDateTime.now());
    }

    //El primer vendedor que recibe un lead del contacto queda como su propietario (no reemplaza a uno existente)
    @Modifying
    @Query("UPDATE Usuario u SET u.propietario = :propietario WHERE u.id = :id AND u.propietario IS NULL")
    int asignarPropietarioSiNoTiene(@Param("id") Integer id, @Param("propietario") Empleado propietario);
}
