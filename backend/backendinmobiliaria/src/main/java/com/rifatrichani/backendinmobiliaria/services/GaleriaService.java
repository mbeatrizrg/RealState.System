package com.rifatrichani.backendinmobiliaria.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;

@Service
public class GaleriaService {
    private final GaleriaRepository galeriaRepository;
    private final InmuebleRepository inmuebleRepository;
    public GaleriaService(GaleriaRepository gr, InmuebleRepository ir) {
        this.galeriaRepository = gr;
        this.inmuebleRepository = ir;
    }

    //Agregar imagen
    @Transactional
    public void agregarImagen(GaleriaRequesDTO dto) {
        Galeria galeria = new Galeria();    
        galeria.setUrlImagen(dto.getUrlImagen());
        galeria.setInmueble(inmuebleRepository.getReferenceById(dto.getInmuebleId()));
        galeriaRepository.save(galeria);
    }

    //Eliminar imagen
    @Transactional
    public void eliminarImagen(Integer imagenId) {
        galeriaRepository.deleteById(imagenId);
    }
}