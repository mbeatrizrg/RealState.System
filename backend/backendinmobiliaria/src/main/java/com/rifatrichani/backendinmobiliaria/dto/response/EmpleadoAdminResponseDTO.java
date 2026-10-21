package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;

//Lo que recibe el administrador cuando consulta los empleados (nunca incluye la contraseña)
public class EmpleadoAdminResponseDTO {
    private Integer id;
    private String nombreCompleto;
    private Integer cedula;
    private String correo;
    private Boolean esAdmin;
    private Boolean activo;
    private Boolean tieneContrasena;
    private LocalDateTime ultimaEntrada;
    private LocalDateTime ultimaSalida;
    private boolean sesionAbierta;
    private Long cantidadClientesLeads;
    private Long cantidadComunicaciones;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public Integer getCedula() { return cedula; }
    public void setCedula(Integer cedula) { this.cedula = cedula; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public Boolean getEsAdmin() { return esAdmin; }
    public void setEsAdmin(Boolean esAdmin) { this.esAdmin = esAdmin; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Boolean getTieneContrasena() { return tieneContrasena; }
    public void setTieneContrasena(Boolean tieneContrasena) { this.tieneContrasena = tieneContrasena; }
    public LocalDateTime getUltimaEntrada() { return ultimaEntrada; }
    public void setUltimaEntrada(LocalDateTime ultimaEntrada) { this.ultimaEntrada = ultimaEntrada; }
    public LocalDateTime getUltimaSalida() { return ultimaSalida; }
    public void setUltimaSalida(LocalDateTime ultimaSalida) { this.ultimaSalida = ultimaSalida; }
    public boolean isSesionAbierta() { return sesionAbierta; }
    public void setSesionAbierta(boolean sesionAbierta) { this.sesionAbierta = sesionAbierta; }
    public Long getCantidadClientesLeads() { return cantidadClientesLeads; }
    public void setCantidadClientesLeads(Long cantidadClientesLeads) { this.cantidadClientesLeads = cantidadClientesLeads; }
    public Long getCantidadComunicaciones() { return cantidadComunicaciones; }
    public void setCantidadComunicaciones(Long cantidadComunicaciones) { this.cantidadComunicaciones = cantidadComunicaciones; }
}
