package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rifatrichani.backendinmobiliaria.models.EstatusInmueble;



public interface EstatusInmuebleRepository extends JpaRepository<EstatusInmueble,Integer>{
    Optional<EstatusInmueble> findByNombre(String nombre);

}
