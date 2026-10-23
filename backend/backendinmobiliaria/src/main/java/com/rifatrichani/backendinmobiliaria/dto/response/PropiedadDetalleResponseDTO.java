package com.rifatrichani.backendinmobiliaria.dto.response;

import java.util.List;

/* Ficha del inmueble en el CRM: datos, fotos, caracteristicas, documentos legales, negocios y clientes interesados.
   puedeEditar: el empleado autenticado es el asesor captador o un administrador */
public record PropiedadDetalleResponseDTO(
        PropiedadResponseDTO propiedad,
        String descripcion,
        List<String> extras,
        List<ImagenResponseDTO> imagenes,
        List<ArchivoResponseDTO> documentos,
        List<NegocioResponseDTO> negocios,
        List<Interesado> interesados,
        boolean puedeEditar) {

    //Contacto con un lead sobre el inmueble (pidio informacion desde el portal o lo vinculo un vendedor)
    public record Interesado(Integer leadId, Integer contactoId, String nombre, String correo, String telefono, Integer estatusId) {}
}
