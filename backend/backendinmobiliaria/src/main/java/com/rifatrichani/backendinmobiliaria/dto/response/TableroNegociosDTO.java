package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
import java.util.List;

/* Vista de tablero (pipeline): una columna por etapa con sus tarjetas y el monto total acumulado.
   cantidad y total cuentan todos los negocios de la etapa aunque solo se envien los primeros maximoPorColumna */
public record TableroNegociosDTO(List<Columna> columnas, int maximoPorColumna) {

    public record Columna(Integer etapaId, String etapa, boolean abierta, long cantidad, BigDecimal total,
                          List<NegocioResponseDTO> negocios) {}
}
