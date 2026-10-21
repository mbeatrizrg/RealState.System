-- =====================================================================
-- 002 - Bloquear el acceso directo a las tablas desde la API publica de Supabase
--
-- Supabase expone cada tabla del esquema "public" por REST (PostgREST) usando la "anon key".
-- Si RLS esta desactivado, cualquiera con esa clave puede leer o modificar las tablas
-- (incluyendo correos y hashes de contraseñas) sin pasar por el backend.
--
-- Activar RLS sin politicas bloquea ese acceso. El backend NO se ve afectado porque se conecta
-- con el usuario "postgres", que es dueño de las tablas y no esta sujeto a RLS.
--
-- Revisar antes de ejecutar: si el frontend usa supabase-js directamente para alguna tabla,
-- esa tabla necesitara una politica explicita.
-- =====================================================================

ALTER TABLE "Empleado"       ENABLE ROW LEVEL SECURITY;
ALTER TABLE "Usuario"        ENABLE ROW LEVEL SECURITY;
ALTER TABLE "Inmueble"       ENABLE ROW LEVEL SECURITY;
ALTER TABLE "Venta"          ENABLE ROW LEVEL SECURITY;
ALTER TABLE lead             ENABLE ROW LEVEL SECURITY;
ALTER TABLE comunicaciones   ENABLE ROW LEVEL SECURITY;
ALTER TABLE favoritos        ENABLE ROW LEVEL SECURITY;
ALTER TABLE vistos           ENABLE ROW LEVEL SECURITY;
ALTER TABLE galeria          ENABLE ROW LEVEL SECURITY;
ALTER TABLE canal            ENABLE ROW LEVEL SECURITY;
ALTER TABLE ciudad           ENABLE ROW LEVEL SECURITY;
ALTER TABLE tipo_inmueble    ENABLE ROW LEVEL SECURITY;
ALTER TABLE estatus_inmueble ENABLE ROW LEVEL SECURITY;
ALTER TABLE extras           ENABLE ROW LEVEL SECURITY;
