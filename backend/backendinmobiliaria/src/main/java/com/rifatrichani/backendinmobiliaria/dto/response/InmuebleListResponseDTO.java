package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;

//Este se usa para que cuando se busque por el filtrado
//Se vean la informacion CRUCIAL del inmueble
public class InmuebleListResponseDTO {
    private Integer id;
    private String titulo;
    private BigDecimal precio;
    private String urlImagenPrincipal;
    private String ciudad;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public String getUrlImagenPrincipal() {
        return urlImagenPrincipal;
    }
    public void setUrlImagenPrincipal(String urlImagenPrincipal) {
        this.urlImagenPrincipal = urlImagenPrincipal;
    }
    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    
}
