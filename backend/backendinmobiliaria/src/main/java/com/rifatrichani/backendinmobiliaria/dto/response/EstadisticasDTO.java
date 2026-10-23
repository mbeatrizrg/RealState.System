package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/* Panel de Estadisticas e Informes. Se calcula todo en una sola respuesta (las tres pestañas usan los mismos filtros).
   Definiciones (ver EstadisticaServiceImpl):
   - consulta: un lead (un cliente pidio informacion de un inmueble); mensajes: comunicaciones registradas en los leads
   - visita realizada: tarea de tipo VISITA o reunion completada, vinculada al inmueble (directo o por su negocio)
   - nivel de interaccion: el punto mas avanzado de cada par cliente-inmueble segun su lead, su negocio y sus visitas */
public record EstadisticasDTO(
        LocalDate desde,
        LocalDate hasta,
        Resumen resumen,
        List<Porcion> porNivel,
        List<Porcion> porTipo,
        Serie serie,
        List<FilaInmueble> inmuebles,
        List<FilaVendedor> vendedores,
        List<PuntoVentas> ventasPorPeriodo,
        List<EtapaValor> pipeline) {

    public record Resumen(long consultas, long mensajes, long visitas, long negociosAbiertos, long cierresMes,
                          long cierresPeriodo, BigDecimal montoCerradoPeriodo, long clientesInteresados) {}

    //clave: codigo del nivel (INFORMACION...) o id del tipo de inmueble ("otros" = los tipos que no caben)
    public record Porcion(String clave, String etiqueta, long cantidad) {}

    public record Serie(String agrupacion, List<PuntoSerie> puntos) {}

    //consultas incluye los mensajes de los leads
    public record PuntoSerie(String clave, String etiqueta, long consultas, long visitas, long cierres) {}

    public record FilaInmueble(Integer id, String codigo, String titulo, String tipo, String zona, long clientes,
                               long consultas, long visitas, long ofertas, String vendedor) {}

    public record FilaVendedor(Integer id, String nombre, long creados, long abiertos, long ganados, long perdidos,
                               BigDecimal montoGanado, BigDecimal valorPipeline, Double tasaCierre,
                               long tareasCompletadas, long tareasVencidas) {}

    public record PuntoVentas(String clave, String etiqueta, long cierres, BigDecimal monto) {}

    public record EtapaValor(Integer etapaId, String etapa, long cantidad, BigDecimal valor) {}

    // ----- Vista detallada de un inmueble: quienes interactuaron y que hicieron -----
    public record InteraccionInmueble(Integer id, String codigo, String titulo, String tipo, String zona, String vendedor,
                                      List<ContactoInteraccion> contactos) {}

    public record ContactoInteraccion(Integer contactoId, String nombre, String correo, String telefono, String nivel,
                                      String etiquetaNivel, long consultas, long visitas, Integer negocioId, String negocio,
                                      String etapa, LocalDateTime ultimaInteraccion, List<Accion> acciones) {}

    public record Accion(LocalDateTime fecha, String tipo, String descripcion) {}
}
