package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import com.rifatrichani.backendinmobiliaria.dto.response.VistosResponseCrmDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

public interface VistoService {
    void registrarVista(Integer inmuebleId, Integer idUsuario);
    //Admin: todas las vistas. Vendedor: las de sus inmuebles
    List<VistosResponseCrmDTO> obtenerVistosParaCrm(CuentaAutenticada empleado);
    List<VistosResponseCrmDTO> obtenerVistosPorInmuebleCrm(Integer inmuebleId, CuentaAutenticada empleado);
}
