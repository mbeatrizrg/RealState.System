package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroEstadisticaDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.InteraccionInmueble;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;

//Modulo de Estadisticas e Informes del CRM: metricas, graficas y tablas del comportamiento de los clientes y las ventas
public interface EstadisticaService {
    //Secciones que se pueden incluir en el informe exportado (las mismas que se pueden ocultar en el panel)
    List<String> SECCIONES = List.of("resumen", "pastel", "serie", "inmuebles", "vendedores");

    EstadisticasDTO calcular(FiltroEstadisticaDTO filtro);

    //Contactos que interactuaron con el inmueble y que acciones hicieron. nivel opcional (porcion del pastel)
    InteraccionInmueble interaccionInmueble(Integer inmuebleId, String nivel);

    ArchivoExportado exportar(FiltroEstadisticaDTO filtro, FormatoExportacion formato, List<String> secciones);
}
