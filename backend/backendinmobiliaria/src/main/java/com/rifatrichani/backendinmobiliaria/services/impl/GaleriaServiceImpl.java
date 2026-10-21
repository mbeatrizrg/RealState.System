package com.rifatrichani.backendinmobiliaria.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.security.ControlAcceso;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.GaleriaService;

@Service
public class GaleriaServiceImpl implements GaleriaService{
    private final GaleriaRepository galeriaRepository;
    private final InmuebleRepository inmuebleRepository;
    public GaleriaServiceImpl(GaleriaRepository gr, InmuebleRepository ir) {
        this.galeriaRepository = gr;
        this.inmuebleRepository = ir;
    }

    //Solo el vendedor encargado del inmueble o un administrador puede cambiar sus fotos
    @Transactional
    @Override
    public Integer agregarImagen(GaleriaRequesDTO dto, CuentaAutenticada empleado) {
        Inmueble inmueble = inmuebleRepository.findById(dto.getInmuebleId())
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + dto.getInmuebleId()));
        ControlAcceso.verificarEncargado(inmueble, empleado);
        Galeria galeria = new Galeria();
        galeria.setUrlImagen(dto.getUrlImagen().trim());
        galeria.setInmueble(inmueble);
        return galeriaRepository.save(galeria).getId();
    }

    @Transactional
    @Override
    public void eliminarImagen(Integer imagenId, CuentaAutenticada empleado) {
        Galeria galeria = galeriaRepository.findById(imagenId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + imagenId));
        ControlAcceso.verificarEncargado(galeria.getInmueble(), empleado);
        galeriaRepository.delete(galeria);
    }
}
