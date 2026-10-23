package com.rifatrichani.backendinmobiliaria.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroContactoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoContactosDTO;
import com.rifatrichani.backendinmobiliaria.models.Ciudad;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.EstatusLead;
import com.rifatrichani.backendinmobiliaria.models.Extra;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.models.TipoInmueble;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.impl.ContactoServiceImpl;
import com.rifatrichani.backendinmobiliaria.services.impl.ExportadorTablas;

import jakarta.persistence.EntityManager;

//Ejecuta las consultas reales del modulo de Contactos (Criteria, subconsultas y orden) sobre H2 en memoria
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({ContactoServiceImpl.class, ExportadorTablas.class})
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:contactos;MODE=PostgreSQL"
})
class ContactoServiceTest {

    @Autowired ContactoService contactoService;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired EntityManager em;

    Empleado ana;
    Empleado luis;
    Usuario carla;   // de Ana, leads: estatus 1 y luego 3 (el mas reciente manda)
    Usuario pedro;   // de Luis, lead estatus 1
    Usuario maria;   // sin asignar, sin leads, creada hace 40 dias
    CuentaAutenticada vendedoraAna;
    CuentaAutenticada admin;

    @BeforeEach
    void datos() {
        for (int i = 1; i <= 7; i++) {
            EstatusLead e = new EstatusLead();
            e.setId(i);
            e.setNombre(i == 1 ? "Nuevo" : i == 3 ? "Calificado" : "Estatus " + i);
            e.setOrden(i);
            em.persist(e);
        }
        ana = empleado("Ana", "Pérez", 11);
        luis = empleado("Luis", "Gómez", 12);
        Empleado jefe = empleado("Jefa", "Admin", 13);
        jefe.setEsAdmin(true);

        carla = usuario("Carla", "Rojas", "carla@correo.com", "0414-111", ana);
        pedro = usuario("Pedro", "Díaz", "pedro@correo.com", "0424-222", luis);
        maria = usuario("María", "López", "maria@correo.com", "0412-333", null);
        em.flush();
        //@CreationTimestamp pone la fecha actual: se cambia para probar el filtro por fecha
        em.createQuery("UPDATE Usuario u SET u.fechaCreacion = :f WHERE u.id = :id")
                .setParameter("f", LocalDateTime.now().minusDays(40)).setParameter("id", maria.getId()).executeUpdate();

        Inmueble casaAna = inmueble("Casa Ana", "R-1", ana);
        Inmueble casaLuis = inmueble("Casa Luis", "R-2", luis);
        lead(carla, casaAna, 1);
        lead(carla, casaLuis, 3);
        lead(pedro, casaLuis, 1);
        em.flush();
        em.clear();

        vendedoraAna = new CuentaAutenticada(ana.getId(), TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_VENDEDOR);
        admin = new CuentaAutenticada(jefe.getId(), TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_ADMIN);
    }

    @Test
    void lasPestanasSeparanMiosYSinAsignar() {
        assertThat(nombres(filtro("todos"), vendedoraAna)).containsExactlyInAnyOrder("Carla", "Pedro", "María");
        assertThat(nombres(filtro("mios"), vendedoraAna)).containsExactly("Carla");
        assertThat(nombres(filtro("sin_asignar"), vendedoraAna)).containsExactly("María");

        ConteoContactosDTO conteo = contactoService.contar(filtro("todos"), vendedoraAna);
        assertThat(conteo).isEqualTo(new ConteoContactosDTO(3, 1, 1));
    }

    @Test
    void elEstatusEsElDelLeadMasReciente() {
        ContactoResponseDTO filaCarla = contactoService.buscar(filtro("mios"), vendedoraAna).getContenido().get(0);
        assertThat(filaCarla.getEstatusId()).isEqualTo(3);
        assertThat(filaCarla.getEstatus()).isEqualTo("Calificado");
        assertThat(filaCarla.getCantidadLeads()).isEqualTo(2);
        assertThat(filaCarla.getNombrePropietario()).isEqualTo("Ana Pérez");

        FiltroContactoDTO nuevos = filtro("todos");
        nuevos.setEstatus(List.of(1));
        assertThat(nombres(nuevos, admin)).containsExactly("Pedro");

        FiltroContactoDTO sinLead = filtro("todos");
        sinLead.setEstatus(List.of(FiltroContactoDTO.SIN_VALOR, 3));
        assertThat(nombres(sinLead, admin)).containsExactlyInAnyOrder("María", "Carla");
    }

    @Test
    void filtraPorPropietarioTextoYFecha() {
        FiltroContactoDTO porPropietario = filtro("todos");
        porPropietario.setPropietarioIds(List.of(luis.getId(), FiltroContactoDTO.SIN_VALOR));
        assertThat(nombres(porPropietario, admin)).containsExactlyInAnyOrder("Pedro", "María");

        FiltroContactoDTO texto = filtro("todos");
        texto.setQ("PEDRO d");
        assertThat(nombres(texto, admin)).containsExactly("Pedro");
        texto.setQ("0412");
        assertThat(nombres(texto, admin)).containsExactly("María");
        texto.setQ("100%");
        assertThat(nombres(texto, admin)).isEmpty();

        FiltroContactoDTO recientes = filtro("todos");
        recientes.setCreadoDesde(LocalDate.now().minusDays(7));
        assertThat(nombres(recientes, admin)).containsExactlyInAnyOrder("Carla", "Pedro");
        recientes.setCreadoDesde(null);
        recientes.setCreadoHasta(LocalDate.now().minusDays(30));
        assertThat(nombres(recientes, admin)).containsExactly("María");
    }

    @Test
    void ordenaPorPropietarioConLosSinAsignarAlFinal() {
        FiltroContactoDTO f = filtro("todos");
        f.setOrden("propietario");
        f.setDireccion("asc");
        assertThat(nombres(f, admin)).containsExactly("Carla", "Pedro", "María");
    }

    @Test
    void ultimaActividadYPropietarioAutomatico() {
        usuarioRepository.registrarActividad(maria.getId());
        assertThat(usuarioRepository.asignarPropietarioSiNoTiene(maria.getId(), em.getReference(Empleado.class, luis.getId()))).isEqualTo(1);
        //No reemplaza a un propietario existente
        assertThat(usuarioRepository.asignarPropietarioSiNoTiene(carla.getId(), em.getReference(Empleado.class, luis.getId()))).isZero();
        em.clear();

        FiltroContactoDTO conActividad = filtro("todos");
        conActividad.setSinActividad(false);
        List<ContactoResponseDTO> filas = contactoService.buscar(conActividad, admin).getContenido();
        assertThat(filas).extracting(ContactoResponseDTO::getNombre).containsExactly("María");
        assertThat(filas.get(0).getPropietarioId()).isEqualTo(luis.getId());
    }

    @Test
    void reglasParaCambiarElPropietario() {
        //Una vendedora puede tomar un contacto sin asignar, pero no uno de otro vendedor
        contactoService.cambiarPropietario(maria.getId(), ana.getId(), vendedoraAna);
        assertThatThrownBy(() -> contactoService.cambiarPropietario(pedro.getId(), ana.getId(), vendedoraAna))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> contactoService.cambiarPropietario(carla.getId(), luis.getId(), vendedoraAna))
                .isInstanceOf(AccessDeniedException.class);
        //Puede liberar los suyos y el admin puede reasignar cualquiera
        contactoService.cambiarPropietario(carla.getId(), null, vendedoraAna);
        contactoService.cambiarPropietario(pedro.getId(), ana.getId(), admin);
        em.flush();
        em.clear();
        assertThat(nombres(filtro("mios"), vendedoraAna)).containsExactlyInAnyOrder("María", "Pedro");
    }

    @Test
    void laFichaIncluyeLeadsYComunicacionesVisibles() {
        var ficha = contactoService.obtenerDetalle(carla.getId(), vendedoraAna);
        assertThat(ficha.getContacto().getCorreo()).isEqualTo("carla@correo.com");
        assertThat(ficha.getLeads()).extracting(l -> l.getTituloInmueble()).containsExactly("Casa Luis", "Casa Ana");
        assertThat(ficha.getComunicaciones()).isEmpty();
    }

    @Test
    void exportaExcelYPdf() {
        List<ColumnaContacto> columnas = ColumnaContacto.desdeClaves(List.of("nombre", "fechaCreacion", "estatus", "leads"));
        var excel = contactoService.exportar(filtro("todos"), columnas, FormatoExportacion.XLSX, false, admin);
        assertThat(excel.nombre()).endsWith(".xlsx");
        assertThat(new String(excel.contenido(), 0, 2, StandardCharsets.ISO_8859_1)).isEqualTo("PK"); //xlsx es un zip

        var pdf = contactoService.exportar(filtro("sin_asignar"), ColumnaContacto.POR_DEFECTO, FormatoExportacion.PDF, true, admin);
        assertThat(pdf.nombre()).startsWith("contactos-sin-asignar-").endsWith(".pdf");
        assertThat(new String(pdf.contenido(), 0, 4, StandardCharsets.ISO_8859_1)).isEqualTo("%PDF");

        assertThatThrownBy(() -> ColumnaContacto.desdeClaves(List.of("contrasena"))).isInstanceOf(IllegalArgumentException.class);
    }

    private List<String> nombres(FiltroContactoDTO f, CuentaAutenticada cuenta) {
        return contactoService.buscar(f, cuenta).getContenido().stream().map(ContactoResponseDTO::getNombre).toList();
    }

    private static FiltroContactoDTO filtro(String vista) {
        FiltroContactoDTO f = new FiltroContactoDTO();
        f.setVista(vista);
        return f;
    }

    private Empleado empleado(String nombre, String apellido, int cedula) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellido(apellido);
        e.setCedula(cedula);
        e.setCorreo(nombre.toLowerCase() + "@empresa.com");
        e.setEsAdmin(false);
        e.setActivo(true);
        e.setFechaEntrada(LocalDateTime.now());
        e.setSalidaSistema(LocalDateTime.now());
        em.persist(e);
        return e;
    }

    private Usuario usuario(String nombre, String apellido, String correo, String telefono, Empleado propietario) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setCorreo(correo);
        u.setTelefono(telefono);
        u.setTipoNegocio("compra");
        u.setPropietario(propietario);
        em.persist(u);
        return u;
    }

    private Inmueble inmueble(String titulo, String rrbr, Empleado vendedor) {
        Ciudad ciudad = new Ciudad();
        ciudad.setNombre("Valencia");
        em.persist(ciudad);
        TipoInmueble tipo = new TipoInmueble();
        tipo.setNombre("Casa");
        em.persist(tipo);
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
        i.setEmpleado(vendedor);
        i.setVendido(false);
        i.setAlquiler(false);
        em.persist(i);
        return i;
    }

    private void lead(Usuario usuario, Inmueble inmueble, int estatus) {
        Lead l = new Lead();
        l.setUsuario(usuario);
        l.setInmueble(inmueble);
        l.setEstatus(estatus);
        em.persist(l);
    }
}
