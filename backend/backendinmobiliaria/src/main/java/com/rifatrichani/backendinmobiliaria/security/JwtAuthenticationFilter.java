package com.rifatrichani.backendinmobiliaria.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    //Inyeccion de dependencias para el proveedor de Jwt, para evitar utilizar @Autowired xd
    private final JwtTokenProvider jwtTokenProvider;
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //Sobreescribe el metodo doFilterInternal de la clase abstracta OncerRequestFilter
    /*Recibe el token de la request, valida si existe y si es valido,obtiene la id y el rol, por medio del proveedor jwt obtiene las autoridades del empleado o
    usuario por medio de la clase de seguridad de Spring Boot  "GrantedAuthority", crear el objeto UsernamePasswordAuthenticationToken de sesión/autenticación ya 
    verificado e inyectarlo en el SecurityContextHolder, pasando como parametros su id, credenciales (nulas) y sus autoridades, esto se puede hacer ya que se encuentra validado
    por el JwtTokenProvider :)*/
    /*PARAMETROS: 
        HttpServletRequest request -> es una interfaz(servlet = intermediario entre el servidor y el cliente, que maneja peticiones http y las procesa y envia respuesta) ,
        que es la entrada de datos del usuario por asi decirlo, este contiene la informacion del cliente, para validar si su jwt es válido
        HttpServelResponse response -> es una interfaz de response, es la salida de datos que devuelve ante la peticion Http del cliente, le permite al servlet configurar 
        y construir el resultado final
        FilterChain filterChain -> es la secuencia de filtros proporcionados por un contenedor servlet, que pasa las peticiones HTTP por medio de una serie de filtros,
        antes de llegar a un recurso final en este caso el controlador
    */ 
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = obtenerTokenDeSolicitud(request);
        if (StringUtils.hasText(token) && jwtTokenProvider.validarToken(token)) {
            String id = jwtTokenProvider.obtenerIdDelJWT(token);
            String rol = jwtTokenProvider.obtenerRolDelJWT(token);
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(rol));
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(id, null, authorities
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
    //Modularizado, obtiene el JWT de la request de usuario, por medio del header, recortando los 7 primeros caracteres
    private String obtenerTokenDeSolicitud(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}