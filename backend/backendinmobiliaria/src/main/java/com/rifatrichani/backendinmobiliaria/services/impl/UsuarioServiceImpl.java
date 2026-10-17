package com.rifatrichani.backendinmobiliaria.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioCorreoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.services.UsuarioService;

import jakarta.transaction.Transactional;
@Service 
public class UsuarioServiceImpl implements UsuarioService{
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder =  passEncoder;
    }
    //Metodo para registrar usuario
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

    //Metodo para actualizar correo
    @Transactional
    @Override 
    public void actualizarCorreo(Long idUsuarioObjetivo, Long idUsuarioAutenticado, UsuarioCorreoRequestDTO dto) {
        if(!idUsuarioObjetivo.equals(idUsuarioAutenticado)){
            throw new SecurityException("Acceso denegado: No tienes permisos para modificar este perfil.");
        }
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El nuevo correo ya está en uso por otra cuenta.");
        }
        Usuario usuario = usuarioRepository.findById(idUsuarioAutenticado)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
        usuario.setCorreo(dto.getCorreo());
        usuarioRepository.save(usuario);
    }
    
}
