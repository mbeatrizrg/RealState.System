package com.rifatrichani.backendinmobiliaria.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "\"Usuario\"")
public class Usuario {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (nullable = false)
    private String nombre;
    @Column (nullable = false)
    private String apellido;
    @Column (nullable = false)
    private String telefono;
    @Column (unique = true,nullable = false)
    private String correo;
    //Tipo de operacion que busca el cliente (compra, alquiler, venta)
    @Column (nullable = false)
    private String tipoNegocio;
    //Codigo de verificacion del correo
    @Column (name = "codverif")
    private Integer codVerif;
    public Usuario(){}
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
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public String getTipoNegocio() {
        return tipoNegocio;
    }
    public void setTipoNegocio(String tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }
    public Integer getCodVerif() {
        return codVerif;
    }
    public void setCodVerif(Integer codVerif) {
        this.codVerif = codVerif;
    }
}
