package com.rifatrichani.backendinmobiliaria.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
@Entity 
@Table (name = "comunicaciones")
public class Comunicacion {
    @Id 
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_canal",nullable = false)
    private Canal canal;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_lead",nullable = false)
    private Lead lead;
    @CreationTimestamp 
    @Column (name = "fecha_creacion",updatable = false)
    private LocalDateTime fechaCreacion;
    @Column (columnDefinition = "TEXT")
    private String descripcion;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Canal getCanal() {
        return canal;
    }
    public void setCanal(Canal canal) {
        this.canal = canal;
    }
    public Lead getLead() {
        return lead;
    }
    public void setLead(Lead lead) {
        this.lead = lead;
    }
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
}
