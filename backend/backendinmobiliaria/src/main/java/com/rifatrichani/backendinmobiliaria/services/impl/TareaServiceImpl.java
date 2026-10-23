package com.rifatrichani.backendinmobiliaria.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroTareaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.TareaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.TareaResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Actividad;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Negocio;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.ActividadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.NegocioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.TareaEspecificaciones;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ColumnaTarea;
import com.rifatrichani.backendinmobiliaria.services.ContactoService;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.TareaService;

/* Todos los empleados ven todas las tareas (pestaña "Todas"). Modificar, cambiar el estado o borrar una tarea: su
   propietario, el propietario del contacto o un administrador (las mismas reglas que el resto del historial).
   Un vendedor solo puede crear tareas para si mismo; un administrador puede asignarlas a cualquier vendedor activo */
@Service
public class TareaServiceImpl implements TareaService {
    private static final Map<String, String> CAMPOS_ORDEN = Map.of(
            "fechaProgramada", "fechaProgramada",
            "titulo", "asunto",
            "estado", "estadoTarea",
            "propietario", "empleado.nombre",
            "contacto", "contacto.nombre",
            "fechaCreacion", "fechaCreacion");

    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final NegocioRepository negocioRepository;
    private final InmuebleRepository inmuebleRepository;
    private final ExportadorTablas exportador;

    public TareaServiceImpl(ActividadRepository ar, UsuarioRepository ur, EmpleadoRepository er, NegocioRepository nr,
                            InmuebleRepository ir, ExportadorTablas ex) {
        this.actividadRepository = ar;
        this.usuarioRepository = ur;
        this.empleadoRepository = er;
        this.negocioRepository = nr;
        this.inmuebleRepository = ir;
        this.exportador = ex;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<TareaResponseDTO> buscar(FiltroTareaDTO filtro, CuentaAutenticada empleado) {
        LocalDateTime ahora = LocalDateTime.now();
        Page<Actividad> pagina = consultar(filtro, empleado, ahora, PageRequest.of(filtro.getPagina(), filtro.getTamano(), orden(filtro)));
        return PaginaResponseDTO.de(pagina, tareas -> tareas.stream().map(t -> mapear(t, empleado, ahora)).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConteoPestanasDTO contar(FiltroTareaDTO filtro, CuentaAutenticada empleado) {
        String vistaOriginal = filtro.getVista();
        LocalDateTime ahora = LocalDateTime.now();
        try {
            filtro.setVista("todas");
            long todas = actividadRepository.count(TareaEspecificaciones.conFiltros(filtro, empleado.id(), ahora, false, true));
            filtro.setVista("mias_pendientes");
            long mias = actividadRepository.count(TareaEspecificaciones.conFiltros(filtro, empleado.id(), ahora, false, true));
            filtro.setVista("completadas_vencidas");
            long cerradas = actividadRepository.count(TareaEspecificaciones.conFiltros(filtro, empleado.id(), ahora, false, true));
            return new ConteoPestanasDTO(todas, mias, cerradas);
        } finally {
            filtro.setVista(vistaOriginal);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoExportado exportar(FiltroTareaDTO filtro, List<ColumnaTarea> columnas, FormatoExportacion formato,
                                     boolean soloPagina, CuentaAutenticada empleado) {
        LocalDateTime ahora = LocalDateTime.now();
        PageRequest paginacion = soloPagina
                ? PageRequest.of(filtro.getPagina(), filtro.getTamano(), orden(filtro))
                : PageRequest.of(0, ContactoService.MAXIMO_EXPORTACION, orden(filtro));
        Page<Actividad> pagina = consultar(filtro, empleado, ahora, paginacion);
        if (!soloPagina && pagina.getTotalElements() > ContactoService.MAXIMO_EXPORTACION) {
            throw new IllegalArgumentException("Hay " + pagina.getTotalElements() + " tareas con estos filtros. Se pueden exportar como máximo "
                    + ContactoService.MAXIMO_EXPORTACION + ": agregue más filtros.");
        }
        List<TareaResponseDTO> tareas = pagina.getContent().stream().map(t -> mapear(t, empleado, ahora)).toList();
        String nombreBase = "tareas-" + filtro.getVista().replace('_', '-') + "-" + LocalDate.now();
        return switch (formato) {
            case XLSX -> new ArchivoExportado(nombreBase + ".xlsx", ArchivoExportado.TIPO_XLSX, exportador.excel("Tareas", tareas, columnas));
            case PDF -> new ArchivoExportado(nombreBase + ".pdf", "application/pdf",
                    exportador.pdf("Tareas", tituloVista(filtro.getVista()), "tareas", tareas, columnas));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public TareaResponseDTO obtener(Integer id, CuentaAutenticada empleado) {
        return mapear(buscarTarea(id), empleado, LocalDateTime.now());
    }

    @Override
    @Transactional
    public TareaResponseDTO crear(TareaRequestDTO dto, CuentaAutenticada empleado) {
        Actividad tarea = new Actividad();
        tarea.setTipo(Actividad.Tipo.TAREA);
        tarea.setEmpleado(resolverPropietario(dto.propietarioId(), null, empleado));
        copiarDatos(dto, tarea);
        tarea.cambiarEstadoTarea(dto.estado() != null ? dto.estado() : Actividad.PENDIENTE);
        actividadRepository.save(tarea);
        if (tarea.getContacto() != null) usuarioRepository.registrarActividad(tarea.getContacto().getId());
        return mapear(tarea, empleado, LocalDateTime.now());
    }

    @Override
    @Transactional
    public TareaResponseDTO actualizar(Integer id, TareaRequestDTO dto, CuentaAutenticada empleado) {
        Actividad tarea = buscarTarea(id);
        ActividadServiceImpl.verificarPermiso(tarea, empleado);
        tarea.setEmpleado(resolverPropietario(dto.propietarioId(), tarea.getEmpleado(), empleado));
        copiarDatos(dto, tarea);
        if (dto.estado() != null) tarea.cambiarEstadoTarea(dto.estado());
        actividadRepository.save(tarea);
        return mapear(tarea, empleado, LocalDateTime.now());
    }

    @Override
    @Transactional
    public TareaResponseDTO cambiarEstado(Integer id, String estado, CuentaAutenticada empleado) {
        if (!List.of(Actividad.PENDIENTE, Actividad.EN_PROCESO, Actividad.COMPLETADA).contains(estado)) {
            throw new IllegalArgumentException("Estado inválido (pendiente, en_proceso o completada).");
        }
        Actividad tarea = buscarTarea(id);
        ActividadServiceImpl.verificarPermiso(tarea, empleado);
        tarea.cambiarEstadoTarea(estado);
        actividadRepository.save(tarea);
        return mapear(tarea, empleado, LocalDateTime.now());
    }

    /* El resultado queda al final de la descripcion de la tarea y, si tiene contacto, tambien como nota en su historial
       (vinculada al mismo negocio) para que se vea en el registro del contacto */
    @Override
    @Transactional
    public TareaResponseDTO completar(Integer id, String resultado, CuentaAutenticada empleado) {
        Actividad tarea = buscarTarea(id);
        ActividadServiceImpl.verificarPermiso(tarea, empleado);
        tarea.cambiarEstadoTarea(Actividad.COMPLETADA);
        String texto = resultado == null ? "" : resultado.trim();
        if (texto.length() > 5000) throw new IllegalArgumentException("El resultado no puede exceder los 5000 caracteres.");
        if (!texto.isEmpty()) {
            String anterior = tarea.getDescripcion();
            tarea.setDescripcion((anterior == null || anterior.isBlank() ? "" : anterior + "\n\n") + "Resultado: " + texto);
            if (tarea.getContacto() != null) {
                Actividad nota = new Actividad();
                nota.setTipo(Actividad.Tipo.NOTA);
                nota.setContacto(tarea.getContacto());
                nota.setNegocio(tarea.getNegocio());
                nota.setEmpleado(empleadoRepository.getReferenceById(empleado.id()));
                nota.setAsunto("Resultado de la tarea: " + recortar(tarea.getAsunto(), 170));
                nota.setDescripcion(texto);
                nota.setCompletada(true);
                actividadRepository.save(nota);
            }
        }
        actividadRepository.save(tarea);
        if (tarea.getContacto() != null) usuarioRepository.registrarActividad(tarea.getContacto().getId());
        return mapear(tarea, empleado, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void eliminar(Integer id, CuentaAutenticada empleado) {
        Actividad tarea = buscarTarea(id);
        ActividadServiceImpl.verificarPermiso(tarea, empleado);
        actividadRepository.delete(tarea);
    }

    // ----- Apoyo -----

    private Page<Actividad> consultar(FiltroTareaDTO filtro, CuentaAutenticada empleado, LocalDateTime ahora, PageRequest paginacion) {
        boolean porPrioridad = "prioridad".equals(filtro.getOrden());
        Specification<Actividad> spec = TareaEspecificaciones.conFiltros(filtro, empleado.id(), ahora, porPrioridad,
                "asc".equals(filtro.getDireccion()));
        //El orden por prioridad lo arma la especificacion: la paginacion va sin Sort para no reemplazarlo
        return actividadRepository.findAll(spec, porPrioridad ? PageRequest.of(paginacion.getPageNumber(), paginacion.getPageSize()) : paginacion);
    }

    /* Vincula contacto, negocio e inmueble. Con negocio, el contacto y el inmueble son los del negocio
       (si se indican otros es un error) */
    private void copiarDatos(TareaRequestDTO dto, Actividad tarea) {
        tarea.setAsunto(dto.titulo().trim());
        tarea.setDescripcion(dto.descripcion() == null || dto.descripcion().isBlank() ? null : dto.descripcion().trim());
        tarea.setTipoTarea(dto.tipoTarea());
        tarea.setPrioridad(dto.prioridad());
        tarea.setFechaProgramada(dto.fechaVencimiento());

        Negocio negocio = null;
        if (dto.negocioId() != null) {
            negocio = negocioRepository.obtenerConRelaciones(dto.negocioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + dto.negocioId()));
        }
        Usuario contacto = null;
        if (dto.contactoId() != null) {
            contacto = usuarioRepository.findById(dto.contactoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado con ID: " + dto.contactoId()));
        }
        Inmueble inmueble = null;
        if (dto.inmuebleId() != null) {
            inmueble = inmuebleRepository.findById(dto.inmuebleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + dto.inmuebleId()));
        }
        if (negocio != null) {
            if (contacto != null && !contacto.getId().equals(negocio.getContacto().getId())) {
                throw new IllegalArgumentException("El contacto no corresponde al negocio seleccionado.");
            }
            if (inmueble != null && !inmueble.getId().equals(negocio.getInmueble().getId())) {
                throw new IllegalArgumentException("El inmueble no corresponde al negocio seleccionado.");
            }
            contacto = negocio.getContacto();
            inmueble = negocio.getInmueble();
        }
        if (contacto == null && inmueble == null) {
            throw new IllegalArgumentException("Vincule la tarea a un contacto, un inmueble o un negocio.");
        }
        tarea.setNegocio(negocio);
        tarea.setContacto(contacto);
        tarea.setInmueble(inmueble);
    }

    private Empleado resolverPropietario(Integer propietarioId, Empleado actual, CuentaAutenticada empleado) {
        Integer id = propietarioId != null ? propietarioId : actual != null ? actual.getId() : empleado.id();
        if (actual != null && actual.getId().equals(id)) return actual;
        if (!empleado.esAdmin() && !empleado.id().equals(id)) {
            throw new AccessDeniedException("Un vendedor solo puede asignarse tareas a sí mismo.");
        }
        Empleado propietario = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + id));
        if (!Boolean.TRUE.equals(propietario.getActivo())) {
            throw new IllegalArgumentException("No se puede asignar una tarea a un empleado inactivo.");
        }
        return propietario;
    }

    private TareaResponseDTO mapear(Actividad t, CuentaAutenticada empleado, LocalDateTime ahora) {
        String guardado = t.getEstadoTarea() != null ? t.getEstadoTarea() : (Boolean.TRUE.equals(t.getCompletada()) ? Actividad.COMPLETADA : Actividad.PENDIENTE);
        boolean vencida = !Actividad.COMPLETADA.equals(guardado) && t.getFechaProgramada() != null && t.getFechaProgramada().isBefore(ahora);
        Usuario c = t.getContacto();
        Negocio n = t.getNegocio();
        Inmueble i = t.getInmueble() != null ? t.getInmueble() : n != null ? n.getInmueble() : null;
        Empleado p = t.getEmpleado();
        boolean puedeEditar;
        try {
            ActividadServiceImpl.verificarPermiso(t, empleado);
            puedeEditar = true;
        } catch (AccessDeniedException e) {
            puedeEditar = false;
        }
        return new TareaResponseDTO(t.getId(), t.getAsunto(), t.getDescripcion(), t.getTipoTarea(),
                t.getPrioridad() != null ? t.getPrioridad() : "media", vencida ? "vencida" : guardado, guardado,
                c != null ? c.getId() : null, c != null ? MapeosCrm.nombreContacto(c) : null,
                n != null ? n.getId() : null, n != null ? n.getNombre() : null,
                i != null ? i.getId() : null, i != null ? i.getRrbr() : null, i != null ? i.getTitulo() : null,
                p != null ? p.getId() : null, MapeosCrm.nombreEmpleado(p), t.getFechaProgramada(), t.getFechaCreacion(), puedeEditar);
    }

    private Actividad buscarTarea(Integer id) {
        return actividadRepository.findById(id)
                .filter(a -> a.getTipo() == Actividad.Tipo.TAREA)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + id));
    }

    private static Sort orden(FiltroTareaDTO filtro) {
        Sort.Direction direccion = "desc".equals(filtro.getDireccion()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String campo = CAMPOS_ORDEN.getOrDefault(filtro.getOrden(), "fechaProgramada");
        return Sort.by(new Sort.Order(direccion, campo).nullsLast()).and(Sort.by(direccion, "id"));
    }

    private static String recortar(String texto, int maximo) {
        return texto == null ? "" : texto.length() > maximo ? texto.substring(0, maximo) + "…" : texto;
    }

    private static String tituloVista(String vista) {
        return switch (vista) {
            case "mias_pendientes" -> "Mis tareas pendientes";
            case "completadas_vencidas" -> "Tareas completadas / vencidas";
            default -> "Todas las tareas";
        };
    }
}
