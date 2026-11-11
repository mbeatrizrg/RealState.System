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
        usuario.setVerificado(true);
        when(usuarioRepository.findByCorreoIgnoreCase("ana@correo.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
    }


}
