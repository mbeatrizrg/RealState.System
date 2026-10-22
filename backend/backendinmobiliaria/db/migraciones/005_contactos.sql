-- =====================================================================
-- 005 - Modulo de Contactos del CRM
-- Ejecutar en Supabase > SQL Editor despues de 004_cedula_usuario.sql.
-- Es idempotente: se puede ejecutar mas de una vez sin errores.
--
-- Un contacto es un "Usuario" (persona con la que la empresa tiene o puede tener relacion comercial).
-- Se agregan los datos que pide la tabla de contactos y que hoy no existen en Supabase:
--   - fecha_creacion:   cuando se registro el contacto
--   - ultima_actividad: ultimo lead, cambio de estatus, comunicacion, vista o favorito del contacto
--   - id_propietario:   vendedor dueño del contacto (NULL = contacto sin asignar)
--   - estatus_lead:     catalogo de las etapas del embudo (lead.id_estatus no tenia catalogo)
-- =====================================================================

-- ----- Fecha de creacion -----
-- Las filas que ya existen toman la fecha en que se ejecuta esta migracion (no hay forma de saber la real)
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS fecha_creacion timestamp NOT NULL DEFAULT now();
CREATE INDEX IF NOT EXISTS usuario_fecha_creacion_idx ON "Usuario" (fecha_creacion);

-- ----- Ultima actividad (la actualiza el backend) -----
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS ultima_actividad timestamp;
CREATE INDEX IF NOT EXISTS usuario_ultima_actividad_idx ON "Usuario" (ultima_actividad);

-- ----- Propietario del contacto (vendedor) -----
-- Si se elimina el empleado el contacto queda sin asignar en lugar de borrarse
ALTER TABLE "Usuario" ADD COLUMN IF NOT EXISTS id_propietario integer;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'usuario_propietario_fk') THEN
        ALTER TABLE "Usuario" ADD CONSTRAINT usuario_propietario_fk
            FOREIGN KEY (id_propietario) REFERENCES "Empleado" (id) ON DELETE SET NULL;
    END IF;
END $$;
CREATE INDEX IF NOT EXISTS usuario_propietario_idx ON "Usuario" (id_propietario);

-- ----- Catalogo de estatus del lead -----
-- El id 1 es el estatus con el que entra un lead nuevo (LeadServiceImpl.ESTATUS_INICIAL)
CREATE TABLE IF NOT EXISTS estatus_lead (
    id     integer PRIMARY KEY,
    nombre varchar(50) NOT NULL UNIQUE,
    orden  integer NOT NULL
);
INSERT INTO estatus_lead (id, nombre, orden) VALUES
    (1, 'Nuevo', 1),
    (2, 'Contactado', 2),
    (3, 'Calificado', 3),
    (4, 'Visita agendada', 4),
    (5, 'En negociación', 5),
    (6, 'Ganado', 6),
    (7, 'Perdido', 7)
ON CONFLICT (id) DO NOTHING;
-- Igual que las demas tablas (ver 002): sin acceso directo desde la API publica de Supabase
ALTER TABLE estatus_lead ENABLE ROW LEVEL SECURITY;

-- La llave foranea solo se crea si todos los leads tienen un estatus del catalogo.
-- Si aparece el aviso, corregir esos leads (o agregar el estatus al catalogo) y volver a ejecutar.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'lead_estatus_fk') THEN
        IF EXISTS (SELECT 1 FROM lead l LEFT JOIN estatus_lead e ON e.id = l.id_estatus WHERE e.id IS NULL) THEN
            RAISE NOTICE 'Hay leads con un id_estatus que no existe en estatus_lead: no se creo lead_estatus_fk';
        ELSE
            ALTER TABLE lead ADD CONSTRAINT lead_estatus_fk FOREIGN KEY (id_estatus) REFERENCES estatus_lead (id);
        END IF;
    END IF;
END $$;

-- ----- Datos iniciales a partir de lo que ya existe -----
-- Propietario: el vendedor encargado del inmueble del lead mas reciente del contacto
UPDATE "Usuario" u
SET id_propietario = i.id_vendedor
FROM lead l
JOIN "Inmueble" i ON i.id = l.id_inmueble
WHERE u.id_propietario IS NULL
  AND l.id_usuario = u.id
  AND l.id = (SELECT max(l2.id) FROM lead l2 WHERE l2.id_usuario = u.id);

-- Ultima actividad: la comunicacion o vista mas reciente del contacto
UPDATE "Usuario" u
SET ultima_actividad = act.fecha
FROM (
    SELECT id_usuario, max(fecha) AS fecha FROM (
        SELECT l.id_usuario, c.fecha_comunicacion AS fecha
        FROM comunicaciones c JOIN lead l ON l.id = c.id_lead
        UNION ALL
        SELECT v.id_usuario, v.fecha_visto FROM vistos v
    ) eventos
    GROUP BY id_usuario
) act
WHERE act.id_usuario = u.id AND u.ultima_actividad IS NULL;
