package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//Lead creado manualmente desde el CRM. El vendedor encargado es el del inmueble (en Supabase el lead no tiene vendedor propio)
public class LeadRequestDTO {
    @NotNull(message = "El ID del usuario/cliente es obligatorio")
    private Integer usuarioId;
    @NotNull(message = "Debe indicar el inmueble de interés")
    private Integer inmuebleId;
    //Etapa del embudo (columna numerica id_estatus, en Supabase no hay tabla de catalogo)
    @NotNull(message = "El estatus del lead es obligatorio")
    @Positive(message = "El estatus del lead debe ser un número positivo")
    private Integer estatus;

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
    public Integer getEstatus() { return estatus; }
    public void setEstatus(Integer estatus) { this.estatus = estatus; }
}
