package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroTareaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.TareaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.TareaResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;

/* Modulo de Tareas del CRM: actividades de tipo TAREA (llamar a un cliente, visita guiada a un inmueble, enviar
   opciones, solicitar documentos...) con tipo, prioridad, estado, vencimiento y un vendedor propietario */
public interface TareaService {

    PaginaResponseDTO<TareaResponseDTO> buscar(FiltroTareaDTO filtro, CuentaAutenticada empleado);

    //todas, mis pendientes y completadas / vencidas
    ConteoPestanasDTO contar(FiltroTareaDTO filtro, CuentaAutenticada empleado);

    ArchivoExportado exportar(FiltroTareaDTO filtro, List<ColumnaTarea> columnas, FormatoExportacion formato,
                              boolean soloPagina, CuentaAutenticada empleado);

    TareaResponseDTO obtener(Integer id, CuentaAutenticada empleado);

    TareaResponseDTO crear(TareaRequestDTO dto, CuentaAutenticada empleado);

    TareaResponseDTO actualizar(Integer id, TareaRequestDTO dto, CuentaAutenticada empleado);

    TareaResponseDTO cambiarEstado(Integer id, String estado, CuentaAutenticada empleado);

    //Marca la tarea como completada y, si se escribe, deja una nota con el resultado de la gestion en el contacto
    TareaResponseDTO completar(Integer id, String resultado, CuentaAutenticada empleado);

    void eliminar(Integer id, CuentaAutenticada empleado);
}
