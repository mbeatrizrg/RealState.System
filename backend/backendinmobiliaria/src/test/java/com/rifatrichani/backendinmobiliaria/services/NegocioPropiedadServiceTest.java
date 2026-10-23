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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.TestPropertySource;

import com.rifatrichani.backendinmobiliaria.dto.request.ActividadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FichaPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FiltroNegocioDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FiltroPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.NegocioRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ActividadResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.NegocioResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.TableroNegociosDTO;
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
import com.rifatrichani.backendinmobiliaria.services.impl.ExportadorTablas;
import com.rifatrichani.backendinmobiliaria.services.impl.NegocioServiceImpl;
import com.rifatrichani.backendinmobiliaria.services.impl.PropiedadServiceImpl;

import jakarta.persistence.EntityManager;

//Modulos de Negocios, Inmuebles y el registro de actividad del contacto, con las consultas reales sobre H2 en memoria
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({NegocioServiceImpl.class, PropiedadServiceImpl.class, ActividadServiceImpl.class, ContactoServiceImpl.class,
        ExportadorTablas.class, AlmacenArchivosCrm.class, CorreoService.class})
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:negocios;MODE=PostgreSQL",
        "app.uploads.dir=target/uploads-test"
})
class NegocioPropiedadServiceTest {

    @Autowired NegocioService negocioService;
    @Autowired PropiedadService propiedadService;
    @Autowired ActividadService actividadService;
    @Autowired EntityManager em;

    Empleado ana;
    Empleado luis;
    Usuario carla;
    Usuario pedro;
    Inmueble casaAna;
    Inmueble localLuis;
    CuentaAutenticada vendedoraAna;
    CuentaAutenticada vendedorLuis;
    CuentaAutenticada admin;

    @BeforeEach
    void datos() {
        String[] etapas = {"Prospecto", "Cita/visita programada", "Visita realizada", "Oferta/negociación", "Trámites legales",
                "Cierre ganado", "Cierre perdido"};
        for (int i = 1; i <= etapas.length; i++) {
            EtapaNegocio e = new EtapaNegocio();
            e.setId(i);
            e.setNombre(etapas[i - 1]);
            e.setOrden(i);
            e.setAbierto(i < EtapaNegocio.CIERRE_GANADO);
            em.persist(e);
        }
        EstatusLead nuevo = new EstatusLead();
        nuevo.setId(1);
        nuevo.setNombre("Nuevo");
        nuevo.setOrden(1);
        em.persist(nuevo);

        ana = empleado("Ana", 11);
        luis = empleado("Luis", 12);
        Empleado jefa = empleado("Jefa", 13);
        jefa.setEsAdmin(true);
        carla = usuario("Carla", "carla@correo.com", ana);
        pedro = usuario("Pedro", "pedro@correo.com", null);

        TipoInmueble casa = tipo("Casa");
        TipoInmueble local = tipo("Local");
        casaAna = inmueble("Casa El Bosque", "R-1", casa, 120000, false, ana);
        localLuis = inmueble("Local Centro", "R-2", local, 800, true, luis);
        Lead lead = new Lead();
        lead.setUsuario(pedro);
        lead.setInmueble(casaAna);
        lead.setEstatus(1);
        em.persist(lead);
        em.flush();
        em.clear();

        vendedoraAna = new CuentaAutenticada(ana.getId(), TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_VENDEDOR);
        vendedorLuis = new CuentaAutenticada(luis.getId(), TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_VENDEDOR);
        admin = new CuentaAutenticada(jefa.getId(), TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_ADMIN);
    }

    @Test
    void crearNegociosYSepararPestanas() {
        crearNegocio("Venta Casa El Bosque", casaAna, carla, "venta", 120000, 1, ana.getId(), vendedoraAna);
        crearNegocio("Alquiler Local Centro", localLuis, pedro, "alquiler", 800, 4, null, vendedorLuis);

        var conteo = negocioService.contar(filtro("todos"), vendedoraAna);
        assertThat(conteo.todos()).isEqualTo(2);
        assertThat(conteo.mios()).isEqualTo(1);
        assertThat(conteo.terceraPestana()).isEqualTo(1);

        FiltroNegocioDTO alquileres = filtro("todos");
        alquileres.setTiposOperacion(List.of("alquiler"));
        assertThat(nombres(alquileres)).containsExactly("Alquiler Local Centro");

        FiltroNegocioDTO texto = filtro("todos");
        texto.setQ("r-1");
        assertThat(nombres(texto)).containsExactly("Venta Casa El Bosque");

        FiltroNegocioDTO porTipo = filtro("todos");
        porTipo.setTipoInmuebleIds(List.of(localLuis.getTipoInmueble().getId()));
        assertThat(nombres(porTipo)).containsExactly("Alquiler Local Centro");
    }

    @Test
    void unVendedorNoPuedeAsignarseNegociosAOtro() {
        assertThatThrownBy(() -> crearNegocio("X", casaAna, carla, "venta", 1, 1, luis.getId(), vendedoraAna))
                .isInstanceOf(AccessDeniedException.class);
        Integer id = crearNegocio("De Ana", casaAna, carla, "venta", 1, 1, ana.getId(), vendedoraAna);
        assertThatThrownBy(() -> negocioService.cambiarEtapa(id, 2, vendedorLuis)).isInstanceOf(AccessDeniedException.class);
        negocioService.cambiarEtapa(id, 2, admin);
    }

    @Test
    void elTableroSumaPorEtapaYElCambioDeEtapaQuedaEnElHistorial() {
        Integer id = crearNegocio("Venta 1", casaAna, carla, "venta", 100000, 1, ana.getId(), vendedoraAna);
        crearNegocio("Venta 2", casaAna, pedro, "venta", 50000, 1, null, vendedoraAna);
        crearNegocio("Alquiler", localLuis, carla, "alquiler", 800, 3, luis.getId(), vendedorLuis);

        TableroNegociosDTO tablero = negocioService.tablero(filtro("todos"), admin);
        assertThat(tablero.columnas()).hasSize(7);
        TableroNegociosDTO.Columna prospecto = tablero.columnas().get(0);
        assertThat(prospecto.cantidad()).isEqualTo(2);
        assertThat(prospecto.total()).isEqualByComparingTo("150000");
        assertThat(prospecto.negocios()).hasSize(2);

        negocioService.cambiarEtapa(id, EtapaNegocio.CIERRE_GANADO, vendedoraAna);
        em.flush();
        em.clear();
        NegocioResponseDTO ganado = negocioService.obtenerDetalle(id, vendedoraAna).negocio();
        assertThat(ganado.etapaAbierta()).isFalse();
        assertThat(ganado.fechaCierre()).isNotNull();

        List<ActividadResponseDTO> historial = actividadService.historialNegocio(id);
        assertThat(historial).extracting(ActividadResponseDTO::tipo).containsOnly("ETAPA");
        assertThat(historial).hasSize(2);
        assertThat(historial.get(0).descripcion()).contains("Cierre ganado");
    }

    @Test
    void elRegistroDelContactoGuardaActividadesYAdjuntos() {
        MockMultipartFile pdf = new MockMultipartFile("archivos", "contrato.pdf", "application/pdf",
                "%PDF-1.4 prueba".getBytes(StandardCharsets.US_ASCII));
        ActividadResponseDTO correo = actividadService.crear(new ActividadRequestDTO("CORREO", carla.getId(), null,
                "Propuesta", "Le envío el contrato", null, null, null, false), List.of(pdf), vendedoraAna);
        assertThat(correo.archivos()).hasSize(1);
        actividadService.crear(new ActividadRequestDTO("TAREA", carla.getId(), null, "Llamar mañana", null,
                LocalDateTime.now().plusDays(1), null, null, false), List.of(), vendedoraAna);

        assertThatThrownBy(() -> actividadService.crear(new ActividadRequestDTO("TAREA", carla.getId(), null, "Sin fecha",
                null, null, null, null, false), List.of(), vendedoraAna)).isInstanceOf(IllegalArgumentException.class);

        em.flush();
        em.clear();
        List<ActividadResponseDTO> historial = actividadService.historialContacto(carla.getId(), vendedoraAna);
        assertThat(historial).extracting(ActividadResponseDTO::tipo).containsExactlyInAnyOrder("CORREO", "TAREA");
        ActividadResponseDTO tarea = historial.stream().filter(a -> a.tipo().equals("TAREA")).findFirst().orElseThrow();
        assertThat(tarea.completada()).isFalse();
        assertThat(actividadService.adjuntosContacto(carla.getId())).extracting("nombre").containsExactly("contrato.pdf");

        //Luis no es autor ni propietario de Carla
        assertThatThrownBy(() -> actividadService.marcarCompletada(tarea.id(), true, vendedorLuis)).isInstanceOf(AccessDeniedException.class);
        actividadService.marcarCompletada(tarea.id(), true, vendedoraAna);
        actividadService.eliminar(correo.id(), vendedoraAna);
        assertThat(actividadService.adjuntosContacto(carla.getId())).isEmpty();
    }

    @Test
    void moduloInmueblesFiltraYActualizaLaFicha() {
        crearNegocio("Venta", casaAna, pedro, "venta", 120000, 2, ana.getId(), vendedoraAna);

        var conteo = propiedadService.contar(filtroPropiedad("todos"), vendedoraAna);
        assertThat(conteo.todos()).isEqualTo(2);
        assertThat(conteo.mios()).isEqualTo(1);
        assertThat(conteo.terceraPestana()).isEqualTo(2);

        assertThatThrownBy(() -> propiedadService.actualizarFicha(casaAna.getId(), ficha("reservado"), vendedorLuis))
                .isInstanceOf(AccessDeniedException.class);
        propiedadService.actualizarFicha(casaAna.getId(), ficha("reservado"), vendedoraAna);
        em.flush();
        em.clear();
        assertThat(propiedadService.contar(filtroPropiedad("todos"), vendedoraAna).terceraPestana()).isEqualTo(1);

        FiltroPropiedadDTO precio = filtroPropiedad("todos");
        precio.setPrecioMin(1000);
        precio.setEstados(List.of("reservado"));
        var filas = propiedadService.buscar(precio, admin).getContenido();
        assertThat(filas).hasSize(1);
        assertThat(filas.get(0).habitaciones()).isEqualTo(3);
        assertThat(filas.get(0).estacionamientos()).isEqualTo(1);
        assertThat(filas.get(0).piso()).isEqualTo(4);
        assertThat(filas.get(0).negociosAbiertos()).isEqualTo(1);

        var detalle = propiedadService.obtenerDetalle(casaAna.getId(), vendedorLuis);
        assertThat(detalle.puedeEditar()).isFalse();
        assertThat(detalle.negocios()).hasSize(1);
        assertThat(detalle.interesados()).extracting("nombre").containsExactly("Pedro Prueba");

        //vendido / alquilado mantiene isVendido para el portal
        propiedadService.actualizarFicha(casaAna.getId(), ficha("vendido"), admin);
        em.flush();
        em.clear();
        assertThat(em.find(Inmueble.class, casaAna.getId()).getVendido()).isTrue();
    }

    @Test
    void exportaNegociosEInmuebles() {
        crearNegocio("Venta", casaAna, carla, "venta", 120000, 1, ana.getId(), vendedoraAna);
        var excel = negocioService.exportar(filtro("todos"), ColumnaNegocio.POR_DEFECTO, FormatoExportacion.XLSX, false, admin);
        assertThat(excel.nombre()).endsWith(".xlsx");
        assertThat(excel.contenido()).isNotEmpty();
        var pdf = propiedadService.exportar(filtroPropiedad("disponibles"), ColumnaPropiedad.POR_DEFECTO, FormatoExportacion.PDF, true, admin);
        assertThat(new String(pdf.contenido(), 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    private Integer crearNegocio(String nombre, Inmueble inmueble, Usuario contacto, String operacion, int valor, int etapa,
                                 Integer propietario, CuentaAutenticada cuenta) {
        Integer id = negocioService.crear(new NegocioRequestDTO(nombre, inmueble.getId(), contacto.getId(), operacion,
                BigDecimal.valueOf(valor), etapa, LocalDate.now().plusMonths(1), propietario), cuenta);
        em.flush();
        return id;
    }

    private List<String> nombres(FiltroNegocioDTO f) {
        return negocioService.buscar(f, admin).getContenido().stream().map(NegocioResponseDTO::nombre).toList();
    }

    private static FiltroNegocioDTO filtro(String vista) {
        FiltroNegocioDTO f = new FiltroNegocioDTO();
        f.setVista(vista);
        return f;
    }

    private static FiltroPropiedadDTO filtroPropiedad(String vista) {
        FiltroPropiedadDTO f = new FiltroPropiedadDTO();
        f.setVista(vista);
        return f;
    }

    private static FichaPropiedadDTO ficha(String estado) {
        return new FichaPropiedadDTO(estado, "Dueño Prueba", "0414-000", "dueno@correo.com", 3, 2, new BigDecimal("150.50"), 1, 4);
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

    private Inmueble inmueble(String titulo, String rrbr, TipoInmueble tipo, int precio, boolean alquiler, Empleado asesor) {
        Ciudad ciudad = new Ciudad();
        ciudad.setNombre("Valencia");
        em.persist(ciudad);
        Extra extra = new Extra();
        extra.setNombre("Pozo");
        em.persist(extra);
        Inmueble i = new Inmueble();
        i.setTitulo(titulo);
        i.setPrecio(precio);
        i.setRrbr(rrbr);
        i.setUbicacion("Centro");
        i.setDescripcion("Prueba");
        i.setCiudad(ciudad);
        i.setTipoInmueble(tipo);
        i.setExtra(extra);
        i.setEmpleado(asesor);
        i.setVendido(false);
        i.setAlquiler(alquiler);
        em.persist(i);
        return i;
    }
}
