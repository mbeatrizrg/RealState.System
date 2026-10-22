package com.rifatrichani.backendinmobiliaria.services.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.CambioContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.ConfirmarCuentaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.RecuperarContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.RestablecerContrasenaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LoginResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PerfilSesionDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CodigosSeguridad;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;
import com.rifatrichani.backendinmobiliaria.security.JwtTokenProvider;
import com.rifatrichani.backendinmobiliaria.security.LimitadorIntentosLogin;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;
import com.rifatrichani.backendinmobiliaria.services.CorreoService;

@Service
public class AuthServicesImpl implements AuthServices {
    //Mismo mensaje para correo inexistente o contraseña incorrecta: no se revela que correos estan registrados
    private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos.";
    private static final String CODIGO_INVALIDO = "El código es inválido o ya expiró.";
    static final int MINUTOS_VIGENCIA_CODIGO = 15;
    static final int MAX_INTENTOS_CODIGO = 5;

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LimitadorIntentosLogin limitador;
    private final CorreoService correoService;
    private final boolean verificacionCorreo;
    //Hash de relleno: si el correo no existe igual se ejecuta un BCrypt, asi el tiempo de respuesta no delata si la cuenta existe
    private final String hashRelleno;

    public AuthServicesImpl(UsuarioRepository ur, EmpleadoRepository er, PasswordEncoder pe, JwtTokenProvider jtp,
                            LimitadorIntentosLogin limitador, CorreoService correoService,
                            @Value("${app.auth.verificacion-correo:true}") boolean verificacionCorreo) {
        this.usuarioRepository = ur;
        this.empleadoRepository = er;
        this.passwordEncoder = pe;
        this.jwtTokenProvider = jtp;
        this.limitador = limitador;
        this.correoService = correoService;
        this.verificacionCorreo = verificacionCorreo;
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
                nombreCompleto(empleado.getNombre(), empleado.getApellido()), jwtTokenProvider.getDuracionMs());
    }

    //Registra la salida; el JwtAuthenticationFilter rechaza los tokens emitidos antes de esta fecha
    @Override
    @Transactional
    public void logoutEmpleadoCrm(Integer idEmpleado) {
        Empleado empleado = buscarEmpleado(idEmpleado);
        empleado.setSalidaSistema(LocalDateTime.now());
        empleadoRepository.save(empleado);
    }

    //Al cambiar la contraseña se cierran todas las sesiones abiertas del empleado (debe volver a iniciar sesion)
    @Override
    @Transactional
    public void cambiarContrasenaEmpleado(Integer idEmpleado, CambioContrasenaDTO dto) {
        Empleado empleado = buscarEmpleado(idEmpleado);
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
                nombreCompleto(usuario.getNombre(), usuario.getApellido()), jwtTokenProvider.getDuracionMs());
    }

    @Override
    @Transactional
    public void logoutUsuarioWeb(Integer idUsuario) {
        Usuario usuario = buscarUsuario(idUsuario);
        usuario.setTokensValidosDesde(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void cambiarContrasenaUsuario(Integer idUsuario, CambioContrasenaDTO dto) {
        Usuario usuario = buscarUsuario(idUsuario);
        if (!passwordEncoder.matches(dto.getContrasenaActual(), usuario.getContrasena())) {
            throw new BadCredentialsException("La contraseña actual no es correcta.");
        }
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasenaNueva()));
        usuario.setTokensValidosDesde(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    //Si el correo no se puede enviar se revierte el registro (la transaccion completa), para que el usuario pueda intentarlo de nuevo
    @Override
    @Transactional
    public String registrarUsuario(UsuarioRegistroDTO dto) {
        String correo = normalizarCorreo(dto.getCorreo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado.");
        }
        if (usuarioRepository.existsByCedula(dto.getCedula())) {
            throw new IllegalArgumentException("La cédula ya se encuentra registrada.");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setTelefono(dto.getTelefono().trim());
        usuario.setCorreo(correo);
        usuario.setCedula(dto.getCedula());
        usuario.setTipoNegocio(dto.getTipoNegocio() != null && !dto.getTipoNegocio().isBlank() ? dto.getTipoNegocio().trim() : "Sin definir");
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        if (!verificacionCorreo) {
            usuario.setVerificado(true);
            usuarioRepository.save(usuario);
            return "Usuario registrado correctamente.";
        }
        usuario.setVerificado(false);
        String codigo = asignarCodigoVerificacion(usuario);
        usuarioRepository.save(usuario);
        correoService.enviarCodigo(usuario.getCorreo(), usuario.getNombre(), "Código de verificación",
                "Gracias por registrarte. Para activar tu cuenta ingresa este código en la página.", codigo, MINUTOS_VIGENCIA_CODIGO);
        return "Usuario registrado. Revise su correo e ingrese el código de verificación.";
    }

    //noRollbackFor: el contador de intentos fallidos se debe guardar aunque se responda error
    @Override
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void confirmarCuenta(ConfirmarCuentaDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(normalizarCorreo(dto.getCorreo()))
                .filter(u -> !Boolean.TRUE.equals(u.getVerificado()) && u.getCodVerif() != null)
                .orElseThrow(() -> new IllegalArgumentException(CODIGO_INVALIDO));
        validarCodigoVerificacion(usuario, dto.getCodigo());
        usuario.setVerificado(true);
        limpiarCodigoVerificacion(usuario);
        usuarioRepository.save(usuario);
    }

    //Siempre termina sin error (el controlador responde un mensaje generico), para no revelar si el correo existe
    @Override
    @Transactional
    public void reenviarCodigo(String correo) {
        usuarioRepository.findByCorreoIgnoreCase(normalizarCorreo(correo))
                .filter(u -> !Boolean.TRUE.equals(u.getVerificado()))
                .filter(u -> puedeReenviar(u.getCodVerifExpira()))
                .ifPresent(u -> {
                    String codigo = asignarCodigoVerificacion(u);
                    usuarioRepository.save(u);
                    correoService.enviarCodigo(u.getCorreo(), u.getNombre(), "Código de verificación",
                            "Este es tu nuevo código para activar tu cuenta.", codigo, MINUTOS_VIGENCIA_CODIGO);
                });
    }

    // ===================== Recuperacion de contraseña =====================

    @Override
    @Transactional
    public void solicitarRecuperacion(RecuperarContrasenaDTO dto) {
        String correo = normalizarCorreo(dto.getCorreo());
        String codigo = CodigosSeguridad.generarCodigo();
        if (TipoCuenta.EMPLEADO.name().equals(dto.getTipo())) {
            empleadoRepository.findByCorreoIgnoreCase(correo)
                    .filter(e -> Boolean.TRUE.equals(e.getActivo()) && puedeReenviar(e.getResetExpira()))
                    .ifPresent(e -> {
                        e.setResetCodigo(CodigosSeguridad.hash(codigo));
                        e.setResetExpira(LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA_CODIGO));
                        e.setResetIntentos(0);
                        empleadoRepository.save(e);
                        enviarCodigoRecuperacion(e.getCorreo(), e.getNombre(), codigo);
                    });
        } else {
            usuarioRepository.findByCorreoIgnoreCase(correo)
                    .filter(u -> u.getContrasena() != null && puedeReenviar(u.getResetExpira()))
                    .ifPresent(u -> {
                        u.setResetCodigo(CodigosSeguridad.hash(codigo));
                        u.setResetExpira(LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA_CODIGO));
                        u.setResetIntentos(0);
                        usuarioRepository.save(u);
                        enviarCodigoRecuperacion(u.getCorreo(), u.getNombre(), codigo);
                    });
        }
    }

    //Al restablecer se cierran todas las sesiones abiertas de la cuenta
    @Override
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void restablecerContrasena(RestablecerContrasenaDTO dto) {
        String correo = normalizarCorreo(dto.getCorreo());
        String hashNueva = passwordEncoder.encode(dto.getContrasenaNueva());
        LocalDateTime ahora = LocalDateTime.now();
        if (TipoCuenta.EMPLEADO.name().equals(dto.getTipo())) {
            Empleado e = empleadoRepository.findByCorreoIgnoreCase(correo)
                    .filter(emp -> emp.getResetCodigo() != null && Boolean.TRUE.equals(emp.getActivo()))
                    .orElseThrow(() -> new IllegalArgumentException(CODIGO_INVALIDO));
            if (!codigoResetValido(e.getResetCodigo(), e.getResetExpira(), e.getResetIntentos(), dto.getCodigo())) {
                e.setResetIntentos(e.getResetIntentos() + 1);
                empleadoRepository.save(e);
                throw new IllegalArgumentException(CODIGO_INVALIDO);
            }
            e.setContrasena(hashNueva);
            e.setResetCodigo(null);
            e.setResetExpira(null);
            e.setResetIntentos(0);
            e.setSalidaSistema(ahora);
            empleadoRepository.save(e);
        } else {
            Usuario u = usuarioRepository.findByCorreoIgnoreCase(correo)
                    .filter(usu -> usu.getResetCodigo() != null)
                    .orElseThrow(() -> new IllegalArgumentException(CODIGO_INVALIDO));
            if (!codigoResetValido(u.getResetCodigo(), u.getResetExpira(), u.getResetIntentos(), dto.getCodigo())) {
                u.setResetIntentos(u.getResetIntentos() + 1);
                usuarioRepository.save(u);
                throw new IllegalArgumentException(CODIGO_INVALIDO);
            }
            u.setContrasena(hashNueva);
            u.setResetCodigo(null);
            u.setResetExpira(null);
            u.setResetIntentos(0);
            u.setTokensValidosDesde(ahora);
            //Recibir el codigo en el correo demuestra que es su direccion: la cuenta queda verificada
            if (!Boolean.TRUE.equals(u.getVerificado())) {
                u.setVerificado(true);
                limpiarCodigoVerificacion(u);
            }
            usuarioRepository.save(u);
        }
        limitador.registrarExito(dto.getTipo().toLowerCase() + ":" + correo);
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilSesionDTO obtenerPerfilSesion(CuentaAutenticada cuenta) {
        PerfilSesionDTO dto = new PerfilSesionDTO();
        dto.setId(cuenta.id());
        dto.setTipo(cuenta.tipo().name());
        dto.setRol(cuenta.rol());
        if (cuenta.tipo() == TipoCuenta.EMPLEADO) {
            Empleado e = buscarEmpleado(cuenta.id());
            dto.setNombre(e.getNombre());
            dto.setApellido(e.getApellido());
            dto.setCorreo(e.getCorreo());
            dto.setCedula(e.getCedula());
            dto.setEsAdmin(e.getEsAdmin());
        } else {
            Usuario u = buscarUsuario(cuenta.id());
            dto.setNombre(u.getNombre());
            dto.setApellido(u.getApellido());
            dto.setCorreo(u.getCorreo());
            dto.setTelefono(u.getTelefono());
            dto.setCedula(u.getCedula());
            dto.setTipoNegocio(u.getTipoNegocio());
            dto.setCorreoPendiente(u.getCorreoPendiente());
        }
        return dto;
    }

    // ===================== Auxiliares =====================

    private Empleado buscarEmpleado(Integer id) {
        return empleadoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado."));
    }

    private Usuario buscarUsuario(Integer id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    //Codigo de verificacion de cuenta (columna entera codverif de Supabase)
    private String asignarCodigoVerificacion(Usuario usuario) {
        String codigo = CodigosSeguridad.generarCodigo();
        usuario.setCodVerif(Integer.valueOf(codigo));
        usuario.setCodVerifExpira(LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA_CODIGO));
        usuario.setCodVerifIntentos(0);
        return codigo;
    }

    //Lanza IllegalArgumentException y suma un intento si el codigo no es valido
    void validarCodigoVerificacion(Usuario usuario, String codigo) {
        if (usuario.getCodVerifIntentos() >= MAX_INTENTOS_CODIGO) {
            throw new IllegalArgumentException("Demasiados intentos. Solicite un nuevo código.");
        }
        boolean vigente = usuario.getCodVerifExpira() != null && LocalDateTime.now().isBefore(usuario.getCodVerifExpira());
        if (!vigente || !CodigosSeguridad.coinciden(String.valueOf(usuario.getCodVerif()), codigo)) {
            usuario.setCodVerifIntentos(usuario.getCodVerifIntentos() + 1);
            usuarioRepository.save(usuario);
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
    }

    private void limpiarCodigoVerificacion(Usuario usuario) {
        usuario.setCodVerif(null);
        usuario.setCodVerifExpira(null);
        usuario.setCodVerifIntentos(0);
    }

    private boolean codigoResetValido(String hashGuardado, LocalDateTime expira, Integer intentos, String codigo) {
        return intentos < MAX_INTENTOS_CODIGO
                && expira != null && LocalDateTime.now().isBefore(expira)
                && CodigosSeguridad.coinciden(hashGuardado, CodigosSeguridad.hash(codigo));
    }

    //Maximo un codigo por minuto por cuenta (evita usar el endpoint para inundar el correo de alguien)
    private boolean puedeReenviar(LocalDateTime expiraCodigoAnterior) {
        return expiraCodigoAnterior == null
                || LocalDateTime.now().isAfter(expiraCodigoAnterior.minusMinutes(MINUTOS_VIGENCIA_CODIGO - 1L));
    }

    private void enviarCodigoRecuperacion(String correo, String nombre, String codigo) {
        correoService.enviarCodigo(correo, nombre, "Recuperación de contraseña",
                "Recibimos una solicitud para restablecer tu contraseña.", codigo, MINUTOS_VIGENCIA_CODIGO);
    }

    private String nombreCompleto(String nombre, String apellido) {
        return nombre + (apellido != null && !apellido.isBlank() ? " " + apellido : "");
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }
}
