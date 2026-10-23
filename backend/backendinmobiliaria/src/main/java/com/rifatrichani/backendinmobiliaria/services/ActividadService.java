package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.ActividadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ActividadResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ArchivoResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Actividad;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Negocio;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

//Historial de actividad del registro del contacto (notas, correos, llamadas, tareas, reuniones) y archivos privados del CRM
public interface ActividadService {
    //Maximo de adjuntos por actividad
    int MAXIMO_ADJUNTOS = 5;

    record ArchivoDescarga(String nombre, String tipoContenido, Resource contenido) {}

    //Actividades del contacto (y de sus negocios) junto con las comunicaciones de sus leads, la mas reciente primero
    List<ActividadResponseDTO> historialContacto(Integer contactoId, CuentaAutenticada empleado);

    List<ActividadResponseDTO> historialNegocio(Integer negocioId);

    //Adjuntos de todas las actividades del contacto (barra lateral derecha del registro)
    List<ArchivoResponseDTO> adjuntosContacto(Integer contactoId);

    ActividadResponseDTO crear(ActividadRequestDTO dto, List<MultipartFile> archivos, CuentaAutenticada empleado);

    void marcarCompletada(Integer id, boolean completada, CuentaAutenticada empleado);

    void eliminar(Integer id, CuentaAutenticada empleado);

    ArchivoDescarga descargarArchivo(Integer archivoId);

    //Lo usa NegocioService: deja constancia de la creacion de un negocio o de un cambio de etapa
    Actividad registrarEtapa(Negocio negocio, Empleado autor, String descripcion);
}
