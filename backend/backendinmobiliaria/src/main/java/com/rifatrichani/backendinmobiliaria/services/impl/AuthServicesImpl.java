package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;

@Service
public class AuthServicesImpl implements AuthServices {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> loginUsuarioWeb(UsuarioLogeoDTO dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "¡Conexión exitosa desde el Backend!");
        response.put("status", "OK");
        return response;
    }

    @Override
    public Map<String, Object> loginEmpleadoCrm(UsuarioLogeoDTO dto) {
        return new HashMap<>();
    }

    @Override
    public void logoutEmpleadoCrm(Integer idEmpleado) {
    }

    @Override
    public Map<String, Object> registrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado.");
        }

        Map<String, Object> response = new HashMap<>();

        // 1. Crear el nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setApellido(dto.getApellido());
        nuevoUsuario.setTelefono(dto.getTelefono());
        nuevoUsuario.setCorreo(dto.getCorreo());
        nuevoUsuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        // 2. Generar un código aleatorio de 6 dígitos (ej. 045921)
        String codigoSecreto = String.format("%06d", new Random().nextInt(1000000));
        nuevoUsuario.setCuentaActiva(false);
        nuevoUsuario.setTokenConfirmacion(codigoSecreto);

        // 3. Guardar en PostgreSQL
        usuarioRepository.save(nuevoUsuario);

        // 4. Enviar el correo con el código
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(nuevoUsuario.getCorreo());
        mensaje.setSubject("Código de confirmación - Rifat Richani Inmobiliaria");
        mensaje.setText("Hola " + nuevoUsuario.getNombre() + ", bienvenido al sistema.\n\n"
                + "Tu código de confirmación es: " + codigoSecreto + "\n\n"
                + "Ingresa este código de 6 dígitos en la aplicación para activar tu cuenta.");
        
        mailSender.send(mensaje);

        response.put("mensaje", "¡Registro exitoso! Revisa tu correo para obtener el código de activación.");
        response.put("status", "OK");
        return response;
    }

    @Override
    public Map<String, Object> confirmarCuenta(String token) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenConfirmacion(token);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setCuentaActiva(true);
            usuario.setTokenConfirmacion(null);
            usuarioRepository.save(usuario);
            
            response.put("mensaje", "¡Cuenta activada con éxito! Ya puedes iniciar sesión.");
            response.put("status", "OK");
        } else {
            response.put("mensaje", "Error: Código inválido o la cuenta ya fue activada.");
            response.put("status", "ERROR");
        }
        return response;
    }
}