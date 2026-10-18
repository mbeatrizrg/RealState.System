package com.rifatrichani.backendinmobiliaria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import com.rifatrichani.backendinmobiliaria.security.JwtAuthenticationEntryPoint;
import com.rifatrichani.backendinmobiliaria.security.JwtAuthenticationFilter;

/* Se define como una clase @Configuration para registrar Beans en el contenedor de spring, @EnableWebSecurity habilita la seguridad web de Spring Security y 
permite personalizar el SecurityFilterChain, EnableMethodSecurity se encarga de habilitar la seguridad a nivel de métodos en los controladores mediante
 anotaciones como @PreAuthorize*/ 
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    /* Inyección de dependencias por constructor de los componentes de seguridad como el punto de entrada para errores 401 (EntryPoint), 
    el filtro validador de JWT y la configuración de CORS. */    
    public SecurityConfig(JwtAuthenticationEntryPoint entryPoint, JwtAuthenticationFilter filter, CorsConfigurationSource corsSource) {
        this.authenticationEntryPoint = entryPoint;
        this.authenticationFilter = filter;
        this.corsConfigurationSource = corsSource;
    }

    // Su objetivo principal es aplicar un algoritmo de Hash irreversible (se llama BCrypt) para guardar y comparar contraseñas de forma segura :)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* Cadena de filtros de seguridad donde se configura el comportamiento HTTP, primero se aplica las reglas de orígenes permitidos definidas en CorsConfig, luego
        se desactiva la protección Cross-Site Request Forgery porque aja no usamos sesiones basadas en cookies, sino tokens JWT enviados manualmente en el header 
        Authorization (arquitectura STATELESS inmune a CSRF) de la peticion, se asigna el JwtAuthenticationEntryPoint para responder con HTTP 401 cuando falle la 
        autenticación, el sesiionManagement Indica a Spring que no cree ni guarde sesiones HttpSession en memoria; cada petición se valida por su propio JWT
        (por mayor seguridad), luego authorizeHttpRequests define qué endpoints son públicos y cuáles necesitan roles o estar logueado, y por ultimo esta
        el addFilterBefore inserta el JwtAuthenticationFilter antes del filtro tradicional UsernamePasswordAuthenticationFilter para interceptar y validar el token 
        en cada petición y listo. :)
    */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource)).csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Endpoints Públicos de Autenticación y Registro Web
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/registro").permitAll()
                // 2. Catálogos Públicos y Consulta de Inmuebles (Sitio Web Hostinger)
                .requestMatchers(HttpMethod.GET, "/api/ciudad/**", "/api/tipo_inmueble/**", "/api/extras/**", "/api/estatus_inmueble/**", "/api/tipo_cedula/**", "/api/canal/**", "/api/estatus_venta/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/inmueble/**").permitAll()
                // 3. Captura de Clics/Vistos desde la Web (Público, con o sin usuario registrado)
                .requestMatchers(HttpMethod.POST, "/api/visto/**").permitAll()
                // 4. Endpoints exclusivos del Administrador
                .requestMatchers("/api/empleado/**").hasAuthority("ROLE_ADMIN")
                // 5. Endpoints de CRM (Vendedor y Administrador)
                .requestMatchers("/api/lead/**", "/api/comunicacion/**", "/api/venta/**", "/api/galeria/**","/api/ranking/**").hasAnyAuthority("ROLE_VENDEDOR", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/visto/crm/**").hasAnyAuthority("ROLE_VENDEDOR", "ROLE_ADMIN")
                // Cualquier otra petición (como favoritos, editar correo o mutar inmuebles) requiere estar autenticado
                .anyRequest().authenticated()
            );
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}