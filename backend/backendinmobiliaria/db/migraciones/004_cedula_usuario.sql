-- =====================================================================
-- 004 - Cedula de identidad de los clientes del portal
-- Ejecutar en Supabase > SQL Editor despues de 003_recuperacion_y_sesiones.sql.
-- Es idempotente: se puede ejecutar mas de una vez sin errores.
-- =====================================================================

-- Mismo formato que la cedula de "Empleado" (columna ci, numero sin prefijo V/E).
-- Se permite NULL porque las cuentas creadas antes de este cambio no tienen cedula;
-- el backend la exige en todos los registros nuevos.
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS ci integer;

-- Una cedula solo puede pertenecer a una cuenta (los NULL no cuentan como repetidos)
CREATE UNIQUE INDEX IF NOT EXISTS usuario_ci_unica ON "Usuario" (ci);
