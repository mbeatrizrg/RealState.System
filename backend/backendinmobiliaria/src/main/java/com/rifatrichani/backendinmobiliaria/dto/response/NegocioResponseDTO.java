package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//Fila de la tabla de negocios y tarjeta del tablero (pipeline)
public record NegocioResponseDTO(
        Integer id,
        String nombre,
        Integer inmuebleId,
        String codigoInmueble,
        String tituloInmueble,
        String tipoInmueble,
        Integer contactoId,
        String nombreContacto,
        String tipoOperacion,
        BigDecimal valor,
        Integer etapaId,
        String etapa,
        boolean etapaAbierta,
        LocalDate fechaCierreEstimada,
        Integer propietarioId,
        String nombrePropietario,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        LocalDateTime fechaCierre) {
}
