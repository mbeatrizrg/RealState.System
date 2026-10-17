package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import com.rifatrichani.backendinmobiliaria.dto.request.VistoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VistosResponseCrmDTO;

public interface VistoService {
    void registrarVista(VistoDTO dto);
    List<VistosResponseCrmDTO> obtenerVistosParaCrm();
    List<VistosResponseCrmDTO> obtenerVistosPorInmuebleCrm(Integer inmuebleId);
}