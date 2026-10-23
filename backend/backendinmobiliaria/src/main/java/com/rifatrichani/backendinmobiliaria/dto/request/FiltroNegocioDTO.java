package com.rifatrichani.backendinmobiliaria.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* Filtros, busqueda y paginacion de la tabla y el tablero de negocios. Todos opcionales, como query params:
   /api/negocios?vista=mios&etapas=1,2&tiposOperacion=venta&cierreDesde=2026-11-01&orden=valor&direccion=desc
   Las fechas son yyyy-MM-dd y los rangos incluyen ambos extremos */
public class FiltroNegocioDTO {
    public static final int TAMANO_MAXIMO = 100;
    //En propietarioIds significa "sin propietario"
    public static final int SIN_VALOR = 0;

    @Pattern(regexp = "todos|mios|sin_asignar", message = "Vista inválida (todos, mios o sin_asignar)")
    private String vista = "todos";

    //Texto libre: nombre del negocio, contacto, titulo o codigo del inmueble
    @Size(max = 100, message = "La búsqueda no puede exceder los 100 caracteres")
    private String q;

    @Size(max = 50, message = "Demasiados propietarios en el filtro")
    private List<Integer> propietarioIds;
    @Size(max = 20, message = "Demasiadas etapas en el filtro")
    private List<Integer> etapas;
    @Size(max = 2)
    private List<@Pattern(regexp = "venta|alquiler", message = "Tipo de operación inválido (venta o alquiler)") String> tiposOperacion;
    @Size(max = 30, message = "Demasiados tipos de inmueble en el filtro")
    private List<Integer> tipoInmuebleIds;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate creadoDesde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate creadoHasta;
    //Fecha estimada de cierre
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate cierreDesde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate cierreHasta;

    // ----- Filtros avanzados -----
    @Min(value = 0, message = "El valor mínimo no puede ser negativo") private BigDecimal valorMin;
    @Min(value = 0, message = "El valor máximo no puede ser negativo") private BigDecimal valorMax;
    @Size(max = 100) private String contacto;
    @Size(max = 100) private String codigoInmueble;
    //true: solo negocios en etapas abiertas; false: solo cerrados
    private Boolean abiertos;
    //Fichas del contacto y del inmueble
    private Integer contactoId;
    private Integer inmuebleId;

    @Min(value = 0, message = "La página no puede ser negativa")
    private int pagina = 0;
    @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
    @Max(value = TAMANO_MAXIMO, message = "El tamaño de página no puede ser mayor a 100")
    private int tamano = 25;
    @Pattern(regexp = "fechaCreacion|nombre|valor|fechaCierre|etapa|propietario|contacto", message = "Orden inválido")
    private String orden = "fechaCreacion";
    @Pattern(regexp = "asc|desc", message = "Dirección inválida (asc o desc)")
    private String direccion = "desc";

    public String getVista() { return vista; }
    public void setVista(String vista) { this.vista = vista; }
    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    public List<Integer> getPropietarioIds() { return propietarioIds; }
    public void setPropietarioIds(List<Integer> propietarioIds) { this.propietarioIds = propietarioIds; }
    public List<Integer> getEtapas() { return etapas; }
    public void setEtapas(List<Integer> etapas) { this.etapas = etapas; }
    public List<String> getTiposOperacion() { return tiposOperacion; }
    public void setTiposOperacion(List<String> tiposOperacion) { this.tiposOperacion = tiposOperacion; }
    public List<Integer> getTipoInmuebleIds() { return tipoInmuebleIds; }
    public void setTipoInmuebleIds(List<Integer> tipoInmuebleIds) { this.tipoInmuebleIds = tipoInmuebleIds; }
    public LocalDate getCreadoDesde() { return creadoDesde; }
    public void setCreadoDesde(LocalDate creadoDesde) { this.creadoDesde = creadoDesde; }
    public LocalDate getCreadoHasta() { return creadoHasta; }
    public void setCreadoHasta(LocalDate creadoHasta) { this.creadoHasta = creadoHasta; }
    public LocalDate getCierreDesde() { return cierreDesde; }
    public void setCierreDesde(LocalDate cierreDesde) { this.cierreDesde = cierreDesde; }
    public LocalDate getCierreHasta() { return cierreHasta; }
    public void setCierreHasta(LocalDate cierreHasta) { this.cierreHasta = cierreHasta; }
    public BigDecimal getValorMin() { return valorMin; }
    public void setValorMin(BigDecimal valorMin) { this.valorMin = valorMin; }
    public BigDecimal getValorMax() { return valorMax; }
    public void setValorMax(BigDecimal valorMax) { this.valorMax = valorMax; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getCodigoInmueble() { return codigoInmueble; }
    public void setCodigoInmueble(String codigoInmueble) { this.codigoInmueble = codigoInmueble; }
    public Boolean getAbiertos() { return abiertos; }
    public void setAbiertos(Boolean abiertos) { this.abiertos = abiertos; }
    public Integer getContactoId() { return contactoId; }
    public void setContactoId(Integer contactoId) { this.contactoId = contactoId; }
    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public int getTamano() { return tamano; }
    public void setTamano(int tamano) { this.tamano = tamano; }
    public String getOrden() { return orden; }
    public void setOrden(String orden) { this.orden = orden; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
