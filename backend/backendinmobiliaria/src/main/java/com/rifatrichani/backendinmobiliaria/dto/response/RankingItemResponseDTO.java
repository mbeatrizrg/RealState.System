package com.rifatrichani.backendinmobiliaria.dto.response;

import java.math.BigDecimal;

public class RankingItemResponseDTO {
    private Long referenciaId;       // ID del Inmueble, Empleado, Ciudad o TipoInmueble
    private String nombrePrincipal;  // Título del inmueble, Nombre del Vendedor o Nombre de la Ciudad
    private String detalleExtra;     // Ej: Ciudad/RRBR del inmueble, o Correo del vendedor
    private Long cantidadTotal;      // Total de vistas, total de ventas cerradas o total de favoritos
    private BigDecimal montoTotal;   // Suma de dinero vendido (usado en el ranking de vendedores), null en otros casos
    
    public RankingItemResponseDTO() {} //Constructor vacío requerido por Jackson para deserialización
    public RankingItemResponseDTO(Long referenciaId, String nombrePrincipal, String detalleExtra, Long cantidadTotal, BigDecimal montoTotal) {
        this.referenciaId = referenciaId;
        this.nombrePrincipal = nombrePrincipal;
        this.detalleExtra = detalleExtra;
        this.cantidadTotal = cantidadTotal;
        this.montoTotal = montoTotal;
    }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }
    public String getNombrePrincipal() { return nombrePrincipal; }
    public void setNombrePrincipal(String nombrePrincipal) { this.nombrePrincipal = nombrePrincipal; }
    public String getDetalleExtra() { return detalleExtra; }
    public void setDetalleExtra(String detalleExtra) { this.detalleExtra = detalleExtra; }
    public Long getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(Long cantidadTotal) { this.cantidadTotal = cantidadTotal; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
}