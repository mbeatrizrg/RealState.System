package com.rifatrichani.backendinmobiliaria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rifatrichani.backendinmobiliaria.models.Lead;

public interface LeadRepository extends JpaRepository<Lead,Integer> {

}
