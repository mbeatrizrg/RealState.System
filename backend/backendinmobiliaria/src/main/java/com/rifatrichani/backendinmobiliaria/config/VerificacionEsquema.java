package com.rifatrichani.backendinmobiliaria.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/* Con ddl-auto=none Hibernate no revisa las tablas al arrancar: si falta una migracion de db/migraciones el backend
   arranca igual y las pantallas fallan despues con un 500 poco claro. Aqui se prueba una consulta minima por migracion
   y se avisa en el log cual hay que ejecutar en Supabase > SQL Editor. No modifica nada */
@Component
public class VerificacionEsquema {
    private static final Logger log = LoggerFactory.getLogger(VerificacionEsquema.class);

    //Consulta de prueba -> migracion que la crea
    private static final Map<String, String> PRUEBAS = new LinkedHashMap<>();
    static {
        PRUEBAS.put("SELECT id_propietario, ultima_actividad FROM \"Usuario\" WHERE 1 = 0", "005_contactos.sql");
        PRUEBAS.put("SELECT estado, fecha_captacion, dueno_nombre, habitaciones FROM \"Inmueble\" WHERE 1 = 0", "006_negocios_inmuebles_actividades.sql");
        PRUEBAS.put("SELECT id FROM etapa_negocio WHERE 1 = 0", "006_negocios_inmuebles_actividades.sql");
        PRUEBAS.put("SELECT id FROM negocio WHERE 1 = 0", "006_negocios_inmuebles_actividades.sql");
        PRUEBAS.put("SELECT id FROM actividad WHERE 1 = 0", "006_negocios_inmuebles_actividades.sql");
        PRUEBAS.put("SELECT id FROM archivo_crm WHERE 1 = 0", "006_negocios_inmuebles_actividades.sql");
        PRUEBAS.put("SELECT tipo_tarea, prioridad, estado_tarea, id_inmueble FROM actividad WHERE 1 = 0", "007_tareas_estadisticas.sql");
        PRUEBAS.put("SELECT fecha_creacion FROM lead WHERE 1 = 0", "007_tareas_estadisticas.sql");
        PRUEBAS.put("SELECT estacionamientos, piso FROM \"Inmueble\" WHERE 1 = 0", "008_caracteristicas_inmueble.sql");
    }

    private final JdbcTemplate jdbc;

    public VerificacionEsquema(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void verificar() {
        PRUEBAS.forEach((consulta, migracion) -> {
            try {
                jdbc.execute(consulta);
            } catch (DataAccessException e) {
                log.error("""

                        ====================================================================================
                         FALTA APLICAR UNA MIGRACION EN LA BASE DE DATOS: db/migraciones/{}
                         Ejecutela en Supabase > SQL Editor y reinicie el backend. Mientras tanto los modulos
                         de Contactos, Negocios, Inmuebles, Tareas y Estadisticas del CRM responden con error.
                         Detalle: {}
                        ====================================================================================""",
                        migracion, e.getMostSpecificCause().getMessage());
            }
        });
    }
}
