package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.FichaPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FiltroPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ArchivoResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;

/* Modulo Inmuebles / Propiedades del CRM: catalogo con estado comercial, dueño, asesor captador y ficha completa.
   El alta y la edicion de los datos basicos siguen en InmuebleService ("Añadir inmueble") */
public interface PropiedadService {
    //Maximo de documentos legales por inmueble
    int MAXIMO_DOCUMENTOS = 20;

    PaginaResponseDTO<PropiedadResponseDTO> buscar(FiltroPropiedadDTO filtro, CuentaAutenticada empleado);

    //todos, mios (captados por el asesor) y disponibles
    ConteoPestanasDTO contar(FiltroPropiedadDTO filtro, CuentaAutenticada empleado);

    ArchivoExportado exportar(FiltroPropiedadDTO filtro, List<ColumnaPropiedad> columnas, FormatoExportacion formato,
                              boolean soloPagina, CuentaAutenticada empleado);

    PropiedadDetalleResponseDTO obtenerDetalle(Integer id, CuentaAutenticada empleado);

    void actualizarFicha(Integer id, FichaPropiedadDTO dto, CuentaAutenticada empleado);

    List<ArchivoResponseDTO> subirDocumentos(Integer id, List<MultipartFile> archivos, CuentaAutenticada empleado);

    void eliminarDocumento(Integer documentoId, CuentaAutenticada empleado);
}
