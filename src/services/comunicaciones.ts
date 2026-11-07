import { api } from './api'
import type { Catalogo } from './catalogos'
import { crearTarea, completarTarea, buscarTareas, type Tarea, type Prioridad } from './tareas'

// Modulo de Comunicaciones y Reportes de Visita del CRM

export interface ComunicacionCrm {
  id: number
  leadId: number
  contactoId?: number | null
  nombreCliente: string
  nombreVendedor: string
  inmuebleId?: number | null
  nombreInmueble?: string | null
  canal: string
  descripcion: string
  fechaCreacion: string
}

export interface LeadCrm {
  id: number
  usuarioId: number
  nombreCliente: string
  correoCliente: string
  telefonoCliente: string
  inmuebleId: number
  tituloInmueble: string
  rrbrInmueble: string
  empleadoId: number
  nombreVendedor: string
  estatus: number
}

export interface NuevaComunicacion {
  leadId: number
  canalId: number
  descripcion: string
}

export interface NuevoReporteVisita {
  contactoId: number | null
  inmuebleId: number | null
  negocioId: number | null
  fechaVisita: string
  calificacion: string
  impresionCliente: string
  observaciones: string
  proximosPasos?: string
  prioridad?: Prioridad
}

export const VALORACIONES_VISITA = [
  { valor: 'Muy interesado (posible oferta)', texto: 'Muy interesado (posible oferta)', clase: 'ganado' },
  { valor: 'Interesado (evaluando opciones)', texto: 'Interesado (evaluando opciones)', clase: 'calificado' },
  { valor: 'Solicitó segunda visita', texto: 'Solicitó segunda visita', clase: 'visita' },
  { valor: 'Con objeciones (precio / ubicación / estado)', texto: 'Con objeciones (precio / ubicación / estado)', clase: 'negociacion' },
  { valor: 'Descartó el inmueble', texto: 'Descartó el inmueble', clase: 'perdido' },
]

export const obtenerComunicacionesCrm = () => api.get<ComunicacionCrm[]>('/api/comunicacion')

export const obtenerComunicacionesPorEmpleado = (empleadoId: number) =>
  api.get<ComunicacionCrm[]>(`/api/comunicacion/empleado/${empleadoId}`)

export const registrarComunicacionCrm = (datos: NuevaComunicacion) =>
  api.post<string>('/api/comunicacion', datos)

export const obtenerLeadsCrm = () => api.get<LeadCrm[]>('/api/lead')

export const obtenerCanalesComunicacion = () => api.get<Catalogo[]>('/api/canal')

// Obtiene los reportes de visita realizados (tareas de tipo VISITA)
export const obtenerReportesVisita = (q = '', soloMios = false) =>
  buscarTareas({
    vista: 'todas',
    q,
    tiposTarea: ['VISITA'],
    estados: ['completada'],
    orden: 'fechaProgramada',
    direccion: 'desc',
    tamano: 100,
    ...(soloMios ? { vista: 'todas' } : {}),
  })

/* Registra un reporte de visita: crea la visita de inmueble y la marca como completada con el reporte detallado,
   lo que guarda automaticamente la nota en el historial del contacto y actualiza las estadisticas de visitas */
export async function crearReporteVisita(datos: NuevoReporteVisita): Promise<Tarea> {
  const resumen = [
    `Valoración de la visita: ${datos.calificacion}`,
    `Impresión del cliente: ${datos.impresionCliente.trim()}`,
    datos.observaciones.trim() ? `Observaciones del inmueble: ${datos.observaciones.trim()}` : '',
    datos.proximosPasos?.trim() ? `Próximos pasos / acuerdos: ${datos.proximosPasos.trim()}` : '',
  ].filter(Boolean).join('\n')

  const fechaIso = datos.fechaVisita.length === 16 ? `${datos.fechaVisita}:00` : datos.fechaVisita
  const tareaCreada = await crearTarea({
    titulo: `Reporte de visita · ${datos.calificacion}`,
    descripcion: `Impresión del cliente: ${datos.impresionCliente.trim()}` + (datos.observaciones.trim() ? `\nObservaciones: ${datos.observaciones.trim()}` : ''),
    tipoTarea: 'VISITA',
    prioridad: datos.prioridad ?? 'media',
    estado: 'pendiente',
    fechaVencimiento: fechaIso,
    contactoId: datos.contactoId,
    inmuebleId: datos.inmuebleId,
    negocioId: datos.negocioId,
    propietarioId: null,
  })

  return await completarTarea(tareaCreada.id, resumen)
}
