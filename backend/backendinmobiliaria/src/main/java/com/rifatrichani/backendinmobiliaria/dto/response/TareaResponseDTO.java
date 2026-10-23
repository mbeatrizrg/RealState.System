package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;

/* Fila de la tabla de tareas y detalle de una tarea. estado: pendiente, en_proceso, vencida o completada
   ("vencida" = sin completar con la fecha de vencimiento pasada; estadoGuardado es el valor real en la base de datos).
   puedeEditar: propietario de la tarea, propietario del contacto o administrador */
public record TareaResponseDTO(
        Integer id,
        String titulo,
        String descripcion,
        String tipoTarea,
        String prioridad,
        String estado,
        String estadoGuardado,
        Integer contactoId,
        String nombreContacto,
        Integer negocioId,
        String nombreNegocio,
        Integer inmuebleId,
        String codigoInmueble,
        String tituloInmueble,
        Integer propietarioId,
        String nombrePropietario,
        LocalDateTime fechaVencimiento,
        LocalDateTime fechaCreacion,
        boolean puedeEditar) {
}
