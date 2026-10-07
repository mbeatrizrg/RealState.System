package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
//Informacion CRUCIAL inmueble favoritos del usuario

public class FavoritoListResponseDTO {
    private Integer favoritoId;
    private Integer inmuebleId;
    private String titulo;
    private String imagenPrincipal;
    private String ciudad;
    private BigDecimal precio;
    
    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public Integer getInmuebleId() {
        return inmuebleId;
    }
    public void setInmuebleId(Integer inmuebleId) {
        this.inmuebleId = inmuebleId;
    }
    public Integer getId() {
        return favoritoId;
    }
    public void setId(Integer favoritoId) {
        this.favoritoId = favoritoId;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getImagenPrincipal() {
        return imagenPrincipal;
    }
    public void setImagenPrincipal(String imagenPrincipal) {
        this.imagenPrincipal = imagenPrincipal;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
}
