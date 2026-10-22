package com.rifatrichani.backendinmobiliaria.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/* Ficha de un contacto: sus datos, sus leads, la bitacora de comunicaciones y lo que hizo en el portal.
   Un vendedor solo recibe las comunicaciones de los leads de sus inmuebles; el admin recibe todas */
public class ContactoDetalleResponseDTO {
    private ContactoResponseDTO contacto;
    private List<LeadResponseDTO> leads;
    private List<ComunicacionResponseDTO> comunicaciones;
    private List<InmuebleActividadDTO> favoritos;
    private List<InmuebleActividadDTO> vistasRecientes;

    //Inmueble que el contacto guardo en favoritos o vio en el portal (fecha = cuando lo vio; null en favoritos)
    public record InmuebleActividadDTO(Integer inmuebleId, String titulo, Integer precio, Boolean vendido, LocalDateTime fecha) {}

    public ContactoResponseDTO getContacto() { return contacto; }
    public void setContacto(ContactoResponseDTO contacto) { this.contacto = contacto; }
    public List<LeadResponseDTO> getLeads() { return leads; }
    public void setLeads(List<LeadResponseDTO> leads) { this.leads = leads; }
    public List<ComunicacionResponseDTO> getComunicaciones() { return comunicaciones; }
    public void setComunicaciones(List<ComunicacionResponseDTO> comunicaciones) { this.comunicaciones = comunicaciones; }
    public List<InmuebleActividadDTO> getFavoritos() { return favoritos; }
    public void setFavoritos(List<InmuebleActividadDTO> favoritos) { this.favoritos = favoritos; }
    public List<InmuebleActividadDTO> getVistasRecientes() { return vistasRecientes; }
    public void setVistasRecientes(List<InmuebleActividadDTO> vistasRecientes) { this.vistasRecientes = vistasRecientes; }
}
