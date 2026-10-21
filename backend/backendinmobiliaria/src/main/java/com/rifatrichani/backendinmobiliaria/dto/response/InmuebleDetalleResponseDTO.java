package com.rifatrichani.backendinmobiliaria.dto.response;

import java.util.List;

//Vista detallada de un inmueble (portal y CRM)
public class InmuebleDetalleResponseDTO {
    private Integer id;
    private String titulo;
    private Integer precio;
    private String descripcion;
    private String rrbr;
    private String ubicacion;
    private Boolean isAlquiler;
    private Boolean isVendido;
    private String nombreCiudad;
    private String nombreTipoInmueble;
    private String nombreEstatus;
    private String nombreEmpleado;
    //Lista para mantener el formato del frontend, aunque en Supabase cada inmueble tiene un solo extra
    private List<String> extras;
    private List<String> urlsGaleria;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getRrbr() { return rrbr; }
    public void setRrbr(String rrbr) { this.rrbr = rrbr; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Boolean getIsAlquiler() { return isAlquiler; }
    public void setIsAlquiler(Boolean isAlquiler) { this.isAlquiler = isAlquiler; }
    public Boolean getIsVendido() { return isVendido; }
    public void setIsVendido(Boolean isVendido) { this.isVendido = isVendido; }
    public String getNombreCiudad() { return nombreCiudad; }
    public void setNombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }
    public String getNombreTipoInmueble() { return nombreTipoInmueble; }
    public void setNombreTipoInmueble(String nombreTipoInmueble) { this.nombreTipoInmueble = nombreTipoInmueble; }
    public String getNombreEstatus() { return nombreEstatus; }
    public void setNombreEstatus(String nombreEstatus) { this.nombreEstatus = nombreEstatus; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }
    public List<String> getExtras() { return extras; }
    public void setExtras(List<String> extras) { this.extras = extras; }
    public List<String> getUrlsGaleria() { return urlsGaleria; }
    public void setUrlsGaleria(List<String> urlsGaleria) { this.urlsGaleria = urlsGaleria; }
}
