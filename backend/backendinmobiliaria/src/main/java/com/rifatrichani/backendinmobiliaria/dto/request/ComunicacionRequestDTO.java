package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComunicacionRequestDTO {
    @NotNull(message = "Debe indicar el canal de comunicación utilizado")
    private Integer canalId;
    @NotNull(message = "Debe indicar a qué Lead pertenece esta comunicación")
    private Integer leadId;
    @NotBlank(message = "Debe escribir un resumen o descripción de la conversación")
    private String descripcion;

    // Getters y Setters
    public Integer getCanalId() { 
        return canalId; 
    }
    public void setCanalId(Integer canalId) { 
        this.canalId = canalId; 
    }

    public Integer getLeadId() { 
        return leadId; 
    }
    public void setLeadId(Integer leadId) {
        this.leadId = leadId; 
    }

    public String getDescripcion() { 
        return descripcion; 
    }
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }
}