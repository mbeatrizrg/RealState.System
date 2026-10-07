package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
import java.util.Set;

public class InmuebleResponseDTO {
    private Integer id;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private Set<String> extras;
    private Integer tipoInmuebleId; 

    private Set<String> urlFoto;
    private String nombreVendedor;
    private String correoEmpleado;
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
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public Set<String> getExtras() {
        return extras;
    }
    public void setExtras(Set<String> extras) {
        this.extras = extras;
    }
    public Integer getTipoInmuebleId() {
        return tipoInmuebleId;
    }
    public void setTipoInmuebleId(Integer tipoInmuebleId) {
        this.tipoInmuebleId = tipoInmuebleId;
    }
    public Set<String> getUrlFoto() {
        return urlFoto;
    }
    public void setUrlFoto(Set<String> urlFoto) {
        this.urlFoto = urlFoto;
    }
    public String getNombreVendedor() {
        return nombreVendedor;
    }
    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }
    public String getCorreoEmpleado() {
        return correoEmpleado;
    }
    public void setCorreoEmpleado(String correoEmpleado) {
        this.correoEmpleado = correoEmpleado;
    }
    
}
