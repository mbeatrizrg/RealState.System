package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//Fila de la tabla del modulo Inmuebles del CRM
public record PropiedadResponseDTO(
        Integer id,
        String codigo,
        String titulo,
        Integer tipoInmuebleId,
        String tipoInmueble,
        String tipoOperacion,
        Integer ciudadId,
        String ciudad,
        String ubicacion,
        Integer precio,
        String estado,
        String duenoNombre,
        String duenoTelefono,
        String duenoCorreo,
        Integer asesorId,
        String nombreAsesor,
        LocalDateTime fechaCaptacion,
        Integer habitaciones,
        Integer banos,
        BigDecimal metrosCuadrados,
        Integer estacionamientos,
        Integer piso,
        long negociosAbiertos,
        String urlImagenPrincipal) {
}
