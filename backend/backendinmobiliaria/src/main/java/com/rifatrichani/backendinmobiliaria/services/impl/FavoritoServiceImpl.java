package com.rifatrichani.backendinmobiliaria.services.impl;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.FavoritoRequestDTO;
import com.rifatrichani.backendinmobiliaria.models.Favorito;
import com.rifatrichani.backendinmobiliaria.repositories.FavoritoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.services.FavoritoService;
import jakarta.transaction.Transactional;

@Service 
public class FavoritoServiceImpl implements FavoritoService{
        private final FavoritoRepository favoritoRepository;
    private final InmuebleRepository inmuebleRepository;
    private final UsuarioRepository usuarioRepository;
    public FavoritoServiceImpl(FavoritoRepository fr, InmuebleRepository ir, UsuarioRepository ur){
        this.favoritoRepository = fr;
        this.inmuebleRepository = ir;
        this.usuarioRepository = ur;
    }

    //Guarda inmueble como favorito
    @Transactional 
    @Override 
    public void guardarFavorito(FavoritoRequestDTO dto){
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuarioRepository.getReferenceById(dto.getUsuarioId()));
        favorito.setInmueble(inmuebleRepository.getReferenceById(dto.getInmuebleId()));
        favoritoRepository.save(favorito);
    }

    //Elimina inmueble como favorito
    @Transactional 
    @Override 
    public void eliminarFavortio(Integer favoritoId){
        if (favoritoRepository.existsById(favoritoId)){
            favoritoRepository.deleteById(favoritoId);
        }
    }
}
