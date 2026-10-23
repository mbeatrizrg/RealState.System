import { api } from './api'
import { descargar } from './tablas'

// Historial de actividad del registro del contacto y archivos privados del CRM (ActividadController)

export type TipoActividad = 'NOTA' | 'CORREO' | 'LLAMADA' | 'TAREA' | 'REUNION'
// ETAPA la registra el backend al crear o mover un negocio; COMUNICACION son las comunicaciones de los leads
export type TipoHistorial = TipoActividad | 'ETAPA' | 'COMUNICACION'

// Adjuntos y documentos: maximo 10 MB cada uno y 5 por actividad (AlmacenArchivosCrm / ActividadService)
export const PESO_MAXIMO_ARCHIVO = 10 * 1024 * 1024
export const MAXIMO_ADJUNTOS = 5

export interface Archivo {
  id: number
  nombre: string
  tipoContenido: string
  tamano: number
  fechaSubida: string
  actividadId: number | null
}

// ActividadResponseDTO
export interface Actividad {
  id: number
  tipo: TipoHistorial
  asunto: string | null
  descripcion: string | null
  contactoId: number
  negocioId: number | null
  nombreNegocio: string | null
  empleadoId: number | null
  nombreEmpleado: string | null
  fechaCreacion: string
  fechaProgramada: string | null
  completada: boolean
  resultado: string | null
  duracionMinutos: number | null
  canal: string | null
  archivos: Archivo[]
}

// ActividadRequestDTO. fechaProgramada en formato "2026-10-22T15:30" (LocalDateTime)
export interface NuevaActividad {
  tipo: TipoActividad
  contactoId: number
  negocioId?: number | null
  asunto?: string
  descripcion?: string
  fechaProgramada?: string | null
  resultado?: string
  duracionMinutos?: number | null
  enviarCorreo?: boolean
}

export const TIPOS_HISTORIAL: { valor: TipoHistorial; texto: string }[] = [
  { valor: 'NOTA', texto: 'Notas' },
  { valor: 'CORREO', texto: 'Correos' },
  { valor: 'LLAMADA', texto: 'Llamadas' },
  { valor: 'REUNION', texto: 'Reuniones' },
  { valor: 'TAREA', texto: 'Tareas' },
  { valor: 'COMUNICACION', texto: 'Comunicaciones de leads' },
  { valor: 'ETAPA', texto: 'Cambios de etapa' },
]

export const RESULTADOS_LLAMADA = ['Contestó', 'No contestó', 'Buzón de voz', 'Número equivocado', 'Ocupado']

export const obtenerHistorialContacto = (contactoId: number | string) =>
  api.get<Actividad[]>(`/api/actividades?contactoId=${contactoId}`)

export const obtenerAdjuntosContacto = (contactoId: number | string) =>
  api.get<Archivo[]>(`/api/actividades/adjuntos?contactoId=${contactoId}`)

// Multipart: "datos" va como JSON (el backend lo lee con @RequestPart) y los adjuntos en "archivos"
export function crearActividad(datos: NuevaActividad, archivos: File[] = []) {
  const formulario = new FormData()
  formulario.append('datos', new Blob([JSON.stringify(datos)], { type: 'application/json' }))
  archivos.forEach((archivo) => formulario.append('archivos', archivo))
  return api.postForm<Actividad>('/api/actividades', formulario)
}

export const marcarCompletada = (id: number, completada: boolean) =>
  api.patch<{ mensaje: string }>(`/api/actividades/${id}/completada`, { completada })

export const eliminarActividad = (id: number) => api.delete<{ mensaje: string }>(`/api/actividades/${id}`)

export const descargarArchivo = (archivo: Archivo) => descargar(`/api/archivos/${archivo.id}`, archivo.nombre)

// 1536 -> "1.5 KB"
export function formatearTamano(bytes: number) {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}
