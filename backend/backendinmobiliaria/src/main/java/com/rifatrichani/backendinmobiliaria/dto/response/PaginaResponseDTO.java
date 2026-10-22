package com.rifatrichani.backendinmobiliaria.dto.response;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/* Respuesta paginada estable para el frontend. No se devuelve el Page de Spring directamente porque su
   formato JSON cambia entre versiones. "pagina" empieza en 0 */
public class PaginaResponseDTO<T> {
    private List<T> contenido;
    private int pagina;
    private int tamano;
    private long totalElementos;
    private int totalPaginas;

    public static <E, T> PaginaResponseDTO<T> de(Page<E> page, Function<List<E>, List<T>> mapeo) {
        PaginaResponseDTO<T> dto = new PaginaResponseDTO<>();
        dto.contenido = mapeo.apply(page.getContent());
        dto.pagina = page.getNumber();
        dto.tamano = page.getSize();
        dto.totalElementos = page.getTotalElements();
        dto.totalPaginas = page.getTotalPages();
        return dto;
    }

    public List<T> getContenido() { return contenido; }
    public int getPagina() { return pagina; }
    public int getTamano() { return tamano; }
    public long getTotalElementos() { return totalElementos; }
    public int getTotalPaginas() { return totalPaginas; }
}
