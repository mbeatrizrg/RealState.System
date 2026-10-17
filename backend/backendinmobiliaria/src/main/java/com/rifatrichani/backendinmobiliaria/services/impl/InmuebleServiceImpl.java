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

    public InmuebleServiceImpl(InmuebleRepository ir, CiudadRepository cr, TipoInmuebleRepository tir,
                               EstatusInmuebleRepository eir, EmpleadoRepository emr, ExtraRepository exr,
                               GaleriaRepository gr) {
        this.inmuebleRepository = ir;
        this.ciudadRepository = cr;
        this.tipoInmuebleRepository = tir;
        this.estatusInmuebleRepository = eir;
        this.empleadoRepository = emr;
        this.extraRepository = exr;
        this.galeriaRepository = gr;
    }

    @Override
    @Transactional
    public Integer registrarInmueble(InmuebleRequestDTO dto, Integer idEmpleadoAutenticado) {
        Inmueble inmueble = new Inmueble();
        inmueble.setTitulo(dto.getTitulo());
        inmueble.setPrecio(dto.getPrecio());
        inmueble.setDescripcion(dto.getDescripcion());
        inmueble.setRrbr("RRBR-" + System.currentTimeMillis());

        inmueble.setCiudad(ciudadRepository.getReferenceById(dto.getCiudadId()));
        inmueble.setTipoInmueble(tipoInmuebleRepository.getReferenceById(dto.getTipoInmuebleId()));
        inmueble.setEstatusInmueble(estatusInmuebleRepository.getReferenceById(dto.getEstatusInmuebleId()));
        // Si el DTO trae empleadoId lo asignamos, de lo contrario usamos el empleado autenticado del JWT
        Integer idVendedor = (dto.getEmpleadoId() != null) ? dto.getEmpleadoId() : idEmpleadoAutenticado;
        inmueble.setEmpleado(empleadoRepository.getReferenceById(idVendedor));

        if (dto.getExtrasId() != null && !dto.getExtrasId().isEmpty()) {
            Set<Extra> extras = new HashSet<>();
            dto.getExtrasId().forEach(id -> extras.add(extraRepository.getReferenceById(id)));
            inmueble.setExtras(extras);
        }

        return inmuebleRepository.save(inmueble).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InmuebleListResponseDTO> obtenerTodosCRM() {
        return inmuebleRepository.findAll().stream()
                .map(this::mapearAListResponseDTO)
                .collect(Collectors.toList());
    }

    // En InmuebleServiceImpl.java:
    @Override
    @Transactional(readOnly = true)
    public List<InmuebleListResponseDTO> buscarPorFiltros(
            Integer ciudadId, 
            Integer tipoInmuebleId, 
            Integer estatusInmuebleId, 
            BigDecimal minPrecio, 
            BigDecimal maxPrecio) {
        return inmuebleRepository.buscarPorFiltrosMultiples(ciudadId, tipoInmuebleId, estatusInmuebleId, minPrecio, maxPrecio)
                .stream()
                .map(this::mapearAListResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InmuebleDetalleResponseDTO obtenerDetalleInmueble(Integer id) {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + id));

        InmuebleDetalleResponseDTO dto = new InmuebleDetalleResponseDTO();
        dto.setId(inmueble.getId());
        dto.setTitulo(inmueble.getTitulo());
        dto.setPrecio(inmueble.getPrecio());
        dto.setDescripcion(inmueble.getDescripcion());
        dto.setRrbr(inmueble.getRrbr());
        dto.setNombreCiudad(inmueble.getCiudad() != null ? inmueble.getCiudad().getNombre() : null);
        dto.setNombreTipoInmueble(inmueble.getTipoInmueble().getNombre());
        dto.setNombreEstatus(inmueble.getEstatusInmueble().getNombre());
        dto.setNombreEmpleado(inmueble.getEmpleado().getNombre() + " " + inmueble.getEmpleado().getApellido());

        // Poblamos la lista de nombres de Extras
        if (inmueble.getExtras() != null) {
            List<String> nombresExtras = inmueble.getExtras().stream()
                    .map(Extra::getNombre)
                    .collect(Collectors.toList());
            dto.setExtras(nombresExtras);
        }

        // Poblamos todas las URLs de la Galería asociadas a este inmueble
        List<String> urlsGaleria = galeriaRepository.findByInmuebleId(inmueble.getId()).stream()
                .map(Galeria::getUrlImagen)
                .collect(Collectors.toList());
        dto.setUrlsGaleria(urlsGaleria);

        return dto;
    }

    @Override
    @Transactional
    public void actualizarInmueble(Integer id, InmuebleRequestDTO dto, Integer idEmpleadoAutenticado) {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + id));
        inmueble.setTitulo(dto.getTitulo());
        inmueble.setPrecio(dto.getPrecio());
        inmueble.setDescripcion(dto.getDescripcion());
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

    @Override
    @Transactional
    public void cambiarEstatus(Integer id, InmuebleEstatusRequestDTO dto, Integer idEmpleadoAutenticado) {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + id));
        inmueble.setEstatusInmueble(estatusInmuebleRepository.getReferenceById(dto.getEstatusInmuebleId()));
        inmuebleRepository.save(inmueble);
    }

    @Override
    @Transactional
    public void eliminarInmueble(Integer id, Integer idEmpleadoAutenticado) {
        if (!inmuebleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inmueble no encontrado con ID: " + id);
        }
        inmuebleRepository.deleteById(id);
    }

    // Mapeador auxiliar que incluye ciudad e imagen principal desde GaleriaRepository
    private InmuebleListResponseDTO mapearAListResponseDTO(Inmueble inmueble) {
        InmuebleListResponseDTO dto = new InmuebleListResponseDTO();
        dto.setId(inmueble.getId());
        dto.setTitulo(inmueble.getTitulo());
        dto.setPrecio(inmueble.getPrecio());
        dto.setCiudad(inmueble.getCiudad() != null ? inmueble.getCiudad().getNombre() : null);
        dto.setTipoInmuebleId(inmueble.getTipoInmueble().getId());
        dto.setTipoInmueble(inmueble.getTipoInmueble().getNombre());

        String imagenPrincipal = galeriaRepository.findFirstByInmuebleIdOrderByIdAsc(inmueble.getId())
                .map(Galeria::getUrlImagen)
                .orElse(null);
        dto.setUrlImagenPrincipal(imagenPrincipal);

        return dto;
    }
}