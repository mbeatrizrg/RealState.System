package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.LeadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LeadResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

public interface LeadService {
    Integer registrarLead(LeadRequestDTO dto, CuentaAutenticada empleado);
    //Portal: devuelve true si se creo un lead nuevo, false si el usuario ya tenia uno para ese inmueble
    boolean registrarInteres(Integer inmuebleId, Integer idUsuario);
    //Admin: todos los leads. Vendedor: solo los de sus inmuebles
    List<LeadResponseDTO> obtenerLeads(CuentaAutenticada empleado);
    List<LeadResponseDTO> obtenerLeadsPorVendedor(Integer empleadoId, CuentaAutenticada empleado);
    LeadResponseDTO obtenerLead(Integer leadId, CuentaAutenticada empleado);
    void actualizarEstatus(Integer leadId, Integer estatus, CuentaAutenticada empleado);
}
