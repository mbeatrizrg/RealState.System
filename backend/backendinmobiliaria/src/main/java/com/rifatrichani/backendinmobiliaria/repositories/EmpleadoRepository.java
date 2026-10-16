package com.rifatrichani.backendinmobiliaria.repositories;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    boolean existsByCorreo(String correo);
    boolean existsByCedula(BigInteger cedula);
    Optional<Empleado> findByCorreo(String correo);

    // Empleados que están actualmente dentro del sistema
    @Query("SELECT e FROM Empleado e WHERE e.fechaEntrada IS NOT NULL AND (e.salidaSistema IS NULL OR e.fechaEntrada > e.salidaSistema)")
    List<Empleado> obtenerEmpleadosActivosEnSistema();

    // Cantidad de clientes/leads asignados a un vendedor
    @Query("SELECT COUNT(l.id) FROM Lead l WHERE l.empleado.id = :empleadoId")
    Long contarLeadsPorEmpleado(@Param("empleadoId") Integer empleadoId);

    // Cantidad de comunicaciones realizadas por un vendedor por medio de leads
    @Query("SELECT COUNT(c.id) FROM Comunicacion c WHERE c.lead.empleado.id = :empleadoId")
    Long contarComunicacionesPorEmpleado(@Param("empleadoId") Integer empleadoId);
}