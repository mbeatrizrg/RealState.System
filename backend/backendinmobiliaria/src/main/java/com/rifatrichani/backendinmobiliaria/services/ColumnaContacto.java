package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.function.Function;

import com.rifatrichani.backendinmobiliaria.dto.response.ContactoResponseDTO;

/* Columnas que se pueden mostrar y exportar en la tabla de contactos. La clave es la misma que usa el frontend
   (src/services/contactos.ts) y el valor puede ser texto, numero o LocalDateTime (Excel guarda las fechas como fecha) */
public enum ColumnaContacto implements ColumnaExportable<ContactoResponseDTO> {
    NOMBRE("nombre", "Nombre", c -> c.getNombre() + " " + c.getApellido()),
    CORREO("correo", "Correo", ContactoResponseDTO::getCorreo),
    TELEFONO("telefono", "Teléfono", ContactoResponseDTO::getTelefono),
    FECHA_CREACION("fechaCreacion", "Fecha de creación", ContactoResponseDTO::getFechaCreacion),
    PROPIETARIO("propietario", "Propietario del contacto", c -> c.getNombrePropietario() != null ? c.getNombrePropietario() : "Sin asignar"),
    ESTATUS("estatus", "Estatus del lead", c -> c.getEstatus() != null ? c.getEstatus() : "Sin lead"),
    ULTIMA_ACTIVIDAD("ultimaActividad", "Última actividad", ContactoResponseDTO::getUltimaActividad),
    CEDULA("cedula", "Cédula", ContactoResponseDTO::getCedula),
    TIPO_NEGOCIO("tipoNegocio", "Tipo de negocio", ContactoResponseDTO::getTipoNegocio),
    LEADS("leads", "Leads", ContactoResponseDTO::getCantidadLeads),
    VERIFICADO("verificado", "Cuenta verificada", c -> Boolean.TRUE.equals(c.getVerificado()) ? "Sí" : "No");

    //Columnas visibles por defecto (las que pide el modulo)
    public static final List<ColumnaContacto> POR_DEFECTO = List.of(NOMBRE, CORREO, TELEFONO, FECHA_CREACION, PROPIETARIO, ESTATUS);

    private final String clave;
    private final String titulo;
    private final Function<ContactoResponseDTO, Object> valor;

    ColumnaContacto(String clave, String titulo, Function<ContactoResponseDTO, Object> valor) {
        this.clave = clave;
        this.titulo = titulo;
        this.valor = valor;
    }

    @Override public String getClave() { return clave; }
    @Override public String getTitulo() { return titulo; }
    @Override public Object valorDe(ContactoResponseDTO contacto) { return valor.apply(contacto); }

    public static List<ColumnaContacto> desdeClaves(List<String> claves) {
        return ColumnaExportable.desdeClaves(ColumnaContacto.class, claves, POR_DEFECTO);
    }
}
