package com.rifatrichani.backendinmobiliaria.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/* Freno contra ataques de fuerza bruta en el login: despues de MAX_FALLOS intentos fallidos sobre la misma cuenta
   se bloquean los intentos durante BLOQUEO. Se guarda en memoria (se reinicia al reiniciar el servidor), suficiente
   para una sola instancia del backend; con varias instancias habria que moverlo a la base de datos o a Redis.
   Los fallos caducan despues de VENTANA y el mapa se limpia al pasar de MAX_REGISTROS: probar miles de correos
   inventados no puede llenar la memoria del servidor */
@Component
public class LimitadorIntentosLogin {

    private static final int MAX_FALLOS = 5;
    private static final Duration BLOQUEO = Duration.ofMinutes(15);
    private static final Duration VENTANA = Duration.ofMinutes(15);
    static final int MAX_REGISTROS = 10_000;

    private final Map<String, Registro> registros = new ConcurrentHashMap<>();

    private record Registro(int fallos, Instant bloqueadoHasta, Instant ultimoFallo) {
        boolean vigente(Instant ahora) {
            return bloqueadoHasta != null ? ahora.isBefore(bloqueadoHasta) : ahora.isBefore(ultimoFallo.plus(VENTANA));
        }
    }

    public void verificarNoBloqueado(String clave) {
        Registro r = registros.get(normalizar(clave));
        if (r != null && r.bloqueadoHasta() != null && Instant.now().isBefore(r.bloqueadoHasta())) {
            throw new IntentosExcedidosException("Demasiados intentos fallidos. Intente de nuevo en unos minutos.");
        }
    }

    public void registrarFallo(String clave) {
        Instant ahora = Instant.now();
        if (registros.size() >= MAX_REGISTROS) {
            registros.values().removeIf(r -> !r.vigente(ahora));
        }
        registros.compute(normalizar(clave), (k, r) -> {
            //Un bloqueo cumplido o fallos viejos empiezan la cuenta de nuevo
            int fallos = (r == null || r.bloqueadoHasta() != null || !r.vigente(ahora)) ? 1 : r.fallos() + 1;
            return fallos >= MAX_FALLOS ? new Registro(0, ahora.plus(BLOQUEO), ahora) : new Registro(fallos, null, ahora);
        });
    }

    public void registrarExito(String clave) {
        registros.remove(normalizar(clave));
    }

    int cantidadRegistros() {
        return registros.size();
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
