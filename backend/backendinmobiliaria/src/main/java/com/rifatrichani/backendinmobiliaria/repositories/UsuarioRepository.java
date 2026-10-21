package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    //Busqueda del CRM por coincidencia parcial en correo, nombre o apellido
    @Query("SELECT us FROM Usuario us WHERE LOWER(us.correo) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(us.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(us.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Usuario> buscarPorTermino(@Param("termino") String termino);
    boolean existsByCorreoIgnoreCase(String correo);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
