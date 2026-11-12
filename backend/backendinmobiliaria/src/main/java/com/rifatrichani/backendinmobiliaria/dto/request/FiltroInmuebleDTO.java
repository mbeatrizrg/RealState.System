package com.rifatrichani.backendinmobiliaria.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/* Filtros, busqueda por texto y paginacion de los listados de inmuebles (portal y CRM). Todos son opcionales.
   Se reciben como query params, ej: /api/inmueble/buscar?q=mirador&ciudadId=1&pagina=0&tamano=12&orden=precio_asc
   vendido y empleadoId solo los usa el CRM: en el portal el controlador los ignora */
public class FiltroInmuebleDTO {
    public static final int TAMANO_MAXIMO = 50;

    //Texto libre: se busca en titulo, ubicacion, descripcion, codigo RRBR y nombre de la ciudad
    @Size(max = 100, message = "La búsqueda no puede exceder los 100 caracteres")
    private String q;
    private Integer ciudadId;
    private Integer tipoInmuebleId;
    private Integer extraId;
    private Boolean isAlquiler;
    @PositiveOrZero(message = "El precio mínimo no puede ser negativo")
    private Integer minPrecio;
    @PositiveOrZero(message = "El precio máximo no puede ser negativo")
    private Integer maxPrecio;
    private Boolean vendido;
    private Integer empleadoId;

    @Min(value = 0, message = "La página no puede ser negativa")
    private int pagina = 0;
    @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
    @Max(value = TAMANO_MAXIMO, message = "El tamaño de página no puede ser mayor a 50")
    private int tamano = 12;
    //Lista cerrada: el orden nunca se arma con texto del cliente
    @Pattern(regexp = "recientes|precio_asc|precio_desc", message = "Orden inválido (recientes, precio_asc o precio_desc)")
    private String orden = "recientes";

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    public Integer getCiudadId() { return ciudadId; }
    public void setCiudadId(Integer ciudadId) { this.ciudadId = ciudadId; }
    public Integer getTipoInmuebleId() { return tipoInmuebleId; }
    public void setTipoInmuebleId(Integer tipoInmuebleId) { this.tipoInmuebleId = tipoInmuebleId; }
    public Integer getExtraId() { return extraId; }
    public void setExtraId(Integer extraId) { this.extraId = extraId; }
    public Boolean getIsAlquiler() { return isAlquiler; }
    public void setIsAlquiler(Boolean isAlquiler) { this.isAlquiler = isAlquiler; }
    public Integer getMinPrecio() { return minPrecio; }
    public void setMinPrecio(Integer minPrecio) { this.minPrecio = minPrecio; }
    public Integer getMaxPrecio() { return maxPrecio; }
    public void setMaxPrecio(Integer maxPrecio) { this.maxPrecio = maxPrecio; }
    public Boolean getVendido() { return vendido; }
    public void setVendido(Boolean vendido) { this.vendido = vendido; }
    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public int getTamano() { return tamano; }
    public void setTamano(int tamano) { this.tamano = tamano; }
    public String getOrden() { return orden; }
    public void setOrden(String orden) { this.orden = orden; }
}
