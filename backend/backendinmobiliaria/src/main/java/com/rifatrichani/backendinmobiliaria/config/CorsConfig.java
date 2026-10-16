package com.rifatrichani.backendinmobiliaria.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//Clase de configuracion que define las reglas CORS(Cross-Origin Resource Sharing)
@Configuration
public class CorsConfig {
    // En application.properties hay que definir, pero mas adelante, osea mañana: app.cors.allowed-origins= http://localhost:5173, https://tu-inmobiliaria.com 
    // Se inyecta el valor de allowedOrigins por medio de @Value desde application.properties, con un valor por defecto de "*", osea que permite de cualquier origen por defecto 
    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;
    /* Metodo implementado como @Bean que es un objeto cuyo ciclo de vida lo maneja el mismo spring, que define la configuracion del CORS, instanciando un objeto del tipo de la clase Configuration con constructor CorsConfiguration
    define los patrones de origen permitidos transforando el Array con los origenes permitidos en una List, define los metodos HTTP permitidos, los headers de cada peticion
    y permite el uso de credenciales y el UrlBasedCorsConfigurationSource y el patrón "/**", aplica toda esta configuracion a todos los endpoints
    */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.trim().split("\\s*,\\s*")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "idUsuarioAuth"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}