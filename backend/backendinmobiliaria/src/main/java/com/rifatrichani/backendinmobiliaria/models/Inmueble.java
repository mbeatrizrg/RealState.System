package com.rifatrichani.backendinmobiliaria.models;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "inmueble")
public class Inmueble {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (length = 500)
    private String titulo;
    @Column 
    private BigDecimal precio;
    @Column 
    private String rrbr;
    @ManyToMany (fetch = FetchType.LAZY)
    @JoinTable (name =  "extra_inmueble",
        joinColumns = @JoinColumn (name = "id_inmueble"),
        inverseJoinColumns = @JoinColumn (name = "id_extras")
    )
    private Set<Extra> extras;
    @ManyToOne  (fetch=FetchType.LAZY)
    @JoinColumn (name = "id_tipo_inmueble", nullable = false)
    private TipoInmueble tipoInmueble;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_estatus_inmueble", nullable = false)
    private EstatusInmueble estatusInmueble;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_vendedor", nullable=false)
    private Empleado empleado;
    @Column (columnDefinition = "TEXT")
    private String descripcion;
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
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public String getRrbr() {
        return rrbr;
    }
    public void setRrbr(String rrbr) {
        this.rrbr = rrbr;
    }
    public Set<Extra> getExtras() {
        return extras;
    }
    public void setExtras(Set<Extra> extras) {
        this.extras = extras;
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
    
}
