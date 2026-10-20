package com.rifatrichani.backendinmobiliaria.models;

import java.math.BigInteger;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table 
public class Empleado {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (length = 255)
    private String nombre;
    @Column (length = 255)
    private String apellido;
    @Column (unique = true)
    private BigInteger cedula;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_tipo_cedula")
    private TipoCedula tipoCedula;
    @Column (length = 255)
    private String contrasena;
    @Column (length = 500)
    private String correo;
    @Column
    private Boolean esAdmin; 
    @Column (name = "ultima_entrada")
    private LocalDateTime fechaEntrada; 
    // Se obvia el @CreationTimeStamp ya que esto lo hace siempre que se cree la entidad del sistema, y no se actualiza
    //por lo que es mejor controlarla manualmente
    @Column (name = "ultima_salida")
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
    public BigInteger getCedula() {
        return cedula;
    }
    public void setCedula(BigInteger cedula) {
        this.cedula = cedula;
    }
    public TipoCedula getTipoCedula() {
        return tipoCedula;
    }
    public void setTipoCedula(TipoCedula tipoCedula) {
        this.tipoCedula = tipoCedula;
    }
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
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
