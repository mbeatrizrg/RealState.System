package com.rifatrichani.backendinmobiliaria.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table (name = "\"Venta\"")
public class Venta {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_inmueble", nullable = false)
    private Inmueble inmueble;
    @Column(nullable = false) //no use double ni float para evitar errores en montos :)
    private BigDecimal comision;
    @Column (name = "monto", nullable = false)
    private BigDecimal montoFinal;
    @CreationTimestamp
    @Column(name = "fecha_venta", nullable = false, updatable = false)
    private LocalDateTime fechaVenta;
    //Referencia (numero o URL) del comprobante de pago
    @Column (nullable = false)
    private String comprobante;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Inmueble getInmueble() {
        return inmueble;
    }
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
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
    public String getComprobante() {
        return comprobante;
    }
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }
}
