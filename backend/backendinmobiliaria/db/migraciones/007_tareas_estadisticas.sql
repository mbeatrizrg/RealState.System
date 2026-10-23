-- =====================================================================
-- 007 - Modulo de Tareas y Estadisticas del CRM
-- Ejecutar en Supabase > SQL Editor despues de 006_negocios_inmuebles_actividades.sql.
-- Es idempotente: se puede ejecutar mas de una vez sin errores.
--
--   - actividad: las TAREA ganan tipo de tarea, prioridad, estado e inmueble vinculado; el contacto pasa a ser opcional
--                (ej. "solicitar documentos del inmueble" no siempre tiene un cliente)
--   - lead:      fecha_creacion, para contar las consultas recibidas por periodo en Estadisticas
-- =====================================================================

-- ----- Tareas -----
-- tipo_tarea: LLAMADA, VISITA (visita guiada a un inmueble), ENVIO (catalogo/correo/WhatsApp), REUNION o TRAMITE
ALTER TABLE actividad ADD COLUMN IF NOT EXISTS tipo_tarea varchar(20);
-- baja, media o alta
ALTER TABLE actividad ADD COLUMN IF NOT EXISTS prioridad varchar(10);
-- pendiente, en_proceso o completada. "Vencida" no se guarda: es una tarea sin completar con la fecha pasada
ALTER TABLE actividad ADD COLUMN IF NOT EXISTS estado_tarea varchar(20);
ALTER TABLE actividad ADD COLUMN IF NOT EXISTS id_inmueble integer REFERENCES "Inmueble" (id) ON DELETE SET NULL;
ALTER TABLE actividad ALTER COLUMN id_usuario DROP NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'actividad_tipo_tarea_chk') THEN
        ALTER TABLE actividad ADD CONSTRAINT actividad_tipo_tarea_chk
            CHECK (tipo_tarea IS NULL OR tipo_tarea IN ('LLAMADA', 'VISITA', 'ENVIO', 'REUNION', 'TRAMITE'));
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'actividad_prioridad_chk') THEN
        ALTER TABLE actividad ADD CONSTRAINT actividad_prioridad_chk
            CHECK (prioridad IS NULL OR prioridad IN ('baja', 'media', 'alta'));
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'actividad_estado_tarea_chk') THEN
        ALTER TABLE actividad ADD CONSTRAINT actividad_estado_tarea_chk
            CHECK (estado_tarea IS NULL OR estado_tarea IN ('pendiente', 'en_proceso', 'completada'));
    END IF;
    -- Solo las actividades que no son tareas (o las tareas con inmueble) pueden quedar sin contacto
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'actividad_contacto_chk') THEN
        ALTER TABLE actividad ADD CONSTRAINT actividad_contacto_chk
            CHECK (id_usuario IS NOT NULL OR tipo = 'TAREA');
    END IF;
END $$;

-- Las tareas creadas antes de esta migracion toman los valores por defecto
UPDATE actividad SET prioridad = 'media' WHERE tipo = 'TAREA' AND prioridad IS NULL;
UPDATE actividad SET estado_tarea = CASE WHEN completada THEN 'completada' ELSE 'pendiente' END
WHERE tipo = 'TAREA' AND estado_tarea IS NULL;

CREATE INDEX IF NOT EXISTS actividad_tareas_idx ON actividad (tipo, id_empleado, fecha_programada);
CREATE INDEX IF NOT EXISTS actividad_inmueble_idx ON actividad (id_inmueble);

-- ----- Fecha de las consultas (leads) -----
-- Los leads que ya existen toman la fecha en que se ejecuta esta migracion (no hay forma de saber la real)
ALTER TABLE lead ADD COLUMN IF NOT EXISTS fecha_creacion timestamp NOT NULL DEFAULT now();
CREATE INDEX IF NOT EXISTS lead_fecha_creacion_idx ON lead (fecha_creacion);
