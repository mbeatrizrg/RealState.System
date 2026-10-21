package com.rifatrichani.backendinmobiliaria.services;

import java.math.BigDecimal;
import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.VentaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VentaResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

public interface VentaService {
    Integer registrarVenta(VentaRequestDTO dto, CuentaAutenticada empleado);
    void asignarComisionAdmin(Integer ventaId, BigDecimal comision);
    //Admin: todas las ventas. Vendedor: solo las suyas
    List<VentaResponseDTO> obtenerVentas(CuentaAutenticada empleado);
}
