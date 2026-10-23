package com.rifatrichani.backendinmobiliaria.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroEstadisticaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FiltroPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.Accion;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.ContactoInteraccion;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.EtapaValor;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.FilaInmueble;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.FilaVendedor;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.InteraccionInmueble;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.Porcion;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.PuntoSerie;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.PuntoVentas;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.Resumen;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.Serie;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Actividad;
import com.rifatrichani.backendinmobiliaria.models.Comunicacion;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.EstatusLead;
import com.rifatrichani.backendinmobiliaria.models.EtapaNegocio;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.models.Negocio;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.ActividadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.ComunicacionRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusLeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EtapaNegocioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.NegocioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.PropiedadEspecificaciones;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.EstadisticaService;
import com.rifatrichani.backendinmobiliaria.services.impl.ExportadorTablas.ColumnaSimple;
import com.rifatrichani.backendinmobiliaria.services.impl.ExportadorTablas.Seccion;

/* Las estadisticas se calculan en memoria a partir de los inmuebles que cumplen los filtros y sus leads, negocios,
   mensajes y visitas: el volumen de una inmobiliaria cabe de sobra y asi cada metrica usa exactamente la misma
   definicion en las tarjetas, las graficas y las tablas.

   Nivel de interaccion de un par cliente-inmueble = el punto mas avanzado entre:
     - su lead:    Nuevo/Contactado/Calificado -> solo pidio informacion, Visita agendada -> agendo visita,
                   En negociacion -> presento oferta, Ganado -> compro/alquilo, Perdido -> descarto
     - su negocio: Prospecto -> informacion, Cita/visita programada -> agendo, Visita realizada -> visito,
                   Oferta y Tramites -> oferta, Cierre ganado -> compro/alquilo, Cierre perdido -> descarto
     - una visita realizada (tarea VISITA o reunion completada) -> al menos visito
   "Descarto" solo se usa si todas sus fuentes son perdidas */
@Service
public class EstadisticaServiceImpl implements EstadisticaService {
    private static final Locale ES = Locale.forLanguageTag("es-VE");
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMM yyyy", ES);
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("d MMM", ES);
    private static final int MAXIMO_TIPOS = 5;
    private static final int MAXIMO_ZONAS = 8;
    private static final int MAXIMO_PROPIEDADES = 10;
    private static final int MAXIMO_FILAS = 200;

    enum Nivel {
        INFORMACION("Solo pidió información"),
        AGENDO("Agendó visita"),
        VISITO("Realizó visita física"),
        OFERTA("Presentó oferta"),
        CERRO("Compró / alquiló"),
        DESCARTO("Descartó");

        final String etiqueta;
        Nivel(String etiqueta) { this.etiqueta = etiqueta; }

        static Nivel deLead(Integer estatus) {
            if (estatus == null) return INFORMACION;
            return switch (estatus) {
                case 4 -> AGENDO;
                case 5 -> OFERTA;
                case 6 -> CERRO;
                case 7 -> DESCARTO;
                default -> INFORMACION;
            };
        }

        static Nivel deEtapa(Integer etapa) {
            return switch (etapa) {
                case 2 -> AGENDO;
                case 3 -> VISITO;
                case 4, 5 -> OFERTA;
                case EtapaNegocio.CIERRE_GANADO -> CERRO;
                case EtapaNegocio.CIERRE_PERDIDO -> DESCARTO;
                default -> INFORMACION;
            };
        }
    }

    //Periodo [desde 00:00, hasta + 1 dia 00:00). Sin fechas no limita
    record Rango(LocalDate desde, LocalDate hasta) {
        boolean contiene(LocalDateTime fecha) {
            if (fecha == null) return false;
            if (desde != null && fecha.isBefore(desde.atStartOfDay())) return false;
            return hasta == null || fecha.isBefore(hasta.plusDays(1).atStartOfDay());
        }
    }

    //Todo lo que un cliente hizo con un inmueble
    static final class Par {
        final Usuario contacto;
        final Inmueble inmueble;
        final List<Lead> leads = new ArrayList<>();
        final List<Negocio> negocios = new ArrayList<>();
        final List<Actividad> visitas = new ArrayList<>();
        final List<Comunicacion> mensajes = new ArrayList<>();

        Par(Usuario contacto, Inmueble inmueble) {
            this.contacto = contacto;
            this.inmueble = inmueble;
        }

        Nivel nivel() {
            List<Nivel> fuentes = new ArrayList<>();
            leads.forEach(l -> fuentes.add(Nivel.deLead(l.getEstatus())));
            negocios.forEach(n -> fuentes.add(Nivel.deEtapa(n.getEtapa().getId())));
            if (!visitas.isEmpty()) fuentes.add(Nivel.VISITO);
            List<Nivel> vigentes = fuentes.stream().filter(n -> n != Nivel.DESCARTO).toList();
            if (vigentes.isEmpty()) return fuentes.isEmpty() ? Nivel.INFORMACION : Nivel.DESCARTO;
            return vigentes.stream().max(Comparator.naturalOrder()).orElse(Nivel.INFORMACION);
        }

        boolean activoEn(Rango rango) {
            return leads.stream().anyMatch(l -> rango.contiene(l.getFechaCreacion()))
                    || negocios.stream().anyMatch(n -> rango.contiene(n.getFechaCreacion()) || rango.contiene(n.getFechaCierre()))
                    || visitas.stream().anyMatch(v -> rango.contiene(fechaVisita(v)))
                    || mensajes.stream().anyMatch(m -> rango.contiene(m.getFechaCreacion()));
        }

        LocalDateTime ultimaInteraccion() {
            List<LocalDateTime> fechas = new ArrayList<>();
            leads.forEach(l -> fechas.add(l.getFechaCreacion()));
            negocios.forEach(n -> fechas.add(n.getFechaActualizacion()));
            visitas.forEach(v -> fechas.add(fechaVisita(v)));
            mensajes.forEach(m -> fechas.add(m.getFechaCreacion()));
            return fechas.stream().filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
        }
    }

    //Datos de los inmuebles filtrados, agrupados por par cliente-inmueble
    record Datos(List<Inmueble> inmuebles, List<Lead> leads, List<Negocio> negocios, List<Actividad> visitas,
                 List<Comunicacion> mensajes, Map<String, Par> pares) {}

    private final InmuebleRepository inmuebleRepository;
    private final LeadRepository leadRepository;
    private final NegocioRepository negocioRepository;
    private final ActividadRepository actividadRepository;
    private final ComunicacionRepository comunicacionRepository;
    private final EtapaNegocioRepository etapaRepository;
    private final EstatusLeadRepository estatusLeadRepository;
    private final ExportadorTablas exportador;

    public EstadisticaServiceImpl(InmuebleRepository ir, LeadRepository lr, NegocioRepository nr, ActividadRepository ar,
                                  ComunicacionRepository cr, EtapaNegocioRepository er, EstatusLeadRepository elr, ExportadorTablas ex) {
        this.inmuebleRepository = ir;
        this.leadRepository = lr;
        this.negocioRepository = nr;
        this.actividadRepository = ar;
        this.comunicacionRepository = cr;
        this.etapaRepository = er;
        this.estatusLeadRepository = elr;
        this.exportador = ex;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasDTO calcular(FiltroEstadisticaDTO f) {
        if (f.getDesde() != null && f.getHasta() != null && f.getDesde().isAfter(f.getHasta())) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final.");
        }
        Rango rango = new Rango(f.getDesde(), f.getHasta());
        LocalDateTime ahora = LocalDateTime.now();

        // ----- Interaccion con inmuebles: el vendedor es el asesor encargado del inmueble -----
        Datos d = cargar(f, true);
        List<Par> paresEnRango = d.pares().values().stream().filter(p -> p.activoEn(rango)).toList();
        List<Negocio> ganadosRango = d.negocios().stream().filter(n -> esGanado(n) && rango.contiene(n.getFechaCierre())).toList();
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        Rango mes = new Rango(inicioMes, inicioMes.plusMonths(1).minusDays(1));
        Resumen resumen = new Resumen(
                d.leads().stream().filter(l -> rango.contiene(l.getFechaCreacion())).count(),
                d.mensajes().stream().filter(m -> rango.contiene(m.getFechaCreacion())).count(),
                d.visitas().stream().filter(v -> rango.contiene(fechaVisita(v))).count(),
                d.negocios().stream().filter(n -> Boolean.TRUE.equals(n.getEtapa().getAbierto())).count(),
                d.negocios().stream().filter(n -> esGanado(n) && mes.contiene(n.getFechaCierre())).count(),
                ganadosRango.size(),
                suma(ganadosRango),
                paresEnRango.stream().map(p -> p.contacto.getId()).distinct().count());

        Nivel nivelFiltro = f.getNivel() != null ? Nivel.valueOf(f.getNivel()) : null;

        // ----- Rendimiento de ventas: el vendedor es el propietario del negocio -----
        List<Negocio> negociosVentas = cargar(f, false).negocios().stream()
                .filter(n -> f.getVendedorIds() == null || f.getVendedorIds().isEmpty()
                        || (n.getPropietario() != null && f.getVendedorIds().contains(n.getPropietario().getId())))
                .toList();
        List<Periodo> periodos = periodos(f);

        return new EstadisticasDTO(f.getDesde(), f.getHasta(), resumen,
                porNivel(paresEnRango),
                porTipo(paresEnRango),
                serie(f.getAgrupacion(), d, periodos, rango),
                tablaInmuebles(d, rango, nivelFiltro),
                vendedores(negociosVentas, rango, ahora, f),
                ventasPorPeriodo(negociosVentas, periodos),
                pipeline(negociosVentas));
    }

    @Override
    @Transactional(readOnly = true)
    public InteraccionInmueble interaccionInmueble(Integer inmuebleId, String nivel) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + inmuebleId));
        Nivel nivelFiltro = nivel != null && !nivel.isBlank() ? Nivel.valueOf(nivel) : null;
        Datos d = agrupar(List.of(inmueble));
        Map<Integer, String> estatus = estatusLeadRepository.findAll().stream()
                .collect(Collectors.toMap(EstatusLead::getId, EstatusLead::getNombre));
        //Historial de los negocios del inmueble (cambios de etapa, notas y tareas vinculadas)
        Map<Integer, List<Actividad>> historialNegocio = actividadRepository.obtenerPorNegociosDeInmueble(inmuebleId).stream()
                .collect(Collectors.groupingBy(a -> a.getNegocio().getId()));

        List<ContactoInteraccion> contactos = d.pares().values().stream()
                .filter(p -> nivelFiltro == null || p.nivel() == nivelFiltro)
                .map(p -> {
                    List<Accion> acciones = new ArrayList<>();
                    p.leads.forEach(l -> acciones.add(new Accion(l.getFechaCreacion(), "Consulta",
                            "Pidió información (estatus del lead: " + estatus.getOrDefault(l.getEstatus(), "Estatus " + l.getEstatus()) + ")")));
                    p.mensajes.forEach(m -> acciones.add(new Accion(m.getFechaCreacion(), "Mensaje",
                            "Por " + m.getCanal().getNombre() + ": " + m.getDescripcion())));
                    p.visitas.forEach(v -> acciones.add(new Accion(fechaVisita(v), "Visita",
                            v.getAsunto() != null ? v.getAsunto() : "Visita realizada")));
                    p.negocios.forEach(n -> {
                        acciones.add(new Accion(n.getFechaCreacion(), "Negocio", "Negocio \"" + n.getNombre() + "\" creado"));
                        historialNegocio.getOrDefault(n.getId(), List.of()).stream()
                                .filter(a -> a.getTipo() == Actividad.Tipo.ETAPA && a.getDescripcion() != null
                                        && a.getDescripcion().startsWith("Etapa cambiada"))
                                .forEach(a -> acciones.add(new Accion(a.getFechaCreacion(), "Etapa", a.getDescripcion())));
                    });
                    acciones.sort(Comparator.comparing(Accion::fecha, Comparator.nullsLast(Comparator.reverseOrder())));
                    Negocio negocio = p.negocios.stream().max(Comparator.comparing(Negocio::getId)).orElse(null);
                    Nivel n = p.nivel();
                    return new ContactoInteraccion(p.contacto.getId(), MapeosCrm.nombreContacto(p.contacto), p.contacto.getCorreo(),
                            p.contacto.getTelefono(), n.name(), n.etiqueta, p.leads.size() + p.mensajes.size(), p.visitas.size(),
                            negocio != null ? negocio.getId() : null, negocio != null ? negocio.getNombre() : null,
                            negocio != null ? negocio.getEtapa().getNombre() : null, p.ultimaInteraccion(), acciones);
                })
                .sorted(Comparator.comparing(ContactoInteraccion::ultimaInteraccion, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        return new InteraccionInmueble(inmueble.getId(), inmueble.getRrbr(), inmueble.getTitulo(), inmueble.getTipoInmueble().getNombre(),
                zona(inmueble), MapeosCrm.nombreEmpleado(inmueble.getEmpleado()), contactos);
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoExportado exportar(FiltroEstadisticaDTO f, FormatoExportacion formato, List<String> secciones) {
        List<String> incluir = secciones == null || secciones.isEmpty() ? SECCIONES : secciones;
        incluir.forEach(s -> { if (!SECCIONES.contains(s)) throw new IllegalArgumentException("Sección desconocida: " + s); });
        EstadisticasDTO e = calcular(f);
        List<Seccion<?>> partes = new ArrayList<>();

        if (incluir.contains("resumen")) {
            Resumen r = e.resumen();
            List<Object[]> filas = List.of(
                    new Object[] {"Consultas recibidas", r.consultas()},
                    new Object[] {"Mensajes en los leads", r.mensajes()},
                    new Object[] {"Visitas a inmuebles realizadas", r.visitas()},
                    new Object[] {"Negocios abiertos", r.negociosAbiertos()},
                    new Object[] {"Cierres del mes", r.cierresMes()},
                    new Object[] {"Cierres del período", r.cierresPeriodo()},
                    new Object[] {"Monto cerrado en el período", r.montoCerradoPeriodo()},
                    new Object[] {"Clientes que interactuaron", r.clientesInteresados()});
            partes.add(new Seccion<>("Resumen", filas, List.of(
                    new ColumnaSimple<Object[]>("Métrica", fila -> fila[0]), new ColumnaSimple<Object[]>("Valor", fila -> fila[1]))));
        }
        if (incluir.contains("pastel")) {
            partes.add(seccionPorciones("Interacción de los clientes", "Estado del cliente frente al inmueble", e.porNivel()));
            partes.add(seccionPorciones("Demanda por tipo", "Tipo de inmueble", e.porTipo()));
        }
        if (incluir.contains("serie")) {
            String eje = switch (e.serie().agrupacion()) { case "zona" -> "Zona"; case "propiedad" -> "Inmueble"; default -> "Período"; };
            partes.add(new Seccion<>("Interacciones por " + eje.toLowerCase(ES), e.serie().puntos(), List.of(
                    new ColumnaSimple<PuntoSerie>(eje, PuntoSerie::etiqueta),
                    new ColumnaSimple<PuntoSerie>("Consultas y mensajes", PuntoSerie::consultas),
                    new ColumnaSimple<PuntoSerie>("Visitas realizadas", PuntoSerie::visitas),
                    new ColumnaSimple<PuntoSerie>("Cierres", PuntoSerie::cierres))));
        }
        if (incluir.contains("inmuebles")) {
            partes.add(new Seccion<>("Detalle por inmueble", e.inmuebles(), List.of(
                    new ColumnaSimple<FilaInmueble>("Código", FilaInmueble::codigo),
                    new ColumnaSimple<FilaInmueble>("Inmueble", FilaInmueble::titulo),
                    new ColumnaSimple<FilaInmueble>("Tipo", FilaInmueble::tipo),
                    new ColumnaSimple<FilaInmueble>("Zona", FilaInmueble::zona),
                    new ColumnaSimple<FilaInmueble>("Clientes que consultaron", FilaInmueble::clientes),
                    new ColumnaSimple<FilaInmueble>("Visitas realizadas", FilaInmueble::visitas),
                    new ColumnaSimple<FilaInmueble>("Ofertas recibidas", FilaInmueble::ofertas),
                    new ColumnaSimple<FilaInmueble>("Vendedor encargado", FilaInmueble::vendedor))));
        }
        if (incluir.contains("vendedores")) {
            partes.add(new Seccion<>("Rendimiento por vendedor", e.vendedores(), List.of(
                    new ColumnaSimple<FilaVendedor>("Vendedor", FilaVendedor::nombre),
                    new ColumnaSimple<FilaVendedor>("Negocios creados", FilaVendedor::creados),
                    new ColumnaSimple<FilaVendedor>("Abiertos", FilaVendedor::abiertos),
                    new ColumnaSimple<FilaVendedor>("Ganados", FilaVendedor::ganados),
                    new ColumnaSimple<FilaVendedor>("Perdidos", FilaVendedor::perdidos),
                    new ColumnaSimple<FilaVendedor>("Monto ganado", FilaVendedor::montoGanado),
                    new ColumnaSimple<FilaVendedor>("Valor en pipeline", FilaVendedor::valorPipeline),
                    new ColumnaSimple<FilaVendedor>("Tasa de cierre", v -> v.tasaCierre() == null ? "—" : Math.round(v.tasaCierre() * 100) + " %"),
                    new ColumnaSimple<FilaVendedor>("Tareas completadas", FilaVendedor::tareasCompletadas),
                    new ColumnaSimple<FilaVendedor>("Tareas vencidas", FilaVendedor::tareasVencidas))));
            partes.add(new Seccion<>("Ventas por período", e.ventasPorPeriodo(), List.of(
                    new ColumnaSimple<PuntoVentas>("Período", PuntoVentas::etiqueta),
                    new ColumnaSimple<PuntoVentas>("Cierres ganados", PuntoVentas::cierres),
                    new ColumnaSimple<PuntoVentas>("Monto", PuntoVentas::monto))));
            partes.add(new Seccion<>("Pipeline por etapa", e.pipeline(), List.of(
                    new ColumnaSimple<EtapaValor>("Etapa", EtapaValor::etapa),
                    new ColumnaSimple<EtapaValor>("Negocios", EtapaValor::cantidad),
                    new ColumnaSimple<EtapaValor>("Valor", EtapaValor::valor))));
        }

        String periodo = (f.getDesde() != null ? f.getDesde().toString() : "inicio") + " a " + (f.getHasta() != null ? f.getHasta().toString() : "hoy");
        String nombreBase = "informe-estadisticas-" + LocalDate.now();
        return switch (formato) {
            case XLSX -> new ArchivoExportado(nombreBase + ".xlsx", ArchivoExportado.TIPO_XLSX, exportador.excelInforme(partes));
            case PDF -> new ArchivoExportado(nombreBase + ".pdf", "application/pdf",
                    exportador.pdfInforme("Estadísticas e informes", "Período: " + periodo, partes));
        };
    }

    // ----- Carga y agrupacion -----

    //conVendedor = el filtro de vendedor se aplica al asesor del inmueble (interaccion); sin el, solo a los negocios (ventas)
    private Datos cargar(FiltroEstadisticaDTO f, boolean conVendedor) {
        FiltroPropiedadDTO filtro = new FiltroPropiedadDTO();
        filtro.setVista("todos");
        if (conVendedor) filtro.setAsesorIds(f.getVendedorIds());
        filtro.setTipoInmuebleIds(f.getTipoInmuebleIds());
        filtro.setCiudadIds(f.getCiudadIds());
        filtro.setZona(f.getZona());
        filtro.setOperacion(f.getOperacion());
        filtro.setPrecioMin(f.getPrecioMin());
        filtro.setPrecioMax(f.getPrecioMax());
        filtro.setEstados(f.getEstados());
        filtro.setCodigo(f.getCodigo());
        List<Inmueble> inmuebles = inmuebleRepository.findAll(PropiedadEspecificaciones.conFiltros(filtro, 0), Pageable.unpaged()).getContent();
        return agrupar(inmuebles);
    }

    private Datos agrupar(List<Inmueble> inmuebles) {
        if (inmuebles.isEmpty()) return new Datos(List.of(), List.of(), List.of(), List.of(), List.of(), Map.of());
        Map<Integer, Inmueble> porId = inmuebles.stream().collect(Collectors.toMap(Inmueble::getId, Function.identity()));
        Set<Integer> ids = porId.keySet();
        List<Lead> leads = leadRepository.obtenerPorInmuebles(ids);
        List<Negocio> negocios = negocioRepository.findByInmuebleIdIn(ids);
        List<Comunicacion> mensajes = comunicacionRepository.obtenerPorInmuebles(ids);
        List<Actividad> visitas = actividadRepository.obtenerVisitasRealizadas().stream()
                .filter(v -> inmuebleDeVisita(v) != null && ids.contains(inmuebleDeVisita(v).getId()))
                .toList();

        Map<String, Par> pares = new LinkedHashMap<>();
        Function<Lead, Par> parDeLead = l -> pares.computeIfAbsent(clave(l.getUsuario(), l.getInmueble().getId()),
                k -> new Par(l.getUsuario(), porId.get(l.getInmueble().getId())));
        leads.forEach(l -> parDeLead.apply(l).leads.add(l));
        Map<Integer, Lead> leadPorId = leads.stream().collect(Collectors.toMap(Lead::getId, Function.identity()));
        mensajes.forEach(m -> {
            Lead l = leadPorId.get(m.getLead().getId());
            if (l != null) parDeLead.apply(l).mensajes.add(m);
        });
        negocios.forEach(n -> pares.computeIfAbsent(clave(n.getContacto(), n.getInmueble().getId()),
                k -> new Par(n.getContacto(), porId.get(n.getInmueble().getId()))).negocios.add(n));
        //Las visitas sin cliente no forman un par, pero cuentan en las metricas del inmueble
        visitas.forEach(v -> {
            Usuario c = v.getContacto() != null ? v.getContacto() : v.getNegocio() != null ? v.getNegocio().getContacto() : null;
            if (c != null) {
                Inmueble i = inmuebleDeVisita(v);
                pares.computeIfAbsent(clave(c, i.getId()), k -> new Par(c, porId.get(i.getId()))).visitas.add(v);
            }
        });
        return new Datos(inmuebles, leads, negocios, visitas, mensajes, pares);
    }

    // ----- Graficas -----

    private static List<Porcion> porNivel(List<Par> pares) {
        Map<Nivel, Long> conteo = pares.stream().collect(Collectors.groupingBy(Par::nivel, Collectors.counting()));
        List<Porcion> porciones = new ArrayList<>();
        for (Nivel n : Nivel.values()) {
            long cantidad = conteo.getOrDefault(n, 0L);
            if (n != Nivel.DESCARTO || cantidad > 0) porciones.add(new Porcion(n.name(), n.etiqueta, cantidad));
        }
        return porciones;
    }

    //Los tipos mas consultados; el resto se agrupa en "Otros" para no pasar de 6 porciones
    private static List<Porcion> porTipo(List<Par> pares) {
        Map<Integer, Long> conteo = pares.stream().collect(Collectors.groupingBy(p -> p.inmueble.getTipoInmueble().getId(), Collectors.counting()));
        Map<Integer, String> nombres = new HashMap<>();
        pares.forEach(p -> nombres.put(p.inmueble.getTipoInmueble().getId(), p.inmueble.getTipoInmueble().getNombre()));
        List<Map.Entry<Integer, Long>> orden = conteo.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey())).toList();
        List<Porcion> porciones = new ArrayList<>();
        long otros = 0;
        for (int i = 0; i < orden.size(); i++) {
            Map.Entry<Integer, Long> e = orden.get(i);
            if (i < MAXIMO_TIPOS) porciones.add(new Porcion(String.valueOf(e.getKey()), nombres.get(e.getKey()), e.getValue()));
            else otros += e.getValue();
        }
        if (otros > 0) porciones.add(new Porcion("otros", "Otros", otros));
        return porciones;
    }

    private static Serie serie(String agrupacion, Datos d, List<Periodo> periodos, Rango rango) {
        String modo = agrupacion == null ? "tiempo" : agrupacion;
        if ("tiempo".equals(modo)) {
            List<PuntoSerie> puntos = periodos.stream().map(p -> new PuntoSerie(p.clave(), p.etiqueta(),
                    d.leads().stream().filter(l -> p.rango().contiene(l.getFechaCreacion())).count()
                            + d.mensajes().stream().filter(m -> p.rango().contiene(m.getFechaCreacion())).count(),
                    d.visitas().stream().filter(v -> p.rango().contiene(fechaVisita(v))).count(),
                    d.negocios().stream().filter(n -> esGanado(n) && p.rango().contiene(n.getFechaCierre())).count())).toList();
            return new Serie(modo, puntos);
        }
        //Por zona (ciudad) o por inmueble: totales del periodo, los mas activos primero
        Function<Inmueble, String> claveDe = "zona".equals(modo)
                ? i -> i.getCiudad() != null ? String.valueOf(i.getCiudad().getId()) : "sin"
                : i -> String.valueOf(i.getId());
        Function<Inmueble, String> etiquetaDe = "zona".equals(modo)
                ? i -> i.getCiudad() != null ? i.getCiudad().getNombre() : "Sin ciudad"
                : i -> i.getRrbr() + " · " + i.getTitulo();
        Map<String, long[]> totales = new LinkedHashMap<>();
        Map<String, String> etiquetas = new HashMap<>();
        Function<Inmueble, long[]> fila = i -> {
            etiquetas.put(claveDe.apply(i), etiquetaDe.apply(i));
            return totales.computeIfAbsent(claveDe.apply(i), k -> new long[3]);
        };
        d.leads().stream().filter(l -> rango.contiene(l.getFechaCreacion())).forEach(l -> fila.apply(l.getInmueble())[0]++);
        Map<Integer, Lead> leadPorId = d.leads().stream().collect(Collectors.toMap(Lead::getId, Function.identity()));
        d.mensajes().stream().filter(m -> rango.contiene(m.getFechaCreacion()))
                .forEach(m -> { Lead l = leadPorId.get(m.getLead().getId()); if (l != null) fila.apply(l.getInmueble())[0]++; });
        d.visitas().stream().filter(v -> rango.contiene(fechaVisita(v))).forEach(v -> fila.apply(inmuebleDeVisita(v))[1]++);
        d.negocios().stream().filter(n -> esGanado(n) && rango.contiene(n.getFechaCierre())).forEach(n -> fila.apply(n.getInmueble())[2]++);

        int maximo = "zona".equals(modo) ? MAXIMO_ZONAS : MAXIMO_PROPIEDADES;
        List<Map.Entry<String, long[]>> orden = totales.entrySet().stream()
                .sorted(Comparator.comparingLong((Map.Entry<String, long[]> e) -> e.getValue()[0] + e.getValue()[1] + e.getValue()[2]).reversed())
                .toList();
        List<PuntoSerie> puntos = new ArrayList<>();
        long[] resto = new long[3];
        for (int i = 0; i < orden.size(); i++) {
            long[] v = orden.get(i).getValue();
            if (i < maximo) puntos.add(new PuntoSerie(orden.get(i).getKey(), etiquetas.get(orden.get(i).getKey()), v[0], v[1], v[2]));
            else for (int j = 0; j < 3; j++) resto[j] += v[j];
        }
        if (resto[0] + resto[1] + resto[2] > 0) {
            puntos.add(new PuntoSerie("otros", "zona".equals(modo) ? "Otras zonas" : "Otros inmuebles", resto[0], resto[1], resto[2]));
        }
        return new Serie(modo, puntos);
    }

    private static List<FilaInmueble> tablaInmuebles(Datos d, Rango rango, Nivel nivelFiltro) {
        Map<Integer, List<Par>> paresPorInmueble = d.pares().values().stream()
                .filter(p -> p.activoEn(rango))
                .filter(p -> nivelFiltro == null || p.nivel() == nivelFiltro)
                .collect(Collectors.groupingBy(p -> p.inmueble.getId()));
        Map<Integer, Long> consultas = d.leads().stream().filter(l -> rango.contiene(l.getFechaCreacion()))
                .collect(Collectors.groupingBy(l -> l.getInmueble().getId(), Collectors.counting()));
        Map<Integer, Long> visitas = d.visitas().stream().filter(v -> rango.contiene(fechaVisita(v)))
                .collect(Collectors.groupingBy(v -> inmuebleDeVisita(v).getId(), Collectors.counting()));
        return d.inmuebles().stream()
                .map(i -> {
                    List<Par> pares = paresPorInmueble.getOrDefault(i.getId(), List.of());
                    long ofertas = pares.stream().filter(p -> p.nivel() == Nivel.OFERTA || p.nivel() == Nivel.CERRO).count();
                    return new FilaInmueble(i.getId(), i.getRrbr(), i.getTitulo(), i.getTipoInmueble().getNombre(), zona(i), pares.size(),
                            consultas.getOrDefault(i.getId(), 0L), visitas.getOrDefault(i.getId(), 0L), ofertas,
                            MapeosCrm.nombreEmpleado(i.getEmpleado()));
                })
                .filter(fila -> nivelFiltro != null ? fila.clientes() > 0 : fila.clientes() + fila.consultas() + fila.visitas() > 0)
                .sorted(Comparator.comparingLong(FilaInmueble::clientes).reversed()
                        .thenComparing(Comparator.comparingLong(FilaInmueble::consultas).reversed())
                        .thenComparing(FilaInmueble::codigo, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(MAXIMO_FILAS)
                .toList();
    }

    // ----- Rendimiento de ventas -----

    private List<FilaVendedor> vendedores(List<Negocio> negocios, Rango rango, LocalDateTime ahora, FiltroEstadisticaDTO f) {
        record Clave(Integer id, String nombre) {}
        Function<Empleado, Clave> claveDe = e -> e == null ? new Clave(null, "Sin asignar") : new Clave(e.getId(), MapeosCrm.nombreEmpleado(e));
        Map<Clave, List<Negocio>> porVendedor = negocios.stream().collect(Collectors.groupingBy(n -> claveDe.apply(n.getPropietario())));
        boolean filtraVendedor = f.getVendedorIds() != null && !f.getVendedorIds().isEmpty();
        Map<Clave, List<Actividad>> tareas = actividadRepository.obtenerTareas().stream()
                .filter(t -> t.getEmpleado() != null && (!filtraVendedor || f.getVendedorIds().contains(t.getEmpleado().getId())))
                .collect(Collectors.groupingBy(t -> claveDe.apply(t.getEmpleado())));
        Set<Clave> claves = new java.util.LinkedHashSet<>(porVendedor.keySet());
        claves.addAll(tareas.keySet());

        return claves.stream().map(c -> {
            List<Negocio> lista = porVendedor.getOrDefault(c, List.of());
            List<Negocio> ganados = lista.stream().filter(n -> esGanado(n) && rango.contiene(n.getFechaCierre())).toList();
            long perdidos = lista.stream().filter(n -> n.getEtapa().getId() == EtapaNegocio.CIERRE_PERDIDO && rango.contiene(n.getFechaCierre())).count();
            List<Negocio> abiertos = lista.stream().filter(n -> Boolean.TRUE.equals(n.getEtapa().getAbierto())).toList();
            List<Actividad> susTareas = tareas.getOrDefault(c, List.of());
            Double tasa = ganados.size() + perdidos == 0 ? null : (double) ganados.size() / (ganados.size() + perdidos);
            return new FilaVendedor(c.id(), c.nombre(),
                    lista.stream().filter(n -> rango.contiene(n.getFechaCreacion())).count(),
                    abiertos.size(), ganados.size(), perdidos, suma(ganados), suma(abiertos), tasa,
                    susTareas.stream().filter(t -> Boolean.TRUE.equals(t.getCompletada()) && rango.contiene(t.getFechaProgramada())).count(),
                    susTareas.stream().filter(t -> !Boolean.TRUE.equals(t.getCompletada()) && t.getFechaProgramada() != null
                            && t.getFechaProgramada().isBefore(ahora)).count());
        })
        .sorted(Comparator.comparing(FilaVendedor::montoGanado).reversed().thenComparing(FilaVendedor::nombre))
        .toList();
    }

    private static List<PuntoVentas> ventasPorPeriodo(List<Negocio> negocios, List<Periodo> periodos) {
        return periodos.stream().map(p -> {
            List<Negocio> ganados = negocios.stream().filter(n -> esGanado(n) && p.rango().contiene(n.getFechaCierre())).toList();
            return new PuntoVentas(p.clave(), p.etiqueta(), ganados.size(), suma(ganados));
        }).toList();
    }

    //Negocios abiertos por etapa (en el orden del pipeline)
    private List<EtapaValor> pipeline(List<Negocio> negocios) {
        return etapaRepository.findAllByOrderByOrdenAsc().stream()
                .filter(e -> Boolean.TRUE.equals(e.getAbierto()))
                .map(e -> {
                    List<Negocio> enEtapa = negocios.stream().filter(n -> n.getEtapa().getId().equals(e.getId())).toList();
                    return new EtapaValor(e.getId(), e.getNombre(), enEtapa.size(), suma(enEtapa));
                })
                .toList();
    }

    // ----- Periodos de las series -----

    record Periodo(String clave, String etiqueta, Rango rango) {}

    /* Sin fecha inicial se muestran los ultimos 12 meses. Hasta 92 dias se agrupa por semana (desde el lunes);
       si no, por mes */
    static List<Periodo> periodos(FiltroEstadisticaDTO f) {
        LocalDate fin = f.getHasta() != null ? f.getHasta() : LocalDate.now();
        LocalDate inicio = f.getDesde() != null ? f.getDesde() : fin.minusMonths(11).withDayOfMonth(1);
        List<Periodo> periodos = new ArrayList<>();
        if (ChronoUnit.DAYS.between(inicio, fin) <= 92) {
            for (LocalDate s = inicio.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); !s.isAfter(fin); s = s.plusWeeks(1)) {
                LocalDate desde = s.isBefore(inicio) ? inicio : s;
                LocalDate hasta = s.plusDays(6).isAfter(fin) ? fin : s.plusDays(6);
                periodos.add(new Periodo(desde.toString(), "Sem. " + desde.format(DIA), new Rango(desde, hasta)));
            }
        } else {
            for (LocalDate m = inicio.withDayOfMonth(1); !m.isAfter(fin); m = m.plusMonths(1)) {
                LocalDate desde = m.isBefore(inicio) ? inicio : m;
                LocalDate finMes = m.plusMonths(1).minusDays(1);
                periodos.add(new Periodo(m.toString().substring(0, 7), m.format(MES), new Rango(desde, finMes.isAfter(fin) ? fin : finMes)));
            }
        }
        return periodos;
    }

    // ----- Apoyo -----

    private static String clave(Usuario contacto, Integer inmuebleId) {
        return contacto.getId() + "-" + inmuebleId;
    }

    private static Inmueble inmuebleDeVisita(Actividad v) {
        return v.getInmueble() != null ? v.getInmueble() : v.getNegocio() != null ? v.getNegocio().getInmueble() : null;
    }

    static LocalDateTime fechaVisita(Actividad v) {
        return v.getFechaProgramada() != null ? v.getFechaProgramada() : v.getFechaCreacion();
    }

    private static boolean esGanado(Negocio n) {
        return n.getEtapa().getId() == EtapaNegocio.CIERRE_GANADO;
    }

    private static BigDecimal suma(List<Negocio> negocios) {
        return negocios.stream().map(Negocio::getValor).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static String zona(Inmueble i) {
        return i.getCiudad() != null ? i.getUbicacion() + ", " + i.getCiudad().getNombre() : i.getUbicacion();
    }

    private static Seccion<Porcion> seccionPorciones(String titulo, String categoria, List<Porcion> porciones) {
        long total = porciones.stream().mapToLong(Porcion::cantidad).sum();
        return new Seccion<>(titulo, porciones, List.of(
                new ColumnaSimple<Porcion>(categoria, Porcion::etiqueta),
                new ColumnaSimple<Porcion>("Clientes", Porcion::cantidad),
                new ColumnaSimple<Porcion>("Porcentaje", p -> total == 0 ? "0 %" : Math.round(p.cantidad() * 1000.0 / total) / 10.0 + " %")));
    }
}
