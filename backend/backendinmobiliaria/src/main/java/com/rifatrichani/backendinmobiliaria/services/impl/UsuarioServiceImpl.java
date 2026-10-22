package com.rifatrichani.backendinmobiliaria.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.ActualizarPerfilDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.CambioCorreoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.UsuarioResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CodigosSeguridad;
import com.rifatrichani.backendinmobiliaria.services.CorreoService;
import com.rifatrichani.backendinmobiliaria.services.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private static final String CODIGO_INVALIDO = "El código es inválido o ya expiró.";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, CorreoService correoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPerfil(Integer idUsuario) {
        return mapearAUsuarioResponseDTO(buscarUsuario(idUsuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarPerfil(Integer idUsuario, ActualizarPerfilDTO dto) {
        Usuario usuario = buscarUsuario(idUsuario);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setTelefono(dto.getTelefono().trim());
        if (dto.getTipoNegocio() != null && !dto.getTipoNegocio().isBlank()) {
            usuario.setTipoNegocio(dto.getTipoNegocio().trim());
        }
        return mapearAUsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    /* El correo nuevo no se aplica hasta confirmar el codigo enviado a esa direccion: asi nadie puede asociar
       a su cuenta un correo que no le pertenece. Se pide la contraseña actual por si el token fue robado */
    @Override
    @Transactional
    public void solicitarCambioCorreo(Integer idUsuario, CambioCorreoDTO dto) {
        Usuario usuario = buscarUsuario(idUsuario);
        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getContrasena())) {
            throw new BadCredentialsException("La contraseña actual no es correcta.");
        }
        String correoNuevo = dto.getCorreoNuevo().trim().toLowerCase();
        if (correoNuevo.equalsIgnoreCase(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo nuevo es igual al actual.");
        }
        if (usuarioRepository.existsByCorreoIgnoreCase(correoNuevo)) {
            throw new IllegalArgumentException("El correo ya está en uso por otra cuenta.");
        }
        //Maximo un codigo por minuto
        if (usuario.getCodVerifExpira() != null
                && LocalDateTime.now().isBefore(usuario.getCodVerifExpira().minusMinutes(AuthServicesImpl.MINUTOS_VIGENCIA_CODIGO - 1L))) {
            throw new IllegalArgumentException("Espere un minuto antes de solicitar otro código.");
        }
        String codigo = CodigosSeguridad.generarCodigo();
        usuario.setCorreoPendiente(correoNuevo);
        usuario.setCodVerif(Integer.valueOf(codigo));
        usuario.setCodVerifExpira(LocalDateTime.now().plusMinutes(AuthServicesImpl.MINUTOS_VIGENCIA_CODIGO));
        usuario.setCodVerifIntentos(0);
        usuarioRepository.save(usuario);
        correoService.enviarCodigo(correoNuevo, usuario.getNombre(), "Confirma tu nuevo correo",
                "Solicitaste usar esta dirección en tu cuenta. Ingresa el código en la página para confirmarla.",
                codigo, AuthServicesImpl.MINUTOS_VIGENCIA_CODIGO);
    }

    @Override
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void confirmarCambioCorreo(Integer idUsuario, String codigo) {
        Usuario usuario = buscarUsuario(idUsuario);
        if (usuario.getCorreoPendiente() == null || usuario.getCodVerif() == null) {
            throw new IllegalArgumentException("No hay un cambio de correo pendiente.");
        }
        if (usuario.getCodVerifIntentos() >= AuthServicesImpl.MAX_INTENTOS_CODIGO) {
            throw new IllegalArgumentException("Demasiados intentos. Solicite el cambio de nuevo.");
        }
        boolean vigente = usuario.getCodVerifExpira() != null && LocalDateTime.now().isBefore(usuario.getCodVerifExpira());
        if (!vigente || !CodigosSeguridad.coinciden(String.valueOf(usuario.getCodVerif()), codigo)) {
            usuario.setCodVerifIntentos(usuario.getCodVerifIntentos() + 1);
            usuarioRepository.save(usuario);
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        //Otra cuenta pudo registrar ese correo mientras tanto
        if (usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreoPendiente())) {
            throw new IllegalArgumentException("El correo ya está en uso por otra cuenta.");
        }
        usuario.setCorreo(usuario.getCorreoPendiente());
        usuario.setCorreoPendiente(null);
        usuario.setCodVerif(null);
        usuario.setCodVerifExpira(null);
        usuario.setCodVerifIntentos(0);
        usuarioRepository.save(usuario);
    }

    // CRM: Lista todos los clientes registrados para seleccionarlos al crear un Lead
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodosLosUsuariosCrm() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearAUsuarioResponseDTO)
                .collect(Collectors.toList());
    }

    // CRM: Busca clientes por coincidencia en nombre, apellido o correo
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarUsuariosPorTerminoCrm(String termino) {
        if (termino == null || termino.isBlank()) {
            return obtenerTodosLosUsuariosCrm();
        }
        return usuarioRepository.buscarPorTermino(termino.trim()).stream()
                .map(this::mapearAUsuarioResponseDTO)
                .collect(Collectors.toList());
    }

    private Usuario buscarUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    private UsuarioResponseDTO mapearAUsuarioResponseDTO(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        dto.setTelefono(u.getTelefono());
        dto.setCedula(u.getCedula());
        dto.setTipoNegocio(u.getTipoNegocio());
        dto.setVerificado(u.getVerificado());
        return dto;
    }
}
