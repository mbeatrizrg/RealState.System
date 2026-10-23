package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/* Evento del historial de un contacto o negocio. tipo: NOTA, CORREO, LLAMADA, TAREA, REUNION, ETAPA o COMUNICACION
   (las comunicaciones registradas en los leads se muestran en el mismo historial; su id es el de la comunicacion) */
public record ActividadResponseDTO(
        Integer id,
        String tipo,
        String asunto,
        String descripcion,
        Integer contactoId,
        Integer negocioId,
        String nombreNegocio,
        Integer empleadoId,
        String nombreEmpleado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaProgramada,
        boolean completada,
        String resultado,
        Integer duracionMinutos,
        //Canal de la comunicacion (solo COMUNICACION)
        String canal,
        List<ArchivoResponseDTO> archivos) {
}
