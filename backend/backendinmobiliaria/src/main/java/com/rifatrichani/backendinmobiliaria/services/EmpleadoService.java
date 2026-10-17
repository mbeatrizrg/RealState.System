package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.request.EmpleadoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EmpleadoAdminResponseDTO;

public interface EmpleadoService {
    void registrarEmpleado(EmpleadoRequestDTO dto);
    List<EmpleadoAdminResponseDTO> obtenerTodosConMetricasAdmin();
    List<EmpleadoAdminResponseDTO> obtenerEmpleadosActivosAdmin();
}