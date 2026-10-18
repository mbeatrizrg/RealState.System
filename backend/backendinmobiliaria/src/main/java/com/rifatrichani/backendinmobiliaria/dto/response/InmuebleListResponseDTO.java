package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;

public class InmuebleListResponseDTO {
    private Integer id;
    private String titulo;
    private BigDecimal precio;
    private String urlImagenPrincipal;
    private String ciudad;
    private String ubicacion;
    private Boolean isAlquiler;
    private Integer tipoInmuebleId;
    private String tipoInmueble;
    private String estatusInmueble;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getUrlImagenPrincipal() { return urlImagenPrincipal; }
    public void setUrlImagenPrincipal(String urlImagenPrincipal) { this.urlImagenPrincipal = urlImagenPrincipal; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Boolean getIsAlquiler() { return isAlquiler; }
    public void setIsAlquiler(Boolean isAlquiler) { this.isAlquiler = isAlquiler; }
    public Integer getTipoInmuebleId() { return tipoInmuebleId; }
    public void setTipoInmuebleId(Integer tipoInmuebleId) { this.tipoInmuebleId = tipoInmuebleId; }
    public String getTipoInmueble() { return tipoInmueble; }
    public void setTipoInmueble(String tipoInmueble) { this.tipoInmueble = tipoInmueble; }
    public String getEstatusInmueble() { return estatusInmueble; }
    public void setEstatusInmueble(String estatusInmueble) { this.estatusInmueble = estatusInmueble; }
}