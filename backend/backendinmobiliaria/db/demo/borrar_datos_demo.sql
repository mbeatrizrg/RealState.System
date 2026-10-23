-- =====================================================================
-- Quita los datos de demostracion cargados con datos_demo.sql (inmuebles RRBR 8100101, 8100102 y 8100103 y todo
-- lo que depende de ellos). Los canales de contacto se conservan si alguna comunicacion real los usa.
-- =====================================================================
DO $$
DECLARE
    inmuebles integer[];
BEGIN
    SELECT array_agg(id) INTO inmuebles FROM "Inmueble" WHERE "RRBR" IN ('8100101', '8100102', '8100103');
    IF inmuebles IS NULL THEN
        RAISE NOTICE 'No hay datos de demostracion';
        RETURN;
    END IF;

    DELETE FROM actividad WHERE id_inmueble = ANY (inmuebles)
        OR id_negocio IN (SELECT id FROM negocio WHERE id_inmueble = ANY (inmuebles))
        OR (asunto = 'Llamada: Contestó' AND descripcion = 'Confirmó interés en el local para una franquicia.');
    DELETE FROM negocio WHERE id_inmueble = ANY (inmuebles);
    DELETE FROM comunicaciones WHERE id_lead IN (SELECT id FROM lead WHERE id_inmueble = ANY (inmuebles));
    DELETE FROM lead WHERE id_inmueble = ANY (inmuebles);
    DELETE FROM favoritos WHERE id_inmueble = ANY (inmuebles);
    DELETE FROM vistos WHERE id_inmueble = ANY (inmuebles);
    DELETE FROM galeria WHERE id_inmueble = ANY (inmuebles);
    DELETE FROM "Venta" WHERE id_inmueble = ANY (inmuebles);
    DELETE FROM "Inmueble" WHERE id = ANY (inmuebles);
    DELETE FROM canal c WHERE nombre IN ('WhatsApp', 'Llamada', 'Correo', 'Presencial', 'Instagram')
        AND NOT EXISTS (SELECT 1 FROM comunicaciones WHERE id_canal = c.id);
    UPDATE "Usuario" SET id_propietario = NULL WHERE id IN (2, 3, 6, 7, 8);
END $$;
