package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeadResponseDTO {
    private Integer id;
    private Long usuarioId;
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private Integer empleadoId;
    private String nombreVendedor;
    private String estatusVenta;
    private LocalDate fechaVisita;
    private Boolean esVisitado;
    private LocalDateTime fechaCreacion;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getCorreoCliente() { return correoCliente; }
    public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }
    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }
    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }
    public String getNombreVendedor() { return nombreVendedor; }
    public void setNombreVendedor(String nombreVendedor) { this.nombreVendedor = nombreVendedor; }
    public String getEstatusVenta() { return estatusVenta; }
    public void setEstatusVenta(String estatusVenta) { this.estatusVenta = estatusVenta; }
    public LocalDate getFechaVisita() { return fechaVisita; }
    public void setFechaVisita(LocalDate fechaVisita) { this.fechaVisita = fechaVisita; }
    public Boolean getEsVisitado() { return esVisitado; }
    public void setEsVisitado(Boolean esVisitado) { this.esVisitado = esVisitado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}