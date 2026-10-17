package com.rifatrichani.backendinmobiliaria.services;

import java.math.BigDecimal;
import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.request.VentaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VentaResponseDTO;

public interface VentaService {
    void registrarVenta(VentaRequestDTO dto);
    void asignarComisionAdmin(Long ventaId, BigDecimal comision);
    List<VentaResponseDTO> obtenerTodasLasVentas();
}