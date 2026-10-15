package com.rifatrichani.backendinmobiliaria.services.impl;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.services.GaleriaService;
import jakarta.transaction.Transactional;

@Service 
public class GaleriaServiceImpl implements GaleriaService{
    private final GaleriaRepository galeriaRepository;
    private final InmuebleRepository inmuebleRepository;
    public GaleriaServiceImpl(GaleriaRepository gr, InmuebleRepository ir) {
        this.galeriaRepository = gr;
        this.inmuebleRepository = ir;
    }

    //Agregar imagen
    @Transactional
    @Override 
    public void agregarImagen(GaleriaRequesDTO dto) {
        Galeria galeria = new Galeria();    
        galeria.setUrlImagen(dto.getUrlImagen());
        galeria.setInmueble(inmuebleRepository.getReferenceById(dto.getInmuebleId()));
        galeriaRepository.save(galeria);
    }

    //Eliminar imagen
    @Transactional
    @Override 
    public void eliminarImagen(Integer imagenId) {
        galeriaRepository.deleteById(imagenId);
    }
}
