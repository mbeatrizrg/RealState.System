package com.rifatrichani.backendinmobiliaria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rifatrichani.backendinmobiliaria.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{}
