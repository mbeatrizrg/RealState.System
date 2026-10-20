package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;

public class FavoritoRequestDTO {
    @NotNull 
    private Long usuarioId;
    @NotNull 
    private Integer inmuebleId;
    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    public Integer getInmuebleId() {
        return inmuebleId;
    }
    public void setInmuebleId(Integer inmuebleId) {
        this.inmuebleId = inmuebleId;
    }
    
}
