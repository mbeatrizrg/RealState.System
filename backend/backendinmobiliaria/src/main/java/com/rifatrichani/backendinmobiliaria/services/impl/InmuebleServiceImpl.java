package com.rifatrichani.backendinmobiliaria.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleEstatusRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Extra;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.repositories.*;
import com.rifatrichani.backendinmobiliaria.services.InmuebleService;

@Service
public class InmuebleServiceImpl implements InmuebleService {
    private final InmuebleRepository inmuebleRepository;
    private final CiudadRepository ciudadRepository;
    private final TipoInmuebleRepository tipoInmuebleRepository;
    private final EstatusInmuebleRepository estatusInmuebleRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ExtraRepository extraRepository;
    private final GaleriaRepository galeriaRepository;
    //Inyección de dependencias a través del constructor
    public InmuebleServiceImpl(InmuebleRepository ir, CiudadRepository cr, TipoInmuebleRepository tir, EstatusInmuebleRepository eir, EmpleadoRepository emr, ExtraRepository exr,GaleriaRepository gr) {
        this.inmuebleRepository = ir;
        this.ciudadRepository = cr;
        this.tipoInmuebleRepository = tir;
        this.estatusInmuebleRepository = eir;
        this.empleadoRepository = emr;
        this.extraRepository = exr;
        this.galeriaRepository = gr;
    }
    
    //Registro de inmueble junto con su RRBR
    @Override
    @Transactional
    public Integer registrarInmueble(InmuebleRequestDTO dto, Integer idEmpleadoAutenticado) {
        String codigoRrbr = (dto.getRrbr() != null && !dto.getRrbr().isBlank()) ? dto.getRrbr(): "RRBR-" + System.currentTimeMillis();
        if (inmuebleRepository.existsByRrbr(codigoRrbr)) {
            throw new IllegalArgumentException("El código RRBR '" + codigoRrbr + "' ya está asignado a otro inmueble.");
        }
        Inmueble inmueble = new Inmueble();
        inmueble.setTitulo(dto.getTitulo());
        inmueble.setPrecio(dto.getPrecio());
        inmueble.setDescripcion(dto.getDescripcion());
        inmueble.setRrbr(codigoRrbr);
        inmueble.setUbicacion(dto.getUbicacion());
        inmueble.setIsAlquiler(dto.getIsAlquiler());
        inmueble.setCiudad(ciudadRepository.getReferenceById(dto.getCiudadId()));
        inmueble.setTipoInmueble(tipoInmuebleRepository.getReferenceById(dto.getTipoInmuebleId()));
        inmueble.setEstatusInmueble(estatusInmuebleRepository.getReferenceById(dto.getEstatusInmuebleId()));
        Integer idVendedor = (dto.getEmpleadoId() != null) ? dto.getEmpleadoId() : idEmpleadoAutenticado;
        inmueble.setEmpleado(empleadoRepository.getReferenceById(idVendedor));
        if (dto.getExtrasId() != null && !dto.getExtrasId().isEmpty()) {
            Set<Extra> extras = new HashSet<>();
            dto.getExtrasId().forEach(id -> extras.add(extraRepository.getReferenceById(id)));
            inmueble.setExtras(extras);
        }
        return inmuebleRepository.save(inmueble).getId();
    }

    // Obtiene todos los inmuebles para el CRM, mapeando a DTO de response
    @Override
    @Transactional(readOnly = true)
    public List<InmuebleListResponseDTO> obtenerTodosCRM() {
        return inmuebleRepository.findAll().stream().map(this::mapearAListResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InmuebleListResponseDTO> buscarPorFiltros(Integer ciudadId,Integer tipoInmuebleId,Integer estatusInmuebleId,Boolean isAlquiler,BigDecimal minPrecio,BigDecimal maxPrecio) {
        return inmuebleRepository.buscarPorFiltrosMultiples(ciudadId, tipoInmuebleId, estatusInmuebleId, isAlquiler, minPrecio, maxPrecio).stream().map(this::mapearAListResponseDTO).collect(Collectors.toList());
    }

    //
    @Override
    @Transactional(readOnly = true)
    public InmuebleDetalleResponseDTO obtenerDetalleInmueble(Integer id) {
        Inmueble inmueble = inmuebleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + id));
        InmuebleDetalleResponseDTO dto = new InmuebleDetalleResponseDTO();
        dto.setId(inmueble.getId());
        dto.setTitulo(inmueble.getTitulo());
        dto.setPrecio(inmueble.getPrecio());
        dto.setDescripcion(inmueble.getDescripcion());
        dto.setRrbr(inmueble.getRrbr());
        dto.setUbicacion(inmueble.getUbicacion());
        dto.setIsAlquiler(inmueble.getIsAlquiler());
        dto.setNombreCiudad(inmueble.getCiudad() != null ? inmueble.getCiudad().getNombre() : null);
        dto.setNombreTipoInmueble(inmueble.getTipoInmueble().getNombre());
        dto.setNombreEstatus(inmueble.getEstatusInmueble().getNombre());
        dto.setNombreEmpleado(inmueble.getEmpleado().getNombre() + " " + inmueble.getEmpleado().getApellido());
        if (inmueble.getExtras() != null) {
            dto.setExtras(inmueble.getExtras().stream().map(Extra::getNombre).collect(Collectors.toList()));
        }
        dto.setUrlsGaleria(galeriaRepository.findByInmuebleId(inmueble.getId()).stream()
                .map(Galeria::getUrlImagen)
                .collect(Collectors.toList()));
        return dto;
    }

    // Implementación del método actualizarInmueble
    @Override
    @Transactional
    public void actualizarInmueble(Integer id, InmuebleRequestDTO dto, Integer idEmpleadoAutenticado) {
        Inmueble inmueble = inmuebleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + id));
        inmueble.setTitulo(dto.getTitulo());
        inmueble.setPrecio(dto.getPrecio());
        inmueble.setDescripcion(dto.getDescripcion());
        inmueble.setUbicacion(dto.getUbicacion());
        inmueble.setIsAlquiler(dto.getIsAlquiler());
        if (dto.getRrbr() != null && !dto.getRrbr().isBlank()) {
            inmueble.setRrbr(dto.getRrbr());
        }
        inmueble.setCiudad(ciudadRepository.getReferenceById(dto.getCiudadId()));
        inmueble.setTipoInmueble(tipoInmuebleRepository.getReferenceById(dto.getTipoInmuebleId()));
        inmueble.setEstatusInmueble(estatusInmuebleRepository.getReferenceById(dto.getEstatusInmuebleId()));
        if (dto.getExtrasId() != null) {
            Set<Extra> extras = new HashSet<>();
            dto.getExtrasId().forEach(extraId -> extras.add(extraRepository.getReferenceById(extraId)));
            inmueble.setExtras(extras);
        }
        inmuebleRepository.save(inmueble);
    }

    // Implementación del método cambiarEstatus
    @Override
    @Transactional
    public void cambiarEstatus(Integer id, InmuebleEstatusRequestDTO dto, Integer idEmpleadoAutenticado) {
        Inmueble inmueble = inmuebleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + id));
        inmueble.setEstatusInmueble(estatusInmuebleRepository.getReferenceById(dto.getEstatusInmuebleId()));
        inmuebleRepository.save(inmueble);
    }

    // Implementación del método eliminarInmueble
    @Override
    @Transactional
    public void eliminarInmueble(Integer id, Integer idEmpleadoAutenticado) {
        if (!inmuebleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inmueble no encontrado con ID: " + id);
        }
        inmuebleRepository.deleteById(id);
    }

    //Mapeo de entidad a DTO para la lista de inmuebles
    private InmuebleListResponseDTO mapearAListResponseDTO(Inmueble inmueble) {
        InmuebleListResponseDTO dto = new InmuebleListResponseDTO();
        dto.setId(inmueble.getId());
        dto.setTitulo(inmueble.getTitulo());
        dto.setPrecio(inmueble.getPrecio());
        dto.setUbicacion(inmueble.getUbicacion());
        dto.setIsAlquiler(inmueble.getIsAlquiler());
        dto.setCiudad(inmueble.getCiudad() != null ? inmueble.getCiudad().getNombre() : null);
        dto.setTipoInmuebleId(inmueble.getTipoInmueble().getId());
        dto.setTipoInmueble(inmueble.getTipoInmueble().getNombre());
        dto.setEstatusInmueble(inmueble.getEstatusInmueble().getNombre());
        String imagenPrincipal = galeriaRepository.findFirstByInmuebleIdOrderByIdAsc(inmueble.getId())
                .map(Galeria::getUrlImagen)
                .orElse(null);
        dto.setUrlImagenPrincipal(imagenPrincipal);
        return dto;
    }
}