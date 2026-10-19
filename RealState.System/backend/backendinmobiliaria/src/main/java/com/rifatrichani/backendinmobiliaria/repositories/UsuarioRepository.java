package com.rifatrichani.backendinmobiliaria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Usuario;
import java.util.List;
import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    @Query ("SELECT us FROM Usuario us WHERE(LOWER(us.correo) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(us.nombre) LIKE LOWER(CONCAT('%',:termino, '%')))")
    public List<Usuario> findByCorreoOrName(@Param ("termino") String termino);
    boolean existsByCorreo (String correo);
    Optional<Usuario>findByCorreo(String correo);

}
