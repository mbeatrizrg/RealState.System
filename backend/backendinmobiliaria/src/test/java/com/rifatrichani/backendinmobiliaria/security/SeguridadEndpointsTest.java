package com.rifatrichani.backendinmobiliaria.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rifatrichani.backendinmobiliaria.config.CorsConfig;
import com.rifatrichani.backendinmobiliaria.config.SecurityConfig;
import com.rifatrichani.backendinmobiliaria.controllers.AuthController;
import com.rifatrichani.backendinmobiliaria.controllers.EmpleadoController;
import com.rifatrichani.backendinmobiliaria.controllers.FavoritoController;
import com.rifatrichani.backendinmobiliaria.controllers.InmuebleController;
import com.rifatrichani.backendinmobiliaria.controllers.VistoController;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;
import com.rifatrichani.backendinmobiliaria.services.EmpleadoService;
import com.rifatrichani.backendinmobiliaria.services.FavoritoService;
import com.rifatrichani.backendinmobiliaria.services.InmuebleService;
import com.rifatrichani.backendinmobiliaria.services.VistoService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

//Verifica las reglas de acceso y el filtro JWT sin base de datos (los repositorios y servicios son simulados)
@WebMvcTest(controllers = {InmuebleController.class, AuthController.class, EmpleadoController.class, FavoritoController.class, VistoController.class})
@Import({SecurityConfig.class, CorsConfig.class, JwtTokenProvider.class, JwtAuthenticationEntryPoint.class})
@TestPropertySource(properties = {
        "app.jwt.secret=c2VjcmV0by1kZS1wcnVlYmFzLXNvbG8tcGFyYS10ZXN0cy0xMjM0NTY3ODkwYWJjZGVm",
        "app.cors.allowed-origins=http://localhost:5173"
})
class SeguridadEndpointsTest {

    @Autowired MockMvc mvc;
    @Autowired JwtTokenProvider jwt;

    @MockitoBean EmpleadoRepository empleadoRepository;
    @MockitoBean UsuarioRepository usuarioRepository;
    @MockitoBean InmuebleService inmuebleService;
    @MockitoBean AuthServices authServices;
    @MockitoBean EmpleadoService empleadoService;
    @MockitoBean FavoritoService favoritoService;
    @MockitoBean VistoService vistoService;

    private Empleado empleado(int id, boolean admin) {
        Empleado e = new Empleado();
        e.setId(id);
        e.setNombre("Prueba");
        e.setEsAdmin(admin);
        e.setActivo(true);
        e.setContrasena("$2a$10$hash");
        e.setFechaEntrada(LocalDateTime.now().minusHours(1));
        e.setSalidaSistema(LocalDateTime.now().minusHours(2));
        when(empleadoRepository.findById(id)).thenReturn(Optional.of(e));
        return e;
    }

    private void usuario(int id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setVerificado(true);
        u.setContrasena("$2a$10$hash");
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(u));
    }

    private String bearer(int id, TipoCuenta tipo, String rol) {
        return "Bearer " + jwt.generarToken(id, tipo, rol);
    }

    @Test
    void portalPublicoNoRequiereToken() throws Exception {
        when(inmuebleService.buscarPorFiltros(any(), any(), any(), any(), any(), any())).thenReturn(List.of());
        mvc.perform(get("/api/inmueble/buscar")).andExpect(status().isOk());
    }

    @Test
    void crmSinTokenResponde401() throws Exception {
        mvc.perform(get("/api/inmueble/crm")).andExpect(status().isUnauthorized());
    }

    @Test
    void endpointDePruebaEliminadoNoEsPublico() throws Exception {
        mvc.perform(post("/api/auth/crear-vendedor-prueba")).andExpect(status().isUnauthorized());
    }

    @Test
    void vendedorNoEntraAlPanelDeAdmin() throws Exception {
        empleado(5, false);
        mvc.perform(get("/api/empleado").header("Authorization", bearer(5, TipoCuenta.EMPLEADO, "ROLE_VENDEDOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEntraAlPanelDeAdmin() throws Exception {
        empleado(1, true);
        mvc.perform(get("/api/empleado").header("Authorization", bearer(1, TipoCuenta.EMPLEADO, "ROLE_ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void elRolSeTomaDeLaBaseDeDatosYNoDelToken() throws Exception {
        //El token dice ADMIN pero en la base de datos el empleado ya no es administrador
        empleado(7, false);
        mvc.perform(get("/api/empleado").header("Authorization", bearer(7, TipoCuenta.EMPLEADO, "ROLE_ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioDelPortalNoEntraAlCrm() throws Exception {
        usuario(5);
        mvc.perform(get("/api/inmueble/crm").header("Authorization", bearer(5, TipoCuenta.USUARIO, "ROLE_USUARIO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void tokenFirmadoConOtraClaveEsRechazado() throws Exception {
        empleado(1, true);
        String falso = Jwts.builder().setSubject("1").setIssuer("rifatrichani-api")
                .claim("tipo", "EMPLEADO").claim("rol", "ROLE_ADMIN")
                .signWith(Keys.hmacShaKeyFor("otra-clave-cualquiera-de-32-bytes-o-mas!!".getBytes()), SignatureAlgorithm.HS256)
                .compact();
        mvc.perform(get("/api/empleado").header("Authorization", "Bearer " + falso)).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenEmitidoAntesDelLogoutEsRechazado() throws Exception {
        Empleado e = empleado(3, true);
        String token = bearer(3, TipoCuenta.EMPLEADO, "ROLE_ADMIN");
        e.setSalidaSistema(LocalDateTime.now().plusSeconds(5));
        mvc.perform(get("/api/empleado").header("Authorization", token)).andExpect(status().isUnauthorized());
    }

    @Test
    void empleadoDesactivadoPierdeElAcceso() throws Exception {
        Empleado e = empleado(4, true);
        e.setActivo(false);
        mvc.perform(get("/api/empleado").header("Authorization", bearer(4, TipoCuenta.EMPLEADO, "ROLE_ADMIN")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void favoritosRequierenUsuarioDelPortal() throws Exception {
        empleado(2, true);
        mvc.perform(get("/api/favorito")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/favorito").header("Authorization", bearer(2, TipoCuenta.EMPLEADO, "ROLE_ADMIN")))
                .andExpect(status().isForbidden());
        usuario(9);
        mvc.perform(get("/api/favorito").header("Authorization", bearer(9, TipoCuenta.USUARIO, "ROLE_USUARIO")))
                .andExpect(status().isOk());
    }

    @Test
    void vistaAnonimaSeAceptaSinGuardar() throws Exception {
        mvc.perform(post("/api/visto").contentType(MediaType.APPLICATION_JSON).content("{\"inmueble\": 1}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void corsRechazaOrigenesNoPermitidos() throws Exception {
        mvc.perform(options("/api/inmueble/buscar").header("Origin", "https://sitio-malicioso.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
        mvc.perform(options("/api/inmueble/buscar").header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
    }
}
