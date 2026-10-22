package com.rifatrichani.backendinmobiliaria.services;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroInmuebleDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

//Las operaciones del CRM reciben la cuenta autenticada para validar que el vendedor sea el encargado del inmueble
public interface InmuebleService {
    Integer registrarInmueble(InmuebleRequestDTO dto, CuentaAutenticada empleado);
    PaginaResponseDTO<InmuebleListResponseDTO> buscarCrm(FiltroInmuebleDTO filtro);
    PaginaResponseDTO<InmuebleListResponseDTO> buscarPublico(FiltroInmuebleDTO filtro);
    InmuebleDetalleResponseDTO obtenerDetalleInmueble(Integer id);
    void actualizarInmueble(Integer id, InmuebleRequestDTO dto, CuentaAutenticada empleado);
    void eliminarInmueble(Integer id, CuentaAutenticada empleado);
}
