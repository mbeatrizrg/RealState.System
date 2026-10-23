package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.function.Function;

import com.rifatrichani.backendinmobiliaria.dto.response.NegocioResponseDTO;

//Columnas de la tabla de negocios (mismas claves que src/services/negocios.ts)
public enum ColumnaNegocio implements ColumnaExportable<NegocioResponseDTO> {
    NOMBRE("nombre", "Nombre del negocio", NegocioResponseDTO::nombre),
    INMUEBLE("inmueble", "Inmueble asociado", n -> n.codigoInmueble() + " · " + n.tipoInmueble()),
    CONTACTO("contacto", "Contacto / cliente", NegocioResponseDTO::nombreContacto),
    OPERACION("operacion", "Tipo de operación", n -> "alquiler".equals(n.tipoOperacion()) ? "Alquiler" : "Venta"),
    VALOR("valor", "Valor", NegocioResponseDTO::valor),
    ETAPA("etapa", "Etapa del negocio", NegocioResponseDTO::etapa),
    CIERRE("fechaCierre", "Fecha estimada de cierre", NegocioResponseDTO::fechaCierreEstimada),
    PROPIETARIO("propietario", "Propietario del negocio", n -> n.nombrePropietario() != null ? n.nombrePropietario() : "Sin asignar"),
    CREACION("fechaCreacion", "Fecha de creación", NegocioResponseDTO::fechaCreacion),
    TITULO_INMUEBLE("tituloInmueble", "Título del inmueble", NegocioResponseDTO::tituloInmueble),
    ACTUALIZACION("fechaActualizacion", "Última actualización", NegocioResponseDTO::fechaActualizacion);

    public static final List<ColumnaNegocio> POR_DEFECTO = List.of(NOMBRE, INMUEBLE, CONTACTO, OPERACION, VALOR, ETAPA, CIERRE, PROPIETARIO);

    private final String clave;
    private final String titulo;
    private final Function<NegocioResponseDTO, Object> valor;

    ColumnaNegocio(String clave, String titulo, Function<NegocioResponseDTO, Object> valor) {
        this.clave = clave;
        this.titulo = titulo;
        this.valor = valor;
    }

    @Override public String getClave() { return clave; }
    @Override public String getTitulo() { return titulo; }
    @Override public Object valorDe(NegocioResponseDTO negocio) { return valor.apply(negocio); }

    public static List<ColumnaNegocio> desdeClaves(List<String> claves) {
        return ColumnaExportable.desdeClaves(ColumnaNegocio.class, claves, POR_DEFECTO);
    }
}
