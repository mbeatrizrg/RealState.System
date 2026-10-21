package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

//Paso 1 de "Olvidé mi contraseña": se envia un codigo al correo si la cuenta existe
public class RecuperarContrasenaDTO {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo electrónico válido.")
    private String correo;
    //USUARIO (portal web) o EMPLEADO (CRM)
    @NotBlank(message = "Debe indicar el tipo de cuenta")
    @Pattern(regexp = "USUARIO|EMPLEADO", message = "El tipo de cuenta debe ser USUARIO o EMPLEADO")
    private String tipo;

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
