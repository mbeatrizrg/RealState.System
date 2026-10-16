package com.rifatrichani.backendinmobiliaria.security;

import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Su trabajo principal es interceptar peticiones no autenticadas y devolver el error 401 (sin autorizacion) :)
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /* Sobrescribe el método commence de la interfaz AuthenticationEntryPoint, se dispara automáticamente cuando un usuario intenta acceder a un endpoint protegido sin 
    estar autenticado (osea sin token JWT o con un token inválido o expirado), corta el flujo antes de llegar al Controlador y envía directamente al 
    cliente una respuesta en formato JSON con el código de estado 401.
    PARÁMETROS:
        HttpServletRequest request -> La peticion HTTP que intento acceder al recurso pero no pudo
        HttpServletResponse response -> El objeto de respuesta HTTP donde se configura el json, el estatus 401 y se escribe un mensaje de error para el cliente
        AuthenticationException authException -> Excepcion interna que datalla el tipo de error de authentificacion
    */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"No autorizado. Debe iniciar sesión con un token válido.\"}");
    }
}