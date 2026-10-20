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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationEntryPoint entryPoint, JwtAuthenticationFilter filter, CorsConfigurationSource corsSource) {
        this.authenticationEntryPoint = entryPoint;
        this.authenticationFilter = filter;
        this.corsConfigurationSource = corsSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource)).csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Permitir ruta interna de errores de Spring para no disfrazar errores 404 como 401
                .requestMatchers("/error").permitAll()
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
                // Documentación Swagger (si se instala)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            );
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}