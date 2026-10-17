package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.FavoritoRequestDTO;
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
import jakarta.transaction.Transactional;

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
    @Transactional
    @Override
    public void guardarFavorito(FavoritoRequestDTO dto) {
        // Evitamos que el usuario guarde duplicado el mismo inmueble en favoritos
        if (favoritoRepository.existsByUsuarioIdAndInmuebleId(dto.getUsuarioId(), dto.getInmuebleId())) {
            throw new IllegalArgumentException("Este inmueble ya se encuentra en tu lista de favoritos.");
        }
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuarioRepository.getReferenceById(dto.getUsuarioId()));
        favorito.setInmueble(inmuebleRepository.getReferenceById(dto.getInmuebleId()));
        favoritoRepository.save(favorito);
    }
    @Transactional
    @Override
    public void eliminarFavortio(Integer favoritoId) {
        if (!favoritoRepository.existsById(favoritoId)) {
            throw new ResourceNotFoundException("El favorito con ID " + favoritoId + " no existe.");
        }
        favoritoRepository.deleteById(favoritoId);
    }
    @Override
    public List<FavoritoListResponseDTO> obtenerFavoritosPorUsuario(Long usuarioId) {
        return favoritoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(fav -> {
                    Inmueble inm = fav.getInmueble();
                    FavoritoListResponseDTO dto = new FavoritoListResponseDTO();
                    dto.setId(fav.getId()); // En FavoritoListResponseDTO el setter de favoritoId se llama setId
                    dto.setInmuebleId(inm.getId());
                    dto.setTitulo(inm.getTitulo());
                    dto.setCiudad(inm.getCiudad() != null ? inm.getCiudad().getNombre() : null);
                    dto.setPrecio(inm.getPrecio());

                    String imgPrincipal = galeriaRepository.findFirstByInmuebleIdOrderByIdAsc(inm.getId())
                            .map(Galeria::getUrlImagen)
                            .orElse(null);
                    dto.setImagenPrincipal(imgPrincipal);
                    return dto;
                }).collect(Collectors.toList());
    }
}