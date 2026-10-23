package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.response.ActividadResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ArchivoResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ComunicacionResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.NegocioResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Actividad;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Negocio;
import com.rifatrichani.backendinmobiliaria.models.Usuario;

//Conversion de entidades a DTOs compartida por los modulos de Negocios, Inmuebles y el historial de actividad
final class MapeosCrm {

    private MapeosCrm() {}

    static String nombreEmpleado(Empleado e) {
        if (e == null) return null;
        return e.getNombre() + (e.getApellido() != null ? " " + e.getApellido() : "");
    }

    static String nombreContacto(Usuario u) {
        return u.getNombre() + " " + u.getApellido();
    }

    static NegocioResponseDTO negocio(Negocio n) {
        Inmueble i = n.getInmueble();
        Empleado p = n.getPropietario();
        return new NegocioResponseDTO(n.getId(), n.getNombre(), i.getId(), i.getRrbr(), i.getTitulo(),
                i.getTipoInmueble().getNombre(), n.getContacto().getId(), nombreContacto(n.getContacto()),
                n.getTipoOperacion(), n.getValor(), n.getEtapa().getId(), n.getEtapa().getNombre(),
                Boolean.TRUE.equals(n.getEtapa().getAbierto()), n.getFechaCierreEstimada(),
                p != null ? p.getId() : null, nombreEmpleado(p), n.getFechaCreacion(), n.getFechaActualizacion(), n.getFechaCierre());
    }

    static PropiedadResponseDTO propiedad(Inmueble i, long negociosAbiertos, String urlImagenPrincipal) {
        Empleado asesor = i.getEmpleado();
        return new PropiedadResponseDTO(i.getId(), i.getRrbr(), i.getTitulo(), i.getTipoInmueble().getId(),
                i.getTipoInmueble().getNombre(), Boolean.TRUE.equals(i.getAlquiler()) ? "alquiler" : "venta",
                i.getCiudad() != null ? i.getCiudad().getId() : null, i.getCiudad() != null ? i.getCiudad().getNombre() : null,
                i.getUbicacion(), i.getPrecio(), i.getEstado(), i.getDuenoNombre(), i.getDuenoTelefono(), i.getDuenoCorreo(),
                asesor != null ? asesor.getId() : null, nombreEmpleado(asesor), i.getFechaCaptacion(),
                i.getHabitaciones(), i.getBanos(), i.getMetrosCuadrados(), i.getEstacionamientos(), i.getPiso(),
                negociosAbiertos, urlImagenPrincipal);
    }

    static ActividadResponseDTO actividad(Actividad a, List<ArchivoResponseDTO> archivos) {
        Negocio n = a.getNegocio();
        Empleado e = a.getEmpleado();
        return new ActividadResponseDTO(a.getId(), a.getTipo().name(), a.getAsunto(), a.getDescripcion(),
                a.getContacto() != null ? a.getContacto().getId() : null, n != null ? n.getId() : null, n != null ? n.getNombre() : null,
                e != null ? e.getId() : null, nombreEmpleado(e), a.getFechaCreacion(), a.getFechaProgramada(),
                Boolean.TRUE.equals(a.getCompletada()), a.getResultado(), a.getDuracionMinutos(), null, archivos);
    }

    //Las comunicaciones de los leads se muestran en el mismo historial que las actividades
    static ActividadResponseDTO comunicacion(ComunicacionResponseDTO c, Integer contactoId) {
        return new ActividadResponseDTO(c.getId(), "COMUNICACION", "Comunicación por " + c.getCanal(), c.getDescripcion(),
                contactoId, null, null, null, c.getNombreVendedor(), c.getFechaCreacion(), null, true, null, null,
                c.getCanal(), List.of());
    }
}
