package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rifatrichani.backendinmobiliaria.models.Galeria;

public interface GaleriaRepository extends JpaRepository<Galeria, Integer> {
    // Obtiene todas las imágenes de un inmueble para InmuebleDetalleResponseDTO
    List<Galeria> findByInmuebleId(Integer inmuebleId);

    // Obtiene la primera imagen subida de un inmueble para usarla como imagenPrincipal en listados
    //  y favoritos
    Optional<Galeria> findFirstByInmuebleIdOrderByIdAsc(Integer inmuebleId);
}