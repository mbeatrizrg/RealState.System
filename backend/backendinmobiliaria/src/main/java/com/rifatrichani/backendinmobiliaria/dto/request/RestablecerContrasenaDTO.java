package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Paso 2 de "Olvidé mi contraseña": codigo recibido por correo + contraseña nueva
public class RestablecerContrasenaDTO extends RecuperarContrasenaDTO {
    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El código debe tener 6 dígitos")
    private String codigo;
    @NotBlank(message = "Debe indicar la nueva contraseña")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String contrasenaNueva;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getContrasenaNueva() { return contrasenaNueva; }
    public void setContrasenaNueva(String contrasenaNueva) { this.contrasenaNueva = contrasenaNueva; }
}
