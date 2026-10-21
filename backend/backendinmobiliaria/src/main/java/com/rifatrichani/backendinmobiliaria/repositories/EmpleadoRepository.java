package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rifatrichani.backendinmobiliaria.models.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByCedula(Integer cedula);
    Optional<Empleado> findByCorreoIgnoreCase(String correo);

    // Empleados con sesion abierta: su ultima entrada es posterior a su ultima salida
    @Query("SELECT e FROM Empleado e WHERE e.activo = true AND e.fechaEntrada > e.salidaSistema")
    List<Empleado> obtenerEmpleadosActivosEnSistema();

    // En Supabase el lead no tiene vendedor propio: pertenece al vendedor encargado del inmueble
    @Query("SELECT COUNT(l.id) FROM Lead l WHERE l.inmueble.empleado.id = :empleadoId")
    Long contarLeadsPorEmpleado(@Param("empleadoId") Integer empleadoId);

    @Query("SELECT COUNT(c.id) FROM Comunicacion c WHERE c.lead.inmueble.empleado.id = :empleadoId")
    Long contarComunicacionesPorEmpleado(@Param("empleadoId") Integer empleadoId);
}
