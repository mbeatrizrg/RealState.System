package com.rifatrichani.backendinmobiliaria.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* Filtros de la tabla del modulo Inmuebles del CRM (/api/propiedades). Todos opcionales, como query params:
   /api/propiedades?vista=disponibles&asesorIds=2&tipoInmuebleIds=1,3&ciudadIds=4&precioMin=50000&estados=disponible,reservado */
public class FiltroPropiedadDTO {
    public static final int TAMANO_MAXIMO = 100;

    //todos, mios (captados por el asesor autenticado) o disponibles
    @Pattern(regexp = "todos|mios|disponibles", message = "Vista inválida (todos, mios o disponibles)")
    private String vista = "todos";

    //Texto libre: titulo, codigo RRBR o ubicacion
    @Size(max = 100, message = "La búsqueda no puede exceder los 100 caracteres")
    private String q;

    @Size(max = 50, message = "Demasiados asesores en el filtro")
    private List<Integer> asesorIds;
    @Size(max = 30, message = "Demasiados tipos de inmueble en el filtro")
    private List<Integer> tipoInmuebleIds;
    //Zona: ciudades marcadas en la lista y/o texto en la ubicacion
    @Size(max = 50, message = "Demasiadas ciudades en el filtro")
    private List<Integer> ciudadIds;
    @Size(max = 100) private String zona;
    @Min(value = 0, message = "El precio mínimo no puede ser negativo") private Integer precioMin;
    @Min(value = 0, message = "El precio máximo no puede ser negativo") private Integer precioMax;
    @Size(max = 4)
    private List<@Pattern(regexp = "disponible|reservado|vendido|alquilado", message = "Estado inválido") String> estados;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate captadoDesde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate captadoHasta;

    // ----- Filtros avanzados -----
    @Size(max = 100) private String codigo;
    @Size(max = 150) private String dueno;
    @Pattern(regexp = "venta|alquiler", message = "Tipo de operación inválido (venta o alquiler)") private String operacion;
    @Min(0) private Integer habitacionesMin;
    @Min(0) private Integer banosMin;
    @Min(0) private BigDecimal metrosMin;
    @Min(0) private BigDecimal metrosMax;

    @Min(value = 0, message = "La página no puede ser negativa")
    private int pagina = 0;
    @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
    @Max(value = TAMANO_MAXIMO, message = "El tamaño de página no puede ser mayor a 100")
    private int tamano = 25;
    @Pattern(regexp = "fechaCaptacion|titulo|codigo|precio|estado|asesor|zona", message = "Orden inválido")
    private String orden = "fechaCaptacion";
    @Pattern(regexp = "asc|desc", message = "Dirección inválida (asc o desc)")
    private String direccion = "desc";

    public String getVista() { return vista; }
    public void setVista(String vista) { this.vista = vista; }
    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    public List<Integer> getAsesorIds() { return asesorIds; }
    public void setAsesorIds(List<Integer> asesorIds) { this.asesorIds = asesorIds; }
    public List<Integer> getTipoInmuebleIds() { return tipoInmuebleIds; }
    public void setTipoInmuebleIds(List<Integer> tipoInmuebleIds) { this.tipoInmuebleIds = tipoInmuebleIds; }
    public List<Integer> getCiudadIds() { return ciudadIds; }
    public void setCiudadIds(List<Integer> ciudadIds) { this.ciudadIds = ciudadIds; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public Integer getPrecioMin() { return precioMin; }
    public void setPrecioMin(Integer precioMin) { this.precioMin = precioMin; }
    public Integer getPrecioMax() { return precioMax; }
    public void setPrecioMax(Integer precioMax) { this.precioMax = precioMax; }
    public List<String> getEstados() { return estados; }
    public void setEstados(List<String> estados) { this.estados = estados; }
    public LocalDate getCaptadoDesde() { return captadoDesde; }
    public void setCaptadoDesde(LocalDate captadoDesde) { this.captadoDesde = captadoDesde; }
    public LocalDate getCaptadoHasta() { return captadoHasta; }
    public void setCaptadoHasta(LocalDate captadoHasta) { this.captadoHasta = captadoHasta; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDueno() { return dueno; }
    public void setDueno(String dueno) { this.dueno = dueno; }
    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }
    public Integer getHabitacionesMin() { return habitacionesMin; }
    public void setHabitacionesMin(Integer habitacionesMin) { this.habitacionesMin = habitacionesMin; }
    public Integer getBanosMin() { return banosMin; }
    public void setBanosMin(Integer banosMin) { this.banosMin = banosMin; }
    public BigDecimal getMetrosMin() { return metrosMin; }
    public void setMetrosMin(BigDecimal metrosMin) { this.metrosMin = metrosMin; }
    public BigDecimal getMetrosMax() { return metrosMax; }
    public void setMetrosMax(BigDecimal metrosMax) { this.metrosMax = metrosMax; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public int getTamano() { return tamano; }
    public void setTamano(int tamano) { this.tamano = tamano; }
    public String getOrden() { return orden; }
    public void setOrden(String orden) { this.orden = orden; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
