package com.rifatrichani.backendinmobiliaria.dto.response;

import java.util.List;

/* Pagina del negocio: sus datos, el inmueble vinculado (con fotos), el cliente comprador/inquilino y el historial.
   puedeEditar: administrador, propietario del negocio o negocio sin asignar */
public record NegocioDetalleResponseDTO(
        NegocioResponseDTO negocio,
        PropiedadResponseDTO inmueble,
        List<ImagenResponseDTO> fotos,
        ContactoResponseDTO contacto,
        List<ActividadResponseDTO> historial,
        boolean puedeEditar) {
}
