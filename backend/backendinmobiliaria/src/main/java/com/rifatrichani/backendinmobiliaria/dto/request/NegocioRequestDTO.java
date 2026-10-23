package com.rifatrichani.backendinmobiliaria.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* Crear o editar un negocio. propietarioId null = sin asignar; un vendedor solo puede ponerse a si mismo o dejarlo
   sin asignar (ver NegocioServiceImpl) */
public record NegocioRequestDTO(
        @NotBlank(message = "El nombre del negocio es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
        String nombre,
        @NotNull(message = "Seleccione el inmueble del negocio")
        Integer inmuebleId,
        @NotNull(message = "Seleccione el contacto (cliente) del negocio")
        Integer contactoId,
        @NotNull(message = "Indique si es una venta o un alquiler")
        @Pattern(regexp = "venta|alquiler", message = "Tipo de operación inválido (venta o alquiler)")
        String tipoOperacion,
        @NotNull(message = "El valor del negocio es obligatorio")
        @DecimalMin(value = "0", message = "El valor no puede ser negativo")
        @Digits(integer = 12, fraction = 2, message = "Valor inválido")
        BigDecimal valor,
        @NotNull(message = "Seleccione la etapa del negocio")
        Integer etapaId,
        LocalDate fechaCierreEstimada,
        Integer propietarioId) {
}
