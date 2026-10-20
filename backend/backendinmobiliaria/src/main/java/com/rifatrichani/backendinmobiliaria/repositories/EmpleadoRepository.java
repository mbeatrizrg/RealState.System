package com.rifatrichani.backendinmobiliaria.repositories;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rifatrichani.backendinmobiliaria.models.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado,Integer>{
    boolean existsByCorreo(String correo);
    boolean existsByCedula(BigInteger cedula);
}
