package com.rifatrichani.backendinmobiliaria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity //Activa @PreAuthorize en los controladores, la configuracion JWT se agregara aqui
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) //API stateless, no usa cookies de sesion
            .cors(Customizer.withDefaults()) //Usa el bean de CorsConfig
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                //Portal web publico: catalogos para los filtros y consulta de inmuebles
                .requestMatchers(HttpMethod.GET,
                    "/api/ciudad", "/api/tipo_inmueble", "/api/estatus_inmueble", "/api/extras", "/api/tipo_cedula",
                    "/api/inmueble/buscar", "/api/inmueble/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuario/registro", "/api/visto").permitAll()
                .requestMatchers("/error").permitAll()
                //Todo lo demas (CRM) requiere autenticacion
                .anyRequest().authenticated())
            //TEMPORAL: autenticacion basica hasta implementar el filtro JWT
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
