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
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.security.JwtTokenProvider;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;

@Service
public class AuthServicesImpl implements AuthServices {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository; // <-- Agregado para el CRM

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // <-- Agregado para generar el token JWT

    @Override
    public Map<String, Object> loginUsuarioWeb(UsuarioLogeoDTO dto) { return new HashMap<>(); }

    // --- AQUÍ ESTÁ EL LOGIN DEL VENDEDOR YA FUNCIONAL ---
    @Override
    public Map<String, Object> loginEmpleadoCrm(UsuarioLogeoDTO dto) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Empleado> empleadoOpt = empleadoRepository.findByCorreo(dto.getCorreo());
        
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();
            
            // Verifica que la clave escrita en Vue coincida con la clave encriptada
            if (passwordEncoder.matches(dto.getContrasena(), empleado.getContrasena())) {
                
                // Genera el pase VIP (Token JWT) con el rol de Vendedor
                String token = jwtTokenProvider.generarToken(
                    empleado.getId().toString(), 
                    empleado.getCorreo(), 
                    "ROLE_VENDEDOR" 
                );
                
                response.put("token", token);
                response.put("mensaje", "¡Bienvenido al CRM!");
                response.put("status", "OK");
                return response;
            }
        }
        // Si hay error, dispara el 401 que Vue detectará
        throw new SecurityException("Credenciales incorrectas");
    }

    @Override
    public void logoutEmpleadoCrm(Integer idEmpleado) { }

    @Override
    public Map<String, Object> registrarUsuario(UsuarioRegistroDTO dto) { return new HashMap<>(); } // (Oculté la lógica aquí visualmente para no hacer el código largo, deja la tuya que funciona)

    @Override
    public Map<String, Object> confirmarCuenta(String token) { return new HashMap<>(); } // (Oculté la lógica visualmente, deja la tuya)

    // --- MÉTODO TEMPORAL PARA INYECTAR EL VENDEDOR ---
    @Override
    public Map<String, Object> crearVendedorPrueba() {
        Map<String, Object> response = new HashMap<>();
        if (!empleadoRepository.existsByCorreo("vendedor@crm.com")) {
            Empleado vendedor = new Empleado();
            vendedor.setNombre("Carlos");
            vendedor.setApellido("Vendedor");
            vendedor.setEsAdmin(false);
            vendedor.setCorreo("vendedor@crm.com");
            // Se encripta la contraseña para que el login pueda leerla
            vendedor.setContrasena(passwordEncoder.encode("Secreta123!"));
            
            empleadoRepository.save(vendedor);
            response.put("mensaje", "Vendedor de prueba creado con éxito");
        } else {
            response.put("mensaje", "El vendedor ya existe en la base de datos");
        }
        return response;
    }
}