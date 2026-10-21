package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.ComunicacionRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ComunicacionResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

public interface ComunicacionService {
    void registrarComunicacion(ComunicacionRequestDTO dto, CuentaAutenticada empleado);
    List<ComunicacionResponseDTO> obtenerPorLead(Integer leadId, CuentaAutenticada empleado);
    List<ComunicacionResponseDTO> obtenerPorEmpleado(Integer empleadoId, CuentaAutenticada empleado);
}
