package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rifatrichani.backendinmobiliaria.models.Favorito;

public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {
    // Lista todos los favoritos guardados por un cliente específico
    List<Favorito> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // Verifica si un usuario ya tiene marcado un inmueble como favorito usado en VistosResponseCrmDTO :)
    boolean existsByUsuarioIdAndInmuebleId(Long usuarioId, Integer inmuebleId);
}