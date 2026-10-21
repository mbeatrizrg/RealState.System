package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rifatrichani.backendinmobiliaria.models.Galeria;

public interface GaleriaRepository extends JpaRepository<Galeria,Integer>{
    List<Galeria> findByInmuebleIdOrderByIdAsc(Integer inmuebleId);
    //Trae las imagenes de varios inmuebles en una sola consulta (para las tarjetas)
    List<Galeria> findByInmuebleIdInOrderByIdAsc(Collection<Integer> inmuebleIds);
}
