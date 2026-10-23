package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.ActividadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ActividadResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ArchivoResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Actividad;
import com.rifatrichani.backendinmobiliaria.models.ArchivoCrm;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Negocio;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.ActividadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ArchivoCrmRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ComunicacionRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.NegocioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ActividadService;
import com.rifatrichani.backendinmobiliaria.services.CorreoService;
import com.rifatrichani.backendinmobiliaria.services.impl.AlmacenArchivosCrm.ArchivoGuardado;

/* Registro de actividad de los contactos. Cualquier empleado del CRM ve el historial (igual que la tabla de contactos),
   pero las comunicaciones de los leads siguen la regla de /api/comunicacion: el vendedor solo ve las de sus inmuebles.
   Modificar o borrar una actividad: su autor, el propietario del contacto o un administrador */
@Service
public class ActividadServiceImpl implements ActividadService {
    private final ActividadRepository actividadRepository;
    private final ArchivoCrmRepository archivoRepository;
    private final ComunicacionRepository comunicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final NegocioRepository negocioRepository;
    private final AlmacenArchivosCrm almacen;
    private final CorreoService correoService;

    public ActividadServiceImpl(ActividadRepository ar, ArchivoCrmRepository acr, ComunicacionRepository cr, UsuarioRepository ur,
                                EmpleadoRepository er, NegocioRepository nr, AlmacenArchivosCrm al, CorreoService cs) {
        this.actividadRepository = ar;
        this.archivoRepository = acr;
        this.comunicacionRepository = cr;
        this.usuarioRepository = ur;
        this.empleadoRepository = er;
        this.negocioRepository = nr;
        this.almacen = al;
        this.correoService = cs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDTO> historialContacto(Integer contactoId, CuentaAutenticada empleado) {
        if (!usuarioRepository.existsById(contactoId)) {
            throw new ResourceNotFoundException("Contacto no encontrado con ID: " + contactoId);
        }
        List<ActividadResponseDTO> historial = new ArrayList<>(mapear(actividadRepository.findByContactoIdOrderByFechaCreacionDescIdDesc(contactoId)));
        comunicacionRepository.obtenerPorUsuarioId(contactoId).stream()
                .filter(c -> empleado.esAdmin() || empleado.id().equals(c.getLead().getInmueble().getEmpleado().getId()))
                .map(ComunicacionServiceImpl::mapearAComunicacionDTO)
                .map(c -> MapeosCrm.comunicacion(c, contactoId))
                .forEach(historial::add);
        historial.sort(Comparator.comparing(ActividadResponseDTO::fechaCreacion, Comparator.nullsLast(Comparator.reverseOrder())));
        return historial;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDTO> historialNegocio(Integer negocioId) {
        return mapear(actividadRepository.findByNegocioIdOrderByFechaCreacionDescIdDesc(negocioId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivoResponseDTO> adjuntosContacto(Integer contactoId) {
        return archivoRepository.obtenerPorContacto(contactoId).stream().map(ArchivoResponseDTO::de).toList();
    }

    @Override
    @Transactional
    public ActividadResponseDTO crear(ActividadRequestDTO dto, List<MultipartFile> archivos, CuentaAutenticada empleado) {
        List<MultipartFile> adjuntos = archivos == null ? List.of() : archivos.stream().filter(a -> a != null && !a.isEmpty()).toList();
        if (adjuntos.size() > MAXIMO_ADJUNTOS) {
            throw new IllegalArgumentException("Se pueden adjuntar como máximo " + MAXIMO_ADJUNTOS + " archivos por actividad.");
        }
        Actividad.Tipo tipo = Actividad.Tipo.valueOf(dto.tipo());
        Usuario contacto = usuarioRepository.findById(dto.contactoId())
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado con ID: " + dto.contactoId()));
        validar(tipo, dto);

        Actividad actividad = new Actividad();
        actividad.setTipo(tipo);
        actividad.setContacto(contacto);
        actividad.setEmpleado(empleadoRepository.getReferenceById(empleado.id()));
        actividad.setAsunto(textoOpcional(dto.asunto()));
        actividad.setDescripcion(textoOpcional(dto.descripcion()));
        actividad.setFechaProgramada(dto.fechaProgramada());
        actividad.setResultado(tipo == Actividad.Tipo.LLAMADA ? textoOpcional(dto.resultado()) : null);
        actividad.setDuracionMinutos(tipo == Actividad.Tipo.LLAMADA || tipo == Actividad.Tipo.REUNION ? dto.duracionMinutos() : null);
        //Notas, correos y llamadas ya ocurrieron; tareas y reuniones quedan pendientes
        actividad.setCompletada(tipo != Actividad.Tipo.TAREA && tipo != Actividad.Tipo.REUNION);
        if (tipo == Actividad.Tipo.TAREA) {
            //Tarea rapida del registro del contacto: el modulo de Tareas permite elegir tipo y prioridad
            actividad.setPrioridad("media");
            actividad.cambiarEstadoTarea(Actividad.PENDIENTE);
        }
        if (dto.negocioId() != null) {
            Negocio negocio = negocioRepository.findById(dto.negocioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + dto.negocioId()));
            if (!negocio.getContacto().getId().equals(contacto.getId())) {
                throw new IllegalArgumentException("El negocio indicado no pertenece a este contacto.");
            }
            actividad.setNegocio(negocio);
        }
        actividadRepository.save(actividad);

        //Los archivos se escriben en disco antes del commit: si algo falla despues se borran para no dejar huerfanos
        List<ArchivoGuardado> guardados = new ArrayList<>();
        try {
            List<ArchivoCrm> registros = new ArrayList<>();
            for (MultipartFile adjunto : adjuntos) {
                ArchivoGuardado guardado = almacen.guardar(adjunto);
                guardados.add(guardado);
                ArchivoCrm registro = new ArchivoCrm();
                registro.setNombre(guardado.nombre());
                registro.setRuta(guardado.ruta());
                registro.setTipoContenido(guardado.tipoContenido());
                registro.setTamano(guardado.tamano());
                registro.setActividad(actividad);
                registro.setEmpleado(actividad.getEmpleado());
                registros.add(archivoRepository.save(registro));
            }
            if (tipo == Actividad.Tipo.CORREO && dto.enviarCorreo()) {
                Empleado autor = empleadoRepository.findById(empleado.id()).orElseThrow();
                correoService.enviarMensaje(contacto.getCorreo(), actividad.getAsunto(),
                        actividad.getDescripcion() != null ? actividad.getDescripcion() : "", MapeosCrm.nombreEmpleado(autor), adjuntos);
            }
            usuarioRepository.registrarActividad(contacto.getId());
            return MapeosCrm.actividad(actividad, registros.stream().map(ArchivoResponseDTO::de).toList());
        } catch (RuntimeException e) {
            guardados.forEach(g -> almacen.eliminar(g.ruta()));
            throw e;
        }
    }

    @Override
    @Transactional
    public void marcarCompletada(Integer id, boolean completada, CuentaAutenticada empleado) {
        Actividad actividad = buscar(id);
        verificarPermiso(actividad, empleado);
        if (actividad.getTipo() == Actividad.Tipo.TAREA) {
            actividad.cambiarEstadoTarea(completada ? Actividad.COMPLETADA : Actividad.PENDIENTE);
        } else {
            actividad.setCompletada(completada);
        }
        actividadRepository.save(actividad);
    }

    //Los cambios de etapa no se borran: son la traza del negocio
    @Override
    @Transactional
    public void eliminar(Integer id, CuentaAutenticada empleado) {
        Actividad actividad = buscar(id);
        if (actividad.getTipo() == Actividad.Tipo.ETAPA) {
            throw new IllegalArgumentException("Los cambios de etapa de un negocio no se pueden eliminar.");
        }
        verificarPermiso(actividad, empleado);
        List<ArchivoCrm> archivos = archivoRepository.findByActividadId(id);
        archivoRepository.deleteAll(archivos);
        actividadRepository.delete(actividad);
        actividadRepository.flush();
        archivos.forEach(a -> almacen.eliminar(a.getRuta()));
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescarga descargarArchivo(Integer archivoId) {
        ArchivoCrm archivo = archivoRepository.findById(archivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado con ID: " + archivoId));
        return new ArchivoDescarga(archivo.getNombre(), archivo.getTipoContenido(), almacen.leer(archivo.getRuta()));
    }

    @Override
    @Transactional
    public Actividad registrarEtapa(Negocio negocio, Empleado autor, String descripcion) {
        Actividad actividad = new Actividad();
        actividad.setTipo(Actividad.Tipo.ETAPA);
        actividad.setContacto(negocio.getContacto());
        actividad.setNegocio(negocio);
        actividad.setEmpleado(autor);
        actividad.setAsunto(negocio.getNombre());
        actividad.setDescripcion(descripcion);
        actividad.setCompletada(true);
        usuarioRepository.registrarActividad(negocio.getContacto().getId());
        return actividadRepository.save(actividad);
    }

    private static void validar(Actividad.Tipo tipo, ActividadRequestDTO dto) {
        boolean sinDescripcion = dto.descripcion() == null || dto.descripcion().isBlank();
        boolean sinAsunto = dto.asunto() == null || dto.asunto().isBlank();
        switch (tipo) {
            case NOTA -> { if (sinDescripcion) throw new IllegalArgumentException("Escriba el contenido de la nota."); }
            case CORREO -> {
                if (sinAsunto) throw new IllegalArgumentException("El correo necesita un asunto.");
                if (sinDescripcion) throw new IllegalArgumentException("Escriba el mensaje del correo.");
            }
            case TAREA, REUNION -> {
                if (sinAsunto) throw new IllegalArgumentException(tipo == Actividad.Tipo.TAREA ? "Indique el título de la tarea." : "Indique el título de la reunión.");
                if (dto.fechaProgramada() == null) {
                    throw new IllegalArgumentException(tipo == Actividad.Tipo.TAREA ? "Indique la fecha de vencimiento de la tarea." : "Indique la fecha de la reunión.");
                }
            }
            case LLAMADA -> { if (dto.resultado() == null || dto.resultado().isBlank()) throw new IllegalArgumentException("Indique el resultado de la llamada."); }
            default -> throw new IllegalArgumentException("Tipo de actividad inválido.");
        }
    }

    static void verificarPermiso(Actividad actividad, CuentaAutenticada empleado) {
        if (empleado.esAdmin()) return;
        boolean esAutor = actividad.getEmpleado() != null && empleado.id().equals(actividad.getEmpleado().getId());
        Empleado propietario = actividad.getContacto() != null ? actividad.getContacto().getPropietario() : null;
        boolean esPropietario = propietario != null && empleado.id().equals(propietario.getId());
        if (!esAutor && !esPropietario) {
            throw new AccessDeniedException("Solo el autor de la actividad, el propietario del contacto o un administrador pueden modificarla.");
        }
    }

    private Actividad buscar(Integer id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
    }

    //Los adjuntos de toda la lista en una sola consulta
    private List<ActividadResponseDTO> mapear(List<Actividad> actividades) {
        if (actividades.isEmpty()) return List.of();
        Map<Integer, List<ArchivoResponseDTO>> archivos = archivoRepository
                .obtenerPorActividades(actividades.stream().map(Actividad::getId).toList()).stream()
                .map(ArchivoResponseDTO::de)
                .collect(Collectors.groupingBy(ArchivoResponseDTO::actividadId));
        return actividades.stream().map(a -> MapeosCrm.actividad(a, archivos.getOrDefault(a.getId(), List.of()))).toList();
    }

    private static String textoOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
