package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.response.UsuarioResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.services.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPerfil(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(this::mapearAUsuarioResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
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

    private UsuarioResponseDTO mapearAUsuarioResponseDTO(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        dto.setTelefono(u.getTelefono());
        dto.setTipoNegocio(u.getTipoNegocio());
        dto.setVerificado(u.getVerificado());
        return dto;
    }
}
