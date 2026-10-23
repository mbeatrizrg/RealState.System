-- =====================================================================
-- Datos de demostracion para la presentacion y el informe de base de datos.
-- Agrega 3 inmuebles (codigos RRBR 8100101, 8100102, 8100103) y TODO su movimiento: fotos, leads, comunicaciones,
-- favoritos, vistas, negocios, una venta y actividades. Tambien carga el catalogo de canales de contacto.
-- No modifica los inmuebles existentes. Para quitarlo: borrar_datos_demo.sql
-- Si los datos ya estan cargados, no hace nada.
-- =====================================================================
DO $$
DECLARE
    inm_a integer; inm_b integer; inm_c integer;
    c_whatsapp integer; c_llamada integer; c_correo integer; c_presencial integer;
    l1 integer; l2 integer; l3 integer; l4 integer; l5 integer; l6 integer; l7 integer;
    n_ganado integer; n_cita integer; n_oferta integer;
BEGIN
    IF EXISTS (SELECT 1 FROM "Inmueble" WHERE "RRBR" = '8100101') THEN
        RAISE NOTICE 'Los datos de demostracion ya estan cargados';
        RETURN;
    END IF;

    -- ----- Canales de contacto (catalogo) -----
    INSERT INTO canal (nombre) SELECT v FROM (VALUES ('WhatsApp'), ('Llamada'), ('Correo'), ('Presencial'), ('Instagram')) AS t(v)
    WHERE NOT EXISTS (SELECT 1 FROM canal WHERE lower(nombre) = lower(t.v));
    SELECT id INTO c_whatsapp FROM canal WHERE nombre = 'WhatsApp';
    SELECT id INTO c_llamada FROM canal WHERE nombre = 'Llamada';
    SELECT id INTO c_correo FROM canal WHERE nombre = 'Correo';
    SELECT id INTO c_presencial FROM canal WHERE nombre = 'Presencial';

    -- ----- Inmuebles (ciudad 2 Valencia, 9 San Diego, 10 Guacara; vendedores 2 ana, 3 mateo, 4 carlos) -----
    INSERT INTO "Inmueble" (titulo, precio, descripcion, "RRBR", ubicacion, id_ciudad, id_vendedor, id_tipo, id_extras,
                            "isVendido", "isAlquiler", estado, fecha_captacion, dueno_nombre, dueno_telefono, habitaciones,
                            banos, metros_cuadrados, estacionamientos, piso)
    VALUES ('Apartamento en El Parral', 85000, 'Apartamento remodelado con vista a la ciudad, cocina empotrada y aire acondicionado en las habitaciones.',
            '8100101', 'Urb. El Parral, Av. 4', 2, 2, 1, 3, false, false, 'disponible', '2026-06-20 09:00', 'Luis Herrera', '0414-5550101',
            3, 2, 110, 1, 6)
    RETURNING id INTO inm_a;
    INSERT INTO "Inmueble" (titulo, precio, descripcion, "RRBR", ubicacion, id_ciudad, id_vendedor, id_tipo, id_extras,
                            "isVendido", "isAlquiler", estado, fecha_captacion, dueno_nombre, dueno_telefono, habitaciones,
                            banos, metros_cuadrados, estacionamientos, piso)
    VALUES ('Townhouse en San Diego', 120000, 'Townhouse de dos niveles en conjunto cerrado con piscina y vigilancia privada.',
            '8100102', 'Conj. Res. Los Jarales', 9, 3, 2, 2, true, false, 'vendido', '2026-05-12 10:30', 'Carmen Rojas', '0424-5550202',
            4, 3, 210, 2, 0)
    RETURNING id INTO inm_b;
    INSERT INTO "Inmueble" (titulo, precio, descripcion, "RRBR", ubicacion, id_ciudad, id_vendedor, id_tipo, id_extras,
                            "isVendido", "isAlquiler", estado, fecha_captacion, dueno_nombre, dueno_telefono, habitaciones,
                            banos, metros_cuadrados, estacionamientos, piso)
    VALUES ('Local comercial en Guacara', 650, 'Local en planta baja sobre avenida principal, con planta eléctrica general y alto tránsito peatonal.',
            '8100103', 'Av. Carabobo, C.C. Guacara Plaza', 10, 4, 4, 5, false, true, 'disponible', '2026-08-01 14:00', 'Inversiones Pérez', '0412-5550303',
            NULL, 1, 80, 1, 0)
    RETURNING id INTO inm_c;

    -- ----- Galeria (imagenes del portal) -----
    INSERT INTO galeria (id_inmueble, foto) VALUES
        (inm_a, '/building.png'), (inm_a, '/innerbuilding.png'), (inm_a, '/innerbuilding2.png'),
        (inm_b, '/PhotoPrincipal.png'), (inm_b, '/building2.png'), (inm_b, '/innerbuilding4.png'),
        (inm_c, '/building2.png');

    -- ----- Leads (consultas) de los clientes 1, 2, 3, 6, 7 y 8 -----
    INSERT INTO lead (id_inmueble, id_usuario, id_estatus, fecha_creacion) VALUES (inm_a, 1, 1, '2026-07-10 11:20') RETURNING id INTO l1;
    INSERT INTO lead (id_inmueble, id_usuario, id_estatus, fecha_creacion) VALUES (inm_a, 2, 4, '2026-08-02 16:05') RETURNING id INTO l2;
    INSERT INTO lead (id_inmueble, id_usuario, id_estatus, fecha_creacion) VALUES (inm_b, 3, 6, '2026-06-15 09:40') RETURNING id INTO l3;
    INSERT INTO lead (id_inmueble, id_usuario, id_estatus, fecha_creacion) VALUES (inm_b, 6, 5, '2026-09-05 18:10') RETURNING id INTO l4;
    INSERT INTO lead (id_inmueble, id_usuario, id_estatus, fecha_creacion) VALUES (inm_c, 7, 2, '2026-09-20 10:00') RETURNING id INTO l5;
    INSERT INTO lead (id_inmueble, id_usuario, id_estatus, fecha_creacion) VALUES (inm_c, 8, 3, '2026-10-05 15:30') RETURNING id INTO l6;
    INSERT INTO lead (id_inmueble, id_usuario, id_estatus, fecha_creacion) VALUES (inm_c, 2, 7, '2026-08-20 12:00') RETURNING id INTO l7;

    -- ----- Comunicaciones -----
    INSERT INTO comunicaciones (id_canal, id_lead, fecha_comunicacion, descripcion) VALUES
        (c_whatsapp, l1, '2026-07-10 12:00', 'Se enviaron fotos y precio del apartamento.'),
        (c_llamada, l2, '2026-08-03 09:30', 'El cliente pidió agendar una visita para el sábado.'),
        (c_presencial, l3, '2026-06-20 10:00', 'Visita guiada al townhouse; le gustó la zona y la piscina.'),
        (c_correo, l3, '2026-09-10 08:15', 'Se envió el borrador del contrato de compraventa.'),
        (c_whatsapp, l4, '2026-09-06 19:00', 'Consultó si el precio era negociable.'),
        (c_llamada, l5, '2026-09-21 11:00', 'Interesado en el local para una franquicia de comida.'),
        (c_correo, l6, '2026-10-06 10:45', 'Se envió la ficha técnica del local y las condiciones de alquiler.'),
        (c_whatsapp, l7, '2026-08-25 17:20', 'Indicó que el local le quedaba lejos; desistió.');

    -- ----- Favoritos y vistas del portal -----
    INSERT INTO favoritos (id_inmueble, id_usuario, favorito) VALUES
        (inm_a, 1, true), (inm_a, 2, true), (inm_b, 3, true), (inm_c, 7, true), (inm_c, 8, true);
    INSERT INTO vistos (id_inmueble, id_usuario, fecha_visto, visto) VALUES
        (inm_a, 1, '2026-07-09 20:15', true), (inm_a, 2, '2026-08-01 21:40', true), (inm_a, 6, '2026-09-01 08:05', true),
        (inm_b, 3, '2026-06-14 19:30', true), (inm_b, 6, '2026-09-04 22:10', true), (inm_c, 7, '2026-09-19 13:45', true),
        (inm_c, 8, '2026-10-04 18:20', true), (inm_c, 2, '2026-08-19 16:00', true);

    -- ----- Negocios (etapas: 1 Prospecto, 2 Cita, 4 Oferta, 6 Ganado, 7 Perdido) -----
    INSERT INTO negocio (nombre, id_inmueble, id_usuario, tipo_operacion, valor, id_etapa, fecha_cierre_estimada, id_propietario,
                         fecha_creacion, fecha_actualizacion, fecha_cierre)
    VALUES ('Venta Townhouse en San Diego', inm_b, 3, 'venta', 120000, 6, '2026-09-30', 3, '2026-06-16 09:00', '2026-09-28 16:00', '2026-09-28 16:00')
    RETURNING id INTO n_ganado;
    INSERT INTO negocio (nombre, id_inmueble, id_usuario, tipo_operacion, valor, id_etapa, fecha_cierre_estimada, id_propietario,
                         fecha_creacion, fecha_actualizacion, fecha_cierre)
    VALUES ('Venta Apartamento en El Parral', inm_a, 2, 'venta', 85000, 2, '2026-11-30', 2, '2026-08-03 10:00', '2026-08-03 10:00', NULL)
    RETURNING id INTO n_cita;
    INSERT INTO negocio (nombre, id_inmueble, id_usuario, tipo_operacion, valor, id_etapa, fecha_cierre_estimada, id_propietario,
                         fecha_creacion, fecha_actualizacion, fecha_cierre)
    VALUES ('Alquiler Local en Guacara', inm_c, 8, 'alquiler', 650, 4, '2026-11-15', 4, '2026-10-06 11:00', '2026-10-15 09:00', NULL)
    RETURNING id INTO n_oferta;
    INSERT INTO negocio (nombre, id_inmueble, id_usuario, tipo_operacion, valor, id_etapa, fecha_cierre_estimada, id_propietario,
                         fecha_creacion, fecha_actualizacion, fecha_cierre) VALUES
        ('Venta Townhouse en San Diego (2)', inm_b, 6, 'venta', 118000, 7, '2026-10-15', 3, '2026-09-06 12:00', '2026-09-28 16:30', '2026-09-28 16:30'),
        ('Venta Apartamento en El Parral (2)', inm_a, 1, 'venta', 85000, 1, '2026-12-15', NULL, '2026-07-11 09:00', '2026-07-11 09:00', NULL);

    -- ----- Venta del townhouse (comision del 5 %) -----
    INSERT INTO "Venta" (id_inmueble, comision, monto, fecha_venta, comprobante)
    VALUES (inm_b, 6000, 120000, '2026-09-28 16:00', 'TRF-0001234');

    -- ----- Actividades del CRM -----
    INSERT INTO actividad (tipo, asunto, descripcion, id_usuario, id_negocio, id_empleado, fecha_creacion, fecha_programada,
                           completada, tipo_tarea, prioridad, estado_tarea, id_inmueble, resultado, duracion_minutos) VALUES
        ('REUNION', 'Visita guiada al townhouse', 'Recorrido por la casa y el conjunto.', 3, n_ganado, 3, '2026-06-18 08:00', '2026-06-20 10:00',
         true, NULL, NULL, NULL, NULL, NULL, 60),
        ('TAREA', 'Mostrar el apartamento de El Parral', 'Llevar llaves y ficha del inmueble.', 2, n_cita, 2, '2026-08-03 10:30', '2026-08-08 10:00',
         true, 'VISITA', 'alta', 'completada', inm_a, NULL, NULL),
        ('TAREA', 'Enviar condiciones de alquiler del local', 'Enviar por correo el contrato modelo.', 8, n_oferta, 4, '2026-10-06 11:30', '2026-10-25 12:00',
         false, 'ENVIO', 'media', 'pendiente', inm_c, NULL, NULL),
        ('TAREA', 'Solicitar documentos del townhouse', 'Pedir título de propiedad y solvencias al dueño.', NULL, NULL, 3, '2026-07-01 09:00', '2026-07-05 09:00',
         true, 'TRAMITE', 'alta', 'completada', inm_b, NULL, NULL),
        ('LLAMADA', 'Llamada: Contestó', 'Confirmó interés en el local para una franquicia.', 7, NULL, 4, '2026-09-21 11:00', '2026-09-21 11:00',
         true, NULL, NULL, NULL, NULL, 'Contestó', 12),
        ('NOTA', NULL, 'El cliente prefiere pisos altos y necesita un puesto de estacionamiento.', 2, n_cita, 2, '2026-08-03 09:45', NULL,
         true, NULL, NULL, NULL, NULL, NULL, NULL);

    -- ----- Propietario (vendedor) de los contactos que tienen negocios -----
    UPDATE "Usuario" SET id_propietario = 2 WHERE id = 2 AND id_propietario IS NULL;
    UPDATE "Usuario" SET id_propietario = 3 WHERE id IN (3, 6) AND id_propietario IS NULL;
    UPDATE "Usuario" SET id_propietario = 4 WHERE id IN (7, 8) AND id_propietario IS NULL;
    UPDATE "Usuario" SET ultima_actividad = now() WHERE id IN (1, 2, 3, 6, 7, 8);
END $$;
