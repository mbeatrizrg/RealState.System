-- =====================================================================
-- 008 - Caracteristicas del inmueble que se muestran en el portal y en el CRM
-- Ejecutar en Supabase > SQL Editor despues de 007_tareas_estadisticas.sql.
-- Es idempotente: se puede ejecutar mas de una vez sin errores.
--
-- Habitaciones, baños y metros cuadrados ya existen (006). Se agregan:
--   - estacionamientos: puestos de estacionamiento
--   - piso:             piso en el que esta (apartamentos, oficinas, locales en edificio)
-- =====================================================================

ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS estacionamientos smallint;
ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS piso smallint;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'inmueble_caracteristicas_chk') THEN
        ALTER TABLE "Inmueble" ADD CONSTRAINT inmueble_caracteristicas_chk CHECK (
            (habitaciones IS NULL OR habitaciones >= 0)
            AND (banos IS NULL OR banos >= 0)
            AND (metros_cuadrados IS NULL OR metros_cuadrados >= 0)
            AND (estacionamientos IS NULL OR estacionamientos >= 0)
        );
    END IF;
END $$;
