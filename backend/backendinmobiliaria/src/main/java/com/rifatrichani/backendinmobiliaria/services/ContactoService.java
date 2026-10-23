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

    //Tambien los usan los modulos de Negocios e Inmuebles
    enum FormatoExportacion {
        XLSX, PDF;

        public static FormatoExportacion desde(String formato) {
            return switch (formato.toLowerCase(java.util.Locale.ROOT)) {
                case "xlsx", "excel" -> XLSX;
                case "pdf" -> PDF;
                default -> throw new IllegalArgumentException("Formato inválido (xlsx o pdf)");
            };
        }
    }

    record ArchivoExportado(String nombre, String tipoContenido, byte[] contenido) {
        public static final String TIPO_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    PaginaResponseDTO<ContactoResponseDTO> buscar(FiltroContactoDTO filtro, CuentaAutenticada empleado);

    ConteoContactosDTO contar(FiltroContactoDTO filtro, CuentaAutenticada empleado);

    //soloPagina: exporta solo la pagina del filtro; si no, todos los resultados (hasta MAXIMO_EXPORTACION)
    ArchivoExportado exportar(FiltroContactoDTO filtro, List<ColumnaContacto> columnas, FormatoExportacion formato,
                              boolean soloPagina, CuentaAutenticada empleado);

    ContactoDetalleResponseDTO obtenerDetalle(Integer usuarioId, CuentaAutenticada empleado);

    //Fila del contacto (la usa la pagina del negocio para mostrar al cliente)
    ContactoResponseDTO obtenerResumen(Integer usuarioId);

    void cambiarPropietario(Integer usuarioId, Integer empleadoId, CuentaAutenticada empleado);

    List<OpcionFiltroDTO> obtenerPropietarios();

    List<OpcionFiltroDTO> obtenerEstatusLead();
}
