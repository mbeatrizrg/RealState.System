package com.rifatrichani.backendinmobiliaria.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroNegocioDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.NegocioRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EtapaNegocioDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ImagenResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.NegocioDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.NegocioResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.TableroNegociosDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.EtapaNegocio;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Negocio;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.ActividadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EtapaNegocioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.NegocioEspecificaciones;
import com.rifatrichani.backendinmobiliaria.repositories.NegocioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.ActividadService;
import com.rifatrichani.backendinmobiliaria.services.ColumnaNegocio;
import com.rifatrichani.backendinmobiliaria.services.ContactoService;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.NegocioService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/* Modulo de Negocios. Todos los empleados ven todos los negocios (pestaña "Todos"); editar, mover de etapa o borrar
   lo pueden hacer el propietario del negocio, cualquiera si esta sin asignar, o un administrador.
   Un vendedor solo puede ponerse a si mismo como propietario o dejar el negocio sin asignar */
@Service
public class NegocioServiceImpl implements NegocioService {
    private static final Map<String, String> CAMPOS_ORDEN = Map.of(
            "fechaCreacion", "fechaCreacion",
            "nombre", "nombre",
            "valor", "valor",
            "fechaCierre", "fechaCierreEstimada",
            "etapa", "etapa.orden",
            "propietario", "propietario.nombre",
            "contacto", "contacto.nombre");

    private final NegocioRepository negocioRepository;
    private final EtapaNegocioRepository etapaRepository;
    private final InmuebleRepository inmuebleRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final GaleriaRepository galeriaRepository;
    private final ActividadRepository actividadRepository;
    private final ActividadService actividadService;
    private final ContactoService contactoService;
    private final ExportadorTablas exportador;
    private final EntityManager em;

    public NegocioServiceImpl(NegocioRepository nr, EtapaNegocioRepository etr, InmuebleRepository ir, UsuarioRepository ur,
                              EmpleadoRepository er, GaleriaRepository gr, ActividadRepository ar, ActividadService as,
                              ContactoService cs, ExportadorTablas ex, EntityManager em) {
        this.negocioRepository = nr;
        this.etapaRepository = etr;
        this.inmuebleRepository = ir;
        this.usuarioRepository = ur;
        this.empleadoRepository = er;
        this.galeriaRepository = gr;
        this.actividadRepository = ar;
        this.actividadService = as;
        this.contactoService = cs;
        this.exportador = ex;
        this.em = em;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<NegocioResponseDTO> buscar(FiltroNegocioDTO filtro, CuentaAutenticada empleado) {
        Page<Negocio> pagina = negocioRepository.findAll(NegocioEspecificaciones.conFiltros(filtro, empleado.id()),
                PageRequest.of(filtro.getPagina(), filtro.getTamano(), orden(filtro)));
        return PaginaResponseDTO.de(pagina, negocios -> negocios.stream().map(MapeosCrm::negocio).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConteoPestanasDTO contar(FiltroNegocioDTO filtro, CuentaAutenticada empleado) {
        String vistaOriginal = filtro.getVista();
        try {
            filtro.setVista("todos");
            long todos = negocioRepository.count(NegocioEspecificaciones.conFiltros(filtro, empleado.id()));
            filtro.setVista("mios");
            long mios = negocioRepository.count(NegocioEspecificaciones.conFiltros(filtro, empleado.id()));
            filtro.setVista("sin_asignar");
            long sinAsignar = negocioRepository.count(NegocioEspecificaciones.conFiltros(filtro, empleado.id()));
            return new ConteoPestanasDTO(todos, mios, sinAsignar);
        } finally {
            filtro.setVista(vistaOriginal);
        }
    }

    /* Una columna por etapa (solo las elegidas en el filtro, si hay). La cantidad y el monto total se calculan con una
       sola consulta agrupada; las tarjetas son las primeras TARJETAS_POR_COLUMNA de cada etapa */
    @Override
    @Transactional(readOnly = true)
    public TableroNegociosDTO tablero(FiltroNegocioDTO filtro, CuentaAutenticada empleado) {
        Map<Integer, Object[]> totales = totalesPorEtapa(NegocioEspecificaciones.conFiltros(filtro, empleado.id()));
        List<Integer> etapasFiltro = filtro.getEtapas();
        List<TableroNegociosDTO.Columna> columnas = new ArrayList<>();
        try {
            for (EtapaNegocio etapa : etapaRepository.findAllByOrderByOrdenAsc()) {
                if (etapasFiltro != null && !etapasFiltro.isEmpty() && !etapasFiltro.contains(etapa.getId())) continue;
                Object[] total = totales.get(etapa.getId());
                long cantidad = total == null ? 0 : (Long) total[0];
                BigDecimal monto = total == null || total[1] == null ? BigDecimal.ZERO : (BigDecimal) total[1];
                List<NegocioResponseDTO> tarjetas = List.of();
                if (cantidad > 0) {
                    filtro.setEtapas(List.of(etapa.getId()));
                    tarjetas = negocioRepository.findAll(NegocioEspecificaciones.conFiltros(filtro, empleado.id()),
                                    PageRequest.of(0, TARJETAS_POR_COLUMNA, orden(filtro)))
                            .map(MapeosCrm::negocio).getContent();
                }
                columnas.add(new TableroNegociosDTO.Columna(etapa.getId(), etapa.getNombre(), Boolean.TRUE.equals(etapa.getAbierto()),
                        cantidad, monto, tarjetas));
            }
        } finally {
            filtro.setEtapas(etapasFiltro);
        }
        return new TableroNegociosDTO(columnas, TARJETAS_POR_COLUMNA);
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoExportado exportar(FiltroNegocioDTO filtro, List<ColumnaNegocio> columnas, FormatoExportacion formato,
                                     boolean soloPagina, CuentaAutenticada empleado) {
        Pageable paginacion = soloPagina
                ? PageRequest.of(filtro.getPagina(), filtro.getTamano(), orden(filtro))
                : PageRequest.of(0, ContactoService.MAXIMO_EXPORTACION, orden(filtro));
        Page<Negocio> pagina = negocioRepository.findAll(NegocioEspecificaciones.conFiltros(filtro, empleado.id()), paginacion);
        if (!soloPagina && pagina.getTotalElements() > ContactoService.MAXIMO_EXPORTACION) {
            throw new IllegalArgumentException("Hay " + pagina.getTotalElements() + " negocios con estos filtros. Se pueden exportar como máximo "
                    + ContactoService.MAXIMO_EXPORTACION + ": agregue más filtros.");
        }
        List<NegocioResponseDTO> negocios = pagina.getContent().stream().map(MapeosCrm::negocio).toList();
        String nombreBase = "negocios-" + filtro.getVista().replace('_', '-') + "-" + LocalDate.now();
        return switch (formato) {
            case XLSX -> new ArchivoExportado(nombreBase + ".xlsx", ArchivoExportado.TIPO_XLSX, exportador.excel("Negocios", negocios, columnas));
            case PDF -> new ArchivoExportado(nombreBase + ".pdf", "application/pdf",
                    exportador.pdf("Negocios", tituloVista(filtro.getVista()), "negocios", negocios, columnas));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<EtapaNegocioDTO> obtenerEtapas() {
        return etapaRepository.findAllByOrderByOrdenAsc().stream()
                .map(e -> new EtapaNegocioDTO(e.getId(), e.getNombre(), Boolean.TRUE.equals(e.getAbierto())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NegocioDetalleResponseDTO obtenerDetalle(Integer id, CuentaAutenticada empleado) {
        Negocio negocio = buscarNegocio(id);
        Inmueble inmueble = negocio.getInmueble();
        List<Galeria> galeria = galeriaRepository.findByInmuebleIdOrderByIdAsc(inmueble.getId());
        long abiertos = negocioRepository.contarAbiertosPorInmueble(List.of(inmueble.getId())).stream()
                .findFirst().map(fila -> (Long) fila[1]).orElse(0L);
        return new NegocioDetalleResponseDTO(
                MapeosCrm.negocio(negocio),
                MapeosCrm.propiedad(inmueble, abiertos, galeria.isEmpty() ? null : galeria.get(0).getUrlImagen()),
                galeria.stream().map(g -> new ImagenResponseDTO(g.getId(), g.getUrlImagen())).toList(),
                contactoService.obtenerResumen(negocio.getContacto().getId()),
                actividadService.historialNegocio(id),
                puedeEditar(negocio, empleado));
    }

    @Override
    @Transactional
    public Integer crear(NegocioRequestDTO dto, CuentaAutenticada empleado) {
        Negocio negocio = new Negocio();
        copiarDatos(dto, negocio, empleado);
        EtapaNegocio etapa = buscarEtapa(dto.etapaId());
        negocio.setEtapa(etapa);
        negocio.setFechaCierre(Boolean.TRUE.equals(etapa.getAbierto()) ? null : LocalDateTime.now());
        negocioRepository.save(negocio);
        actividadService.registrarEtapa(negocio, empleadoRepository.getReferenceById(empleado.id()),
                "Negocio creado en la etapa \"" + etapa.getNombre() + "\".");
        return negocio.getId();
    }

    @Override
    @Transactional
    public void actualizar(Integer id, NegocioRequestDTO dto, CuentaAutenticada empleado) {
        Negocio negocio = buscarNegocio(id);
        verificarEdicion(negocio, empleado);
        copiarDatos(dto, negocio, empleado);
        moverAEtapa(negocio, dto.etapaId(), empleado);
        negocioRepository.save(negocio);
    }

    @Override
    @Transactional
    public void cambiarEtapa(Integer id, Integer etapaId, CuentaAutenticada empleado) {
        Negocio negocio = buscarNegocio(id);
        verificarEdicion(negocio, empleado);
        moverAEtapa(negocio, etapaId, empleado);
        negocioRepository.save(negocio);
    }

    @Override
    @Transactional
    public void eliminar(Integer id, CuentaAutenticada empleado) {
        Negocio negocio = buscarNegocio(id);
        verificarEdicion(negocio, empleado);
        actividadRepository.desvincularNegocio(id);
        negocioRepository.delete(negocio);
        negocioRepository.flush();
    }

    //Registra el cambio en el historial y la fecha real de cierre (se limpia si el negocio se reabre)
    private void moverAEtapa(Negocio negocio, Integer etapaId, CuentaAutenticada empleado) {
        EtapaNegocio anterior = negocio.getEtapa();
        if (anterior != null && anterior.getId().equals(etapaId)) return;
        EtapaNegocio nueva = buscarEtapa(etapaId);
        negocio.setEtapa(nueva);
        negocio.setFechaCierre(Boolean.TRUE.equals(nueva.getAbierto()) ? null : LocalDateTime.now());
        actividadService.registrarEtapa(negocio, empleadoRepository.getReferenceById(empleado.id()),
                "Etapa cambiada de \"" + (anterior != null ? anterior.getNombre() : "-") + "\" a \"" + nueva.getNombre() + "\".");
    }

    private void copiarDatos(NegocioRequestDTO dto, Negocio negocio, CuentaAutenticada empleado) {
        Inmueble inmueble = inmuebleRepository.findById(dto.inmuebleId())
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + dto.inmuebleId()));
        Usuario contacto = usuarioRepository.findById(dto.contactoId())
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado con ID: " + dto.contactoId()));
        negocio.setNombre(dto.nombre().trim());
        negocio.setInmueble(inmueble);
        negocio.setContacto(contacto);
        negocio.setTipoOperacion(dto.tipoOperacion());
        negocio.setValor(dto.valor());
        negocio.setFechaCierreEstimada(dto.fechaCierreEstimada());
        negocio.setPropietario(resolverPropietario(dto.propietarioId(), negocio.getPropietario(), empleado));
    }

    private Empleado resolverPropietario(Integer propietarioId, Empleado actual, CuentaAutenticada empleado) {
        if (propietarioId == null) return null;
        if (actual != null && actual.getId().equals(propietarioId)) return actual;
        if (!empleado.esAdmin() && !empleado.id().equals(propietarioId)) {
            throw new AccessDeniedException("Un vendedor solo puede asignarse el negocio a sí mismo o dejarlo sin asignar.");
        }
        Empleado propietario = empleadoRepository.findById(propietarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con ID: " + propietarioId));
        if (!Boolean.TRUE.equals(propietario.getActivo())) {
            throw new IllegalArgumentException("No se puede asignar un negocio a un empleado inactivo.");
        }
        return propietario;
    }

    private static boolean puedeEditar(Negocio negocio, CuentaAutenticada empleado) {
        return empleado.esAdmin() || negocio.getPropietario() == null || empleado.id().equals(negocio.getPropietario().getId());
    }

    private static void verificarEdicion(Negocio negocio, CuentaAutenticada empleado) {
        if (!puedeEditar(negocio, empleado)) {
            throw new AccessDeniedException("Solo el propietario del negocio o un administrador puede modificarlo.");
        }
    }

    //[cantidad, suma del valor] por etapa con los mismos filtros de la tabla
    private Map<Integer, Object[]> totalesPorEtapa(Specification<Negocio> spec) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> consulta = cb.createQuery(Object[].class);
        Root<Negocio> root = consulta.from(Negocio.class);
        consulta.multiselect(root.get("etapa").get("id"), cb.count(root), cb.sum(root.<BigDecimal>get("valor")))
                .where(spec.toPredicate(root, consulta, cb))
                .groupBy(root.get("etapa").get("id"));
        Map<Integer, Object[]> totales = new HashMap<>();
        for (Object[] fila : em.createQuery(consulta).getResultList()) {
            totales.put((Integer) fila[0], new Object[] {fila[1], fila[2]});
        }
        return totales;
    }

    private Negocio buscarNegocio(Integer id) {
        return negocioRepository.obtenerConRelaciones(id)
                .orElseThrow(() -> new ResourceNotFoundException("Negocio no encontrado con ID: " + id));
    }

    private EtapaNegocio buscarEtapa(Integer id) {
        return etapaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Etapa de negocio inválida: " + id));
    }

    private static Sort orden(FiltroNegocioDTO filtro) {
        Sort.Direction direccion = "asc".equals(filtro.getDireccion()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String campo = CAMPOS_ORDEN.getOrDefault(filtro.getOrden(), "fechaCreacion");
        return Sort.by(new Sort.Order(direccion, campo).nullsLast()).and(Sort.by(direccion, "id"));
    }

    private static String tituloVista(String vista) {
        return switch (vista) {
            case "mios" -> "Mis negocios";
            case "sin_asignar" -> "Negocios sin asignar";
            default -> "Todos los negocios";
        };
    }
}
