package com.rifatrichani.backendinmobiliaria.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;

class JwtTokenProviderTest {
    private static final String CLAVE = "c2VjcmV0by1kZS1wcnVlYmFzLXNvbG8tcGFyYS10ZXN0cy0xMjM0NTY3ODkwYWJjZGVm";

    @Test
    void noArrancaSinClave() {
        assertThrows(IllegalStateException.class, () -> new JwtTokenProvider("", 1000));
    }

    @Test
    void noArrancaConClaveCorta() {
        assertThrows(IllegalStateException.class, () -> new JwtTokenProvider("Y29ydGE=", 1000));
    }

    @Test
    void generaYValidaToken() {
        JwtTokenProvider provider = new JwtTokenProvider(CLAVE, 60_000);
        var datos = provider.validar(provider.generarToken(8, TipoCuenta.USUARIO, "ROLE_USUARIO")).orElseThrow();
        assertEquals(8, datos.id());
        assertEquals(TipoCuenta.USUARIO, datos.tipo());
    }

    @Test
    void rechazaTokenExpiradoYTextoBasura() {
        JwtTokenProvider provider = new JwtTokenProvider(CLAVE, -1000);
        assertTrue(provider.validar(provider.generarToken(1, TipoCuenta.EMPLEADO, "ROLE_ADMIN")).isEmpty());
        assertTrue(provider.validar("no.es.un.jwt").isEmpty());
    }

    @Test
    void rechazaTokenSinFirma() {
        JwtTokenProvider provider = new JwtTokenProvider(CLAVE, 60_000);
        String sinFirma = io.jsonwebtoken.Jwts.builder().setSubject("1").setIssuer("rifatrichani-api")
                .claim("tipo", "EMPLEADO").compact();
        assertTrue(provider.validar(sinFirma).isEmpty());
    }
}
