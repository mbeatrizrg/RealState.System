package com.rifatrichani.backendinmobiliaria.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/* Archivo privado del CRM (db/migraciones/006): adjunto de una actividad o documento legal de un inmueble.
   No se sirve en /uploads: se descarga con token en GET /api/archivos/{id} */
@Entity
@Table (name = "archivo_crm")
public class ArchivoCrm {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    //Nombre original (solo para mostrar y para la descarga)
    @Column (nullable = false)
    private String nombre;
    //Nombre en disco generado por el backend (UUID + extension)
    @Column (length = 100, nullable = false, unique = true)
    private String ruta;
    @Column (name = "tipo_contenido", length = 100, nullable = false)
    private String tipoContenido;
    @Column (nullable = false)
    private Long tamano;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_actividad")
    private Actividad actividad;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_inmueble")
    private Inmueble inmueble;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_empleado")
    private Empleado empleado;
    @CreationTimestamp
    @Column (name = "fecha_subida", nullable = false, updatable = false)
    private LocalDateTime fechaSubida;
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
    public String getRuta() {
        return ruta;
    }
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    public String getTipoContenido() {
        return tipoContenido;
    }
    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }
    public Long getTamano() {
        return tamano;
    }
    public void setTamano(Long tamano) {
        this.tamano = tamano;
    }
    public Actividad getActividad() {
        return actividad;
    }
    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }
    public Inmueble getInmueble() {
        return inmueble;
    }
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }
    public Empleado getEmpleado() {
        return empleado;
    }
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }
    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}
