package com.rifatrichani.backendinmobiliaria.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/* Oportunidad comercial de venta o alquiler: une un inmueble de la inmobiliaria con un contacto interesado y se le hace
   seguimiento por etapas hasta cerrarla (db/migraciones/006) */
@Entity
@Table (name = "negocio")
public class Negocio {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (length = 150, nullable = false)
    private String nombre;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_inmueble", nullable = false)
    private Inmueble inmueble;
    //Contacto comprador o inquilino
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_usuario", nullable = false)
    private Usuario contacto;
    //venta o alquiler
    @Column (name = "tipo_operacion", length = 10, nullable = false)
    private String tipoOperacion;
    @Column (precision = 14, scale = 2, nullable = false)
    private BigDecimal valor;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_etapa", nullable = false)
    private EtapaNegocio etapa;
    @Column (name = "fecha_cierre_estimada")
    private LocalDate fechaCierreEstimada;
    //Vendedor dueño del negocio. Null = sin asignar
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_propietario")
    private Empleado propietario;
    @CreationTimestamp
    @Column (name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    @UpdateTimestamp
    @Column (name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
    //Cuando paso a una etapa de cierre (ganado o perdido); null mientras esta abierto
    @Column (name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Inmueble getInmueble() {
        return inmueble;
    }
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }
    public Usuario getContacto() {
        return contacto;
    }
    public void setContacto(Usuario contacto) {
        this.contacto = contacto;
    }
    public String getTipoOperacion() {
        return tipoOperacion;
    }
    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    public EtapaNegocio getEtapa() {
        return etapa;
    }
    public void setEtapa(EtapaNegocio etapa) {
        this.etapa = etapa;
    }
    public LocalDate getFechaCierreEstimada() {
        return fechaCierreEstimada;
    }
    public void setFechaCierreEstimada(LocalDate fechaCierreEstimada) {
        this.fechaCierreEstimada = fechaCierreEstimada;
    }
    public Empleado getPropietario() {
        return propietario;
    }
    public void setPropietario(Empleado propietario) {
        this.propietario = propietario;
    }
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }
    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
}
