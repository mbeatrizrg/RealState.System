package com.rifatrichani.backendinmobiliaria.config;

import static com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.ROL_ADMIN;
import static com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.ROL_USUARIO;
import static com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.ROL_VENDEDOR;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rifatrichani.backendinmobiliaria.security.JwtAuthenticationEntryPoint;
import com.rifatrichani.backendinmobiliaria.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //Activa @PreAuthorize en los controladores (segunda capa de control ademas de las reglas por ruta)
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint manejadorErrores;
    private final JwtAuthenticationFilter authenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint manejadorErrores, JwtAuthenticationFilter filter) {
        this.manejadorErrores = manejadorErrores;
        this.authenticationFilter = filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //El filtro JWT es un @Component; sin esto Spring Boot lo registraria tambien como filtro global del servidor (fuera de Spring Security)
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> desactivarRegistroAutomaticoJwt(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registro = new FilterRegistrationBean<>(filter);
        registro.setEnabled(false);
        return registro;
    }

    /* Reglas por ruta. El orden importa: gana la primera regla que coincide, por eso las rutas privadas que comparten
       prefijo con una publica (ej. /api/inmueble/crm) van antes que la publica */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) //Usa el bean CorsConfigurationSource de CorsConfig
            .csrf(csrf -> csrf.disable()) //API stateless con token en header, no usa cookies de sesion
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(manejadorErrores).accessDeniedHandler(manejadorErrores))
            .authorizeHttpRequests(auth -> auth
                //Ruta interna de errores de Spring, para no disfrazar un 404 o 400 como 401
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 1. Autenticacion y registro del portal (publicos)
                .requestMatchers(HttpMethod.POST, "/api/auth/login/empleado", "/api/auth/login/usuario",
                    "/api/auth/confirmar", "/api/auth/reenviar-codigo", "/api/auth/recuperar", "/api/auth/recuperar/confirmar").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/registro").permitAll()

                // 2. CRM: rutas privadas que comparten prefijo con rutas publicas
                .requestMatchers("/api/inmueble/crm/**").hasAnyAuthority(ROL_VENDEDOR, ROL_ADMIN)
                .requestMatchers("/api/visto/crm/**").hasAnyAuthority(ROL_VENDEDOR, ROL_ADMIN)

                // 3. Portal publico: catalogos para los filtros y consulta de inmuebles
                .requestMatchers(HttpMethod.GET, "/api/ciudad/**", "/api/tipo_inmueble/**", "/api/estatus_inmueble/**", "/api/extras/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/inmueble/buscar", "/api/inmueble/{id}").permitAll()
                //Telemetria del detalle: se acepta anonima pero solo se guarda si hay un usuario con sesion (ver VistoController)
                .requestMatchers(HttpMethod.POST, "/api/visto").permitAll()

                // 4. Usuario del portal con sesion iniciada
                .requestMatchers("/api/favorito/**").hasAuthority(ROL_USUARIO)
                .requestMatchers(HttpMethod.POST, "/api/lead/interes").hasAuthority(ROL_USUARIO)
                .requestMatchers("/api/usuario/perfil/**").hasAuthority(ROL_USUARIO)

                // 5. Exclusivo del administrador
                .requestMatchers("/api/empleado/**").hasAuthority(ROL_ADMIN)

                // 6. CRM (vendedor y administrador)
                .requestMatchers("/api/inmueble/**", "/api/lead/**", "/api/comunicacion/**", "/api/venta/**",
                    "/api/galeria/**", "/api/ranking/**", "/api/canal/**", "/api/usuario/crm/**").hasAnyAuthority(ROL_VENDEDOR, ROL_ADMIN)

                // 7. Lo demas (ej. /api/auth/me, /api/auth/logout) solo requiere un token valido
                .anyRequest().authenticated())
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
