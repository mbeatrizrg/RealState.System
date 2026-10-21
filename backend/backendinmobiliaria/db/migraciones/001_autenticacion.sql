-- =====================================================================
-- 001 - Columnas necesarias para la autenticacion con JWT
-- Ejecutar en Supabase > SQL Editor ANTES de arrancar el backend con esta version.
-- Es idempotente: se puede ejecutar mas de una vez sin errores.
-- =====================================================================

-- ----- Empleados del CRM (vendedores y administradores) -----
-- Hash BCrypt de la contraseña (nunca texto plano). Null = el empleado aun no puede iniciar sesion.
ALTER TABLE "Empleado" ADD COLUMN IF NOT EXISTS contrasena text;
-- Permite desactivar un empleado sin borrarlo (sus inmuebles y ventas se conservan)
ALTER TABLE "Empleado" ADD COLUMN IF NOT EXISTS activo boolean NOT NULL DEFAULT true;
-- El login busca por correo, se evita que dos empleados compartan correo
CREATE UNIQUE INDEX IF NOT EXISTS empleado_correo_unico ON "Empleado" (lower(correo));

-- ----- Usuarios del portal web -----
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS contrasena text;
-- La cuenta solo puede iniciar sesion despues de confirmar el codigo enviado al correo (columna codverif)
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS verificado boolean NOT NULL DEFAULT false;
-- Vencimiento del codigo de verificacion y cantidad de intentos fallidos (evita adivinarlo por fuerza bruta)
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS codverif_expira timestamp;
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS codverif_intentos integer NOT NULL DEFAULT 0;

-- ----- Un usuario no puede tener dos leads del mismo inmueble -----
-- (si ya hay duplicados en la tabla, eliminarlos antes de crear el indice)
CREATE UNIQUE INDEX IF NOT EXISTS lead_usuario_inmueble_unico ON lead (id_usuario, id_inmueble);
