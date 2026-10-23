package com.rifatrichani.backendinmobiliaria.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* Crear o editar una tarea. Debe tener al menos un contacto, un negocio o un inmueble vinculado.
   propietarioId null = el empleado que la crea; solo un administrador puede asignarla a otro vendedor */
public record TareaRequestDTO(
        @NotBlank(message = "El título de la tarea es obligatorio")
        @Size(max = 200, message = "El título no puede exceder los 200 caracteres")
        String titulo,

        @Size(max = 5000, message = "La descripción no puede exceder los 5000 caracteres")
        String descripcion,

        @NotNull(message = "Indique el tipo de tarea")
        @Pattern(regexp = "LLAMADA|VISITA|ENVIO|REUNION|TRAMITE", message = "Tipo de tarea inválido")
        String tipoTarea,

        @NotNull(message = "Indique la prioridad")
        @Pattern(regexp = "baja|media|alta", message = "Prioridad inválida (baja, media o alta)")
        String prioridad,

        //Sin valor = pendiente
        @Pattern(regexp = "pendiente|en_proceso|completada", message = "Estado inválido (pendiente, en_proceso o completada)")
        String estado,

        @NotNull(message = "Indique la fecha y hora de vencimiento")
        LocalDateTime fechaVencimiento,

        Integer contactoId,
        Integer negocioId,
        Integer inmuebleId,
        Integer propietarioId) {
}
