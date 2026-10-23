package com.rifatrichani.backendinmobiliaria.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.FichaPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FiltroPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ArchivoResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ImagenResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadDetalleResponseDTO.Interesado;
import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.ArchivoCrm;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.ArchivoCrmRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.NegocioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.PropiedadEspecificaciones;
import com.rifatrichani.backendinmobiliaria.security.ControlAcceso;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ColumnaPropiedad;
import com.rifatrichani.backendinmobiliaria.services.ContactoService;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.PropiedadService;
import com.rifatrichani.backendinmobiliaria.services.impl.AlmacenArchivosCrm.ArchivoGuardado;

/* Modulo Inmuebles del CRM. Todos los empleados consultan el catalogo; la ficha (estado, dueño, caracteristicas) y los
   documentos legales solo los modifica el asesor captador o un administrador (igual que la edicion del inmueble) */
@Service
public class PropiedadServiceImpl implements PropiedadService {
    private static final Map<String, String> CAMPOS_ORDEN = Map.of(
            "fechaCaptacion", "fechaCaptacion",
            "titulo", "titulo",
            "codigo", "rrbr",
            "precio", "precio",
            "estado", "estado",
            "asesor", "empleado.nombre",
            "zona", "ubicacion");

    private final InmuebleRepository inmuebleRepository;
    private final GaleriaRepository galeriaRepository;
    private final NegocioRepository negocioRepository;
    private final LeadRepository leadRepository;
    private final ArchivoCrmRepository archivoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final AlmacenArchivosCrm almacen;
    private final ExportadorTablas exportador;

    public PropiedadServiceImpl(InmuebleRepository ir, GaleriaRepository gr, NegocioRepository nr, LeadRepository lr,
                                ArchivoCrmRepository ar, EmpleadoRepository er, AlmacenArchivosCrm al, ExportadorTablas ex) {
        this.inmuebleRepository = ir;
        this.galeriaRepository = gr;
        this.negocioRepository = nr;
        this.leadRepository = lr;
        this.archivoRepository = ar;
        this.empleadoRepository = er;
        this.almacen = al;
        this.exportador = ex;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<PropiedadResponseDTO> buscar(FiltroPropiedadDTO filtro, CuentaAutenticada empleado) {
        Page<Inmueble> pagina = inmuebleRepository.findAll(PropiedadEspecificaciones.conFiltros(filtro, empleado.id()),
                PageRequest.of(filtro.getPagina(), filtro.getTamano(), orden(filtro)));
        return PaginaResponseDTO.de(pagina, this::mapear);
    }

    @Override
    @Transactional(readOnly = true)
    public ConteoPestanasDTO contar(FiltroPropiedadDTO filtro, CuentaAutenticada empleado) {
        String vistaOriginal = filtro.getVista();
        try {
            filtro.setVista("todos");
            long todos = inmuebleRepository.count(PropiedadEspecificaciones.conFiltros(filtro, empleado.id()));
            filtro.setVista("mios");
            long mios = inmuebleRepository.count(PropiedadEspecificaciones.conFiltros(filtro, empleado.id()));
            filtro.setVista("disponibles");
            long disponibles = inmuebleRepository.count(PropiedadEspecificaciones.conFiltros(filtro, empleado.id()));
            return new ConteoPestanasDTO(todos, mios, disponibles);
        } finally {
            filtro.setVista(vistaOriginal);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoExportado exportar(FiltroPropiedadDTO filtro, List<ColumnaPropiedad> columnas, FormatoExportacion formato,
                                     boolean soloPagina, CuentaAutenticada empleado) {
        Pageable paginacion = soloPagina
                ? PageRequest.of(filtro.getPagina(), filtro.getTamano(), orden(filtro))
                : PageRequest.of(0, ContactoService.MAXIMO_EXPORTACION, orden(filtro));
        Page<Inmueble> pagina = inmuebleRepository.findAll(PropiedadEspecificaciones.conFiltros(filtro, empleado.id()), paginacion);
        if (!soloPagina && pagina.getTotalElements() > ContactoService.MAXIMO_EXPORTACION) {
            throw new IllegalArgumentException("Hay " + pagina.getTotalElements() + " inmuebles con estos filtros. Se pueden exportar como máximo "
                    + ContactoService.MAXIMO_EXPORTACION + ": agregue más filtros.");
        }
        List<PropiedadResponseDTO> propiedades = mapear(pagina.getContent());
        String nombreBase = "inmuebles-" + filtro.getVista() + "-" + LocalDate.now();
        return switch (formato) {
            case XLSX -> new ArchivoExportado(nombreBase + ".xlsx", ArchivoExportado.TIPO_XLSX, exportador.excel("Inmuebles", propiedades, columnas));
            case PDF -> new ArchivoExportado(nombreBase + ".pdf", "application/pdf",
                    exportador.pdf("Inmuebles", tituloVista(filtro.getVista()), "inmuebles", propiedades, columnas));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public PropiedadDetalleResponseDTO obtenerDetalle(Integer id, CuentaAutenticada empleado) {
        Inmueble inmueble = buscarInmueble(id);
        List<Galeria> galeria = galeriaRepository.findByInmuebleIdOrderByIdAsc(id);
        List<Interesado> interesados = leadRepository.obtenerPorInmuebleId(id).stream()
                .map(l -> {
                    Usuario u = l.getUsuario();
                    return new Interesado(l.getId(), u.getId(), MapeosCrm.nombreContacto(u), u.getCorreo(), u.getTelefono(), l.getEstatus());
                })
                .toList();
        return new PropiedadDetalleResponseDTO(
                mapear(List.of(inmueble)).get(0),
                inmueble.getDescripcion(),
                inmueble.getExtra() == null ? List.of() : List.of(inmueble.getExtra().getNombre()),
                galeria.stream().map(g -> new ImagenResponseDTO(g.getId(), g.getUrlImagen())).toList(),
                archivoRepository.findByInmuebleIdOrderByIdDesc(id).stream().map(ArchivoResponseDTO::de).toList(),
                negocioRepository.findByInmuebleIdOrderByIdDesc(id).stream().map(MapeosCrm::negocio).toList(),
                interesados,
                puedeEditar(inmueble, empleado));
    }

    @Override
    @Transactional
    public void actualizarFicha(Integer id, FichaPropiedadDTO dto, CuentaAutenticada empleado) {
        Inmueble inmueble = buscarInmueble(id);
        ControlAcceso.verificarEncargado(inmueble, empleado);
        inmueble.cambiarEstado(dto.estado());
        inmueble.setDuenoNombre(textoOpcional(dto.duenoNombre()));
        inmueble.setDuenoTelefono(textoOpcional(dto.duenoTelefono()));
        inmueble.setDuenoCorreo(textoOpcional(dto.duenoCorreo()));
        inmueble.setHabitaciones(dto.habitaciones());
        inmueble.setBanos(dto.banos());
        inmueble.setMetrosCuadrados(dto.metrosCuadrados());
        inmueble.setEstacionamientos(dto.estacionamientos());
        inmueble.setPiso(dto.piso());
        inmuebleRepository.save(inmueble);
    }

    @Override
    @Transactional
    public List<ArchivoResponseDTO> subirDocumentos(Integer id, List<MultipartFile> archivos, CuentaAutenticada empleado) {
        Inmueble inmueble = buscarInmueble(id);
        ControlAcceso.verificarEncargado(inmueble, empleado);
        List<MultipartFile> validos = archivos == null ? List.of() : archivos.stream().filter(a -> a != null && !a.isEmpty()).toList();
        if (validos.isEmpty()) {
            throw new IllegalArgumentException("Seleccione al menos un documento.");
        }
        if (archivoRepository.findByInmuebleIdOrderByIdDesc(id).size() + validos.size() > MAXIMO_DOCUMENTOS) {
            throw new IllegalArgumentException("Un inmueble puede tener como máximo " + MAXIMO_DOCUMENTOS + " documentos.");
        }
        List<ArchivoGuardado> guardados = new ArrayList<>();
        try {
            List<ArchivoResponseDTO> respuesta = new ArrayList<>();
            for (MultipartFile archivo : validos) {
                ArchivoGuardado guardado = almacen.guardar(archivo);
                guardados.add(guardado);
                ArchivoCrm documento = new ArchivoCrm();
                documento.setNombre(guardado.nombre());
                documento.setRuta(guardado.ruta());
                documento.setTipoContenido(guardado.tipoContenido());
                documento.setTamano(guardado.tamano());
                documento.setInmueble(inmueble);
                documento.setEmpleado(empleadoRepository.getReferenceById(empleado.id()));
                respuesta.add(ArchivoResponseDTO.de(archivoRepository.save(documento)));
            }
            return respuesta;
        } catch (RuntimeException e) {
            guardados.forEach(g -> almacen.eliminar(g.ruta()));
            throw e;
        }
    }

    @Override
    @Transactional
    public void eliminarDocumento(Integer documentoId, CuentaAutenticada empleado) {
        ArchivoCrm documento = archivoRepository.findById(documentoId)
                .filter(d -> d.getInmueble() != null)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + documentoId));
        ControlAcceso.verificarEncargado(documento.getInmueble(), empleado);
        archivoRepository.delete(documento);
        archivoRepository.flush();
        almacen.eliminar(documento.getRuta());
    }

    //Imagen principal y negocios abiertos de toda la pagina con dos consultas
    private List<PropiedadResponseDTO> mapear(List<Inmueble> inmuebles) {
        if (inmuebles.isEmpty()) return List.of();
        List<Integer> ids = inmuebles.stream().map(Inmueble::getId).toList();
        Map<Integer, String> imagenPrincipal = new HashMap<>();
        galeriaRepository.findByInmuebleIdInOrderByIdAsc(ids)
                .forEach(g -> imagenPrincipal.putIfAbsent(g.getInmueble().getId(), g.getUrlImagen()));
        Map<Integer, Long> abiertos = negocioRepository.contarAbiertosPorInmueble(ids).stream()
                .collect(Collectors.toMap(fila -> (Integer) fila[0], fila -> (Long) fila[1]));
        return inmuebles.stream()
                .map(i -> MapeosCrm.propiedad(i, abiertos.getOrDefault(i.getId(), 0L), imagenPrincipal.get(i.getId())))
                .toList();
    }

    private static boolean puedeEditar(Inmueble inmueble, CuentaAutenticada empleado) {
        return empleado.esAdmin() || (inmueble.getEmpleado() != null && empleado.id().equals(inmueble.getEmpleado().getId()));
    }

    private Inmueble buscarInmueble(Integer id) {
        return inmuebleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + id));
    }

    private static Sort orden(FiltroPropiedadDTO filtro) {
        Sort.Direction direccion = "asc".equals(filtro.getDireccion()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String campo = CAMPOS_ORDEN.getOrDefault(filtro.getOrden(), "fechaCaptacion");
        return Sort.by(new Sort.Order(direccion, campo).nullsLast()).and(Sort.by(direccion, "id"));
    }

    private static String textoOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private static String tituloVista(String vista) {
        return switch (vista) {
            case "mios" -> "Mis inmuebles captados";
            case "disponibles" -> "Inmuebles disponibles";
            default -> "Todos los inmuebles";
        };
    }
}
