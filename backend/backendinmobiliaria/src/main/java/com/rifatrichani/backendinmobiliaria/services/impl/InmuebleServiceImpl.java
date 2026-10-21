package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleEstatusRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.InmuebleRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.InmuebleListResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.repositories.CiudadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusInmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ExtraRepository;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.TipoInmuebleRepository;
import com.rifatrichani.backendinmobiliaria.security.ControlAcceso;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
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

    public InmuebleServiceImpl(InmuebleRepository ir, CiudadRepository cr, TipoInmuebleRepository tir, EstatusInmuebleRepository eir, EmpleadoRepository emr, ExtraRepository exr, GaleriaRepository gr) {
        this.inmuebleRepository = ir;
        this.ciudadRepository = cr;
        this.tipoInmuebleRepository = tir;
        this.estatusInmuebleRepository = eir;
        this.empleadoRepository = emr;
        this.extraRepository = exr;
        this.galeriaRepository = gr;
    }

    //Registro de inmueble junto con su RRBR. Si no se envia RRBR se genera uno
    @Override
    @Transactional
    public Integer registrarInmueble(InmuebleRequestDTO dto, CuentaAutenticada empleado) {
        String codigoRrbr = (dto.getRrbr() != null && !dto.getRrbr().isBlank()) ? dto.getRrbr().trim() : "RRBR-" + System.currentTimeMillis();
        if (inmuebleRepository.existsByRrbr(codigoRrbr)) {
            throw new IllegalArgumentException("El código RRBR '" + codigoRrbr + "' ya está asignado a otro inmueble.");
        }
        Inmueble inmueble = new Inmueble();
        inmueble.setRrbr(codigoRrbr);
        inmueble.setVendido(false);
        inmueble.setEmpleado(resolverEncargado(dto.getEmpleadoId(), empleado));
        copiarDatos(dto, inmueble);
        return inmuebleRepository.save(inmueble).getId();
    }

    //CRM: todos los inmuebles, o solo los de un vendedor si se indica
    @Override
    @Transactional(readOnly = true)
    public List<InmuebleListResponseDTO> obtenerTodosCRM(Integer soloDelEmpleadoId) {
        List<Inmueble> inmuebles = soloDelEmpleadoId == null
                ? inmuebleRepository.obtenerTodosCrm()
                : inmuebleRepository.findByEmpleadoIdOrderByIdDesc(soloDelEmpleadoId);
        return mapearListaResponseDTO(inmuebles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InmuebleListResponseDTO> buscarPorFiltros(Integer ciudadId, Integer tipoInmuebleId, Integer estatusInmuebleId, Boolean isAlquiler, Integer minPrecio, Integer maxPrecio) {
        return mapearListaResponseDTO(inmuebleRepository.buscarPorFiltrosMultiples(ciudadId, tipoInmuebleId, estatusInmuebleId, isAlquiler, minPrecio, maxPrecio));
    }

    @Override
    @Transactional(readOnly = true)
    public InmuebleDetalleResponseDTO obtenerDetalleInmueble(Integer id) {
        Inmueble inmueble = buscarInmueble(id);
        InmuebleDetalleResponseDTO dto = new InmuebleDetalleResponseDTO();
        dto.setId(inmueble.getId());
        dto.setTitulo(inmueble.getTitulo());
        dto.setPrecio(inmueble.getPrecio());
        dto.setDescripcion(inmueble.getDescripcion());
        dto.setRrbr(inmueble.getRrbr());
        dto.setUbicacion(inmueble.getUbicacion());
        dto.setIsAlquiler(inmueble.getAlquiler());
        dto.setIsVendido(inmueble.getVendido());
        dto.setNombreCiudad(inmueble.getCiudad() != null ? inmueble.getCiudad().getNombre() : null);
        dto.setNombreTipoInmueble(inmueble.getTipoInmueble().getNombre());
        dto.setNombreEstatus(inmueble.getEstatusInmueble().getNombre());
        Empleado vendedor = inmueble.getEmpleado();
        dto.setNombreEmpleado(vendedor.getNombre() + (vendedor.getApellido() != null ? " " + vendedor.getApellido() : ""));
        dto.setExtras(inmueble.getExtra() == null ? List.of() : List.of(inmueble.getExtra().getNombre()));
        dto.setUrlsGaleria(galeriaRepository.findByInmuebleIdOrderByIdAsc(id).stream()
                .map(Galeria::getUrlImagen)
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    @Transactional
    public void actualizarInmueble(Integer id, InmuebleRequestDTO dto, CuentaAutenticada empleado) {
        Inmueble inmueble = buscarInmueble(id);
        ControlAcceso.verificarEncargado(inmueble, empleado);
        if (dto.getRrbr() != null && !dto.getRrbr().isBlank()) {
            String codigoRrbr = dto.getRrbr().trim();
            if (inmuebleRepository.existsByRrbrAndIdNot(codigoRrbr, id)) {
                throw new IllegalArgumentException("El código RRBR '" + codigoRrbr + "' ya está asignado a otro inmueble.");
            }
            inmueble.setRrbr(codigoRrbr);
        }
        //Solo un administrador puede reasignar el inmueble a otro vendedor
        if (dto.getEmpleadoId() != null && !dto.getEmpleadoId().equals(inmueble.getEmpleado().getId())) {
            inmueble.setEmpleado(resolverEncargado(dto.getEmpleadoId(), empleado));
        }
        copiarDatos(dto, inmueble);
        inmuebleRepository.save(inmueble);
    }

    @Override
    @Transactional
    public void cambiarEstatus(Integer id, InmuebleEstatusRequestDTO dto, CuentaAutenticada empleado) {
        Inmueble inmueble = buscarInmueble(id);
        ControlAcceso.verificarEncargado(inmueble, empleado);
        inmueble.setEstatusInmueble(estatusInmuebleRepository.getReferenceById(dto.getEstatusInmuebleId()));
        inmuebleRepository.save(inmueble);
    }

    //Si el inmueble tiene fotos, leads o ventas asociadas la base de datos rechaza el borrado (409 desde el GlobalExceptionHandler)
    @Override
    @Transactional
    public void eliminarInmueble(Integer id, CuentaAutenticada empleado) {
        Inmueble inmueble = buscarInmueble(id);
        ControlAcceso.verificarEncargado(inmueble, empleado);
        inmuebleRepository.delete(inmueble);
        inmuebleRepository.flush();
    }

    private Inmueble buscarInmueble(Integer id) {
        return inmuebleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + id));
    }

    private void copiarDatos(InmuebleRequestDTO dto, Inmueble inmueble) {
        inmueble.setTitulo(dto.getTitulo().trim());
        inmueble.setPrecio(dto.getPrecio());
        inmueble.setDescripcion(dto.getDescripcion());
        inmueble.setUbicacion(dto.getUbicacion().trim());
        inmueble.setAlquiler(dto.getIsAlquiler());
        inmueble.setCiudad(ciudadRepository.getReferenceById(dto.getCiudadId()));
        inmueble.setTipoInmueble(tipoInmuebleRepository.getReferenceById(dto.getTipoInmuebleId()));
        inmueble.setEstatusInmueble(estatusInmuebleRepository.getReferenceById(dto.getEstatusInmuebleId()));
        inmueble.setExtra(extraRepository.getReferenceById(dto.getExtraId()));
    }

    //El encargado es el empleado autenticado. Un administrador puede indicar otro vendedor (que debe existir y estar activo)
    private Empleado resolverEncargado(Integer empleadoIdSolicitado, CuentaAutenticada empleado) {
        if (empleadoIdSolicitado == null || empleadoIdSolicitado.equals(empleado.id())) {
            return empleadoRepository.getReferenceById(empleado.id());
        }
        if (!empleado.esAdmin()) {
            throw new SecurityException("Solo un administrador puede asignar inmuebles a otro vendedor.");
        }
        return empleadoRepository.findById(empleadoIdSolicitado)
                .filter(e -> Boolean.TRUE.equals(e.getActivo()))
                .orElseThrow(() -> new IllegalArgumentException("El vendedor indicado no existe o está inactivo."));
    }

    //Mapea la lista de inmuebles a tarjetas, buscando la imagen principal de todos en una sola consulta
    private List<InmuebleListResponseDTO> mapearListaResponseDTO(List<Inmueble> inmuebles) {
        if (inmuebles.isEmpty()) {
            return List.of();
        }
        Map<Integer, String> imagenPrincipal = new HashMap<>();
        List<Integer> ids = inmuebles.stream().map(Inmueble::getId).collect(Collectors.toList());
        //Las imagenes vienen ordenadas por id, la primera de cada inmueble es la principal
        galeriaRepository.findByInmuebleIdInOrderByIdAsc(ids)
                .forEach(g -> imagenPrincipal.putIfAbsent(g.getInmueble().getId(), g.getUrlImagen()));

        return inmuebles.stream()
                .map(inmueble -> mapearAListResponseDTO(inmueble, imagenPrincipal.get(inmueble.getId())))
                .collect(Collectors.toList());
    }

    private InmuebleListResponseDTO mapearAListResponseDTO(Inmueble inmueble, String urlImagenPrincipal) {
        InmuebleListResponseDTO dto = new InmuebleListResponseDTO();
        dto.setId(inmueble.getId());
        dto.setTitulo(inmueble.getTitulo());
        dto.setPrecio(inmueble.getPrecio());
        dto.setUrlImagenPrincipal(urlImagenPrincipal);
        dto.setUbicacion(inmueble.getUbicacion());
        dto.setIsAlquiler(inmueble.getAlquiler());
        dto.setIsVendido(inmueble.getVendido());
        dto.setCiudad(inmueble.getCiudad() != null ? inmueble.getCiudad().getNombre() : null);
        dto.setTipoInmuebleId(inmueble.getTipoInmueble().getId());
        dto.setTipoInmueble(inmueble.getTipoInmueble().getNombre());
        dto.setEstatusInmueble(inmueble.getEstatusInmueble().getNombre());
        dto.setEmpleadoId(inmueble.getEmpleado() != null ? inmueble.getEmpleado().getId() : null);
        return dto;
    }
}
