package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;

public class VistoDTO {
    //Este no es NotNull, porque por ejemplo el usuario no esta registrado
    private Long usuario;
    @NotNull 
    private Integer inmueble;
    //Getters y Setters DTO
    public Long getUsuario() {
        return usuario;
    }
    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }
    public Integer getInmueble() {
        return inmueble;
    }
    public void setInmueble(Integer inmueble) {
        this.inmueble = inmueble;
    }
}
