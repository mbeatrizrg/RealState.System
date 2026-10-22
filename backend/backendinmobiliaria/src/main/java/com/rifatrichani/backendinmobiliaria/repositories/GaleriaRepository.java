package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rifatrichani.backendinmobiliaria.models.Galeria;

public interface GaleriaRepository extends JpaRepository<Galeria,Integer>{
    //Todas las imagenes de un inmueble, la primera es la principal
    List<Galeria> findByInmuebleIdOrderByIdAsc(Integer inmuebleId);
    //Trae las imagenes de varios inmuebles en una sola consulta (para las tarjetas)
    List<Galeria> findByInmuebleIdInOrderByIdAsc(Collection<Integer> inmuebleIds);
    //Para validar el maximo de fotos por inmueble
    long countByInmuebleId(Integer inmuebleId);
    //Imagen principal de un solo inmueble (favoritos)
    Optional<Galeria> findFirstByInmuebleIdOrderByIdAsc(Integer inmuebleId);
}
