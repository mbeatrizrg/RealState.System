package com.rifatrichani.backendinmobiliaria.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* Filtros del modulo de Estadisticas (/api/estadisticas). Todos opcionales, como query params:
   /api/estadisticas?vendedorIds=2&desde=2026-01-01&hasta=2026-10-31&tipoInmuebleIds=1&ciudadIds=3&operacion=venta
   vendedorIds: en la interaccion con inmuebles es el asesor encargado del inmueble; en el rendimiento de ventas es el
   propietario del negocio. nivel filtra la tabla de inmuebles por la porcion elegida en la grafica de pastel */
public class FiltroEstadisticaDTO {
    @Size(max = 50, message = "Demasiados vendedores en el filtro")
    private List<Integer> vendedorIds;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate desde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hasta;

    @Size(max = 30, message = "Demasiados tipos de inmueble en el filtro")
    private List<Integer> tipoInmuebleIds;
    @Size(max = 50, message = "Demasiadas ciudades en el filtro")
    private List<Integer> ciudadIds;
    @Size(max = 100) private String zona;
    @Pattern(regexp = "venta|alquiler", message = "Tipo de operación inválido (venta o alquiler)") private String operacion;

    // ----- Filtros avanzados -----
    @Min(value = 0, message = "El precio mínimo no puede ser negativo") private Integer precioMin;
    @Min(value = 0, message = "El precio máximo no puede ser negativo") private Integer precioMax;
    @Size(max = 4)
    private List<@Pattern(regexp = "disponible|reservado|vendido|alquilado", message = "Estado inválido") String> estados;
    @Size(max = 100) private String codigo;

    @Pattern(regexp = "INFORMACION|AGENDO|VISITO|OFERTA|CERRO|DESCARTO", message = "Nivel de interacción inválido")
    private String nivel;

    //Agrupacion de la grafica de barras/lineas: tiempo, zona o propiedad
    @Pattern(regexp = "tiempo|zona|propiedad", message = "Agrupación inválida (tiempo, zona o propiedad)")
    private String agrupacion = "tiempo";

    public List<Integer> getVendedorIds() { return vendedorIds; }
    public void setVendedorIds(List<Integer> vendedorIds) { this.vendedorIds = vendedorIds; }
    public LocalDate getDesde() { return desde; }
    public void setDesde(LocalDate desde) { this.desde = desde; }
    public LocalDate getHasta() { return hasta; }
    public void setHasta(LocalDate hasta) { this.hasta = hasta; }
    public List<Integer> getTipoInmuebleIds() { return tipoInmuebleIds; }
    public void setTipoInmuebleIds(List<Integer> tipoInmuebleIds) { this.tipoInmuebleIds = tipoInmuebleIds; }
    public List<Integer> getCiudadIds() { return ciudadIds; }
    public void setCiudadIds(List<Integer> ciudadIds) { this.ciudadIds = ciudadIds; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }
    public Integer getPrecioMin() { return precioMin; }
    public void setPrecioMin(Integer precioMin) { this.precioMin = precioMin; }
    public Integer getPrecioMax() { return precioMax; }
    public void setPrecioMax(Integer precioMax) { this.precioMax = precioMax; }
    public List<String> getEstados() { return estados; }
    public void setEstados(List<String> estados) { this.estados = estados; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getAgrupacion() { return agrupacion; }
    public void setAgrupacion(String agrupacion) { this.agrupacion = agrupacion; }
}
