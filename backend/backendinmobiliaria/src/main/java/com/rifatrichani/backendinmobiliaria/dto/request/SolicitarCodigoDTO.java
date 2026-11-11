package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SolicitarCodigoDTO(
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    @NotBlank(message = "El apellido es obligatorio") String apellido,
    @NotBlank(message = "El teléfono es obligatorio") String telefono,
    @NotBlank(message = "El correo es obligatorio") @Email(message = "Formato de correo inválido") String correo,
    @NotNull(message = "El ID del inmueble es obligatorio") Integer idInmueble
) {}