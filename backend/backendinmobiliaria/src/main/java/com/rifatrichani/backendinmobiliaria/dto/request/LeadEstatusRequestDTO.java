package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//Avance del lead en el embudo de venta
public class LeadEstatusRequestDTO {
    @NotNull(message = "El estatus del lead es obligatorio")
    @Positive(message = "El estatus del lead debe ser un número positivo")
    private Integer estatus;

    public Integer getEstatus() { return estatus; }
    public void setEstatus(Integer estatus) { this.estatus = estatus; }
}
