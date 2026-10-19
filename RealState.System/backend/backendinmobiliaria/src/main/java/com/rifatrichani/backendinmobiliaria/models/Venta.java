package com.rifatrichani.backendinmobiliaria.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
@Entity 
@Table (name = "venta")
public class Venta {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_inmueble")
    private Inmueble inmueble;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_usuario")
    private Usuario usuario;
    @Column(precision = 12,scale = 2)
    private BigDecimal comision;
    @Column (nullable = false, precision = 12,scale = 2) //12 digitos 2 decimales, no use double ni float para evitar errores en montos :)
    private BigDecimal montoFinal;
    @CreationTimestamp 
    @Column(name = "fecha_venta",updatable = false) 
    private LocalDateTime fechaVenta;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Inmueble getInmueble() {
        return inmueble;
    }
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public BigDecimal getComision() {
        return comision;
    }
    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }
    public BigDecimal getMontoFinal() {
        return montoFinal;
    }
    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }
    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }    

}
