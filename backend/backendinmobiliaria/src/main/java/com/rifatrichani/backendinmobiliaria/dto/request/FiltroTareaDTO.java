package com.rifatrichani.backendinmobiliaria.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* Filtros de la tabla del modulo de Tareas (/api/tareas). Todos opcionales, como query params:
   /api/tareas?vista=mias_pendientes&tiposTarea=VISITA,LLAMADA&estados=vencida&prioridades=alta&venceHasta=2026-10-31
   estados acepta "vencida": tarea sin completar cuya fecha de vencimiento ya paso */
public class FiltroTareaDTO {
    public static final int TAMANO_MAXIMO = 100;
    //En propietarioIds significa "sin propietario"
    public static final int SIN_VALOR = 0;

    @Pattern(regexp = "todas|mias_pendientes|completadas_vencidas", message = "Vista inválida (todas, mias_pendientes o completadas_vencidas)")
    private String vista = "todas";

    //Texto libre: titulo, descripcion o nombre del contacto
    @Size(max = 100, message = "La búsqueda no puede exceder los 100 caracteres")
    private String q;

    @Size(max = 50, message = "Demasiados propietarios en el filtro")
    private List<Integer> propietarioIds;

    @Size(max = 5)
    private List<@Pattern(regexp = "LLAMADA|VISITA|ENVIO|REUNION|TRAMITE", message = "Tipo de tarea inválido") String> tiposTarea;

    @Size(max = 4)
    private List<@Pattern(regexp = "pendiente|en_proceso|vencida|completada", message = "Estado de tarea inválido") String> estados;

    @Size(max = 3)
    private List<@Pattern(regexp = "baja|media|alta", message = "Prioridad inválida") String> prioridades;

    //Fecha de vencimiento (rango de dias completos)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate venceDesde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate venceHasta;

    // ----- Filtros avanzados -----
    @Size(max = 100) private String contacto;
    @Size(max = 100) private String codigoInmueble;
    @Size(max = 150) private String negocio;
    //true: solo tareas sin contacto asociado
    private Boolean sinContacto;
    private Integer contactoId;
    private Integer inmuebleId;
    private Integer negocioId;

    @Min(value = 0, message = "La página no puede ser negativa")
    private int pagina = 0;
    @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
    @Max(value = TAMANO_MAXIMO, message = "El tamaño de página no puede ser mayor a 100")
    private int tamano = 25;
    @Pattern(regexp = "fechaProgramada|titulo|prioridad|estado|propietario|contacto|fechaCreacion", message = "Orden inválido")
    private String orden = "fechaProgramada";
    @Pattern(regexp = "asc|desc", message = "Dirección inválida (asc o desc)")
    private String direccion = "asc";

    public String getVista() { return vista; }
    public void setVista(String vista) { this.vista = vista; }
    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    public List<Integer> getPropietarioIds() { return propietarioIds; }
    public void setPropietarioIds(List<Integer> propietarioIds) { this.propietarioIds = propietarioIds; }
    public List<String> getTiposTarea() { return tiposTarea; }
    public void setTiposTarea(List<String> tiposTarea) { this.tiposTarea = tiposTarea; }
    public List<String> getEstados() { return estados; }
    public void setEstados(List<String> estados) { this.estados = estados; }
    public List<String> getPrioridades() { return prioridades; }
    public void setPrioridades(List<String> prioridades) { this.prioridades = prioridades; }
    public LocalDate getVenceDesde() { return venceDesde; }
    public void setVenceDesde(LocalDate venceDesde) { this.venceDesde = venceDesde; }
    public LocalDate getVenceHasta() { return venceHasta; }
    public void setVenceHasta(LocalDate venceHasta) { this.venceHasta = venceHasta; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getCodigoInmueble() { return codigoInmueble; }
    public void setCodigoInmueble(String codigoInmueble) { this.codigoInmueble = codigoInmueble; }
    public String getNegocio() { return negocio; }
    public void setNegocio(String negocio) { this.negocio = negocio; }
    public Boolean getSinContacto() { return sinContacto; }
    public void setSinContacto(Boolean sinContacto) { this.sinContacto = sinContacto; }
    public Integer getContactoId() { return contactoId; }
    public void setContactoId(Integer contactoId) { this.contactoId = contactoId; }
    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
    public Integer getNegocioId() { return negocioId; }
    public void setNegocioId(Integer negocioId) { this.negocioId = negocioId; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public int getTamano() { return tamano; }
    public void setTamano(int tamano) { this.tamano = tamano; }
    public String getOrden() { return orden; }
    public void setOrden(String orden) { this.orden = orden; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
