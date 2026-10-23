package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;

import com.rifatrichani.backendinmobiliaria.models.ArchivoCrm;

//Adjunto de una actividad o documento de un inmueble. Se descarga en GET /api/archivos/{id}
public record ArchivoResponseDTO(Integer id, String nombre, String tipoContenido, Long tamano, LocalDateTime fechaSubida,
                                 Integer actividadId) {

    public static ArchivoResponseDTO de(ArchivoCrm a) {
        return new ArchivoResponseDTO(a.getId(), a.getNombre(), a.getTipoContenido(), a.getTamano(), a.getFechaSubida(),
                a.getActividad() != null ? a.getActividad().getId() : null);
    }
}
