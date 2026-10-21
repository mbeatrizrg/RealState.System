package com.rifatrichani.backendinmobiliaria.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/* Freno contra ataques de fuerza bruta en el login: despues de MAX_FALLOS intentos fallidos sobre la misma cuenta
   se bloquean los intentos durante BLOQUEO. Se guarda en memoria (se reinicia al reiniciar el servidor), suficiente
   para una sola instancia del backend; con varias instancias habria que moverlo a la base de datos o a Redis */
@Component
public class LimitadorIntentosLogin {

    private static final int MAX_FALLOS = 5;
    private static final Duration BLOQUEO = Duration.ofMinutes(15);

    private final Map<String, Registro> registros = new ConcurrentHashMap<>();

    private record Registro(int fallos, Instant bloqueadoHasta) {}

    public void verificarNoBloqueado(String clave) {
        Registro r = registros.get(normalizar(clave));
        if (r != null && r.bloqueadoHasta() != null && Instant.now().isBefore(r.bloqueadoHasta())) {
            throw new IntentosExcedidosException("Demasiados intentos fallidos. Intente de nuevo en unos minutos.");
        }
    }

    public void registrarFallo(String clave) {
        registros.compute(normalizar(clave), (k, r) -> {
            int fallos = (r == null || r.bloqueadoHasta() != null) ? 1 : r.fallos() + 1;
            return fallos >= MAX_FALLOS ? new Registro(0, Instant.now().plus(BLOQUEO)) : new Registro(fallos, null);
        });
    }

    public void registrarExito(String clave) {
        registros.remove(normalizar(clave));
    }

    private String normalizar(String clave) {
        return clave == null ? "" : clave.trim().toLowerCase();
    }

    public static class IntentosExcedidosException extends RuntimeException {
        public IntentosExcedidosException(String mensaje) {
            super(mensaje);
        }
    }
}
