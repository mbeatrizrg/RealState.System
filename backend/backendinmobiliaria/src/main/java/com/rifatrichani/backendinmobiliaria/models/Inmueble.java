package com.rifatrichani.backendinmobiliaria.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//Los nombres entre comillas respetan mayusculas y tildes de la tabla en Supabase
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
    @Column (name = "\"ubicación\"", nullable = false)
    private String ubicacion;
    //En Supabase cada inmueble tiene un solo extra (columna id_extras)
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_extras", nullable = false)
    private Extra extra;
    @ManyToOne  (fetch=FetchType.LAZY)
    @JoinColumn (name = "id_tipo", nullable = false)
    private TipoInmueble tipoInmueble;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_estatus_inmueble", nullable = false)
    private EstatusInmueble estatusInmueble;
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
    public EstatusInmueble getEstatusInmueble() {
        return estatusInmueble;
    }
    public void setEstatusInmueble(EstatusInmueble estatusInmueble) {
        this.estatusInmueble = estatusInmueble;
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
}
