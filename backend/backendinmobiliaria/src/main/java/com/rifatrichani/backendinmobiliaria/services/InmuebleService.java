package com.rifatrichani.backendinmobiliaria.services;
import java.math.BigDecimal;
import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleEstatusRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleDetalleResponseDTO;


public interface InmuebleService {
    Integer registrarInmueble(InmuebleRequestDTO dto, Integer idEmpleadoAutenticado);
    List<InmuebleListResponseDTO> obtenerTodosCRM();
    List<InmuebleListResponseDTO> buscarPorFiltros(Integer ciudadId, Integer tipoInmuebleId, Integer estatusInmuebleId, BigDecimal minPrecio, BigDecimal maxPrecio);
    InmuebleDetalleResponseDTO obtenerDetalleInmueble(Integer id);
    void actualizarInmueble(Integer id, InmuebleRequestDTO dto, Integer idEmpleadoAutenticado);
    void cambiarEstatus(Integer id, InmuebleEstatusRequestDTO dto, Integer idEmpleadoAutenticado);
    void eliminarInmueble(Integer id, Integer idEmpleadoAutenticado);
}