package com.rifatrichani.backendinmobiliaria.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/* Historial del contacto en el CRM (db/migraciones/006): notas, correos, llamadas, tareas y reuniones.
   ETAPA la registra el backend cuando un negocio cambia de etapa. Si tiene negocio tambien aparece en su historial */
@Entity
@Table (name = "actividad")
public class Actividad {
    public enum Tipo { NOTA, CORREO, LLAMADA, TAREA, REUNION, ETAPA }
    //Modulo de Tareas (db/migraciones/007). "vencida" no se guarda: se calcula con la fecha programada
    public static final java.util.List<String> TIPOS_TAREA = java.util.List.of("LLAMADA", "VISITA", "ENVIO", "REUNION", "TRAMITE");
    public static final String PENDIENTE = "pendiente";
    public static final String EN_PROCESO = "en_proceso";
    public static final String COMPLETADA = "completada";

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated (EnumType.STRING)
    @Column (length = 20, nullable = false)
    private Tipo tipo;
    @Column (length = 200)
    private String asunto;
    @Column (columnDefinition = "TEXT")
    private String descripcion;
    //Opcional solo en las TAREA (db/migraciones/007): una tarea puede ser sobre un inmueble sin cliente
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_usuario")
    private Usuario contacto;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_negocio")
    private Negocio negocio;
    //Autor
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_empleado")
    private Empleado empleado;
    @CreationTimestamp
    @Column (name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    //Vencimiento de la tarea o fecha de la reunion / llamada
    @Column (name = "fecha_programada")
    private LocalDateTime fechaProgramada;
    @Column (nullable = false)
    private Boolean completada = false;
    @Column (length = 50)
    private String resultado;
    @Column (name = "duracion_minutos")
    private Integer duracionMinutos;
    // ----- Solo TAREA (db/migraciones/007) -----
    @Column (name = "tipo_tarea", length = 20)
    private String tipoTarea;
    @Column (length = 10)
    private String prioridad;
    @Column (name = "estado_tarea", length = 20)
    private String estadoTarea;
    //Inmueble vinculado a la tarea (ej. visita guiada). Si la tarea tiene negocio, el inmueble es el del negocio
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_inmueble")
    private Inmueble inmueble;

    //Cambia el estado de la tarea y deja "completada" igual (lo usan el historial y las estadisticas)
    public void cambiarEstadoTarea(String estado) {
        this.estadoTarea = estado;
        this.completada = COMPLETADA.equals(estado);
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Tipo getTipo() {
        return tipo;
    }
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
    public String getAsunto() {
        return asunto;
    }
    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Usuario getContacto() {
        return contacto;
    }
    public void setContacto(Usuario contacto) {
        this.contacto = contacto;
    }
    public Negocio getNegocio() {
        return negocio;
    }
    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }
    public Empleado getEmpleado() {
        return empleado;
    }
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }
    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }
    public Boolean getCompletada() {
        return completada;
    }
    public void setCompletada(Boolean completada) {
        this.completada = completada;
    }
    public String getResultado() {
        return resultado;
    }
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }
    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
    public String getTipoTarea() {
        return tipoTarea;
    }
    public void setTipoTarea(String tipoTarea) {
        this.tipoTarea = tipoTarea;
    }
    public String getPrioridad() {
        return prioridad;
    }
    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
    public String getEstadoTarea() {
        return estadoTarea;
    }
    public void setEstadoTarea(String estadoTarea) {
        this.estadoTarea = estadoTarea;
    }
    public Inmueble getInmueble() {
        return inmueble;
    }
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }
}
