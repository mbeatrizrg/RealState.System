package com.rifatrichani.backendinmobiliaria.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* Accesos rapidos de la ficha del contacto: crear nota, enviar correo, registrar llamada, programar tarea o reunion.
   Se envia como parte "datos" (JSON) de un multipart; los adjuntos van en las partes "archivos".
   enviarCorreo: en un CORREO, true lo envia de verdad al contacto (SMTP); false solo lo registra en el historial */
public record ActividadRequestDTO(
        @NotNull(message = "Indique el tipo de actividad")
        @Pattern(regexp = "NOTA|CORREO|LLAMADA|TAREA|REUNION", message = "Tipo de actividad inválido")
        String tipo,
        @NotNull(message = "Indique el contacto")
        Integer contactoId,
        Integer negocioId,
        @Size(max = 200, message = "El asunto no puede exceder los 200 caracteres")
        String asunto,
        @Size(max = 5000, message = "La descripción no puede exceder los 5000 caracteres")
        String descripcion,
        LocalDateTime fechaProgramada,
        @Size(max = 50, message = "El resultado no puede exceder los 50 caracteres")
        String resultado,
        @Min(value = 0, message = "La duración no puede ser negativa")
        @Max(value = 1440, message = "La duración no puede pasar de 24 horas")
        Integer duracionMinutos,
        boolean enviarCorreo) {
}
