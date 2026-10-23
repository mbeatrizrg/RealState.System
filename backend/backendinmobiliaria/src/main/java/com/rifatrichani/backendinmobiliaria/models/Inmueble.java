package com.rifatrichani.backendinmobiliaria.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

//Los nombres entre comillas respetan las mayusculas de la tabla en Supabase.
//El estado comercial esta en "estado" (006); isVendido se conserva porque el portal y las ventas lo usan
@Entity
@Table (name = "\"Inmueble\"")
public class Inmueble {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (nullable = false)
    private String titulo;
    @Column (nullable = false)
    private Integer precio;
    @Column (name = "\"RRBR\"", nullable = false, unique = true)
    private String rrbr;
    @Column (name = "ubicacion", nullable = false)
    private String ubicacion;
    //En Supabase cada inmueble tiene un solo extra (columna id_extras)
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_extras", nullable = false)
    private Extra extra;
    @ManyToOne  (fetch=FetchType.LAZY)
    @JoinColumn (name = "id_tipo", nullable = false)
    private TipoInmueble tipoInmueble;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_vendedor", nullable=false)
    private Empleado empleado;
    @Column (columnDefinition = "TEXT", nullable = false)
    private String descripcion;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_ciudad", nullable = false)
    private Ciudad ciudad;
    @Column (name = "\"isVendido\"", nullable = false)
    private Boolean vendido;
    @Column (name = "\"isAlquiler\"", nullable = false)
    private Boolean alquiler;
    //Modulo de Inmuebles (db/migraciones/006): disponible, reservado, vendido o alquilado. isVendido se mantiene sincronizado
    @Column (length = 20, nullable = false)
    private String estado = ESTADO_DISPONIBLE;
    @CreationTimestamp
    @Column (name = "fecha_captacion", nullable = false, updatable = false)
    private LocalDateTime fechaCaptacion;
    //Dueño del inmueble (no siempre tiene cuenta en el portal)
    @Column (name = "dueno_nombre", length = 150)
    private String duenoNombre;
    @Column (name = "dueno_telefono", length = 30)
    private String duenoTelefono;
    @Column (name = "dueno_correo")
    private String duenoCorreo;
    @Column
    private Integer habitaciones;
    @Column
    private Integer banos;
    @Column (name = "metros_cuadrados", precision = 10, scale = 2)
    private BigDecimal metrosCuadrados;
    //db/migraciones/008: puestos de estacionamiento y piso (apartamentos, oficinas, locales en edificio)
    @Column
    private Integer estacionamientos;
    @Column
    private Integer piso;

    public static final String ESTADO_DISPONIBLE = "disponible";
    public static final List<String> ESTADOS = List.of(ESTADO_DISPONIBLE, "reservado", "vendido", "alquilado");

    //Cambia el estado y deja isVendido igual que antes (el portal oculta los vendidos y los alquilados)
    public void cambiarEstado(String nuevo) {
        this.estado = nuevo;
        this.vendido = "vendido".equals(nuevo) || "alquilado".equals(nuevo);
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public Integer getPrecio() {
        return precio;
    }
    public void setPrecio(Integer precio) {
        this.precio = precio;
    }
    public String getRrbr() {
        return rrbr;
    }
    public void setRrbr(String rrbr) {
        this.rrbr = rrbr;
    }
    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    public Extra getExtra() {
        return extra;
    }
    public void setExtra(Extra extra) {
        this.extra = extra;
    }
    public TipoInmueble getTipoInmueble() {
        return tipoInmueble;
    }
    public void setTipoInmueble(TipoInmueble tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }
    public Empleado getEmpleado() {
        return empleado;
    }
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Ciudad getCiudad() {
        return ciudad;
    }
    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }
    public Boolean getVendido() {
        return vendido;
    }
    public void setVendido(Boolean vendido) {
        this.vendido = vendido;
    }
    public Boolean getAlquiler() {
        return alquiler;
    }
    public void setAlquiler(Boolean alquiler) {
        this.alquiler = alquiler;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public LocalDateTime getFechaCaptacion() {
        return fechaCaptacion;
    }
    public void setFechaCaptacion(LocalDateTime fechaCaptacion) {
        this.fechaCaptacion = fechaCaptacion;
    }
    public String getDuenoNombre() {
        return duenoNombre;
    }
    public void setDuenoNombre(String duenoNombre) {
        this.duenoNombre = duenoNombre;
    }
    public String getDuenoTelefono() {
        return duenoTelefono;
    }
    public void setDuenoTelefono(String duenoTelefono) {
        this.duenoTelefono = duenoTelefono;
    }
    public String getDuenoCorreo() {
        return duenoCorreo;
    }
    public void setDuenoCorreo(String duenoCorreo) {
        this.duenoCorreo = duenoCorreo;
    }
    public Integer getHabitaciones() {
        return habitaciones;
    }
    public void setHabitaciones(Integer habitaciones) {
        this.habitaciones = habitaciones;
    }
    public Integer getBanos() {
        return banos;
    }
    public void setBanos(Integer banos) {
        this.banos = banos;
    }
    public BigDecimal getMetrosCuadrados() {
        return metrosCuadrados;
    }
    public void setMetrosCuadrados(BigDecimal metrosCuadrados) {
        this.metrosCuadrados = metrosCuadrados;
    }
    public Integer getEstacionamientos() {
        return estacionamientos;
    }
    public void setEstacionamientos(Integer estacionamientos) {
        this.estacionamientos = estacionamientos;
    }
    public Integer getPiso() {
        return piso;
    }
    public void setPiso(Integer piso) {
        this.piso = piso;
    }
}
