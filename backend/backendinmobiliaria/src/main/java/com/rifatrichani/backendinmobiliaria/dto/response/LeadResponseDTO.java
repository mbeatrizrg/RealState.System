package com.rifatrichani.backendinmobiliaria.dto.response;

public class LeadResponseDTO {
    private Integer id;
    private Integer usuarioId;
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private Integer inmuebleId;
    private String tituloInmueble;
    private String rrbrInmueble;
    private Integer empleadoId;
    private String nombreVendedor;
    private Integer estatus;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getCorreoCliente() { return correoCliente; }
    public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }
    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }
    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
    public String getTituloInmueble() { return tituloInmueble; }
    public void setTituloInmueble(String tituloInmueble) { this.tituloInmueble = tituloInmueble; }
    public String getRrbrInmueble() { return rrbrInmueble; }
    public void setRrbrInmueble(String rrbrInmueble) { this.rrbrInmueble = rrbrInmueble; }
    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }
    public String getNombreVendedor() { return nombreVendedor; }
    public void setNombreVendedor(String nombreVendedor) { this.nombreVendedor = nombreVendedor; }
    public Integer getEstatus() { return estatus; }
    public void setEstatus(Integer estatus) { this.estatus = estatus; }
}
