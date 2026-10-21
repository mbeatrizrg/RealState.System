package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.EmpleadoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EmpleadoAdminResponseDTO;

public interface EmpleadoService {
    Integer registrarEmpleado(EmpleadoRequestDTO dto);
    List<EmpleadoAdminResponseDTO> obtenerTodosConMetricasAdmin();
    List<EmpleadoAdminResponseDTO> obtenerEmpleadosConSesionAbierta();
    void cambiarEstadoActivo(Integer empleadoId, boolean activo, Integer idAdminAutenticado);
    void restablecerContrasena(Integer empleadoId, String contrasenaProvisional);
}
