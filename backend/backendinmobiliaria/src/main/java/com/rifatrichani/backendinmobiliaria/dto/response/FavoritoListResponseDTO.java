package com.rifatrichani.backendinmobiliaria.dto.response;

//Inmueble guardado en favoritos por el usuario
public class FavoritoListResponseDTO {
    private Integer favoritoId;
    private Integer inmuebleId;
    private String titulo;
    private String imagenPrincipal;
    private String ciudad;
    private Integer precio;
    private Boolean isVendido;

    public Integer getFavoritoId() { return favoritoId; }
    public void setFavoritoId(Integer favoritoId) { this.favoritoId = favoritoId; }
    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getImagenPrincipal() { return imagenPrincipal; }
    public void setImagenPrincipal(String imagenPrincipal) { this.imagenPrincipal = imagenPrincipal; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
    public Boolean getIsVendido() { return isVendido; }
    public void setIsVendido(Boolean isVendido) { this.isVendido = isVendido; }
}
