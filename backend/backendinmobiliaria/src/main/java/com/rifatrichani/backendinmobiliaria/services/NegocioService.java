package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroNegocioDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.NegocioRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EtapaNegocioDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.NegocioDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.NegocioResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.TableroNegociosDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;

//Modulo de Negocios del CRM: oportunidades de venta o alquiler de un inmueble con un contacto
public interface NegocioService {
    //Tarjetas que se envian por columna del tablero (la cantidad y el total si cuentan todas)
    int TARJETAS_POR_COLUMNA = 50;

    PaginaResponseDTO<NegocioResponseDTO> buscar(FiltroNegocioDTO filtro, CuentaAutenticada empleado);

    ConteoPestanasDTO contar(FiltroNegocioDTO filtro, CuentaAutenticada empleado);

    TableroNegociosDTO tablero(FiltroNegocioDTO filtro, CuentaAutenticada empleado);

    ArchivoExportado exportar(FiltroNegocioDTO filtro, List<ColumnaNegocio> columnas, FormatoExportacion formato,
                              boolean soloPagina, CuentaAutenticada empleado);

    List<EtapaNegocioDTO> obtenerEtapas();

    NegocioDetalleResponseDTO obtenerDetalle(Integer id, CuentaAutenticada empleado);

    Integer crear(NegocioRequestDTO dto, CuentaAutenticada empleado);

    void actualizar(Integer id, NegocioRequestDTO dto, CuentaAutenticada empleado);

    void cambiarEtapa(Integer id, Integer etapaId, CuentaAutenticada empleado);

    void eliminar(Integer id, CuentaAutenticada empleado);
}
