package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;

//Quien vio un inmueble (para que el vendedor contacte a los interesados)
public class VistosResponseCrmDTO {
    private Integer id;
    private Integer inmuebleId;
    private String titulo;
    private Integer usuarioId;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private LocalDateTime fechaVista;
    private boolean favoritoDelUsuario;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDateTime getFechaVista() { return fechaVista; }
    public void setFechaVista(LocalDateTime fechaVista) { this.fechaVista = fechaVista; }
    public boolean isFavoritoDelUsuario() { return favoritoDelUsuario; }
    public void setFavoritoDelUsuario(boolean favoritoDelUsuario) { this.favoritoDelUsuario = favoritoDelUsuario; }
}
