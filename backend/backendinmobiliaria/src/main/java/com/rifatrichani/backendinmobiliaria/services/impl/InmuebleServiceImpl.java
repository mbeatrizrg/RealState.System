package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.models.Extra;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.repositories.CiudadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusInmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ExtraRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.TipoInmuebleRepository;
import com.rifatrichani.backendinmobiliaria.services.InmuebleService;
import java.util.Set;
import jakarta.transaction.Transactional;

@Service 
public class InmuebleServiceImpl implements InmuebleService{
    private final InmuebleRepository inmuebleRepository;
    private final CiudadRepository ciudadRepository;
    private final TipoInmuebleRepository tipoInmuebleRepository;
    private final EstatusInmuebleRepository estatusInmuebleRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ExtraRepository extraRepository;
    public InmuebleServiceImpl(InmuebleRepository ir, CiudadRepository cr, TipoInmuebleRepository tir, EstatusInmuebleRepository eir, EmpleadoRepository emr, ExtraRepository exr) {
        this.inmuebleRepository = ir;
        this.ciudadRepository = cr;
        this.tipoInmuebleRepository = tir;
        this.estatusInmuebleRepository = eir;
        this.empleadoRepository = emr;
        this.extraRepository = exr;
    }
    //Registro de inmueble
    @Transactional
    @Override 
    public Integer registrarInmueble(InmuebleRequestDTO dto) {
        Inmueble inmueble = new Inmueble();
        inmueble.setTitulo(dto.getTitulo());
        inmueble.setPrecio(dto.getPrecio());
        inmueble.setDescripcion(dto.getDescripcion());
        inmueble.setRrbr("RRBR-" + System.currentTimeMillis()); 
        inmueble.setCiudad(ciudadRepository.getReferenceById(dto.getCiudadId()));
        inmueble.setTipoInmueble(tipoInmuebleRepository.getReferenceById(dto.getTipoInmuebleId()));
        inmueble.setEstatusInmueble(estatusInmuebleRepository.getReferenceById(dto.getEstatusInmuebleId()));
        inmueble.setEmpleado(empleadoRepository.getReferenceById(dto.getEmpleadoId()));
        if (dto.getExtrasId() != null && !dto.getExtrasId().isEmpty()) {
            Set<Extra> extras = new HashSet<>();
            dto.getExtrasId().forEach(id -> extras.add(extraRepository.getReferenceById(id)));
            inmueble.setExtras(extras);
        }
        return inmuebleRepository.save(inmueble).getId();
    }
    @Transactional 
    @Override 
    public void eliminarInmueble(Integer inmuebleId){
        if(inmuebleRepository.existsById(inmuebleId)){
            inmuebleRepository.deleteById(inmuebleId);
        }
    }
}
