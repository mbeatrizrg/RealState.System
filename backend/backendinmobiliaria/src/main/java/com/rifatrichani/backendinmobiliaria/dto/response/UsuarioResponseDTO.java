package com.rifatrichani.backendinmobiliaria.dto.response;

//Datos de un cliente (sin contraseña ni codigos de verificacion)
public class UsuarioResponseDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private Integer cedula;
    private String tipoNegocio;
    private Boolean verificado;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
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
    public Boolean getVerificado() { return verificado; }
    public void setVerificado(Boolean verificado) { this.verificado = verificado; }
    public Integer getCedula() { return cedula; }
    public void setCedula(Integer cedula) { this.cedula = cedula; }
}
