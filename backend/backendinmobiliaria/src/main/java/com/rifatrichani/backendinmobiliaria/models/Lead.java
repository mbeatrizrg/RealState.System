package com.rifatrichani.backendinmobiliaria.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//Prospecto: un usuario interesado en un inmueble
@Entity
@Table (name = "lead")
public class Lead {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_inmueble", nullable = false)
    private Inmueble inmueble;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_usuario", nullable = false)
    private Usuario usuario;
    //Etapa del embudo de venta. En Supabase es un numero sin tabla de catalogo (no existe estatus_venta)
    @Column (name = "id_estatus", nullable = false)
    private Integer estatus;
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
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Integer getEstatus() {
        return estatus;
    }
    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }
}
