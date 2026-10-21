package com.rifatrichani.backendinmobiliaria.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

//El vendedor registra la venta desde su vista. La comision la asigna despues el administrador
public class VentaRequestDTO {
    @NotNull(message = "Debe indicar el inmueble vendido.")
    private Integer inmuebleId;
    @NotNull(message = "Debe especificar el monto final de la venta.")
    @Positive(message = "El monto final debe ser mayor a cero.")
    private BigDecimal montoFinal;
    //Numero de referencia o URL del comprobante de pago
    @NotBlank(message = "Debe indicar el comprobante de pago.")
    @Size(max = 500, message = "El comprobante no puede exceder los 500 caracteres.")
    private String comprobante;

    public Integer getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(Integer inmuebleId) { this.inmuebleId = inmuebleId; }
    public BigDecimal getMontoFinal() { return montoFinal; }
    public void setMontoFinal(BigDecimal montoFinal) { this.montoFinal = montoFinal; }
    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }
}
