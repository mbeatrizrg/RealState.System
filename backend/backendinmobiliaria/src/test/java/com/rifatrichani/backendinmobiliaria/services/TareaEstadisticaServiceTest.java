package com.rifatrichani.backendinmobiliaria.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.TestPropertySource;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroEstadisticaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FiltroTareaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.NegocioRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.TareaRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EstadisticasDTO.Porcion;
import com.rifatrichani.backendinmobiliaria.dto.response.TareaResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Actividad;
import com.rifatrichani.backendinmobiliaria.models.Ciudad;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.EstatusLead;
import com.rifatrichani.backendinmobiliaria.models.EtapaNegocio;
import com.rifatrichani.backendinmobiliaria.models.Extra;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.models.TipoInmueble;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.impl.ActividadServiceImpl;
import com.rifatrichani.backendinmobiliaria.services.impl.AlmacenArchivosCrm;
import com.rifatrichani.backendinmobiliaria.services.impl.ContactoServiceImpl;
import com.rifatrichani.backendinmobiliaria.services.impl.EstadisticaServiceImpl;
import com.rifatrichani.backendinmobiliaria.services.impl.ExportadorTablas;
import com.rifatrichani.backendinmobiliaria.services.impl.NegocioServiceImpl;
import com.rifatrichani.backendinmobiliaria.services.impl.TareaServiceImpl;

import jakarta.persistence.EntityManager;

//Modulos de Tareas y Estadisticas con las consultas reales sobre H2 en memoria
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({TareaServiceImpl.class, EstadisticaServiceImpl.class, NegocioServiceImpl.class, ActividadServiceImpl.class,
        ContactoServiceImpl.class, ExportadorTablas.class, AlmacenArchivosCrm.class, CorreoService.class})
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:tareas;MODE=PostgreSQL",
        "app.uploads.dir=target/uploads-test"
})
class TareaEstadisticaServiceTest {

    @Autowired TareaService tareaService;
    @Autowired EstadisticaService estadisticaService;
    @Autowired NegocioService negocioService;
    @Autowired EntityManager em;

    Empleado ana;
    Empleado luis;
    Usuario carla;
    Usuario pedro;
    Inmueble casa;
    Inmueble local;
    CuentaAutenticada vendedoraAna;
    CuentaAutenticada vendedorLuis;
    CuentaAutenticada admin;

    @BeforeEach
    void datos() {
        for (int i = 1; i <= 7; i++) {
            EtapaNegocio e = new EtapaNegocio();
            e.setId(i);
            e.setNombre("Etapa " + i);
            e.setOrden(i);
            e.setAbierto(i < EtapaNegocio.CIERRE_GANADO);
            em.persist(e);
            EstatusLead s = new EstatusLead();
            s.setId(i);
            s.setNombre("Estatus " + i);
            s.setOrden(i);
            em.persist(s);
        }
        ana = empleado("Ana", 21);
        luis = empleado("Luis", 22);
        Empleado jefa = empleado("Jefa", 23);
        jefa.setEsAdmin(true);
        carla = usuario("Carla", "carla@correo.com", ana);
        pedro = usuario("Pedro", "pedro@correo.com", null);
        casa = inmueble("Casa El Bosque", "R-1", tipo("Casa"), ana);
        local = inmueble("Local Centro", "R-2", tipo("Local"), luis);
        lead(carla, casa, 1);
        lead(pedro, casa, 4);
        lead(pedro, local, 1);
        em.flush();
        em.clear();
        vendedoraAna = new CuentaAutenticada(ana.getId(), TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_VENDEDOR);
        vendedorLuis = new CuentaAutenticada(luis.getId(), TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_VENDEDOR);
        admin = new CuentaAutenticada(jefa.getId(), TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_ADMIN);
    }

    @Test
    void lasPestanasYElEstadoVencidaSeCalculanConLaFecha() {
        tarea("Llamar a Carla", "LLAMADA", "alta", LocalDateTime.now().plusDays(1), carla.getId(), null, vendedoraAna);
        tarea("Enviar catálogo", "ENVIO", "baja", LocalDateTime.now().minusDays(2), pedro.getId(), null, vendedoraAna);
        TareaResponseDTO deLuis = tarea("Solicitar documentos", "TRAMITE", "media", LocalDateTime.now().plusDays(3), null, local.getId(), vendedorLuis);
        tareaService.completar(deLuis.id(), null, vendedorLuis);

        var conteo = tareaService.contar(filtro("todas"), vendedoraAna);
        assertThat(conteo.todos()).isEqualTo(3);
        assertThat(conteo.mios()).isEqualTo(2);
        //La vencida y la completada
        assertThat(conteo.terceraPestana()).isEqualTo(2);

        FiltroTareaDTO vencidas = filtro("todas");
        vencidas.setEstados(List.of("vencida"));
        List<TareaResponseDTO> filas = tareaService.buscar(vencidas, admin).getContenido();
        assertThat(filas).extracting(TareaResponseDTO::titulo).containsExactly("Enviar catálogo");
        assertThat(filas.get(0).estado()).isEqualTo("vencida");
        assertThat(filas.get(0).estadoGuardado()).isEqualTo("pendiente");

        FiltroTareaDTO porPrioridad = filtro("todas");
        porPrioridad.setOrden("prioridad");
        porPrioridad.setDireccion("desc");
        assertThat(tareaService.buscar(porPrioridad, admin).getContenido()).extracting(TareaResponseDTO::prioridad)
                .containsExactly("alta", "media", "baja");

        FiltroTareaDTO porInmueble = filtro("todas");
        porInmueble.setCodigoInmueble("r-2");
        assertThat(tareaService.buscar(porInmueble, admin).getContenido()).extracting(TareaResponseDTO::titulo).containsExactly("Solicitar documentos");
    }

    @Test
    void reglasDePropietarioYResultadoComoNota() {
        assertThatThrownBy(() -> tareaService.crear(new TareaRequestDTO("X", null, "LLAMADA", "media", null,
                LocalDateTime.now(), carla.getId(), null, null, luis.getId()), vendedoraAna)).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> tareaService.crear(new TareaRequestDTO("Sin vínculo", null, "LLAMADA", "media", null,
                LocalDateTime.now(), null, null, null, null), vendedoraAna)).isInstanceOf(IllegalArgumentException.class);

        TareaResponseDTO visita = tarea("Visita guiada", "VISITA", "alta", LocalDateTime.now().plusHours(2), carla.getId(), casa.getId(), vendedoraAna);
        assertThatThrownBy(() -> tareaService.cambiarEstado(visita.id(), "en_proceso", vendedorLuis)).isInstanceOf(AccessDeniedException.class);
        assertThat(tareaService.cambiarEstado(visita.id(), "en_proceso", vendedoraAna).estado()).isEqualTo("en_proceso");

        TareaResponseDTO hecha = tareaService.completar(visita.id(), "Le gustó, pidió una segunda visita", vendedoraAna);
        assertThat(hecha.estado()).isEqualTo("completada");
        assertThat(hecha.descripcion()).contains("Resultado: Le gustó");
        em.flush();
        em.clear();
        List<Actividad> notas = em.createQuery("SELECT a FROM Actividad a WHERE a.tipo = :t", Actividad.class)
                .setParameter("t", Actividad.Tipo.NOTA).getResultList();
        assertThat(notas).hasSize(1);
        assertThat(notas.get(0).getDescripcion()).isEqualTo("Le gustó, pidió una segunda visita");
    }

    @Test
    void lasEstadisticasCombinanLeadsNegociosYVisitas() {
        //Pedro agendo visita en la casa (lead estatus 4) y ademas la visito: queda en "Realizó visita física"
        TareaResponseDTO visita = tarea("Visita", "VISITA", "media", LocalDateTime.now().minusHours(1), pedro.getId(), casa.getId(), vendedoraAna);
        tareaService.completar(visita.id(), null, vendedoraAna);
        //Carla compro la casa
        Integer negocio = negocioService.crear(new NegocioRequestDTO("Venta casa", casa.getId(), carla.getId(), "venta",
                new BigDecimal("120000"), 1, null, ana.getId()), vendedoraAna);
        negocioService.cambiarEtapa(negocio, EtapaNegocio.CIERRE_GANADO, vendedoraAna);
        em.flush();
        em.clear();

        EstadisticasDTO e = estadisticaService.calcular(new FiltroEstadisticaDTO());
        assertThat(e.resumen().consultas()).isEqualTo(3);
        assertThat(e.resumen().visitas()).isEqualTo(1);
        assertThat(e.resumen().cierresMes()).isEqualTo(1);
        assertThat(e.resumen().montoCerradoPeriodo()).isEqualByComparingTo("120000");
        assertThat(e.porNivel()).extracting(Porcion::clave, Porcion::cantidad)
                .contains(org.assertj.core.groups.Tuple.tuple("INFORMACION", 1L), org.assertj.core.groups.Tuple.tuple("VISITO", 1L),
                        org.assertj.core.groups.Tuple.tuple("CERRO", 1L));
        assertThat(e.porTipo().get(0).etiqueta()).isEqualTo("Casa");

        var filaCasa = e.inmuebles().stream().filter(f -> f.codigo().equals("R-1")).findFirst().orElseThrow();
        assertThat(filaCasa.clientes()).isEqualTo(2);
        assertThat(filaCasa.visitas()).isEqualTo(1);
        assertThat(filaCasa.ofertas()).isEqualTo(1);

        //Porcion del pastel: solo los que visitaron
        FiltroEstadisticaDTO visitaron = new FiltroEstadisticaDTO();
        visitaron.setNivel("VISITO");
        assertThat(estadisticaService.calcular(visitaron).inmuebles()).extracting("codigo").containsExactly("R-1");

        var detalle = estadisticaService.interaccionInmueble(casa.getId(), null);
        assertThat(detalle.contactos()).extracting("nombre").containsExactlyInAnyOrder("Carla Prueba", "Pedro Prueba");
        var pedroEnCasa = detalle.contactos().stream().filter(c -> c.nombre().startsWith("Pedro")).findFirst().orElseThrow();
        assertThat(pedroEnCasa.nivel()).isEqualTo("VISITO");
        assertThat(pedroEnCasa.acciones()).extracting("tipo").contains("Consulta", "Visita");

        assertThat(e.vendedores()).extracting("nombre").contains("Ana Prueba");
        assertThat(e.vendedores().get(0).ganados()).isEqualTo(1);

        FiltroEstadisticaDTO soloLocales = new FiltroEstadisticaDTO();
        soloLocales.setTipoInmuebleIds(List.of(local.getTipoInmueble().getId()));
        assertThat(estadisticaService.calcular(soloLocales).resumen().consultas()).isEqualTo(1);
    }

    @Test
    void exportaElInformeYLasTareas() {
        tarea("Llamar", "LLAMADA", "alta", LocalDateTime.now().plusDays(1), carla.getId(), null, vendedoraAna);
        var excel = estadisticaService.exportar(new FiltroEstadisticaDTO(), FormatoExportacion.XLSX, List.of());
        assertThat(excel.nombre()).endsWith(".xlsx");
        var pdf = estadisticaService.exportar(new FiltroEstadisticaDTO(), FormatoExportacion.PDF, List.of("resumen", "inmuebles"));
        assertThat(new String(pdf.contenido(), 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
        var tareas = tareaService.exportar(filtro("todas"), ColumnaTarea.POR_DEFECTO, FormatoExportacion.PDF, false, admin);
        assertThat(tareas.contenido()).isNotEmpty();
    }

    @Test
    void losPeriodosSeAgrupanPorSemanaOMes() {
        FiltroEstadisticaDTO corto = new FiltroEstadisticaDTO();
        corto.setDesde(LocalDate.of(2026, 10, 1));
        corto.setHasta(LocalDate.of(2026, 10, 31));
        EstadisticasDTO e = estadisticaService.calcular(corto);
        assertThat(e.serie().puntos()).hasSize(5);
        assertThat(e.serie().puntos().get(0).etiqueta()).startsWith("Sem.");
        assertThat(estadisticaService.calcular(new FiltroEstadisticaDTO()).serie().puntos()).hasSize(12);
    }

    private TareaResponseDTO tarea(String titulo, String tipo, String prioridad, LocalDateTime vence, Integer contactoId,
                                   Integer inmuebleId, CuentaAutenticada cuenta) {
        TareaResponseDTO t = tareaService.crear(new TareaRequestDTO(titulo, null, tipo, prioridad, null, vence, contactoId, null,
                inmuebleId, null), cuenta);
        em.flush();
        return t;
    }

    private static FiltroTareaDTO filtro(String vista) {
        FiltroTareaDTO f = new FiltroTareaDTO();
        f.setVista(vista);
        return f;
    }

    private Empleado empleado(String nombre, int cedula) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellido("Prueba");
        e.setCedula(cedula);
        e.setCorreo(nombre.toLowerCase() + "@empresa.com");
        e.setEsAdmin(false);
        e.setActivo(true);
        e.setFechaEntrada(LocalDateTime.now());
        e.setSalidaSistema(LocalDateTime.now());
        em.persist(e);
        return e;
    }

    private Usuario usuario(String nombre, String correo, Empleado propietario) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido("Prueba");
        u.setCorreo(correo);
        u.setTelefono("0414-111");
        u.setTipoNegocio("compra");
        u.setPropietario(propietario);
        em.persist(u);
        return u;
    }

    private TipoInmueble tipo(String nombre) {
        TipoInmueble t = new TipoInmueble();
        t.setNombre(nombre);
        em.persist(t);
        return t;
    }

    private Inmueble inmueble(String titulo, String rrbr, TipoInmueble tipo, Empleado asesor) {
        Ciudad ciudad = new Ciudad();
        ciudad.setNombre("Valencia");
        em.persist(ciudad);
        Extra extra = new Extra();
        extra.setNombre("Pozo");
        em.persist(extra);
        Inmueble i = new Inmueble();
        i.setTitulo(titulo);
        i.setPrecio(100000);
        i.setRrbr(rrbr);
        i.setUbicacion("Centro");
        i.setDescripcion("Prueba");
        i.setCiudad(ciudad);
        i.setTipoInmueble(tipo);
        i.setExtra(extra);
        i.setEmpleado(asesor);
        i.setVendido(false);
        i.setAlquiler(false);
        em.persist(i);
        return i;
    }

    private void lead(Usuario u, Inmueble i, int estatus) {
        Lead l = new Lead();
        l.setUsuario(u);
        l.setInmueble(i);
        l.setEstatus(estatus);
        em.persist(l);
    }
}
