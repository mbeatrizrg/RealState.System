package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleEstatusRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

//Las operaciones del CRM reciben la cuenta autenticada para validar que el vendedor sea el encargado del inmueble
public interface InmuebleService {
    Integer registrarInmueble(InmuebleRequestDTO dto, CuentaAutenticada empleado);
    List<InmuebleListResponseDTO> obtenerTodosCRM(Integer soloDelEmpleadoId);
    List<InmuebleListResponseDTO> buscarPorFiltros(Integer ciudadId, Integer tipoInmuebleId, Integer estatusInmuebleId, Boolean isAlquiler, Integer minPrecio, Integer maxPrecio);
    InmuebleDetalleResponseDTO obtenerDetalleInmueble(Integer id);
    void actualizarInmueble(Integer id, InmuebleRequestDTO dto, CuentaAutenticada empleado);
    void cambiarEstatus(Integer id, InmuebleEstatusRequestDTO dto, CuentaAutenticada empleado);
    void eliminarInmueble(Integer id, CuentaAutenticada empleado);
}
