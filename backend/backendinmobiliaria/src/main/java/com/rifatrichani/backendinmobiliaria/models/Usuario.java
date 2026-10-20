package com.rifatrichani.backendinmobiliaria.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario") 
public class Usuario {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 255)
    private String nombre;
    
    @Column(length = 255)
    private String apellido;
    
    @Column(length = 255)
    private String telefono;
    
    @Column(length = 255, unique = true, nullable = false)
    private String correo;
    
    @Column(length = 255, nullable = false)
    private String contrasena;

    @Column(name = "cuenta_activa")
    private boolean cuentaActiva = false;

    @Column(name = "token_confirmacion")
    private String tokenConfirmacion;

    // Constructor vacío obligatorio
    public Usuario(){}

    // --- GETTERS Y SETTERS MANUALES (A PRUEBA DE FALLOS) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public boolean isCuentaActiva() { return cuentaActiva; }
    public void setCuentaActiva(boolean cuentaActiva) { this.cuentaActiva = cuentaActiva; }

    public String getTokenConfirmacion() { return tokenConfirmacion; }
    public void setTokenConfirmacion(String tokenConfirmacion) { this.tokenConfirmacion = tokenConfirmacion; }
}