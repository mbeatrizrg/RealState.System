package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;

//El usuario se toma del token, solo se envia el inmueble
public class FavoritoRequestDTO {
    @NotNull(message = "Debe indicar el inmueble")
    private Integer inmuebleId;

    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
}
