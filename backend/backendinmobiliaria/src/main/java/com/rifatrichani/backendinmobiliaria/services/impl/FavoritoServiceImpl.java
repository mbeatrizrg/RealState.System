package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.response.FavoritoListResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Favorito;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.repositories.FavoritoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.services.FavoritoService;

@Service
public class FavoritoServiceImpl implements FavoritoService {
    private final FavoritoRepository favoritoRepository;
    private final InmuebleRepository inmuebleRepository;
    private final UsuarioRepository usuarioRepository;
    private final GaleriaRepository galeriaRepository;

    public FavoritoServiceImpl(FavoritoRepository fr, InmuebleRepository ir, UsuarioRepository ur, GaleriaRepository gr) {
        this.favoritoRepository = fr;
        this.inmuebleRepository = ir;
        this.usuarioRepository = ur;
        this.galeriaRepository = gr;
    }

    //Si el inmueble ya estuvo en favoritos se reactiva la fila existente (no se duplican filas)
    @Override
    @Transactional
    public void guardarFavorito(Integer idUsuario, Integer inmuebleId) {
        if (!inmuebleRepository.existsById(inmuebleId)) {
            throw new ResourceNotFoundException("Inmueble no encontrado con ID: " + inmuebleId);
        }
        Favorito favorito = favoritoRepository.findFirstByUsuarioIdAndInmuebleId(idUsuario, inmuebleId).orElseGet(() -> {
            Favorito nuevo = new Favorito();
            nuevo.setUsuario(usuarioRepository.getReferenceById(idUsuario));
            nuevo.setInmueble(inmuebleRepository.getReferenceById(inmuebleId));
            return nuevo;
        });
        favorito.setFavorito(true);
        favoritoRepository.save(favorito);
    }

    @Override
    @Transactional
    public void quitarFavorito(Integer idUsuario, Integer inmuebleId) {
        favoritoRepository.findFirstByUsuarioIdAndInmuebleId(idUsuario, inmuebleId).ifPresent(favorito -> {
            favorito.setFavorito(false);
            favoritoRepository.save(favorito);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoritoListResponseDTO> obtenerFavoritosPorUsuario(Integer idUsuario) {
        return favoritoRepository.findByUsuarioIdAndFavoritoTrueOrderByIdDesc(idUsuario).stream()
                .map(fav -> {
                    Inmueble inm = fav.getInmueble();
                    FavoritoListResponseDTO dto = new FavoritoListResponseDTO();
                    dto.setFavoritoId(fav.getId());
                    dto.setInmuebleId(inm.getId());
                    dto.setTitulo(inm.getTitulo());
                    dto.setCiudad(inm.getCiudad() != null ? inm.getCiudad().getNombre() : null);
                    dto.setPrecio(inm.getPrecio());
                    dto.setIsVendido(inm.getVendido());
                    dto.setImagenPrincipal(galeriaRepository.findFirstByInmuebleIdOrderByIdAsc(inm.getId())
                            .map(Galeria::getUrlImagen)
                            .orElse(null));
                    return dto;
                }).collect(Collectors.toList());
    }
}
