package com.rifatrichani.backendinmobiliaria.dto.response;

//Catalogo de etapas para filtros, selects y columnas del tablero. abierta = false en Cierre ganado / Cierre perdido
public record EtapaNegocioDTO(Integer id, String nombre, boolean abierta) {
}
