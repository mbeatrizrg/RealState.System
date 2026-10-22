// Color de la etiqueta de estatus segun el id del catalogo estatus_lead (db/migraciones/005_contactos.sql)
const CLASES: Record<number, string> = {
  1: 'nuevo',
  2: 'contactado',
  3: 'calificado',
  4: 'visita',
  5: 'negociacion',
  6: 'ganado',
  7: 'perdido',
}

// null = el contacto no tiene leads
export const claseEstatus = (estatusId: number | null | undefined) =>
  estatusId == null ? 'sin-lead' : (CLASES[estatusId] ?? 'nuevo')
