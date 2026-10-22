package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroContactoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoContactosDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.OpcionFiltroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

public interface ContactoService {
    //Maximo de filas de una exportacion (Excel o PDF)
    int MAXIMO_EXPORTACION = 5000;

    enum FormatoExportacion { XLSX, PDF }

    record ArchivoExportado(String nombre, String tipoContenido, byte[] contenido) {}

    PaginaResponseDTO<ContactoResponseDTO> buscar(FiltroContactoDTO filtro, CuentaAutenticada empleado);

    ConteoContactosDTO contar(FiltroContactoDTO filtro, CuentaAutenticada empleado);

    //soloPagina: exporta solo la pagina del filtro; si no, todos los resultados (hasta MAXIMO_EXPORTACION)
    ArchivoExportado exportar(FiltroContactoDTO filtro, List<ColumnaContacto> columnas, FormatoExportacion formato,
                              boolean soloPagina, CuentaAutenticada empleado);

    ContactoDetalleResponseDTO obtenerDetalle(Integer usuarioId, CuentaAutenticada empleado);

    void cambiarPropietario(Integer usuarioId, Integer empleadoId, CuentaAutenticada empleado);

    List<OpcionFiltroDTO> obtenerPropietarios();

    List<OpcionFiltroDTO> obtenerEstatusLead();
}
