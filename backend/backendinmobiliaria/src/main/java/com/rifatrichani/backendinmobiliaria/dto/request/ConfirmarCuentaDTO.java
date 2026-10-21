package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

//Se pide el correo junto al codigo: asi el codigo solo se compara contra UNA cuenta y no se puede adivinar probando contra todas
public class ConfirmarCuentaDTO {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo electrónico válido.")
    private String correo;
    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El código debe tener 6 dígitos")
    private String codigo;

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
