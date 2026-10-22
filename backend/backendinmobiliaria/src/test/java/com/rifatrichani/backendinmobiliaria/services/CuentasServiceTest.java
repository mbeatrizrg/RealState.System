package com.rifatrichani.backendinmobiliaria.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.rifatrichani.backendinmobiliaria.dto.request.CambioCorreoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.RecuperarContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.RestablecerContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.JwtTokenProvider;
import com.rifatrichani.backendinmobiliaria.security.LimitadorIntentosLogin;
import com.rifatrichani.backendinmobiliaria.services.impl.AuthServicesImpl;
import com.rifatrichani.backendinmobiliaria.services.impl.UsuarioServiceImpl;

//Logica de recuperacion de contraseña y cambio de correo, sin base de datos ni servidor de correo
class CuentasServiceTest {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private UsuarioRepository usuarioRepository;
    private CorreoService correoService;
    private AuthServicesImpl authServices;
    private UsuarioServiceImpl usuarioService;
    private Usuario usuario;
    //Ultimo codigo "enviado" por correo
    private final AtomicReference<String> codigoEnviado = new AtomicReference<>();

    @BeforeEach
    void preparar() {
        usuarioRepository = mock(UsuarioRepository.class);
        correoService = mock(CorreoService.class);
        doAnswer(inv -> { codigoEnviado.set(inv.getArgument(4)); return null; })
                .when(correoService).enviarCodigo(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt());
        JwtTokenProvider jwt = new JwtTokenProvider("c2VjcmV0by1kZS1wcnVlYmFzLXNvbG8tcGFyYS10ZXN0cy0xMjM0NTY3ODkwYWJjZGVm", 60_000);
        authServices = new AuthServicesImpl(usuarioRepository, mock(EmpleadoRepository.class), encoder, jwt,
                new LimitadorIntentosLogin(), correoService, true);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, encoder, correoService);

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Ana");
        usuario.setCorreo("ana@correo.com");
        usuario.setContrasena(encoder.encode("ClaveVieja123"));
        usuario.setVerificado(true);
        when(usuarioRepository.findByCorreoIgnoreCase("ana@correo.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
    }

    private RestablecerContrasenaDTO restablecer(String codigo) {
        RestablecerContrasenaDTO dto = new RestablecerContrasenaDTO();
        dto.setCorreo("ana@correo.com");
        dto.setTipo("USUARIO");
        dto.setCodigo(codigo);
        dto.setContrasenaNueva("ClaveNueva456");
        return dto;
    }

    private void solicitarRecuperacion(String correo) {
        RecuperarContrasenaDTO dto = new RecuperarContrasenaDTO();
        dto.setCorreo(correo);
        dto.setTipo("USUARIO");
        authServices.solicitarRecuperacion(dto);
    }

    @Test
    void recuperarConCorreoInexistenteNoFallaNiEnviaCorreo() {
        solicitarRecuperacion("nadie@correo.com");
        verify(correoService, never()).enviarCodigo(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    void elCodigoDeRecuperacionSeGuardaComoHash() {
        solicitarRecuperacion("ana@correo.com");
        assertNotNull(codigoEnviado.get());
        assertNotNull(usuario.getResetCodigo());
        assertTrue(!usuario.getResetCodigo().contains(codigoEnviado.get()));
    }

    @Test
    void codigoCorrectoCambiaLaContrasenaYCierraSesiones() {
        solicitarRecuperacion("ana@correo.com");
        authServices.restablecerContrasena(restablecer(codigoEnviado.get()));
        assertTrue(encoder.matches("ClaveNueva456", usuario.getContrasena()));
        assertNull(usuario.getResetCodigo());
        assertNotNull(usuario.getTokensValidosDesde());
    }

    @Test
    void codigoIncorrectoSumaIntentosYSeBloqueaAlQuinto() {
        solicitarRecuperacion("ana@correo.com");
        String correcto = codigoEnviado.get();
        String incorrecto = correcto.equals("111111") ? "222222" : "111111";
        for (int i = 0; i < 5; i++) {
            assertThrows(IllegalArgumentException.class, () -> authServices.restablecerContrasena(restablecer(incorrecto)));
        }
        assertEquals(5, usuario.getResetIntentos());
        //Ni siquiera el codigo correcto sirve despues de 5 intentos fallidos
        assertThrows(IllegalArgumentException.class, () -> authServices.restablecerContrasena(restablecer(correcto)));
        assertTrue(encoder.matches("ClaveVieja123", usuario.getContrasena()));
    }

    @Test
    void codigoVencidoNoSirve() {
        solicitarRecuperacion("ana@correo.com");
        usuario.setResetExpira(LocalDateTime.now().minusMinutes(1));
        assertThrows(IllegalArgumentException.class, () -> authServices.restablecerContrasena(restablecer(codigoEnviado.get())));
    }

    @Test
    void cambioDeCorreoExigeLaContrasenaActual() {
        CambioCorreoDTO dto = new CambioCorreoDTO();
        dto.setCorreoNuevo("nuevo@correo.com");
        dto.setContrasena("incorrecta");
        assertThrows(BadCredentialsException.class, () -> usuarioService.solicitarCambioCorreo(1, dto));
    }

    @Test
    void elCorreoSoloCambiaAlConfirmarElCodigoEnviadoALaDireccionNueva() {
        CambioCorreoDTO dto = new CambioCorreoDTO();
        dto.setCorreoNuevo("Nuevo@Correo.com");
        dto.setContrasena("ClaveVieja123");
        usuarioService.solicitarCambioCorreo(1, dto);
        verify(correoService).enviarCodigo(eq("nuevo@correo.com"), any(), any(), any(), any(), anyInt());
        assertEquals("ana@correo.com", usuario.getCorreo());

        usuarioService.confirmarCambioCorreo(1, codigoEnviado.get());
        assertEquals("nuevo@correo.com", usuario.getCorreo());
        assertNull(usuario.getCorreoPendiente());
    }

    private UsuarioRegistroDTO registro(int cedula) {
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setNombre("Luis");
        dto.setApellido("Pérez");
        dto.setTelefono("04145555555");
        dto.setCorreo("luis@correo.com");
        dto.setCedula(cedula);
        dto.setContrasena("ClaveSegura123");
        return dto;
    }

    @Test
    void elRegistroGuardaLaCedula() {
        authServices.registrarUsuario(registro(12345678));
        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.argThat(u -> Integer.valueOf(12345678).equals(u.getCedula())));
    }

    @Test
    void elRegistroRechazaUnaCedulaRepetida() {
        when(usuarioRepository.existsByCedula(12345678)).thenReturn(true);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authServices.registrarUsuario(registro(12345678)));
        assertEquals("La cédula ya se encuentra registrada.", error.getMessage());
        verify(usuarioRepository, never()).save(any());
    }
}
