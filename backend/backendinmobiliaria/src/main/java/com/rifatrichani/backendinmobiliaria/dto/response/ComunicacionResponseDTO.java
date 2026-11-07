package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;

public class ComunicacionResponseDTO {
    private Integer id;
    private Integer leadId;
    private String nombreCliente;
    private String nombreVendedor;
    private String canal;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private String nombreInmueble;
    private String reporteVisita;
    private Integer contactoId;
    private Integer inmuebleId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getLeadId() { return leadId; }
    public void setLeadId(Integer leadId) { this.leadId = leadId; }
    
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    
    public String getNombreVendedor() { return nombreVendedor; }
    public void setNombreVendedor(String nombreVendedor) { this.nombreVendedor = nombreVendedor; }
    
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public String getNombreInmueble() { return nombreInmueble; }
    public void setNombreInmueble(String nombreInmueble) { this.nombreInmueble = nombreInmueble; }
    
    public String getReporteVisita() { return reporteVisita; }
    public void setReporteVisita(String reporteVisita) { this.reporteVisita = reporteVisita; }

    public Integer getContactoId() { return contactoId; }
    public void setContactoId(Integer contactoId) { this.contactoId = contactoId; }

    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
}