package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioCorreoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.UsuarioResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.services.UsuarioService;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registro de usuario desde el sitio web con contraseña hasheada en BCrypt
    @Transactional
    @Override
    public void registrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuarioRepository.save(usuario);
    }

    // Actualización de correo validando que el usuario autenticado en el JWT sea el dueño de la cuenta
    @Transactional
    @Override
    public void actualizarCorreo(Long idUsuarioObjetivo, Long idUsuarioAutenticado, UsuarioCorreoRequestDTO dto) {
        if (!idUsuarioObjetivo.equals(idUsuarioAutenticado)) {
            throw new SecurityException("Acceso denegado: No tienes permisos para modificar otro perfil.");
        }
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El nuevo correo ya está en uso por otra cuenta.");
        }
        Usuario usuario = usuarioRepository.findById(idUsuarioAutenticado).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        usuario.setCorreo(dto.getCorreo());
        usuarioRepository.save(usuario);
    }

    // CRM: Lista todos los clientes registrados para seleccionarlos al crear un Lead
    @Override
    public List<UsuarioResponseDTO> obtenerTodosLosUsuariosCrm() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearAUsuarioResponseDTO)
                .collect(Collectors.toList());
    }

    // CRM: Busca clientes por coincidencia en nombre o correo usando el @Query de UsuarioRepository
    @Override
    public List<UsuarioResponseDTO> buscarUsuariosPorTerminoCrm(String termino) {
        if (termino == null || termino.isBlank()) {
            return obtenerTodosLosUsuariosCrm();
        }
        return usuarioRepository.findByCorreoOrName(termino.trim()).stream()
                .map(this::mapearAUsuarioResponseDTO)
                .collect(Collectors.toList());
    }

    private UsuarioResponseDTO mapearAUsuarioResponseDTO(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setName(u.getNombre()); // En UsuarioResponseDTO el atributo es 'name'
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        dto.setTelefono(u.getTelefono());
        return dto;
    }
}