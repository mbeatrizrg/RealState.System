package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioCorreoRequestDTO {
    @NotBlank(message = "El correo es obligatorio") 
    @Email (message = "Ingrese una direccion de correo valida.")
    private String correo;
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
}
