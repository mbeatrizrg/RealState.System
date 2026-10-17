package com.rifatrichani.backendinmobiliaria.services;
import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.request.ComunicacionRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ComunicacionResponseDTO;
public interface ComunicacionService {
    void registrarComunicacion(ComunicacionRequestDTO dto);
    List<ComunicacionResponseDTO> obtenerPorLead(Integer leadId);
    List<ComunicacionResponseDTO> obtenerPorEmpleado(Integer empleadoId);
}