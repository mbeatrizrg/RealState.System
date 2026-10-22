package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rifatrichani.backendinmobiliaria.models.EstatusLead;

public interface EstatusLeadRepository extends JpaRepository<EstatusLead, Integer> {
    List<EstatusLead> findAllByOrderByOrdenAsc();
}
