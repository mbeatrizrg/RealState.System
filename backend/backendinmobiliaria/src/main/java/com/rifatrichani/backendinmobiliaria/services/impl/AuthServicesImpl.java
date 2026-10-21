package com.rifatrichani.backendinmobiliaria.services.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.CambioContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.ConfirmarCuentaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LoginResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;
import com.rifatrichani.backendinmobiliaria.security.JwtTokenProvider;
import com.rifatrichani.backendinmobiliaria.security.LimitadorIntentosLogin;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;

@Service
public class AuthServicesImpl implements AuthServices {
    private static final Logger log = LoggerFactory.getLogger(AuthServicesImpl.class);

    //Mismo mensaje para correo inexistente o contraseña incorrecta: no se revela que correos estan registrados
    private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos.";
    private static final String CODIGO_INVALIDO = "El código es inválido o ya expiró.";
    private static final int MINUTOS_VIGENCIA_CODIGO = 15;
    private static final int MAX_INTENTOS_CODIGO = 5;

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LimitadorIntentosLogin limitador;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final boolean verificacionCorreo;
    private final String remitente;
    private final SecureRandom random = new SecureRandom();
    //Hash de relleno: si el correo no existe igual se ejecuta un BCrypt, asi el tiempo de respuesta no delata si la cuenta existe
    private final String hashRelleno;

    public AuthServicesImpl(UsuarioRepository ur, EmpleadoRepository er, PasswordEncoder pe, JwtTokenProvider jtp,
                            LimitadorIntentosLogin limitador, ObjectProvider<JavaMailSender> mailSender,
                            @Value("${app.auth.verificacion-correo:true}") boolean verificacionCorreo,
                            @Value("${spring.mail.username:}") String remitente) {
        this.usuarioRepository = ur;
        this.empleadoRepository = er;
        this.passwordEncoder = pe;
        this.jwtTokenProvider = jtp;
        this.limitador = limitador;
        this.mailSender = mailSender;
        this.verificacionCorreo = verificacionCorreo;
        this.remitente = remitente;
        this.hashRelleno = pe.encode("relleno-para-igualar-tiempos");
    }

    // ===================== CRM (empleados) =====================

    @Override
    @Transactional
    public LoginResponseDTO loginEmpleadoCrm(UsuarioLogeoDTO dto) {
        String correo = normalizarCorreo(dto.getCorreo());
        String claveLimite = "empleado:" + correo;
        limitador.verificarNoBloqueado(claveLimite);

        Empleado empleado = empleadoRepository.findByCorreoIgnoreCase(correo).orElse(null);
        String hash = (empleado != null && empleado.getContrasena() != null) ? empleado.getContrasena() : hashRelleno;
        boolean claveCorrecta = passwordEncoder.matches(dto.getContrasena(), hash);
        if (empleado == null || empleado.getContrasena() == null || !claveCorrecta || !Boolean.TRUE.equals(empleado.getActivo())) {
            limitador.registrarFallo(claveLimite);
            throw new BadCredentialsException(CREDENCIALES_INVALIDAS);
        }
        limitador.registrarExito(claveLimite);

        empleado.setFechaEntrada(LocalDateTime.now());
        empleadoRepository.save(empleado);

        String rol = Boolean.TRUE.equals(empleado.getEsAdmin()) ? CuentaAutenticada.ROL_ADMIN : CuentaAutenticada.ROL_VENDEDOR;
        String token = jwtTokenProvider.generarToken(empleado.getId(), TipoCuenta.EMPLEADO, rol);
        return new LoginResponseDTO(token, TipoCuenta.EMPLEADO.name(), rol, empleado.getId(),
                empleado.getNombre() + " " + (empleado.getApellido() != null ? empleado.getApellido() : ""),
                jwtTokenProvider.getDuracionMs());
    }

    //Registra la salida; el JwtAuthenticationFilter rechaza los tokens emitidos antes de esta fecha
    @Override
    @Transactional
    public void logoutEmpleadoCrm(Integer idEmpleado) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado."));
        empleado.setSalidaSistema(LocalDateTime.now());
        empleadoRepository.save(empleado);
    }

    //Al cambiar la contraseña se cierran todas las sesiones abiertas del empleado (debe volver a iniciar sesion)
    @Override
    @Transactional
    public void cambiarContrasenaEmpleado(Integer idEmpleado, CambioContrasenaDTO dto) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado."));
        if (!passwordEncoder.matches(dto.getContrasenaActual(), empleado.getContrasena())) {
            throw new BadCredentialsException("La contraseña actual no es correcta.");
        }
        empleado.setContrasena(passwordEncoder.encode(dto.getContrasenaNueva()));
        empleado.setSalidaSistema(LocalDateTime.now());
        empleadoRepository.save(empleado);
    }

    // ===================== Portal web (usuarios) =====================

    @Override
    @Transactional
    public LoginResponseDTO loginUsuarioWeb(UsuarioLogeoDTO dto) {
        String correo = normalizarCorreo(dto.getCorreo());
        String claveLimite = "usuario:" + correo;
        limitador.verificarNoBloqueado(claveLimite);

        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo).orElse(null);
        String hash = (usuario != null && usuario.getContrasena() != null) ? usuario.getContrasena() : hashRelleno;
        boolean claveCorrecta = passwordEncoder.matches(dto.getContrasena(), hash);
        if (usuario == null || usuario.getContrasena() == null || !claveCorrecta) {
            limitador.registrarFallo(claveLimite);
            throw new BadCredentialsException(CREDENCIALES_INVALIDAS);
        }
        limitador.registrarExito(claveLimite);
        //Solo se informa despues de validar la contraseña, asi no sirve para descubrir cuentas
        if (!Boolean.TRUE.equals(usuario.getVerificado())) {
            throw new SecurityException("Debe confirmar su correo antes de iniciar sesión.");
        }

        String token = jwtTokenProvider.generarToken(usuario.getId(), TipoCuenta.USUARIO, CuentaAutenticada.ROL_USUARIO);
        return new LoginResponseDTO(token, TipoCuenta.USUARIO.name(), CuentaAutenticada.ROL_USUARIO, usuario.getId(),
                usuario.getNombre() + " " + usuario.getApellido(), jwtTokenProvider.getDuracionMs());
    }

    //Si el correo no se puede enviar se revierte el registro (la transaccion completa), para que el usuario pueda intentarlo de nuevo
    @Override
    @Transactional
    public String registrarUsuario(UsuarioRegistroDTO dto) {
        String correo = normalizarCorreo(dto.getCorreo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setTelefono(dto.getTelefono().trim());
        usuario.setCorreo(correo);
        usuario.setTipoNegocio(dto.getTipoNegocio() != null && !dto.getTipoNegocio().isBlank() ? dto.getTipoNegocio().trim() : "Sin definir");
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        if (!verificacionCorreo) {
            usuario.setVerificado(true);
            usuarioRepository.save(usuario);
            return "Usuario registrado correctamente.";
        }
        usuario.setVerificado(false);
        asignarCodigo(usuario);
        usuarioRepository.save(usuario);
        enviarCodigo(usuario);
        return "Usuario registrado. Revise su correo e ingrese el código de verificación.";
    }

    //noRollbackFor: el contador de intentos fallidos se debe guardar aunque se responda error
    @Override
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void confirmarCuenta(ConfirmarCuentaDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(normalizarCorreo(dto.getCorreo()))
                .filter(u -> !Boolean.TRUE.equals(u.getVerificado()) && u.getCodVerif() != null)
                .orElseThrow(() -> new IllegalArgumentException(CODIGO_INVALIDO));
        if (usuario.getCodVerifIntentos() >= MAX_INTENTOS_CODIGO) {
            throw new IllegalArgumentException("Demasiados intentos. Solicite un nuevo código.");
        }
        boolean vigente = usuario.getCodVerifExpira() != null && LocalDateTime.now().isBefore(usuario.getCodVerifExpira());
        //Comparacion en tiempo constante
        boolean coincide = MessageDigest.isEqual(
                String.valueOf(usuario.getCodVerif()).getBytes(StandardCharsets.UTF_8),
                dto.getCodigo().getBytes(StandardCharsets.UTF_8));
        if (!vigente || !coincide) {
            usuario.setCodVerifIntentos(usuario.getCodVerifIntentos() + 1);
            usuarioRepository.save(usuario);
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        usuario.setVerificado(true);
        usuario.setCodVerif(null);
        usuario.setCodVerifExpira(null);
        usuario.setCodVerifIntentos(0);
        usuarioRepository.save(usuario);
    }

    //Siempre termina sin error (el controlador responde un mensaje generico), para no revelar si el correo existe
    @Override
    @Transactional
    public void reenviarCodigo(String correo) {
        usuarioRepository.findByCorreoIgnoreCase(normalizarCorreo(correo))
                .filter(u -> !Boolean.TRUE.equals(u.getVerificado()))
                //Maximo un codigo por minuto por cuenta
                .filter(u -> u.getCodVerifExpira() == null
                        || LocalDateTime.now().isAfter(u.getCodVerifExpira().minusMinutes(MINUTOS_VIGENCIA_CODIGO - 1L)))
                .ifPresent(u -> {
                    asignarCodigo(u);
                    usuarioRepository.save(u);
                    enviarCodigo(u);
                });
    }

    // ===================== Auxiliares =====================

    private void asignarCodigo(Usuario usuario) {
        usuario.setCodVerif(100000 + random.nextInt(900000));
        usuario.setCodVerifExpira(LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA_CODIGO));
        usuario.setCodVerifIntentos(0);
    }

    private void enviarCodigo(Usuario usuario) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || remitente == null || remitente.isBlank()) {
            throw new IllegalStateException("El envío de correos no está configurado (MAIL_USERNAME / MAIL_PASSWORD).");
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(usuario.getCorreo());
        mensaje.setSubject("Código de verificación - Rifat Richani Bienes Raíces");
        mensaje.setText("Hola " + usuario.getNombre() + ",\n\nTu código de verificación es: " + usuario.getCodVerif()
                + "\n\nVence en " + MINUTOS_VIGENCIA_CODIGO + " minutos. Si no creaste una cuenta, ignora este mensaje.");
        try {
            sender.send(mensaje);
        } catch (MailException e) {
            log.error("No se pudo enviar el codigo de verificacion a {}", usuario.getCorreo(), e);
            throw new IllegalStateException("No se pudo enviar el correo de verificación. Intente de nuevo más tarde.");
        }
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }
}
