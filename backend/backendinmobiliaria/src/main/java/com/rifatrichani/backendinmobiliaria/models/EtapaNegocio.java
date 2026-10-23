package com.rifatrichani.backendinmobiliaria.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Etapas del pipeline de negocios. Catalogo creado en db/migraciones/006; el id no es autogenerado
@Entity
@Table (name = "etapa_negocio")
public class EtapaNegocio {
    public static final int PROSPECTO = 1;
    public static final int CIERRE_GANADO = 6;
    public static final int CIERRE_PERDIDO = 7;

    @Id
    private Integer id;
    @Column (length = 50, nullable = false, unique = true)
    private String nombre;
    @Column (nullable = false)
    private Integer orden;
    //false en las etapas de cierre (ganado o perdido)
    @Column (nullable = false)
    private Boolean abierto = true;
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
    public Boolean getAbierto() {
        return abierto;
    }
    public void setAbierto(Boolean abierto) {
        this.abierto = abierto;
    }
}
