package com.rifatrichani.backendinmobiliaria.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroContactoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.SolicitarCodigoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.ValidarCodigoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoDetalleResponseDTO.InmuebleActividadDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoContactosDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.OpcionFiltroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.EstatusLead;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.ComunicacionRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusLeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.VistoRepository;
import com.rifatrichani.backendinmobiliaria.security.CodigosSeguridad;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ColumnaContacto;
import com.rifatrichani.backendinmobiliaria.services.ContactoService;
import com.rifatrichani.backendinmobiliaria.services.CorreoService;

/* Modulo de Contactos del CRM. Un contacto es un Usuario; su propietario es el vendedor asignado (Usuario.propietario)
   y su estatus es el del lead mas reciente. Vendedores y administradores ven todos los contactos (pestaña "Todos");
   las restricciones estan en el cambio de propietario y en las comunicaciones de la ficha */
@Service
public class ContactoServiceImpl implements ContactoService {
    private static final int VISTAS_EN_FICHA = 15;
    private static final Map<String, String> CAMPOS_ORDEN = Map.of(
            "fechaCreacion", "fechaCreacion",
            "nombre", "nombre",
            "correo", "correo",
            "telefono", "telefono",
            "ultimaActividad", "ultimaActividad",
            "propietario", "propietario.nombre");

    private final UsuarioRepository usuarioRepository;
    private final LeadRepository leadRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EstatusLeadRepository estatusLeadRepository;
    private final ComunicacionRepository comunicacionRepository;
    private final VistoRepository vistoRepository;
    private final InmuebleRepository inmuebleRepository;
    private final CorreoService correoService;
    private final ExportadorTablas exportador;

    public ContactoServiceImpl(UsuarioRepository ur, LeadRepository lr, EmpleadoRepository er, EstatusLeadRepository elr,
                               ComunicacionRepository cr, VistoRepository vr, 
                               InmuebleRepository ir, CorreoService cs, ExportadorTablas ex) {
        this.usuarioRepository = ur;
        this.leadRepository = lr;
        this.empleadoRepository = er;
        this.estatusLeadRepository = elr;
        this.comunicacionRepository = cr;
        this.vistoRepository = vr;
        this.inmuebleRepository = ir;
        this.correoService = cs;
        this.exportador = ex;
    }

    // ==========================================
    // VERIFICACIÓN PÚBLICA (PORTAL WEB)
    // ==========================================

    @Override
    @Transactional
    public boolean generarYEnviarCodigo(SolicitarCodigoDTO dto) {
        String correo = dto.correo().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo).orElse(null);

        // Un correo solo se puede registrar una vez: si ya fue verificado previamente no permite volver a registrarlo
        if (usuario != null && Boolean.TRUE.equals(usuario.getVerificado())) {
            throw new IllegalArgumentException("Este correo ya se encuentra registrado.");
        }

        String codigo = CodigosSeguridad.generarCodigo();
        if (usuario == null) {
            usuario = new Usuario();
        }

        usuario.setNombre(dto.nombre().trim());
        usuario.setApellido(dto.apellido().trim());
        usuario.setTelefono(dto.telefono().trim());
        usuario.setCorreo(correo);
        if (usuario.getTipoNegocio() == null || usuario.getTipoNegocio().isBlank()) {
            usuario.setTipoNegocio("Sin definir");
        }
        usuario.setCodVerif(codigo);
        usuario.setCodVerifExpira(LocalDateTime.now().plusMinutes(10));
        usuario.setCodVerifIntentos(0);
        usuario.setUltimaActividad(LocalDateTime.now());

        usuarioRepository.save(usuario);

        correoService.enviarCodigo(usuario.getCorreo(), usuario.getNombre(),
                "Código de Verificación",
                "Tu código para confirmar tus datos de contacto e interés en la propiedad.",
                codigo, 10);
        return true;
    }

    @Override
    @Transactional
    public boolean validarCodigoYCrearLead(ValidarCodigoDTO dto) {
        String correo = dto.correo() == null ? "" : dto.correo().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo).orElse(null);

        if (usuario == null || usuario.getCodVerif() == null) {
            return false;
        }

        int intentos = usuario.getCodVerifIntentos() != null ? usuario.getCodVerifIntentos() : 0;
        if (intentos >= 5 || (usuario.getCodVerifExpira() != null && LocalDateTime.now().isAfter(usuario.getCodVerifExpira()))) {
            return false;
        }

        String codigoIngresado = dto.codigo() == null ? "" : dto.codigo().trim();
        if (!String.valueOf(usuario.getCodVerif()).equals(codigoIngresado)) {
            usuario.setCodVerifIntentos(intentos + 1);
            usuarioRepository.save(usuario);
            return false;
        }

        usuario.setVerificado(true);
        usuario.setCodVerif(null);
        usuario.setCodVerifExpira(null);
        usuario.setCodVerifIntentos(0);
        usuario.setUltimaActividad(LocalDateTime.now());

        Inmueble inmueble = inmuebleRepository.findById(dto.idInmueble()).orElse(null);
        if (inmueble != null) {
            if (usuario.getPropietario() == null && inmueble.getEmpleado() != null) {
                usuario.setPropietario(inmueble.getEmpleado());
            }
            if (!leadRepository.existsByUsuarioIdAndInmuebleId(usuario.getId(), inmueble.getId())) {
                Lead lead = new Lead();
                lead.setUsuario(usuario);
                lead.setInmueble(inmueble);
                lead.setEstatus(LeadServiceImpl.ESTATUS_INICIAL);
                leadRepository.save(lead);
            }
        }

        usuarioRepository.save(usuario);
        return true;
    }

    // ==========================================
    // MODULO DE CONTACTOS (CRM)
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<ContactoResponseDTO> buscar(FiltroContactoDTO filtro, CuentaAutenticada empleado) {
        Page<Usuario> pagina = usuarioRepository.findAll(
                ContactoEspecificaciones.conFiltros(filtro, empleado.id()),
                PageRequest.of(filtro.getPagina(), filtro.getTamano(), orden(filtro)));
        return PaginaResponseDTO.de(pagina, this::mapear);
    }

    @Override
    @Transactional(readOnly = true)
    public ConteoContactosDTO contar(FiltroContactoDTO filtro, CuentaAutenticada empleado) {
        String vistaOriginal = filtro.getVista();
        try {
            filtro.setVista("todos");
            long todos = usuarioRepository.count(ContactoEspecificaciones.conFiltros(filtro, empleado.id()));
            filtro.setVista("mios");
            long mios = usuarioRepository.count(ContactoEspecificaciones.conFiltros(filtro, empleado.id()));
            filtro.setVista("sin_asignar");
            long sinAsignar = usuarioRepository.count(ContactoEspecificaciones.conFiltros(filtro, empleado.id()));
            return new ConteoContactosDTO(todos, mios, sinAsignar);
        } finally {
            filtro.setVista(vistaOriginal);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoExportado exportar(FiltroContactoDTO filtro, List<ColumnaContacto> columnas, FormatoExportacion formato,
                                     boolean soloPagina, CuentaAutenticada empleado) {
        Pageable paginacion = soloPagina
                ? PageRequest.of(filtro.getPagina(), filtro.getTamano(), orden(filtro))
                : PageRequest.of(0, MAXIMO_EXPORTACION, orden(filtro));
        Page<Usuario> pagina = usuarioRepository.findAll(ContactoEspecificaciones.conFiltros(filtro, empleado.id()), paginacion);
        if (!soloPagina && pagina.getTotalElements() > MAXIMO_EXPORTACION) {
            throw new IllegalArgumentException("Hay " + pagina.getTotalElements() + " contactos con estos filtros. Se pueden exportar como máximo "
                    + MAXIMO_EXPORTACION + ": agregue más filtros.");
        }
        List<ContactoResponseDTO> contactos = mapear(pagina.getContent());
        String nombreBase = "contactos-" + filtro.getVista().replace('_', '-') + "-" + LocalDate.now();
        return switch (formato) {
            case XLSX -> new ArchivoExportado(nombreBase + ".xlsx", ArchivoExportado.TIPO_XLSX,
                    exportador.excel("Contactos", contactos, columnas));
            case PDF -> new ArchivoExportado(nombreBase + ".pdf", "application/pdf",
                    exportador.pdf("Contactos", tituloVista(filtro.getVista()), "contactos", contactos, columnas));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public ContactoDetalleResponseDTO obtenerDetalle(Integer usuarioId, CuentaAutenticada empleado) {
        Usuario usuario = buscarContacto(usuarioId);
        ContactoDetalleResponseDTO dto = new ContactoDetalleResponseDTO();
        dto.setContacto(mapear(List.of(usuario)).get(0));
        dto.setLeads(leadRepository.obtenerPorUsuarioId(usuarioId).stream()
                .map(LeadServiceImpl::mapearALeadResponseDTO)
                .toList());
        dto.setComunicaciones(comunicacionRepository.obtenerPorUsuarioId(usuarioId).stream()
                .filter(c -> empleado.esAdmin() || empleado.id().equals(c.getLead().getInmueble().getEmpleado().getId()))
                .map(ComunicacionServiceImpl::mapearAComunicacionDTO)
                .toList());
        dto.setVistasRecientes(vistoRepository.obtenerRecientesPorUsuario(usuarioId, PageRequest.of(0, VISTAS_EN_FICHA)).stream()
                .map(v -> new InmuebleActividadDTO(v.getInmueble().getId(), v.getInmueble().getTitulo(),
                        v.getInmueble().getPrecio(), v.getInmueble().getVendido(), v.getFechaVista()))
                .toList());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ContactoResponseDTO obtenerResumen(Integer usuarioId) {
        return mapear(List.of(buscarContacto(usuarioId))).get(0);
    }

    @Override
    @Transactional
    public void cambiarPropietario(Integer usuarioId, Integer empleadoId, CuentaAutenticada empleado) {
        Usuario usuario = buscarContacto(usuarioId);
        Empleado actual = usuario.getPropietario();
        if (!empleado.esAdmin()) {
            boolean tomaSinAsignar = actual == null && empleado.id().equals(empleadoId);
            boolean liberaElSuyo = actual != null && empleado.id().equals(actual.getId()) && empleadoId == null;
            if (!tomaSinAsignar && !liberaElSuyo) {
                throw new AccessDeniedException("Un vendedor solo puede tomar contactos sin asignar o liberar los suyos.");
            }
        }
        Empleado nuevo = null;
        if (empleadoId != null) {
            nuevo = empleadoRepository.findById(empleadoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + empleadoId));
            if (!Boolean.TRUE.equals(nuevo.getActivo())) {
                throw new IllegalArgumentException("No se puede asignar un contacto a un empleado inactivo.");
            }
        }
        usuario.setPropietario(nuevo);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpcionFiltroDTO> obtenerPropietarios() {
        return empleadoRepository.findAll(Sort.by("nombre", "apellido")).stream()
                .map(e -> new OpcionFiltroDTO(e.getId(), nombreEmpleado(e) + (Boolean.TRUE.equals(e.getActivo()) ? "" : " (inactivo)")))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpcionFiltroDTO> obtenerEstatusLead() {
        return estatusLeadRepository.findAllByOrderByOrdenAsc().stream()
                .map(e -> new OpcionFiltroDTO(e.getId(), e.getNombre()))
                .toList();
    }

    private Usuario buscarContacto(Integer usuarioId) {
        return usuarioRepository.obtenerConPropietario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado con ID: " + usuarioId));
    }

    private static Sort orden(FiltroContactoDTO filtro) {
        Sort.Direction direccion = "asc".equals(filtro.getDireccion()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String campo = CAMPOS_ORDEN.getOrDefault(filtro.getOrden(), "fechaCreacion");
        Sort orden = Sort.by(new Sort.Order(direccion, campo).nullsLast());
        if ("nombre".equals(campo)) orden = orden.and(Sort.by(direccion, "apellido"));
        return orden.and(Sort.by(direccion, "id"));
    }

    private List<ContactoResponseDTO> mapear(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) return List.of();
        List<Integer> ids = usuarios.stream().map(Usuario::getId).toList();
        Map<Integer, Integer> estatusPorUsuario = leadRepository.estatusMasRecientePorUsuario(ids).stream()
                .collect(Collectors.toMap(fila -> (Integer) fila[0], fila -> (Integer) fila[1]));
        Map<Integer, Long> leadsPorUsuario = leadRepository.contarPorUsuario(ids).stream()
                .collect(Collectors.toMap(fila -> (Integer) fila[0], fila -> (Long) fila[1]));
        Map<Integer, String> nombresEstatus = estatusLeadRepository.findAll().stream()
                .collect(Collectors.toMap(EstatusLead::getId, EstatusLead::getNombre));
        Function<Integer, String> nombreEstatus = id -> id == null ? null : nombresEstatus.getOrDefault(id, "Estatus " + id);

        return usuarios.stream().map(u -> {
            ContactoResponseDTO dto = new ContactoResponseDTO();
            dto.setId(u.getId());
            dto.setNombre(u.getNombre());
            dto.setApellido(u.getApellido());
            dto.setCorreo(u.getCorreo());
            dto.setTelefono(u.getTelefono());
            dto.setTipoNegocio(u.getTipoNegocio());
            dto.setVerificado(u.getVerificado());
            dto.setFechaCreacion(u.getFechaCreacion());
            dto.setUltimaActividad(u.getUltimaActividad());
            if (u.getPropietario() != null) {
                dto.setPropietarioId(u.getPropietario().getId());
                dto.setNombrePropietario(nombreEmpleado(u.getPropietario()));
            }
            Integer estatus = estatusPorUsuario.get(u.getId());
            dto.setEstatusId(estatus);
            dto.setEstatus(nombreEstatus.apply(estatus));
            dto.setCantidadLeads(leadsPorUsuario.getOrDefault(u.getId(), 0L));
            return dto;
        }).toList();
    }

    private static String nombreEmpleado(Empleado e) {
        return e.getNombre() + (e.getApellido() != null ? " " + e.getApellido() : "");
    }

    private static String tituloVista(String vista) {
        return switch (vista) {
            case "mios" -> "Mis contactos";
            case "sin_asignar" -> "Contactos sin asignar";
            default -> "Todos los contactos";
        };
    }
}

