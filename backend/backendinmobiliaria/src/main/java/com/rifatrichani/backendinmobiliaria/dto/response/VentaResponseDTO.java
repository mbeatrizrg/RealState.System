package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
//Consulta de venta
public class VentaResponseDTO {
    private Long id;
    private Integer inmuebleId;
    private String tituloInmueble;
    private String rrbrInmueble;
    private String nombreVendedor;
    private Long usuarioId;
    private String nombreComprador;
    private BigDecimal montoFinal;
    private BigDecimal comision;
    private LocalDateTime fechaVenta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
    public String getTituloInmueble() { return tituloInmueble; }
    public void setTituloInmueble(String tituloInmueble) { this.tituloInmueble = tituloInmueble; }
    public String getRrbrInmueble() { return rrbrInmueble; }
    public void setRrbrInmueble(String rrbrInmueble) { this.rrbrInmueble = rrbrInmueble; }
    public String getNombreVendedor() { return nombreVendedor; }
    public void setNombreVendedor(String nombreVendedor) { this.nombreVendedor = nombreVendedor; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getNombreComprador() { return nombreComprador; }
    public void setNombreComprador(String nombreComprador) { this.nombreComprador = nombreComprador; }
    public BigDecimal getMontoFinal() { return montoFinal; }
    public void setMontoFinal(BigDecimal montoFinal) { this.montoFinal = montoFinal; }
    public BigDecimal getComision() { return comision; }
    public void setComision(BigDecimal comision) { this.comision = comision; }
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
}