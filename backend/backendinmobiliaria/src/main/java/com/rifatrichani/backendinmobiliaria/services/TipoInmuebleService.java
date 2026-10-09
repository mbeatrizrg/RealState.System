package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.models.TipoInmueble;
import com.rifatrichani.backendinmobiliaria.repositories.TipoInmuebleRepository;
import com.rifatrichani.backendinmobiliaria.dto.response.TipoInmuebleResponseDTO;
@Service 
public class TipoInmuebleService {
    private final TipoInmuebleRepository tipoInmuebleRepositorio;
    public TipoInmuebleService(TipoInmuebleRepository TIR){
        this.tipoInmuebleRepositorio = TIR;
    }

    public List<TipoInmuebleResponseDTO> getAll(){
        List<TipoInmueble> tipoInmuelbles = tipoInmuebleRepositorio.findAll();
        return tipoInmuelbles.stream().map(tipo -> {
            TipoInmuebleResponseDTO dto = new TipoInmuebleResponseDTO();
            dto.setId(tipo.getId());
            dto.setNombre(tipo.getNombre());
            return dto;
    }).collect(Collectors.toList());
    }

}
