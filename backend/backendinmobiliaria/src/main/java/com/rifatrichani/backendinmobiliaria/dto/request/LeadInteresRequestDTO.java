package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;

//Boton "Contáctanos" del portal: el usuario con sesion se interesa en un inmueble. El usuario se toma del token
public class LeadInteresRequestDTO {
    @NotNull(message = "Debe indicar el inmueble de interés")
    private Integer inmuebleId;

    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
}
