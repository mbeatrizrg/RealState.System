package com.rifatrichani.backendinmobiliaria.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;

/* Reemplaza al antiguo endpoint publico /api/auth/crear-vendedor-prueba (que permitia a cualquiera crear una cuenta con
   contraseña conocida). Al arrancar, si ADMIN_INICIAL_CORREO y ADMIN_INICIAL_CONTRASENA estan definidas, asigna esa
   contraseña al empleado ADMINISTRADOR que ya existe en Supabase con ese correo, solo si todavia no tiene contraseña.
   No crea empleados nuevos ni sobrescribe contraseñas. Despues del primer arranque se deben borrar esas variables */
@Component
public class AdminInicialConfig implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminInicialConfig.class);

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final String correo;
    private final String contrasena;

    public AdminInicialConfig(EmpleadoRepository er, PasswordEncoder pe,
                              @Value("${app.auth.admin-inicial.correo:}") String correo,
                              @Value("${app.auth.admin-inicial.contrasena:}") String contrasena) {
        this.empleadoRepository = er;
        this.passwordEncoder = pe;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (correo.isBlank() || contrasena.isBlank()) {
            return;
        }
        if (contrasena.length() < 12) {
            log.warn("ADMIN_INICIAL_CONTRASENA debe tener al menos 12 caracteres, no se aplico.");
            return;
        }
        empleadoRepository.findByCorreoIgnoreCase(correo.trim()).ifPresentOrElse(empleado -> {
            if (!Boolean.TRUE.equals(empleado.getEsAdmin())) {
                log.warn("El empleado {} no es administrador, no se le asigno la contraseña inicial.", correo);
            } else if (empleado.getContrasena() != null) {
                log.info("El administrador {} ya tiene contraseña, se ignora ADMIN_INICIAL_CONTRASENA. Puede borrar esa variable.", correo);
            } else {
                empleado.setContrasena(passwordEncoder.encode(contrasena));
                empleadoRepository.save(empleado);
                log.info("Contraseña inicial asignada al administrador {}. Borre ADMIN_INICIAL_CONTRASENA del entorno.", correo);
            }
        }, () -> log.warn("No existe un empleado con el correo {} en la tabla Empleado.", correo));
    }
}
