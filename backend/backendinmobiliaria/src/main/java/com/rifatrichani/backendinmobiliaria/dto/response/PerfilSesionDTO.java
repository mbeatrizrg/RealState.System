package com.rifatrichani.backendinmobiliaria.dto.response;

//Datos de la cuenta con sesion iniciada (GET /api/auth/me). Los campos que no aplican al tipo de cuenta quedan en null
public class PerfilSesionDTO {
    private Integer id;
    private String tipo;
    private String rol;
    private String nombre;
    private String apellido;
    private String correo;
    //Solo usuarios del portal
    private String telefono;
    private String tipoNegocio;
    private String correoPendiente;
    //Solo empleados
    private Integer cedula;
    private Boolean esAdmin;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(String tipoNegocio) { this.tipoNegocio = tipoNegocio; }
    public String getCorreoPendiente() { return correoPendiente; }
    public void setCorreoPendiente(String correoPendiente) { this.correoPendiente = correoPendiente; }
    public Integer getCedula() { return cedula; }
    public void setCedula(Integer cedula) { this.cedula = cedula; }
    public Boolean getEsAdmin() { return esAdmin; }
    public void setEsAdmin(Boolean esAdmin) { this.esAdmin = esAdmin; }
}
