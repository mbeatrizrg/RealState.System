package com.rifatrichani.backendinmobiliaria.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
//Este registro de venta lo hace el mimso vendedor desde su vista
//Por eso no es necesario poner id_vendedor, ni la comision ya que esto lo haría el mismo administrador
public class VentaRequestDTO {
    @NotNull (message = "Error del sistema: No se recibió la referencia del inmueble vendido.")
    private Integer inmueble;
    @NotNull (message = "Error del sistema: No se recibió la referencia del cliente (usuario).")
    private Long usuario;
    @NotNull (message = "Debe especificar el monto final de la venta.")
    @Positive (message = "El monto final debe ser mayor a cero.")
    private BigDecimal montoFinal;
    public Integer getInmueble() {
        return inmueble;
    }
    public void setInmueble(Integer inmueble) {
        this.inmueble = inmueble;
    }
    public Long getUsuario() {
        return usuario;
    }
    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }
    public BigDecimal getMontoFinal() {
        return montoFinal;
    }
    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }
    

    
}
