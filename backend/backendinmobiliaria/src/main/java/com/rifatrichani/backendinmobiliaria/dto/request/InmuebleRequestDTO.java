package com.rifatrichani.backendinmobiliaria.dto.request;

import java.math.BigDecimal;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InmuebleRequestDTO {
    @NotBlank (message = "Nombre del inmueble obligatorio")
    private String titulo;
    @NotNull  (message = "Precio del inmueble obligatorio")
    private BigDecimal precio;
    private Set<Integer> extrasId;
    @NotNull (message = "Seleccione el tipo de inmueble")
    private Integer tipoInmuebleId;
    @NotNull (message = "Defina el estatus del inmueble")
    private Integer estatusInmuebleId;
    @NotNull (message = "Seleccione el agente inmobiliario encargado del inmueble")
    private Integer empleadoId;
    @NotBlank (message = "Proporcione una descripción del inmueble")
    private String descripcion;
    //Getters y setters DTO
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
    public Set<Integer> getExtrasId() {
        return extrasId;
    }
    public void setExtrasId(Set<Integer> extras) {
        this.extrasId = extras;
    }
    public Integer getTipoInmuebleId() {
        return tipoInmuebleId;
    }
    public void setTipoInmuebleId(Integer tipoInmueble) {
        this.tipoInmuebleId = tipoInmueble;
    }
    public Integer getEstatusInmuebleId() {
        return estatusInmuebleId;
    }
    public void setEstatusInmuebleId(Integer estatusInmueble) {
        this.estatusInmuebleId = estatusInmueble;
    }
    public Integer getEmpleadoId() {
        return empleadoId;
    }
    public void setEmpleadoId(Integer empleado) {
        this.empleadoId = empleado;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
}
