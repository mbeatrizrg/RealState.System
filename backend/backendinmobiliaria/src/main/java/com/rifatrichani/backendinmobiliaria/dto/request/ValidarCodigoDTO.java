package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidarCodigoDTO(
    @NotBlank(message = "El correo es obligatorio") @Email String correo,
    @NotBlank(message = "El código es obligatorio") String codigo,
    @NotNull(message = "El ID del inmueble es obligatorio") Integer idInmueble
) {}