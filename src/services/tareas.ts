import { api } from './api'
import type { Pagina } from './inmuebles'
import type { ConteoPestanas } from './negocios'
import { aQuery, exportarTabla, type ColumnaTabla, type OpcionesExportacion } from './tablas'

// Modulo de Tareas del CRM (TareaController)

export type VistaTareas = 'todas' | 'mias_pendientes' | 'completadas_vencidas'
export type TipoTarea = 'LLAMADA' | 'VISITA' | 'ENVIO' | 'REUNION' | 'TRAMITE'
export type Prioridad = 'baja' | 'media' | 'alta'
// "vencida" la calcula el backend (sin completar y con la fecha pasada); no se puede elegir al guardar
export type EstadoTarea = 'pendiente' | 'en_proceso' | 'vencida' | 'completada'
export type EstadoGuardado = Exclude<EstadoTarea, 'vencida'>
export type OrdenTareas = 'fechaProgramada' | 'titulo' | 'prioridad' | 'estado' | 'propietario' | 'contacto' | 'fechaCreacion'

export const SIN_PROPIETARIO = 0

export const TIPOS_TAREA: { valor: TipoTarea; texto: string }[] = [
  { valor: 'LLAMADA', texto: 'Llamada' },
  { valor: 'VISITA', texto: 'Visita a inmueble' },
  { valor: 'ENVIO', texto: 'Envío de catálogo/correo' },
  { valor: 'REUNION', texto: 'Reunión' },
  { valor: 'TRAMITE', texto: 'Trámite' },
]

export const ESTADOS_TAREA: { valor: EstadoTarea; texto: string }[] = [
  { valor: 'pendiente', texto: 'Pendiente' },
  { valor: 'en_proceso', texto: 'En proceso' },
  { valor: 'vencida', texto: 'Vencida' },
  { valor: 'completada', texto: 'Completada' },
]

export const PRIORIDADES: { valor: Prioridad; texto: string }[] = [
  { valor: 'alta', texto: 'Alta' },
  { valor: 'media', texto: 'Media' },
  { valor: 'baja', texto: 'Baja' },
]

// TareaResponseDTO
export interface Tarea {
  id: number
  titulo: string
  descripcion: string | null
  tipoTarea: TipoTarea | null
  prioridad: Prioridad
  estado: EstadoTarea
  estadoGuardado: EstadoGuardado
  contactoId: number | null
  nombreContacto: string | null
  negocioId: number | null
  nombreNegocio: string | null
  inmuebleId: number | null
  codigoInmueble: string | null
  tituloInmueble: string | null
  propietarioId: number | null
  nombrePropietario: string | null
  fechaVencimiento: string | null
  fechaCreacion: string
  puedeEditar: boolean
}

// TareaRequestDTO. fechaVencimiento en formato LocalDateTime ("2026-10-22T15:30:00")
export interface TareaFormulario {
  titulo: string
  descripcion: string
  tipoTarea: TipoTarea
  prioridad: Prioridad
  estado: EstadoGuardado
  fechaVencimiento: string
  contactoId: number | null
  negocioId: number | null
  inmuebleId: number | null
  propietarioId: number | null
}

export interface FiltrosTareas {
  vista?: VistaTareas
  q?: string
  propietarioIds?: number[]
  tiposTarea?: TipoTarea[]
  estados?: EstadoTarea[]
  prioridades?: Prioridad[]
  venceDesde?: string
  venceHasta?: string
  contacto?: string
  codigoInmueble?: string
  negocio?: string
  sinContacto?: boolean
  contactoId?: number
  inmuebleId?: number
  negocioId?: number
  pagina?: number
  tamano?: number
  orden?: OrdenTareas
  direccion?: 'asc' | 'desc'
}

// ----- Columnas (mismas claves que ColumnaTarea en el backend) -----
export type ClaveColumnaTarea =
  | 'titulo' | 'tipo' | 'estado' | 'prioridad' | 'contacto' | 'vinculo' | 'vencimiento' | 'propietario' | 'fechaCreacion' | 'descripcion'

export const COLUMNAS_TAREA: ColumnaTabla<ClaveColumnaTarea, OrdenTareas>[] = [
  { clave: 'titulo', titulo: 'Título / asunto', orden: 'titulo' },
  { clave: 'tipo', titulo: 'Tipo de tarea' },
  { clave: 'estado', titulo: 'Estado', orden: 'estado' },
  { clave: 'prioridad', titulo: 'Prioridad', orden: 'prioridad' },
  { clave: 'contacto', titulo: 'Contacto asociado', orden: 'contacto' },
  { clave: 'vinculo', titulo: 'Inmueble o negocio' },
  { clave: 'vencimiento', titulo: 'Vencimiento', orden: 'fechaProgramada' },
  { clave: 'propietario', titulo: 'Propietario de la tarea', orden: 'propietario' },
  { clave: 'fechaCreacion', titulo: 'Fecha de creación', orden: 'fechaCreacion' },
  { clave: 'descripcion', titulo: 'Descripción' },
]

export const COLUMNAS_TAREA_POR_DEFECTO: ClaveColumnaTarea[] =
  ['titulo', 'tipo', 'estado', 'prioridad', 'contacto', 'vinculo', 'vencimiento', 'propietario']

// Colores de las etiquetas (clases de contactos/estatus.css)
const CLASES_ESTADO: Record<EstadoTarea, string> = { pendiente: 'nuevo', en_proceso: 'visita', vencida: 'perdido', completada: 'ganado' }
const CLASES_PRIORIDAD: Record<Prioridad, string> = { alta: 'negociacion', media: 'contactado', baja: 'sin-lead' }
export const claseEstadoTarea = (estado: EstadoTarea) => CLASES_ESTADO[estado] ?? 'nuevo'
export const clasePrioridad = (prioridad: Prioridad) => CLASES_PRIORIDAD[prioridad] ?? 'sin-lead'
export const textoTipoTarea = (tipo: TipoTarea | null) => TIPOS_TAREA.find((t) => t.valor === tipo)?.texto ?? 'General'
export const textoEstadoTarea = (estado: EstadoTarea) => ESTADOS_TAREA.find((e) => e.valor === estado)?.texto ?? estado
export const textoPrioridad = (prioridad: Prioridad) => PRIORIDADES.find((p) => p.valor === prioridad)?.texto ?? prioridad

export const buscarTareas = (filtros: FiltrosTareas) => api.get<Pagina<Tarea>>(`/api/tareas${aQuery(filtros)}`)

// terceraPestana = completadas / vencidas
export const contarTareas = (filtros: FiltrosTareas) => api.get<ConteoPestanas>(`/api/tareas/conteos${aQuery(filtros)}`)

export const obtenerTarea = (id: number | string) => api.get<Tarea>(`/api/tareas/${id}`)

export const crearTarea = (datos: TareaFormulario) => api.post<Tarea>('/api/tareas', datos)

export const actualizarTarea = (id: number, datos: TareaFormulario) => api.put<Tarea>(`/api/tareas/${id}`, datos)

export const cambiarEstadoTarea = (id: number, estado: EstadoGuardado) => api.patch<Tarea>(`/api/tareas/${id}/estado`, { estado })

export const completarTarea = (id: number, resultado: string) => api.post<Tarea>(`/api/tareas/${id}/completar`, { resultado })

export const eliminarTarea = (id: number) => api.delete<{ mensaje: string }>(`/api/tareas/${id}`)

export const exportarTareas = (filtros: FiltrosTareas, opciones: OpcionesExportacion) =>
  exportarTabla('/api/tareas', 'tareas', filtros, opciones)
