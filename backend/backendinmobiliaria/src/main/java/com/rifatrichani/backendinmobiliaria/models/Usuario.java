package com.rifatrichani.backendinmobiliaria.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

// Cliente del portal web (Contacto / Lead sin autenticación)
@Entity
@Table(name = "\"Usuario\"")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String telefono;

    @Column(unique = true, nullable = false)
    private String correo;

    // En Supabase la columna se llama tipo_negocio
    @Column(name = "tipo_negocio")
    private String tipoNegocio = "Sin definir";

    // En Supabase codverif es String
    @Column(name = "codverif")
    private String codVerif;

    @Column(name = "codverif_expira")
    private LocalDateTime codVerifExpira;

    @Column(name = "codverif_intentos", nullable = false)
    private Integer codVerifIntentos = 0;

    @Column(nullable = false)
    private Boolean verificado = false;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultima_actividad")
    private LocalDateTime ultimaActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario")
    private Empleado propietario;

    @Column(name = "ci", unique = true)
    private Integer cedula;

    @Transient
    private String contrasena;
    @Transient
    private String resetCodigo;
    @Transient
    private LocalDateTime resetExpira;
    @Transient
    private Integer resetIntentos = 0;
    @Transient
    private String correoPendiente;
    @Transient
    private LocalDateTime tokensValidosDesde;

    public Usuario() {}

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

    public Integer getCedula() {
        return cedula;
    }

    public void setCedula(Integer cedula) {
        this.cedula = cedula;
    }

    public String getTipoNegocio() {
        return tipoNegocio;
    }

    public void setTipoNegocio(String tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }

    public String getCodVerif() {
        return codVerif;
    }

    public void setCodVerif(String codVerif) {
        this.codVerif = codVerif;
    }

    public LocalDateTime getCodVerifExpira() {
        return codVerifExpira;
    }

    public void setCodVerifExpira(LocalDateTime codVerifExpira) {
        this.codVerifExpira = codVerifExpira;
    }

    public Integer getCodVerifIntentos() {
        return codVerifIntentos;
    }

    public void setCodVerifIntentos(Integer codVerifIntentos) {
        this.codVerifIntentos = codVerifIntentos;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(LocalDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public Empleado getPropietario() {
        return propietario;
    }

    public void setPropietario(Empleado propietario) {
        this.propietario = propietario;
    }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getResetCodigo() { return resetCodigo; }
    public void setResetCodigo(String resetCodigo) { this.resetCodigo = resetCodigo; }
    public java.time.LocalDateTime getResetExpira() { return resetExpira; }
    public void setResetExpira(java.time.LocalDateTime resetExpira) { this.resetExpira = resetExpira; }
    public Integer getResetIntentos() { return resetIntentos; }
    public void setResetIntentos(Integer resetIntentos) { this.resetIntentos = resetIntentos; }
    public String getCorreoPendiente() { return correoPendiente; }
    public void setCorreoPendiente(String correoPendiente) { this.correoPendiente = correoPendiente; }
    public java.time.LocalDateTime getTokensValidosDesde() { return tokensValidosDesde; }
    public void setTokensValidosDesde(java.time.LocalDateTime tokensValidosDesde) { this.tokensValidosDesde = tokensValidosDesde; }
}