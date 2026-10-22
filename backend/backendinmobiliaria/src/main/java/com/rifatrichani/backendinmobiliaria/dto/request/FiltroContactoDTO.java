package com.rifatrichani.backendinmobiliaria.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* Filtros, busqueda y paginacion del listado de contactos del CRM. Todos son opcionales y se reciben como query params:
   /api/contactos?vista=mios&q=ana&propietarioIds=2,3&estatus=1,0&creadoDesde=2026-10-01&pagina=0&tamano=25&orden=nombre&direccion=asc
   Las fechas son yyyy-MM-dd y los rangos incluyen ambos extremos */
public class FiltroContactoDTO {
    public static final int TAMANO_MAXIMO = 100;
    //En propietarioIds significa "sin propietario"; en estatus significa "sin ningun lead"
    public static final int SIN_VALOR = 0;

    //Pestaña: todos, mios (propietario = empleado autenticado) o sin_asignar
    @Pattern(regexp = "todos|mios|sin_asignar", message = "Vista inválida (todos, mios o sin_asignar)")
    private String vista = "todos";

    //Texto libre: nombre, apellido, correo o telefono
    @Size(max = 100, message = "La búsqueda no puede exceder los 100 caracteres")
    private String q;

    @Size(max = 50, message = "Demasiados propietarios en el filtro")
    private List<Integer> propietarioIds;
    //Estatus del lead mas reciente del contacto
    @Size(max = 20, message = "Demasiados estatus en el filtro")
    private List<Integer> estatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate creadoDesde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate creadoHasta;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate actividadDesde;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate actividadHasta;
    //true: solo contactos que nunca tuvieron actividad
    private Boolean sinActividad;

    // ----- Filtros avanzados -----
    @Size(max = 100) private String nombre;
    @Size(max = 255) private String correo;
    @Size(max = 30) private String telefono;
    private Integer cedula;
    @Size(max = 50) private String tipoNegocio;
    private Boolean verificado;

    @Min(value = 0, message = "La página no puede ser negativa")
    private int pagina = 0;
    @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
    @Max(value = TAMANO_MAXIMO, message = "El tamaño de página no puede ser mayor a 100")
    private int tamano = 25;
    //Lista cerrada: el orden nunca se arma con texto del cliente
    @Pattern(regexp = "fechaCreacion|nombre|correo|telefono|ultimaActividad|propietario", message = "Orden inválido")
    private String orden = "fechaCreacion";
    @Pattern(regexp = "asc|desc", message = "Dirección inválida (asc o desc)")
    private String direccion = "desc";

    public String getVista() { return vista; }
    public void setVista(String vista) { this.vista = vista; }
    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    public List<Integer> getPropietarioIds() { return propietarioIds; }
    public void setPropietarioIds(List<Integer> propietarioIds) { this.propietarioIds = propietarioIds; }
    public List<Integer> getEstatus() { return estatus; }
    public void setEstatus(List<Integer> estatus) { this.estatus = estatus; }
    public LocalDate getCreadoDesde() { return creadoDesde; }
    public void setCreadoDesde(LocalDate creadoDesde) { this.creadoDesde = creadoDesde; }
    public LocalDate getCreadoHasta() { return creadoHasta; }
    public void setCreadoHasta(LocalDate creadoHasta) { this.creadoHasta = creadoHasta; }
    public LocalDate getActividadDesde() { return actividadDesde; }
    public void setActividadDesde(LocalDate actividadDesde) { this.actividadDesde = actividadDesde; }
    public LocalDate getActividadHasta() { return actividadHasta; }
    public void setActividadHasta(LocalDate actividadHasta) { this.actividadHasta = actividadHasta; }
    public Boolean getSinActividad() { return sinActividad; }
    public void setSinActividad(Boolean sinActividad) { this.sinActividad = sinActividad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Integer getCedula() { return cedula; }
    public void setCedula(Integer cedula) { this.cedula = cedula; }
    public String getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(String tipoNegocio) { this.tipoNegocio = tipoNegocio; }
    public Boolean getVerificado() { return verificado; }
    public void setVerificado(Boolean verificado) { this.verificado = verificado; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public int getTamano() { return tamano; }
    public void setTamano(int tamano) { this.tamano = tamano; }
    public String getOrden() { return orden; }
    public void setOrden(String orden) { this.orden = orden; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
