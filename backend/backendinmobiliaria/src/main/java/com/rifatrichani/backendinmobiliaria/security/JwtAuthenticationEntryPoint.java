package com.rifatrichani.backendinmobiliaria.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* Respuestas de error de Spring Security con el mismo formato {"mensaje": "..."} que el GlobalExceptionHandler:
   - 401 cuando se pide un endpoint protegido sin token, con un token invalido, expirado o revocado
   - 403 cuando el token es valido pero el rol no tiene permiso (ej. un vendedor entrando a /api/empleado) */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        escribir(response, HttpServletResponse.SC_UNAUTHORIZED, "No autorizado. Debe iniciar sesión con un token válido.");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        escribir(response, HttpServletResponse.SC_FORBIDDEN, "No tiene permisos para realizar esta acción.");
    }

    private void escribir(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\": \"" + mensaje + "\"}");
    }
}
