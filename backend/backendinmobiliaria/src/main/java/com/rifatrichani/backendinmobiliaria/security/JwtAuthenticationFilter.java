package com.rifatrichani.backendinmobiliaria.security;

import java.io.IOException;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;
import com.rifatrichani.backendinmobiliaria.security.JwtTokenProvider.DatosToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* Lee el header "Authorization: Bearer <token>", valida el token y carga la cuenta en el SecurityContext.
   Ademas de la firma se revisa la base de datos en cada peticion, asi:
   - un empleado desactivado o borrado pierde el acceso de inmediato
   - el rol (ADMIN/VENDEDOR) es siempre el actual, no el que tenia al iniciar sesion
   - los tokens emitidos antes del ultimo logout del empleado dejan de servir
   Si el token no es valido la peticion sigue como anonima y Spring Security decide si responde 401 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, EmpleadoRepository er, UsuarioRepository ur) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.empleadoRepository = er;
        this.usuarioRepository = ur;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = obtenerTokenDeSolicitud(request);
        if (token != null) {
            jwtTokenProvider.validar(token)
                    .flatMap(this::cargarCuenta)
                    .ifPresent(cuenta -> {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                cuenta, null, List.of(new SimpleGrantedAuthority(cuenta.rol())));
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
        }
        filterChain.doFilter(request, response);
    }

    private Optional<CuentaAutenticada> cargarCuenta(DatosToken datos) {
        if (datos.tipo() == TipoCuenta.EMPLEADO) {
            return empleadoRepository.findById(datos.id())
                    .filter(e -> Boolean.TRUE.equals(e.getActivo()) && e.getContrasena() != null)
                    .filter(e -> !emitidoAntesDelLogout(datos.emitidoEn(), e))
                    .map(e -> new CuentaAutenticada(e.getId(), TipoCuenta.EMPLEADO,
                            Boolean.TRUE.equals(e.getEsAdmin()) ? CuentaAutenticada.ROL_ADMIN : CuentaAutenticada.ROL_VENDEDOR));
        }
        return usuarioRepository.findById(datos.id())
                .filter(u -> Boolean.TRUE.equals(u.getVerificado()) && u.getContrasena() != null)
                .map(Usuario::getId)
                .map(id -> new CuentaAutenticada(id, TipoCuenta.USUARIO, CuentaAutenticada.ROL_USUARIO));
    }

    //El "iat" del token tiene precision de segundos, por eso se compara contra la salida truncada a segundos
    private boolean emitidoAntesDelLogout(Date emitidoEn, Empleado empleado) {
        if (empleado.getSalidaSistema() == null || emitidoEn == null) {
            return false;
        }
        Date salida = Date.from(empleado.getSalidaSistema().truncatedTo(ChronoUnit.SECONDS)
                .atZone(ZoneId.systemDefault()).toInstant());
        return emitidoEn.before(salida);
    }

    private String obtenerTokenDeSolicitud(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }
}
