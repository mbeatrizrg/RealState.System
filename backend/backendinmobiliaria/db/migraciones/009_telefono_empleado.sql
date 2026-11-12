-- Agrega el telefono de contacto (WhatsApp) a la tabla Empleado.
-- Por defecto se asigna 04122109838 a todos los empleados existentes.

ALTER TABLE public."Empleado"
    ADD COLUMN IF NOT EXISTS telefono VARCHAR(30) DEFAULT '04122109838';

UPDATE public."Empleado"
SET telefono = '04122109838'
WHERE telefono IS NULL OR btrim(telefono) = '';
