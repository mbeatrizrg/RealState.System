package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadResponseDTO;

//Columnas de la tabla del modulo Inmuebles (mismas claves que src/services/propiedades.ts)
public enum ColumnaPropiedad implements ColumnaExportable<PropiedadResponseDTO> {
    CODIGO("codigo", "Código", PropiedadResponseDTO::codigo),
    TITULO("titulo", "Título", PropiedadResponseDTO::titulo),
    TIPO("tipo", "Tipo de propiedad", PropiedadResponseDTO::tipoInmueble),
    OPERACION("operacion", "Tipo de operación", p -> "alquiler".equals(p.tipoOperacion()) ? "Alquiler" : "Venta"),
    ZONA("zona", "Zona / ubicación", p -> p.ciudad() != null ? p.ubicacion() + ", " + p.ciudad() : p.ubicacion()),
    PRECIO("precio", "Precio", PropiedadResponseDTO::precio),
    ESTADO("estado", "Estado", p -> capitalizar(p.estado())),
    DUENO("dueno", "Propietario (dueño)", p -> p.duenoNombre() != null ? p.duenoNombre() : "Sin registrar"),
    ASESOR("asesor", "Asesor captador", PropiedadResponseDTO::nombreAsesor),
    CAPTACION("fechaCaptacion", "Fecha de captación", PropiedadResponseDTO::fechaCaptacion),
    HABITACIONES("habitaciones", "Habitaciones", PropiedadResponseDTO::habitaciones),
    BANOS("banos", "Baños", PropiedadResponseDTO::banos),
    METROS("metros", "Metros cuadrados", PropiedadResponseDTO::metrosCuadrados),
    ESTACIONAMIENTOS("estacionamientos", "Estacionamientos", PropiedadResponseDTO::estacionamientos),
    PISO("piso", "Piso", PropiedadResponseDTO::piso),
    NEGOCIOS("negocios", "Negocios abiertos", PropiedadResponseDTO::negociosAbiertos);

    public static final List<ColumnaPropiedad> POR_DEFECTO = List.of(CODIGO, TITULO, TIPO, OPERACION, ZONA, PRECIO, ESTADO, DUENO, ASESOR);

    private final String clave;
    private final String titulo;
    private final Function<PropiedadResponseDTO, Object> valor;

    ColumnaPropiedad(String clave, String titulo, Function<PropiedadResponseDTO, Object> valor) {
        this.clave = clave;
        this.titulo = titulo;
        this.valor = valor;
    }

    @Override public String getClave() { return clave; }
    @Override public String getTitulo() { return titulo; }
    @Override public Object valorDe(PropiedadResponseDTO propiedad) { return valor.apply(propiedad); }

    public static List<ColumnaPropiedad> desdeClaves(List<String> claves) {
        return ColumnaExportable.desdeClaves(ColumnaPropiedad.class, claves, POR_DEFECTO);
    }

    private static String capitalizar(String texto) {
        return texto == null || texto.isEmpty() ? texto : texto.substring(0, 1).toUpperCase(Locale.ROOT) + texto.substring(1);
    }
}
