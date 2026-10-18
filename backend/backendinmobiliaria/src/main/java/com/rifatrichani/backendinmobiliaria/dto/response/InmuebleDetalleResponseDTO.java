package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class InmuebleDetalleResponseDTO {
    private Integer id;
    private String titulo;
    private BigDecimal precio;
    private String descripcion;
    private String rrbr;
    private String ubicacion;
    private Boolean isAlquiler;
    private String nombreCiudad;
    private String nombreTipoInmueble;
    private String nombreEstatus;
    private String nombreEmpleado;
    private List<String> extras;
    private List<String> urlsGaleria;
    private Boolean isVendido;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getRrbr() { return rrbr; }
    public void setRrbr(String rrbr) { this.rrbr = rrbr; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Boolean getIsAlquiler() { return isAlquiler; }
    public void setIsAlquiler(Boolean isAlquiler) { this.isAlquiler = isAlquiler; }
    public String getNombreCiudad() { return nombreCiudad; }
    public void setNombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }
    public String getNombreTipoInmueble() { return nombreTipoInmueble; }
    public void setNombreTipoInmueble(String nombreTipoInmueble) { this.nombreTipoInmueble = nombreTipoInmueble; }
    public String getNombreEstatus() { return nombreEstatus; }
    public void setNombreEstatus(String nombreEstatus) { this.nombreEstatus = nombreEstatus; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }
    public List<String> getExtras() { return extras; }
    public void setExtras(List<String> extras) { this.extras = extras; }
    public List<String> getUrlsGaleria() { return urlsGaleria; }
    public void setUrlsGaleria(List<String> urlsGaleria) { this.urlsGaleria = urlsGaleria; }
    public Boolean getIsVendido() { return isVendido; }
    public void setIsVendido(Boolean isVendido) { this.isVendido = isVendido; }
}