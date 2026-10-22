package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ImagenResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;

public interface  GaleriaService {
    //Cantidad maxima de fotos por inmueble (la primera es la principal)
    int MAX_IMAGENES_POR_INMUEBLE = 6;

    public Integer agregarImagen(GaleriaRequesDTO dto, CuentaAutenticada empleado);
    public List<ImagenResponseDTO> subirImagenes(Integer inmuebleId, List<MultipartFile> archivos, CuentaAutenticada empleado);
    public void eliminarImagen(Integer imagenId, CuentaAutenticada empleado);
}
