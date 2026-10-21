package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//Se pide la contraseña actual: con un token robado no basta para quedarse con la cuenta cambiando el correo
public class CambioCorreoDTO {
    @NotBlank(message = "El correo nuevo es obligatorio")
    @Email(message = "Ingrese un correo electrónico válido.")
    @Size(max = 255)
    private String correoNuevo;
    @NotBlank(message = "Debe indicar su contraseña actual")
    private String contrasena;

    public String getCorreoNuevo() { return correoNuevo; }
    public void setCorreoNuevo(String correoNuevo) { this.correoNuevo = correoNuevo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
