package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GaleriaRequesDTO {
    @NotNull  
    private Integer inmuebleId;
    @NotBlank 
    private String urlImagen;
    public Integer getInmuebleId() {
        return inmuebleId;
    }
    public void setInmuebleId(Integer inmueble) {
        this.inmuebleId = inmueble;
    }
    public String getUrlImagen() {
        return urlImagen;
    }
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
    
}
