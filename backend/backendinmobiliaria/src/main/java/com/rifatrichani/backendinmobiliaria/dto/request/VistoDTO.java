package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;

//El usuario NO se recibe en el cuerpo: se toma del token, asi nadie puede registrar vistas a nombre de otro
public class VistoDTO {
    @NotNull
    private Integer inmueble;

    public Integer getInmueble() {
        return inmueble;
    }
    public void setInmueble(Integer inmueble) {
        this.inmueble = inmueble;
    }
}
