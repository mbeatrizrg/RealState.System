package com.rifatrichani.backendinmobiliaria.models;


import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;

@Entity 
@Table (name = "lead")
public class Lead {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_usuario", nullable = false)
    private Usuario usuario;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_vendedor",nullable = false)
    private Empleado empleado;
    @ManyToOne (fetch = FetchType.LAZY) 
    @JoinColumn (name = "id_estatus_venta",nullable = false)
    private EstatusVenta estatusVenta;
    @Column 
    private LocalDate fechaVisita;
    @Column 
    private Boolean esVisitado;
    @CreationTimestamp 
    @Column 
    private LocalDateTime fechaCreacion;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Empleado getEmpleado() {
        return empleado;
    }
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
    public EstatusVenta getEstatusVenta() {
        return estatusVenta;
    }
    public void setEstatusVenta(EstatusVenta estatusVenta) {
        this.estatusVenta = estatusVenta;
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
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
}
