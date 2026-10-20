package com.rifatrichani.backendinmobiliaria.dto.request;
import java.math.BigInteger;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class EmpleadoRequestDTO {

    @NotBlank(message = "El nombre del empleado es obligatorio")
    private String nombre;
    @NotBlank(message = "El apellido del empleado es obligatorio")
    private String apellido;
    @NotNull(message = "La cédula es obligatoria")
    @Positive(message = "La cédula debe ser un número positivo válido")
    private BigInteger cedula;
    @NotNull(message = "Debe seleccionar el tipo de cédula (Ej. V, E, J)")
    private Integer tipoCedulaId;
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Ingrese un formato de correo válido")
    private String correo;
    @NotBlank(message = "La contraseña provisional es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres de seguridad")
    private String contrasena;
    @NotNull(message = "Debe especificar si el empleado tendrá permisos de Administrador")
    private Boolean esAdmin;

    // Getters y Setters
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

    public Integer getTipoCedulaId() {
        return tipoCedulaId;
    }
    public void setTipoCedulaId(Integer tipoCedulaId) {
        this.tipoCedulaId = tipoCedulaId;
    }

    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Boolean getEsAdmin() {
        return esAdmin;
    }
    public void setEsAdmin(Boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
}
