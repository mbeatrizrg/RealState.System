package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class LeadRequestDTO {
    //Puede ser nulo, aun no se ha validado la fecha de visita o si es visitado
    private LocalDate fechaVisita;
    private Boolean esVisitado;
    @NotNull(message = "El ID del usuario/cliente es obligatorio")
    private Long usuarioId;
    @NotNull(message = "Debe asignar un vendedor a este lead")
    private Integer empleadoId;
    @NotNull(message = "El estatus de venta es obligatorio")
    private Integer estatusVentaId;
    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    public Integer getEmpleadoId() {
        return empleadoId;
    }
    public void setEmpleadoId(Integer empleadoId) {
        this.empleadoId = empleadoId;
    }
    public Integer getEstatusVentaId() {
        return estatusVentaId;
    }
    public void setEstatusVentaId(Integer estatusVentaId) {
        this.estatusVentaId = estatusVentaId;
    }
    public LocalDate getFechaVisita() {
        return fechaVisita;
    }
    public void setFechaVisita(LocalDate fechaVisita) {
        this.fechaVisita = fechaVisita;
    }
    public Boolean getEsVisitado() {
        return esVisitado;
    }
    public void setEsVisitado(Boolean esVisitado) {
        this.esVisitado = esVisitado;
    }
    
}