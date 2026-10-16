package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioLogeoDTO {
    @NotBlank(message = "Este campo no puede estar en blanco.")
    @Email  (message = "Ingrese un correo electronico válido.")
    private String correo;
    @NotBlank(message = "La contraseña es obligatoria.")
    private String contrasena;

    //Getters y Setters DTO
    public String getCorreo() {return correo;}
    public void setCorreo(String correo) {this.correo = correo;}
    public String getContrasena() {return contrasena;}
    public void setContrasena(String contrasena) {this.contrasena = contrasena;}
    
}
