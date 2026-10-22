import { api } from './api'
import type { Catalogo } from './catalogos'
import type { Pagina } from './inmuebles'

// Modulo de Contactos del CRM (ContactoController en el backend)

export type VistaContactos = 'todos' | 'mios' | 'sin_asignar'
export type OrdenContactos = 'fechaCreacion' | 'nombre' | 'correo' | 'telefono' | 'ultimaActividad' | 'propietario'

// En propietarioIds significa "sin propietario"; en estatus significa "sin ningun lead" (FiltroContactoDTO.SIN_VALOR)
export const SIN_VALOR = 0

// ContactoResponseDTO
export interface Contacto {
  id: number
  nombre: string
  apellido: string
  correo: string
  telefono: string
  cedula: number | null
  tipoNegocio: string | null
  verificado: boolean
  fechaCreacion: string | null
  ultimaActividad: string | null
  propietarioId: number | null
  nombrePropietario: string | null
  estatusId: number | null
  estatus: string | null
  cantidadLeads: number
}

// LeadResponseDTO
export interface LeadContacto {
  id: number
  usuarioId: number
  inmuebleId: number
  tituloInmueble: string
  rrbrInmueble: string
  empleadoId: number
  nombreVendedor: string
  estatus: number
}

// ComunicacionResponseDTO
export interface Comunicacion {
  id: number
  leadId: number
  nombreVendedor: string
  canal: string
  descripcion: string
  fechaCreacion: string
}

export interface InmuebleActividad {
  inmuebleId: number
  titulo: string
  precio: number | null
  vendido: boolean
  fecha: string | null
}

// ContactoDetalleResponseDTO
export interface ContactoDetalle {
  contacto: Contacto
  leads: LeadContacto[]
  comunicaciones: Comunicacion[]
  favoritos: InmuebleActividad[]
  vistasRecientes: InmuebleActividad[]
}

export interface ConteoContactos {
  todos: number
  mios: number
  sinAsignar: number
}

// Query params de GET /api/contactos. Fechas en formato yyyy-MM-dd
export interface FiltrosContactos {
  vista?: VistaContactos
  q?: string
  propietarioIds?: number[]
  estatus?: number[]
  creadoDesde?: string
  creadoHasta?: string
  actividadDesde?: string
  actividadHasta?: string
  sinActividad?: boolean
  nombre?: string
  correo?: string
  telefono?: string
  cedula?: string
  tipoNegocio?: string
  verificado?: boolean
  pagina?: number
  tamano?: number
  orden?: OrdenContactos
  direccion?: 'asc' | 'desc'
}

// Campos del panel "Filtros avanzados" (texto tal como se escribe; verificado '' = todas)
export interface FiltrosAvanzados {
  nombre: string
  correo: string
  telefono: string
  cedula: string
  tipoNegocio: string
  verificado: '' | 'true' | 'false'
}

export const filtrosAvanzadosVacios = (): FiltrosAvanzados => ({
  nombre: '', correo: '', telefono: '', cedula: '', tipoNegocio: '', verificado: '',
})

// ----- Columnas de la tabla (mismas claves que ColumnaContacto en el backend) -----
export type ClaveColumna =
  | 'nombre' | 'correo' | 'telefono' | 'fechaCreacion' | 'propietario' | 'estatus'
  | 'ultimaActividad' | 'cedula' | 'tipoNegocio' | 'leads' | 'verificado'

export interface Columna {
  clave: ClaveColumna
  titulo: string
  // Campo por el que ordena el backend al hacer clic en el encabezado (sin valor = no se puede ordenar)
  orden?: OrdenContactos
}

export const COLUMNAS: Columna[] = [
  { clave: 'nombre', titulo: 'Nombre', orden: 'nombre' },
  { clave: 'correo', titulo: 'Correo', orden: 'correo' },
  { clave: 'telefono', titulo: 'Teléfono', orden: 'telefono' },
  { clave: 'fechaCreacion', titulo: 'Fecha de creación', orden: 'fechaCreacion' },
  { clave: 'propietario', titulo: 'Propietario del contacto', orden: 'propietario' },
  { clave: 'estatus', titulo: 'Estatus del lead' },
  { clave: 'ultimaActividad', titulo: 'Última actividad', orden: 'ultimaActividad' },
  { clave: 'cedula', titulo: 'Cédula' },
  { clave: 'tipoNegocio', titulo: 'Tipo de negocio' },
  { clave: 'leads', titulo: 'Leads' },
  { clave: 'verificado', titulo: 'Cuenta verificada' },
]

export const COLUMNAS_POR_DEFECTO: ClaveColumna[] = ['nombre', 'correo', 'telefono', 'fechaCreacion', 'propietario', 'estatus']

// Los filtros vacios no se envian; las listas se envian separadas por coma (propietarioIds=2,3)
function aQuery(filtros: object) {
  const params = new URLSearchParams()
  Object.entries(filtros).forEach(([clave, valor]) => {
    if (valor === undefined || valor === null || valor === '') return
    if (Array.isArray(valor)) {
      if (valor.length) params.append(clave, valor.join(','))
    } else {
      params.append(clave, String(valor))
    }
  })
  const query = params.toString()
  return query ? `?${query}` : ''
}

export const buscarContactos = (filtros: FiltrosContactos) =>
  api.get<Pagina<Contacto>>(`/api/contactos${aQuery(filtros)}`)

export const contarContactos = (filtros: FiltrosContactos) =>
  api.get<ConteoContactos>(`/api/contactos/conteos${aQuery(filtros)}`)

export const obtenerContacto = (id: number | string) => api.get<ContactoDetalle>(`/api/contactos/${id}`)

export const obtenerPropietarios = () => api.get<Catalogo[]>('/api/contactos/propietarios')

export const obtenerEstatusLead = () => api.get<Catalogo[]>('/api/contactos/estatus')

export const obtenerCanales = () => api.get<Catalogo[]>('/api/canal')

// empleadoId null = dejar el contacto sin asignar
export const cambiarPropietario = (id: number, empleadoId: number | null) =>
  api.patch<{ mensaje: string }>(`/api/contactos/${id}/propietario`, { empleadoId })

export const cambiarEstatusLead = (leadId: number, estatus: number) =>
  api.patch<{ mensaje: string }>(`/api/lead/${leadId}/estatus`, { estatus })

export const registrarComunicacion = (leadId: number, canalId: number, descripcion: string) =>
  api.post<void>('/api/comunicacion', { leadId, canalId, descripcion })

export interface OpcionesExportacion {
  formato: 'xlsx' | 'pdf'
  alcance: 'todos' | 'pagina'
  columnas: ClaveColumna[]
}

// Descarga la exportacion con los mismos filtros de la tabla
export async function exportarContactos(filtros: FiltrosContactos, opciones: OpcionesExportacion) {
  const { blob, nombre } = await api.archivo(`/api/contactos/exportar${aQuery({ ...filtros, ...opciones })}`)
  const enlace = document.createElement('a')
  enlace.href = URL.createObjectURL(blob)
  enlace.download = nombre ?? `contactos.${opciones.formato}`
  document.body.appendChild(enlace)
  enlace.click()
  enlace.remove()
  setTimeout(() => URL.revokeObjectURL(enlace.href), 1000)
}
