package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;

public class VistosResponseCrmDTO {
    private Long id;
    private Integer inmuebleId;
    private String titulo;
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String correo;
    private LocalDateTime fechaVista;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getInmuebleId() {
        return inmuebleId;
    }
    public void setInmuebleId(Integer inmuebleId) {
        this.inmuebleId = inmuebleId;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public LocalDateTime getFechaVista() {
        return fechaVista;
    }
    public void setFechaVista(LocalDateTime fechaVista) {
        this.fechaVista = fechaVista;
    }
    
}
