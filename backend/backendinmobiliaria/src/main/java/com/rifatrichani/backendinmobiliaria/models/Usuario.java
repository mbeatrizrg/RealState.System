package com.rifatrichani.backendinmobiliaria.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Cliente del portal web. Las columnas contrasena, verificado y codverif_* se agregan con db/migraciones/001_autenticacion.sql
@Entity
@Table (name = "\"Usuario\"")
public class Usuario {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (nullable = false)
    private String nombre;
    @Column (nullable = false)
    private String apellido;
    @Column (nullable = false)
    private String telefono;
    @Column (unique = true,nullable = false)
    private String correo;
    //Cedula de identidad, mismo formato que Empleado (columna ci). Nula solo en cuentas anteriores a 004_cedula_usuario.sql
    @Column (name = "ci", unique = true)
    private Integer cedula;
    //Tipo de operacion que busca el cliente (compra, alquiler, venta)
    @Column (nullable = false)
    private String tipoNegocio;
    //Codigo de verificacion del correo, se borra al confirmar la cuenta
    @Column (name = "codverif")
    private Integer codVerif;
    @Column (name = "codverif_expira")
    private LocalDateTime codVerifExpira;
    @Column (name = "codverif_intentos", nullable = false)
    private Integer codVerifIntentos = 0;
    //Hash BCrypt, nunca la contraseña en texto plano
    @Column
    private String contrasena;
    @Column (nullable = false)
    private Boolean verificado = false;
    //Cambio de correo: la direccion nueva queda pendiente hasta confirmar el codigo enviado a ella
    @Column (name = "correo_pendiente")
    private String correoPendiente;
    //Los tokens emitidos antes de esta fecha se rechazan (logout, cambio o recuperacion de contraseña)
    @Column (name = "tokens_validos_desde")
    private LocalDateTime tokensValidosDesde;
    //Recuperacion de contraseña: hash SHA-256 del codigo enviado por correo (db/migraciones/003)
    @Column (name = "reset_codigo")
    private String resetCodigo;
    @Column (name = "reset_expira")
    private LocalDateTime resetExpira;
    @Column (name = "reset_intentos", nullable = false)
    private Integer resetIntentos = 0;
    public Usuario(){}
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
    public Integer getCodVerif() {
        return codVerif;
    }
    public void setCodVerif(Integer codVerif) {
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
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    public Boolean getVerificado() {
        return verificado;
    }
    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }
    public String getResetCodigo() {
        return resetCodigo;
    }
    public void setResetCodigo(String resetCodigo) {
        this.resetCodigo = resetCodigo;
    }
    public LocalDateTime getResetExpira() {
        return resetExpira;
    }
    public void setResetExpira(LocalDateTime resetExpira) {
        this.resetExpira = resetExpira;
    }
    public Integer getResetIntentos() {
        return resetIntentos;
    }
    public void setResetIntentos(Integer resetIntentos) {
        this.resetIntentos = resetIntentos;
    }
    public String getCorreoPendiente() {
        return correoPendiente;
    }
    public void setCorreoPendiente(String correoPendiente) {
        this.correoPendiente = correoPendiente;
    }
    public LocalDateTime getTokensValidosDesde() {
        return tokensValidosDesde;
    }
    public void setTokensValidosDesde(LocalDateTime tokensValidosDesde) {
        this.tokensValidosDesde = tokensValidosDesde;
    }
}
