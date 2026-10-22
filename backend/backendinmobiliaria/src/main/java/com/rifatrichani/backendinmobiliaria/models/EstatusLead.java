package com.rifatrichani.backendinmobiliaria.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Etapas del embudo de venta (lead.id_estatus). Catalogo creado en db/migraciones/005_contactos.sql; el id no es autogenerado
@Entity
@Table (name = "estatus_lead")
public class EstatusLead {
    @Id
    private Integer id;
    @Column (length = 50, nullable = false, unique = true)
    private String nombre;
    //Posicion de la etapa dentro del embudo (para ordenar las listas)
    @Column (nullable = false)
    private Integer orden;
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
    public Integer getOrden() {
        return orden;
    }
    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}
