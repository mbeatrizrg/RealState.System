-- =====================================================================
-- 003 - Recuperacion de contraseña, cambio de correo y cierre de sesion de usuarios
-- Ejecutar en Supabase > SQL Editor despues de 001_autenticacion.sql.
-- Es idempotente: se puede ejecutar mas de una vez sin errores.
-- =====================================================================

-- ----- "Olvide mi contraseña" (empleados y usuarios) -----
-- Se guarda el HASH SHA-256 del codigo enviado por correo, nunca el codigo en texto plano
ALTER TABLE "Empleado" ADD COLUMN IF NOT EXISTS reset_codigo text;
ALTER TABLE "Empleado" ADD COLUMN IF NOT EXISTS reset_expira timestamp;
ALTER TABLE "Empleado" ADD COLUMN IF NOT EXISTS reset_intentos integer NOT NULL DEFAULT 0;

ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS reset_codigo text;
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS reset_expira timestamp;
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS reset_intentos integer NOT NULL DEFAULT 0;

-- ----- Cambio de correo del usuario -----
-- El correo nuevo queda pendiente hasta confirmar el codigo enviado a esa direccion (columna codverif)
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS correo_pendiente text;

-- ----- Cierre de sesion de usuarios -----
-- Los tokens emitidos antes de esta fecha se rechazan (logout, cambio o recuperacion de contraseña)
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS tokens_validos_desde timestamp;
