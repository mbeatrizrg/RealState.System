package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;

public class InmuebleEstatusRequestDTO {
    @NotNull(message = "Debe indicar el nuevo estatus del inmueble")
    private Integer estatusInmuebleId;

    public Integer getEstatusInmuebleId() { return estatusInmuebleId; }
    public void setEstatusInmuebleId(Integer estatusInmuebleId) { this.estatusInmuebleId = estatusInmuebleId; }
}
