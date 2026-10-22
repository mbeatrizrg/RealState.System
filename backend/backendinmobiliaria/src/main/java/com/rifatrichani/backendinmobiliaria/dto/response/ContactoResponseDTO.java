package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;

//Fila de la tabla de contactos del CRM. El estatus es el del lead mas reciente del contacto (null = sin leads)
public class ContactoResponseDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private Integer cedula;
    private String tipoNegocio;
    private Boolean verificado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaActividad;
    private Integer propietarioId;
    private String nombrePropietario;
    private Integer estatusId;
    private String estatus;
    private long cantidadLeads;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Integer getCedula() { return cedula; }
    public void setCedula(Integer cedula) { this.cedula = cedula; }
    public String getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(String tipoNegocio) { this.tipoNegocio = tipoNegocio; }
    public Boolean getVerificado() { return verificado; }
    public void setVerificado(Boolean verificado) { this.verificado = verificado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getUltimaActividad() { return ultimaActividad; }
    public void setUltimaActividad(LocalDateTime ultimaActividad) { this.ultimaActividad = ultimaActividad; }
    public Integer getPropietarioId() { return propietarioId; }
    public void setPropietarioId(Integer propietarioId) { this.propietarioId = propietarioId; }
    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }
    public Integer getEstatusId() { return estatusId; }
    public void setEstatusId(Integer estatusId) { this.estatusId = estatusId; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public long getCantidadLeads() { return cantidadLeads; }
    public void setCantidadLeads(long cantidadLeads) { this.cantidadLeads = cantidadLeads; }
}
