package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;

//Mover un negocio de etapa (arrastrar y soltar en el tablero). Body: {"etapaId": 3}
public record CambioEtapaDTO(@NotNull(message = "Indique la etapa") Integer etapaId) {
}
