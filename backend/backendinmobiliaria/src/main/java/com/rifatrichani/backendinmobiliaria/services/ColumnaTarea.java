package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.rifatrichani.backendinmobiliaria.dto.response.TareaResponseDTO;

//Columnas de la tabla de tareas (mismas claves que src/services/tareas.ts)
public enum ColumnaTarea implements ColumnaExportable<TareaResponseDTO> {
    TITULO("titulo", "Título / asunto", TareaResponseDTO::titulo),
    TIPO("tipo", "Tipo de tarea", t -> Textos.TIPOS.getOrDefault(t.tipoTarea(), "General")),
    ESTADO("estado", "Estado", t -> Textos.ESTADOS.getOrDefault(t.estado(), t.estado())),
    PRIORIDAD("prioridad", "Prioridad", t -> Textos.PRIORIDADES.getOrDefault(t.prioridad(), "Media")),
    CONTACTO("contacto", "Contacto asociado", t -> t.nombreContacto() != null ? t.nombreContacto() : "—"),
    VINCULO("vinculo", "Inmueble o negocio", ColumnaTarea::vinculo),
    VENCIMIENTO("vencimiento", "Vencimiento", TareaResponseDTO::fechaVencimiento),
    PROPIETARIO("propietario", "Propietario de la tarea", t -> t.nombrePropietario() != null ? t.nombrePropietario() : "Sin asignar"),
    CREACION("fechaCreacion", "Fecha de creación", TareaResponseDTO::fechaCreacion),
    DESCRIPCION("descripcion", "Descripción", t -> t.descripcion() != null ? t.descripcion() : "");

    public static final List<ColumnaTarea> POR_DEFECTO = List.of(TITULO, TIPO, ESTADO, PRIORIDAD, CONTACTO, VINCULO, VENCIMIENTO, PROPIETARIO);

    //Textos de los codigos guardados (una clase aparte porque las constantes del enum se crean antes que sus campos)
    public static final class Textos {
        public static final Map<String, String> TIPOS = Map.of("LLAMADA", "Llamada", "VISITA", "Visita a inmueble",
                "ENVIO", "Envío de catálogo/correo", "REUNION", "Reunión", "TRAMITE", "Trámite");
        public static final Map<String, String> ESTADOS = Map.of("pendiente", "Pendiente", "en_proceso", "En proceso",
                "vencida", "Vencida", "completada", "Completada");
        public static final Map<String, String> PRIORIDADES = Map.of("baja", "Baja", "media", "Media", "alta", "Alta");

        private Textos() {}
    }

    private final String clave;
    private final String titulo;
    private final Function<TareaResponseDTO, Object> valor;

    ColumnaTarea(String clave, String titulo, Function<TareaResponseDTO, Object> valor) {
        this.clave = clave;
        this.titulo = titulo;
        this.valor = valor;
    }

    @Override public String getClave() { return clave; }
    @Override public String getTitulo() { return titulo; }
    @Override public Object valorDe(TareaResponseDTO tarea) { return valor.apply(tarea); }

    public static List<ColumnaTarea> desdeClaves(List<String> claves) {
        return ColumnaExportable.desdeClaves(ColumnaTarea.class, claves, POR_DEFECTO);
    }

    private static String vinculo(TareaResponseDTO t) {
        if (t.nombreNegocio() != null) return "Negocio: " + t.nombreNegocio();
        if (t.codigoInmueble() != null) return t.codigoInmueble() + " · " + t.tituloInmueble();
        return "—";
    }
}
