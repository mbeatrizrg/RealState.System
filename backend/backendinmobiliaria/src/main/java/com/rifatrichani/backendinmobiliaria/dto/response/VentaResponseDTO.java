package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaResponseDTO {
    private Integer id;
    private Integer inmuebleId;
    private String tituloInmueble;
    private String rrbrInmueble;
    private Integer empleadoId;
    private String nombreVendedor;
    private BigDecimal montoFinal;
    private BigDecimal comision;
    private String comprobante;
    private LocalDateTime fechaVenta;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
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
    public BigDecimal getMontoFinal() { return montoFinal; }
    public void setMontoFinal(BigDecimal montoFinal) { this.montoFinal = montoFinal; }
    public BigDecimal getComision() { return comision; }
    public void setComision(BigDecimal comision) { this.comision = comision; }
    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
}
