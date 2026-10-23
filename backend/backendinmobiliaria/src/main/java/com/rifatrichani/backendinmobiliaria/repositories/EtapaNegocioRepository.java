package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rifatrichani.backendinmobiliaria.models.EtapaNegocio;

public interface EtapaNegocioRepository extends JpaRepository<EtapaNegocio, Integer> {
    List<EtapaNegocio> findAllByOrderByOrdenAsc();
}
