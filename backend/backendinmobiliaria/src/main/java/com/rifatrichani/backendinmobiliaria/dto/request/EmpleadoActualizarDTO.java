package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//Edicion de los datos de un empleado por parte de un administrador (la contraseña se cambia aparte)
public class EmpleadoActualizarDTO {
    @NotBlank(message = "El nombre del empleado es obligatorio")
    private String nombre;
    @NotBlank(message = "El apellido del empleado es obligatorio")
    private String apellido;
    @NotNull(message = "La cédula es obligatoria")
    @Positive(message = "La cédula debe ser un número positivo válido")
    private Integer cedula;
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Ingrese un formato de correo válido")
    private String correo;
    @NotNull(message = "Debe especificar si el empleado es Administrador")
    private Boolean esAdmin;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public Integer getCedula() { return cedula; }
    public void setCedula(Integer cedula) { this.cedula = cedula; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public Boolean getEsAdmin() { return esAdmin; }
    public void setEsAdmin(Boolean esAdmin) { this.esAdmin = esAdmin; }
}
