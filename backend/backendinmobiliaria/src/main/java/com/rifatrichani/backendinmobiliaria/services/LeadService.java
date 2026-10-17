package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.request.LeadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LeadResponseDTO;

public interface LeadService {
    void registrarLead(LeadRequestDTO dto);
    List<LeadResponseDTO> obtenerTodosLosLeads();
    List<LeadResponseDTO> obtenerLeadsPorVendedor(Integer empleadoId);
    void actualizarLead(Integer leadId, LeadRequestDTO dto);
}