package com.rifatrichani.backendinmobiliaria.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "\"Empleado\"")
public class Empleado {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (nullable = false)
    private String nombre;
    @Column
    private String apellido;
    @Column (name = "ci", unique = true, nullable = false)
    private Integer cedula;
    @Column (nullable = false)
    private String correo;
    @Column (name = "administrador", nullable = false)
    private Boolean esAdmin;
    // Se obvia el @CreationTimeStamp ya que esto lo hace siempre que se cree la entidad del sistema, y no se actualiza
    //por lo que es mejor controlarla manualmente
    @Column (name = "fecha_entrada", nullable = false)
    private LocalDateTime fechaEntrada;
    @Column (name = "fecha_salida", nullable = false)
    private LocalDateTime salidaSistema;
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
    public Integer getCedula() {
        return cedula;
    }
    public void setCedula(Integer cedula) {
        this.cedula = cedula;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public Boolean getEsAdmin() {
        return esAdmin;
    }
    public void setEsAdmin(Boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }
    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }
    public LocalDateTime getSalidaSistema() {
        return salidaSistema;
    }
    public void setSalidaSistema(LocalDateTime salidaSistema) {
        this.salidaSistema = salidaSistema;
    }
}
