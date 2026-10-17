package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigInteger;
import java.time.LocalDateTime;
//Esto es lo que recibe el administrador cuando consulta los Empleados el
public class EmpleadoAdminResponseDTO {
    private Integer id;
    private String nombreCompleto;
    private String tipoCedula;
    private BigInteger cedula;
    private String correo;
    private Boolean esAdmin;
    private LocalDateTime ultimaEntrada;
    private LocalDateTime ultimaSalida;
    private boolean activoEnSistema;
    private Long cantidadClientesLeads;
    private Long cantidadComunicaciones;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getTipoCedula() { return tipoCedula; }
    public void setTipoCedula(String tipoCedula) { this.tipoCedula = tipoCedula; }
    public BigInteger getCedula() { return cedula; }
    public void setCedula(BigInteger cedula) { this.cedula = cedula; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public Boolean getEsAdmin() { return esAdmin; }
    public void setEsAdmin(Boolean esAdmin) { this.esAdmin = esAdmin; }
    public LocalDateTime getUltimaEntrada() { return ultimaEntrada; }
    public void setUltimaEntrada(LocalDateTime ultimaEntrada) { this.ultimaEntrada = ultimaEntrada; }
    public LocalDateTime getUltimaSalida() { return ultimaSalida; }
    public void setUltimaSalida(LocalDateTime ultimaSalida) { this.ultimaSalida = ultimaSalida; }
    public boolean isActivoEnSistema() { return activoEnSistema; }
    public void setActivoEnSistema(boolean activoEnSistema) { this.activoEnSistema = activoEnSistema; }
    public Long getCantidadClientesLeads() { return cantidadClientesLeads; }
    public void setCantidadClientesLeads(Long cantidadClientesLeads) { this.cantidadClientesLeads = cantidadClientesLeads; }
    public Long getCantidadComunicaciones() { return cantidadComunicaciones; }
    public void setCantidadComunicaciones(Long cantidadComunicaciones) { this.cantidadComunicaciones = cantidadComunicaciones; }
}