import { api } from './api'
import type { Actividad } from './actividades'
import type { Contacto } from './contactos'
import type { Imagen, Pagina } from './inmuebles'
import type { Propiedad } from './propiedades'
import { aQuery, exportarTabla, type ColumnaTabla, type OpcionesExportacion } from './tablas'

// Modulo de Negocios del CRM (NegocioController en el backend): oportunidades de venta o alquiler de un inmueble

export type VistaNegocios = 'todos' | 'mios' | 'sin_asignar'
export type OrdenNegocios = 'fechaCreacion' | 'nombre' | 'valor' | 'fechaCierre' | 'etapa' | 'propietario' | 'contacto'
export type TipoOperacion = 'venta' | 'alquiler'

// En propietarioIds significa "sin propietario" (FiltroNegocioDTO.SIN_VALOR)
export const SIN_PROPIETARIO = 0

// NegocioResponseDTO. Las fechas de cierre estimada son yyyy-MM-dd; el resto LocalDateTime
export interface Negocio {
  id: number
  nombre: string
  inmuebleId: number
  codigoInmueble: string
  tituloInmueble: string
  tipoInmueble: string
  contactoId: number
  nombreContacto: string
  tipoOperacion: TipoOperacion
  valor: number
  etapaId: number
  etapa: string
  etapaAbierta: boolean
  fechaCierreEstimada: string | null
  propietarioId: number | null
  nombrePropietario: string | null
  fechaCreacion: string
  fechaActualizacion: string
  fechaCierre: string | null
}

export interface EtapaNegocio {
  id: number
  nombre: string
  abierta: boolean
}

export interface ColumnaTablero {
  etapaId: number
  etapa: string
  abierta: boolean
  cantidad: number
  total: number
  negocios: Negocio[]
}

export interface TableroNegocios {
  columnas: ColumnaTablero[]
  maximoPorColumna: number
}

// ConteoPestanasDTO (terceraPestana = sin asignar)
export interface ConteoPestanas {
  todos: number
  mios: number
  terceraPestana: number
}

export interface NegocioDetalle {
  negocio: Negocio
  inmueble: Propiedad
  fotos: Imagen[]
  contacto: Contacto
  historial: Actividad[]
  puedeEditar: boolean
}

// NegocioRequestDTO
export interface NegocioFormulario {
  nombre: string
  inmuebleId: number | null
  contactoId: number | null
  tipoOperacion: TipoOperacion
  valor: number | null
  etapaId: number | null
  fechaCierreEstimada: string | null
  propietarioId: number | null
}

export interface FiltrosNegocios {
  vista?: VistaNegocios
  q?: string
  propietarioIds?: number[]
  etapas?: number[]
  tiposOperacion?: TipoOperacion[]
  tipoInmuebleIds?: number[]
  creadoDesde?: string
  creadoHasta?: string
  cierreDesde?: string
  cierreHasta?: string
  valorMin?: string
  valorMax?: string
  contacto?: string
  codigoInmueble?: string
  abiertos?: boolean
  contactoId?: number
  inmuebleId?: number
  pagina?: number
  tamano?: number
  orden?: OrdenNegocios
  direccion?: 'asc' | 'desc'
}

// ----- Columnas (mismas claves que ColumnaNegocio en el backend) -----
export type ClaveColumnaNegocio =
  | 'nombre' | 'inmueble' | 'contacto' | 'operacion' | 'valor' | 'etapa' | 'fechaCierre' | 'propietario'
  | 'fechaCreacion' | 'tituloInmueble' | 'fechaActualizacion'

export const COLUMNAS_NEGOCIO: ColumnaTabla<ClaveColumnaNegocio, OrdenNegocios>[] = [
  { clave: 'nombre', titulo: 'Nombre del negocio', orden: 'nombre' },
  { clave: 'inmueble', titulo: 'Inmueble asociado' },
  { clave: 'contacto', titulo: 'Contacto / cliente', orden: 'contacto' },
  { clave: 'operacion', titulo: 'Tipo de operación' },
  { clave: 'valor', titulo: 'Valor', orden: 'valor' },
  { clave: 'etapa', titulo: 'Etapa del negocio', orden: 'etapa' },
  { clave: 'fechaCierre', titulo: 'Fecha estimada de cierre', orden: 'fechaCierre' },
  { clave: 'propietario', titulo: 'Propietario del negocio', orden: 'propietario' },
  { clave: 'fechaCreacion', titulo: 'Fecha de creación', orden: 'fechaCreacion' },
  { clave: 'tituloInmueble', titulo: 'Título del inmueble' },
  { clave: 'fechaActualizacion', titulo: 'Última actualización' },
]

export const COLUMNAS_NEGOCIO_POR_DEFECTO: ClaveColumnaNegocio[] =
  ['nombre', 'inmueble', 'contacto', 'operacion', 'valor', 'etapa', 'fechaCierre', 'propietario']

// Color de la etiqueta de la etapa (mismos colores que el estatus del lead)
const CLASES_ETAPA: Record<number, string> = {
  1: 'nuevo', 2: 'visita', 3: 'contactado', 4: 'negociacion', 5: 'calificado', 6: 'ganado', 7: 'perdido',
}
export const claseEtapa = (etapaId: number) => CLASES_ETAPA[etapaId] ?? 'nuevo'

export const buscarNegocios = (filtros: FiltrosNegocios) => api.get<Pagina<Negocio>>(`/api/negocios${aQuery(filtros)}`)

export const contarNegocios = (filtros: FiltrosNegocios) =>
  api.get<ConteoPestanas>(`/api/negocios/conteos${aQuery(filtros)}`)

export const obtenerTablero = (filtros: FiltrosNegocios) =>
  api.get<TableroNegocios>(`/api/negocios/tablero${aQuery(filtros)}`)

export const obtenerEtapas = () => api.get<EtapaNegocio[]>('/api/negocios/etapas')

export const obtenerNegocio = (id: number | string) => api.get<NegocioDetalle>(`/api/negocios/${id}`)

export const crearNegocio = (datos: NegocioFormulario) => api.post<{ id: number; mensaje: string }>('/api/negocios', datos)

export const actualizarNegocio = (id: number, datos: NegocioFormulario) =>
  api.put<{ mensaje: string }>(`/api/negocios/${id}`, datos)

export const cambiarEtapaNegocio = (id: number, etapaId: number) =>
  api.patch<{ mensaje: string }>(`/api/negocios/${id}/etapa`, { etapaId })

export const eliminarNegocio = (id: number) => api.delete<{ mensaje: string }>(`/api/negocios/${id}`)

export const exportarNegocios = (filtros: FiltrosNegocios, opciones: OpcionesExportacion) =>
  exportarTabla('/api/negocios', 'negocios', filtros, opciones)
