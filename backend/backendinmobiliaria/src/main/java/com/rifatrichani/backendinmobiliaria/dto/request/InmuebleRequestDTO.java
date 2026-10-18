package com.rifatrichani.backendinmobiliaria.dto.request;

import java.math.BigDecimal;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InmuebleRequestDTO {
    @NotBlank(message = "Nombre del inmueble obligatorio")
    private String titulo;
    @NotNull(message = "Precio del inmueble obligatorio")
    private BigDecimal precio;
    @NotBlank(message = "Proporcione una descripción del inmueble")
    private String descripcion;
    @Size(max = 255, message = "El código RRBR no puede exceder los 255 caracteres")
    private String rrbr;
    @NotBlank(message = "La ubicación del inmueble es obligatoria")
    private String ubicacion;
    @NotNull(message = "Debe especificar si el inmueble es para alquiler (true) o venta (false)")
    private Boolean isAlquiler;
    @NotNull(message = "Seleccione la ciudad del inmueble")
    private Integer ciudadId;
    @NotNull(message = "Seleccione el agente inmobiliario encargado del inmueble")
    private Integer empleadoId;
    @NotNull(message = "Seleccione el tipo de inmueble")
    private Integer tipoInmuebleId;
    @NotNull(message = "Defina la condición/estatus del inmueble")
    private Integer estatusInmuebleId;
    private Set<Integer> extrasId;

    // Getters y Setters
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
    public Integer getCiudadId() { return ciudadId; }
    public void setCiudadId(Integer ciudadId) { this.ciudadId = ciudadId; }
    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }
    public Integer getTipoInmuebleId() { return tipoInmuebleId; }
    public void setTipoInmuebleId(Integer tipoInmuebleId) { this.tipoInmuebleId = tipoInmuebleId; }
    public Integer getEstatusInmuebleId() { return estatusInmuebleId; }
    public void setEstatusInmuebleId(Integer estatusInmuebleId) { this.estatusInmuebleId = estatusInmuebleId; }
    public Set<Integer> getExtrasId() { return extrasId; }
    public void setExtrasId(Set<Integer> extrasId) { this.extrasId = extrasId; }
}