package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class GaleriaRequesDTO {
    @NotNull(message = "Debe indicar el inmueble")
    private Integer inmuebleId;
    //Solo URLs http(s): evita guardar esquemas como javascript: o data: que luego se muestran en el portal
    @NotBlank(message = "Debe indicar la URL de la imagen")
    @Size(max = 2000, message = "La URL es demasiado larga")
    @Pattern(regexp = "^https?://\\S+$", message = "La imagen debe ser una URL http o https")
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
