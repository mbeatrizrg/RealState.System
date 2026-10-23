-- =====================================================================
-- 006 - Modulos de Negocios e Inmuebles del CRM y registro de actividad de los contactos
-- Ejecutar en Supabase > SQL Editor despues de 005_contactos.sql.
-- Es idempotente: se puede ejecutar mas de una vez sin errores.
--
--   - "Inmueble": estado comercial, fecha de captacion, datos del dueño y caracteristicas fisicas
--   - etapa_negocio: catalogo de las etapas del negocio (pipeline)
--   - negocio:       oportunidad de venta o alquiler que une un inmueble con un contacto
--   - actividad:     notas, correos, llamadas, tareas y reuniones de un contacto (y opcionalmente de un negocio)
--   - archivo_crm:   adjuntos de las actividades y documentos legales de los inmuebles (privados, no se sirven en /uploads)
-- =====================================================================

-- ----- Inmueble: estado, captacion, dueño y caracteristicas -----
ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS estado varchar(20) NOT NULL DEFAULT 'disponible';
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'inmueble_estado_chk') THEN
        ALTER TABLE "Inmueble" ADD CONSTRAINT inmueble_estado_chk
            CHECK (estado IN ('disponible', 'reservado', 'vendido', 'alquilado'));
    END IF;
END $$;
-- Los inmuebles ya marcados como vendidos toman el estado que corresponde a su operacion
UPDATE "Inmueble" SET estado = CASE WHEN "isAlquiler" THEN 'alquilado' ELSE 'vendido' END
WHERE "isVendido" = true AND estado = 'disponible';
CREATE INDEX IF NOT EXISTS inmueble_estado_idx ON "Inmueble" (estado);

-- Las filas que ya existen toman la fecha en que se ejecuta esta migracion
ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS fecha_captacion timestamp NOT NULL DEFAULT now();
CREATE INDEX IF NOT EXISTS inmueble_fecha_captacion_idx ON "Inmueble" (fecha_captacion);

-- Dueño del inmueble (no siempre tiene cuenta en el portal, por eso son datos sueltos y no un "Usuario")
ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS dueno_nombre varchar(150);
ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS dueno_telefono varchar(30);
ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS dueno_correo varchar(255);

ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS habitaciones smallint;
ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS banos smallint;
ALTER TABLE "Inmueble" ADD COLUMN IF NOT EXISTS metros_cuadrados numeric(10, 2);

-- ----- Catalogo de etapas del negocio -----
-- abierto = false para las etapas de cierre (no cuentan como negocios en marcha)
CREATE TABLE IF NOT EXISTS etapa_negocio (
    id      integer PRIMARY KEY,
    nombre  varchar(50) NOT NULL UNIQUE,
    orden   integer NOT NULL,
    abierto boolean NOT NULL DEFAULT true
);
INSERT INTO etapa_negocio (id, nombre, orden, abierto) VALUES
    (1, 'Prospecto', 1, true),
    (2, 'Cita/visita programada', 2, true),
    (3, 'Visita realizada', 3, true),
    (4, 'Oferta/negociación', 4, true),
    (5, 'Trámites legales', 5, true),
    (6, 'Cierre ganado', 6, false),
    (7, 'Cierre perdido', 7, false)
ON CONFLICT (id) DO NOTHING;
ALTER TABLE etapa_negocio ENABLE ROW LEVEL SECURITY;

-- ----- Negocios -----
CREATE TABLE IF NOT EXISTS negocio (
    id                    serial PRIMARY KEY,
    nombre                varchar(150) NOT NULL,
    id_inmueble           integer NOT NULL REFERENCES "Inmueble" (id),
    id_usuario            integer NOT NULL REFERENCES "Usuario" (id),
    tipo_operacion        varchar(10) NOT NULL CHECK (tipo_operacion IN ('venta', 'alquiler')),
    valor                 numeric(14, 2) NOT NULL CHECK (valor >= 0),
    id_etapa              integer NOT NULL REFERENCES etapa_negocio (id),
    fecha_cierre_estimada date,
    -- Vendedor dueño del negocio. NULL = negocio sin asignar
    id_propietario        integer REFERENCES "Empleado" (id) ON DELETE SET NULL,
    fecha_creacion        timestamp NOT NULL DEFAULT now(),
    fecha_actualizacion   timestamp NOT NULL DEFAULT now(),
    -- Fecha real en que paso a una etapa de cierre (ganado o perdido)
    fecha_cierre          timestamp
);
CREATE INDEX IF NOT EXISTS negocio_inmueble_idx ON negocio (id_inmueble);
CREATE INDEX IF NOT EXISTS negocio_usuario_idx ON negocio (id_usuario);
CREATE INDEX IF NOT EXISTS negocio_etapa_idx ON negocio (id_etapa);
CREATE INDEX IF NOT EXISTS negocio_propietario_idx ON negocio (id_propietario);
CREATE INDEX IF NOT EXISTS negocio_fecha_creacion_idx ON negocio (fecha_creacion);
ALTER TABLE negocio ENABLE ROW LEVEL SECURITY;

-- ----- Actividad de los contactos (y de sus negocios) -----
CREATE TABLE IF NOT EXISTS actividad (
    id               serial PRIMARY KEY,
    tipo             varchar(20) NOT NULL CHECK (tipo IN ('NOTA', 'CORREO', 'LLAMADA', 'TAREA', 'REUNION', 'ETAPA')),
    asunto           varchar(200),
    descripcion      text,
    id_usuario       integer NOT NULL REFERENCES "Usuario" (id) ON DELETE CASCADE,
    id_negocio       integer REFERENCES negocio (id) ON DELETE CASCADE,
    -- Autor (empleado que la registro)
    id_empleado      integer REFERENCES "Empleado" (id) ON DELETE SET NULL,
    fecha_creacion   timestamp NOT NULL DEFAULT now(),
    -- Tareas, reuniones y llamadas: cuando vence / ocurre
    fecha_programada timestamp,
    completada       boolean NOT NULL DEFAULT false,
    -- Llamadas: resultado (contesto, no contesto, buzon...) y duracion
    resultado        varchar(50),
    duracion_minutos integer
);
CREATE INDEX IF NOT EXISTS actividad_usuario_idx ON actividad (id_usuario, fecha_creacion DESC);
CREATE INDEX IF NOT EXISTS actividad_negocio_idx ON actividad (id_negocio);
ALTER TABLE actividad ENABLE ROW LEVEL SECURITY;

-- ----- Archivos privados del CRM -----
-- Pertenece a una actividad (adjunto de un correo, grabacion de una llamada...) o a un inmueble (documento legal)
CREATE TABLE IF NOT EXISTS archivo_crm (
    id             serial PRIMARY KEY,
    nombre         varchar(255) NOT NULL,
    -- Nombre del archivo en disco (UUID generado por el backend)
    ruta           varchar(100) NOT NULL UNIQUE,
    tipo_contenido varchar(100) NOT NULL,
    tamano         bigint NOT NULL,
    id_actividad   integer REFERENCES actividad (id) ON DELETE CASCADE,
    id_inmueble    integer REFERENCES "Inmueble" (id) ON DELETE CASCADE,
    id_empleado    integer REFERENCES "Empleado" (id) ON DELETE SET NULL,
    fecha_subida   timestamp NOT NULL DEFAULT now(),
    CHECK (id_actividad IS NOT NULL OR id_inmueble IS NOT NULL)
);
CREATE INDEX IF NOT EXISTS archivo_crm_actividad_idx ON archivo_crm (id_actividad);
CREATE INDEX IF NOT EXISTS archivo_crm_inmueble_idx ON archivo_crm (id_inmueble);
ALTER TABLE archivo_crm ENABLE ROW LEVEL SECURITY;

-- ----- Datos iniciales: un negocio por cada lead existente -----
-- Asi el pipeline no empieza vacio. La etapa se toma del estatus del lead (005): Nuevo/Contactado/Calificado -> Prospecto,
-- Visita agendada -> Cita programada, En negociacion -> Oferta, Ganado -> Cierre ganado, Perdido -> Cierre perdido.
-- Solo se ejecuta si la tabla negocio esta vacia
INSERT INTO negocio (nombre, id_inmueble, id_usuario, tipo_operacion, valor, id_etapa, id_propietario)
SELECT
    CASE WHEN i."isAlquiler" THEN 'Alquiler ' ELSE 'Venta ' END || i.titulo,
    i.id, l.id_usuario,
    CASE WHEN i."isAlquiler" THEN 'alquiler' ELSE 'venta' END,
    i.precio,
    CASE l.id_estatus WHEN 4 THEN 2 WHEN 5 THEN 4 WHEN 6 THEN 6 WHEN 7 THEN 7 ELSE 1 END,
    i.id_vendedor
FROM lead l
JOIN "Inmueble" i ON i.id = l.id_inmueble
WHERE NOT EXISTS (SELECT 1 FROM negocio);
