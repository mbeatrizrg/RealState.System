package com.rifatrichani.backendinmobiliaria.services;

import java.util.Arrays;
import java.util.List;

/* Columna de una tabla del CRM que se puede exportar (ver impl/ExportadorTablas). La implementan los enums
   ColumnaContacto, ColumnaNegocio y ColumnaPropiedad. El valor puede ser texto, numero o LocalDateTime/LocalDate */
public interface ColumnaExportable<T> {
    String getClave();
    String getTitulo();
    Object valorDe(T fila);

    //Convierte las claves recibidas en columnas, en el mismo orden. Una clave desconocida es un error del cliente
    static <C extends Enum<C> & ColumnaExportable<?>> List<C> desdeClaves(Class<C> tipo, List<String> claves, List<C> porDefecto) {
        if (claves == null || claves.isEmpty()) return porDefecto;
        return claves.stream().distinct().map(clave -> Arrays.stream(tipo.getEnumConstants())
                .filter(c -> c.getClave().equals(clave))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Columna desconocida: " + clave)))
                .toList();
    }
}
