package com.rifatrichani.backendinmobiliaria.services.impl;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.VistoDTO;
import com.rifatrichani.backendinmobiliaria.models.Visto;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.VistoRepository;
import com.rifatrichani.backendinmobiliaria.services.VistoService;

import jakarta.transaction.Transactional;

@Service 
public class VistoServiceImpl implements VistoService{
    private final VistoRepository vistoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InmuebleRepository inmuebleRepository;
    public VistoServiceImpl(VistoRepository vr, UsuarioRepository ur, InmuebleRepository ir) {
        this.vistoRepository = vr;
        this.usuarioRepository = ur;
        this.inmuebleRepository = ir;
    }
    //Se registra la vista validando si el usuario esta registrado o no
    @Transactional
    @Override 
    public void registrarVista(VistoDTO dto) {
        Visto visto = new Visto();
        visto.setInmueble(inmuebleRepository.getReferenceById(dto.getInmueble())); 
        if (dto.getUsuario() != null) {  
            visto.setUsuario(usuarioRepository.getReferenceById(dto.getUsuario()));
        } else {
            visto.setUsuario(null); // Es un visitante no registrado
        }
        vistoRepository.save(visto);
    }
}
