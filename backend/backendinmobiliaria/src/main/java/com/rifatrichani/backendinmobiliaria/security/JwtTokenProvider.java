package com.rifatrichani.backendinmobiliaria.security;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;

//Genera y valida los JSON Web Tokens firmados con HMAC-SHA256
@Component
public class JwtTokenProvider {

    private static final String EMISOR = "rifatrichani-api";
    private static final String CLAIM_TIPO = "tipo";

    private final Key clave;
    private final JwtParser parser;
    private final long duracionMs;

    /* La clave se lee de JWT_SECRET (ver application.properties). Si falta o es menor a 256 bits la aplicacion no arranca:
       es preferible fallar al inicio que firmar tokens con una clave debil o conocida */
    public JwtTokenProvider(@Value("${app.jwt.secret:}") String secretoBase64,
                            @Value("${app.jwt.expiration-milliseconds:28800000}") long duracionMs) {
        if (secretoBase64 == null || secretoBase64.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno JWT_SECRET (clave Base64 de al menos 32 bytes).");
        }
        byte[] bytesClave;
        try {
            bytesClave = Decoders.BASE64.decode(secretoBase64.trim());
        } catch (RuntimeException e) {
            throw new IllegalStateException("JWT_SECRET debe estar codificada en Base64.", e);
        }
        if (bytesClave.length < 32) {
            throw new IllegalStateException("JWT_SECRET es demasiado corta: se requieren al menos 32 bytes (256 bits).");
        }
        this.clave = Keys.hmacShaKeyFor(bytesClave);
        this.parser = Jwts.parserBuilder().setSigningKey(clave).requireIssuer(EMISOR).build();
        this.duracionMs = duracionMs;
    }

    //El subject es el id de la cuenta; el rol y el tipo se usan solo como referencia, el filtro los vuelve a leer de la base de datos
    public String generarToken(Integer idCuenta, TipoCuenta tipo, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .setSubject(idCuenta.toString())
                .setIssuer(EMISOR)
                .claim(CLAIM_TIPO, tipo.name())
                .claim("rol", rol)
                .setIssuedAt(ahora)
                .setExpiration(new Date(ahora.getTime() + duracionMs))
                .signWith(clave, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getDuracionMs() {
        return duracionMs;
    }

    //Valida firma, emisor y expiracion en un solo paso. Devuelve vacio si el token no es valido
    public Optional<DatosToken> validar(String token) {
        try {
            Claims claims = parser.parseClaimsJws(token).getBody();
            Integer id = Integer.valueOf(claims.getSubject());
            TipoCuenta tipo = TipoCuenta.valueOf(claims.get(CLAIM_TIPO, String.class));
            return Optional.of(new DatosToken(id, tipo, claims.getIssuedAt()));
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public record DatosToken(Integer id, TipoCuenta tipo, Date emitidoEn) {}
}
