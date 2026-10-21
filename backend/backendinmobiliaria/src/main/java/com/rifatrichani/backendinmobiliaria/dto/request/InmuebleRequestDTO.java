package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

//Datos para registrar o actualizar un inmueble desde el CRM. Los campos siguen las columnas de la tabla "Inmueble" de Supabase
public class InmuebleRequestDTO {
    @NotBlank(message = "Nombre del inmueble obligatorio")
    @Size(max = 500, message = "El título no puede exceder los 500 caracteres")
    private String titulo;
    @NotNull(message = "Precio del inmueble obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Integer precio;
    @NotBlank(message = "Proporcione una descripción del inmueble")
    private String descripcion;
    //Opcional: si no se envia se genera automaticamente
    @Size(max = 255, message = "El código RRBR no puede exceder los 255 caracteres")
    private String rrbr;
    @NotBlank(message = "La ubicación del inmueble es obligatoria")
    @Size(max = 500, message = "La ubicación no puede exceder los 500 caracteres")
    private String ubicacion;
    @NotNull(message = "Debe especificar si el inmueble es para alquiler (true) o venta (false)")
    private Boolean isAlquiler;
    @NotNull(message = "Seleccione la ciudad del inmueble")
    private Integer ciudadId;
    //Opcional y solo para administradores: asigna el inmueble a otro vendedor. Si no se envia queda el empleado autenticado
    private Integer empleadoId;
    @NotNull(message = "Seleccione el tipo de inmueble")
    private Integer tipoInmuebleId;
    @NotNull(message = "Defina la condición/estatus del inmueble")
    private Integer estatusInmuebleId;
    //En Supabase cada inmueble tiene un solo extra (columna id_extras)
    @NotNull(message = "Seleccione el extra del inmueble")
    private Integer extraId;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
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
    public Integer getExtraId() { return extraId; }
    public void setExtraId(Integer extraId) { this.extraId = extraId; }
}
