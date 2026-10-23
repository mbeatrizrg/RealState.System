package com.rifatrichani.backendinmobiliaria.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Datos del modulo Inmuebles que no estan en el formulario "Añadir inmueble": estado, dueño y caracteristicas fisicas
public record FichaPropiedadDTO(
        @NotNull(message = "Indique el estado del inmueble")
        @Pattern(regexp = "disponible|reservado|vendido|alquilado", message = "Estado inválido")
        String estado,
        @Size(max = 150, message = "El nombre del dueño no puede exceder los 150 caracteres")
        String duenoNombre,
        @Size(max = 30, message = "El teléfono del dueño no puede exceder los 30 caracteres")
        String duenoTelefono,
        @Email(message = "El correo del dueño no es válido")
        @Size(max = 255)
        String duenoCorreo,
        @Min(value = 0, message = "Las habitaciones no pueden ser negativas") @Max(99)
        Integer habitaciones,
        @Min(value = 0, message = "Los baños no pueden ser negativos") @Max(99)
        Integer banos,
        @DecimalMin(value = "0", message = "Los metros cuadrados no pueden ser negativos")
        @Digits(integer = 8, fraction = 2, message = "Metros cuadrados inválidos")
        BigDecimal metrosCuadrados,
        @Min(value = 0, message = "Los puestos de estacionamiento no pueden ser negativos") @Max(99)
        Integer estacionamientos,
        @Min(value = -10, message = "Piso inválido") @Max(value = 200, message = "Piso inválido")
        Integer piso) {
}
