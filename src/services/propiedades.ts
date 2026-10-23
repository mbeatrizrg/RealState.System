import { api } from './api'
import type { Archivo } from './actividades'
import type { Imagen, Pagina } from './inmuebles'
import type { ConteoPestanas, Negocio } from './negocios'
import { aQuery, exportarTabla, type ColumnaTabla, type OpcionesExportacion } from './tablas'

// Modulo Inmuebles / Propiedades del CRM (PropiedadController). El alta sigue en "Añadir inmueble" (/api/inmueble)

export type VistaPropiedades = 'todos' | 'mios' | 'disponibles'
export type OrdenPropiedades = 'fechaCaptacion' | 'titulo' | 'codigo' | 'precio' | 'estado' | 'asesor' | 'zona'
export type EstadoInmueble = 'disponible' | 'reservado' | 'vendido' | 'alquilado'

export const ESTADOS_INMUEBLE: { valor: EstadoInmueble; texto: string }[] = [
  { valor: 'disponible', texto: 'Disponible' },
  { valor: 'reservado', texto: 'Reservado' },
  { valor: 'vendido', texto: 'Vendido' },
  { valor: 'alquilado', texto: 'Alquilado' },
]

// PropiedadResponseDTO
export interface Propiedad {
  id: number
  codigo: string
  titulo: string
  tipoInmuebleId: number
  tipoInmueble: string
  tipoOperacion: 'venta' | 'alquiler'
  ciudadId: number | null
  ciudad: string | null
  ubicacion: string
  precio: number
  estado: EstadoInmueble
  duenoNombre: string | null
  duenoTelefono: string | null
  duenoCorreo: string | null
  asesorId: number | null
  nombreAsesor: string | null
  fechaCaptacion: string
  habitaciones: number | null
  banos: number | null
  metrosCuadrados: number | null
  estacionamientos: number | null
  piso: number | null
  negociosAbiertos: number
  urlImagenPrincipal: string | null
}

export interface Interesado {
  leadId: number
  contactoId: number
  nombre: string
  correo: string
  telefono: string
  estatusId: number
}

export interface PropiedadDetalle {
  propiedad: Propiedad
  descripcion: string | null
  extras: string[]
  imagenes: Imagen[]
  documentos: Archivo[]
  negocios: Negocio[]
  interesados: Interesado[]
  puedeEditar: boolean
}

// FichaPropiedadDTO
export interface FichaPropiedad {
  estado: EstadoInmueble
  duenoNombre: string
  duenoTelefono: string
  duenoCorreo: string
  habitaciones: number | null
  banos: number | null
  metrosCuadrados: number | null
  estacionamientos: number | null
  piso: number | null
}

export interface FiltrosPropiedades {
  vista?: VistaPropiedades
  q?: string
  asesorIds?: number[]
  tipoInmuebleIds?: number[]
  ciudadIds?: number[]
  zona?: string
  precioMin?: string
  precioMax?: string
  estados?: EstadoInmueble[]
  captadoDesde?: string
  captadoHasta?: string
  codigo?: string
  dueno?: string
  operacion?: 'venta' | 'alquiler'
  habitacionesMin?: string
  banosMin?: string
  metrosMin?: string
  metrosMax?: string
  pagina?: number
  tamano?: number
  orden?: OrdenPropiedades
  direccion?: 'asc' | 'desc'
}

// ----- Columnas (mismas claves que ColumnaPropiedad en el backend) -----
export type ClaveColumnaPropiedad =
  | 'codigo' | 'titulo' | 'tipo' | 'operacion' | 'zona' | 'precio' | 'estado' | 'dueno' | 'asesor'
  | 'fechaCaptacion' | 'habitaciones' | 'banos' | 'metros' | 'estacionamientos' | 'piso' | 'negocios'

export const COLUMNAS_PROPIEDAD: ColumnaTabla<ClaveColumnaPropiedad, OrdenPropiedades>[] = [
  { clave: 'codigo', titulo: 'Código', orden: 'codigo' },
  { clave: 'titulo', titulo: 'Título', orden: 'titulo' },
  { clave: 'tipo', titulo: 'Tipo de propiedad' },
  { clave: 'operacion', titulo: 'Tipo de operación' },
  { clave: 'zona', titulo: 'Zona / ubicación', orden: 'zona' },
  { clave: 'precio', titulo: 'Precio', orden: 'precio' },
  { clave: 'estado', titulo: 'Estado', orden: 'estado' },
  { clave: 'dueno', titulo: 'Propietario (dueño)' },
  { clave: 'asesor', titulo: 'Asesor captador', orden: 'asesor' },
  { clave: 'fechaCaptacion', titulo: 'Fecha de captación', orden: 'fechaCaptacion' },
  { clave: 'habitaciones', titulo: 'Habitaciones' },
  { clave: 'banos', titulo: 'Baños' },
  { clave: 'metros', titulo: 'Metros cuadrados' },
  { clave: 'estacionamientos', titulo: 'Estacionamientos' },
  { clave: 'piso', titulo: 'Piso' },
  { clave: 'negocios', titulo: 'Negocios abiertos' },
]

export const COLUMNAS_PROPIEDAD_POR_DEFECTO: ClaveColumnaPropiedad[] =
  ['codigo', 'titulo', 'tipo', 'operacion', 'zona', 'precio', 'estado', 'dueno', 'asesor']

const CLASES_ESTADO: Record<EstadoInmueble, string> = {
  disponible: 'ganado', reservado: 'visita', vendido: 'perdido', alquilado: 'calificado',
}
export const claseEstado = (estado: EstadoInmueble) => CLASES_ESTADO[estado] ?? 'sin-lead'
export const textoEstado = (estado: EstadoInmueble) => ESTADOS_INMUEBLE.find((e) => e.valor === estado)?.texto ?? estado

export const buscarPropiedades = (filtros: FiltrosPropiedades) =>
  api.get<Pagina<Propiedad>>(`/api/propiedades${aQuery(filtros)}`)

// terceraPestana = disponibles
export const contarPropiedades = (filtros: FiltrosPropiedades) =>
  api.get<ConteoPestanas>(`/api/propiedades/conteos${aQuery(filtros)}`)

export const obtenerPropiedad = (id: number | string) => api.get<PropiedadDetalle>(`/api/propiedades/${id}`)

export const actualizarFicha = (id: number, ficha: FichaPropiedad) =>
  api.put<{ mensaje: string }>(`/api/propiedades/${id}/ficha`, ficha)

export function subirDocumentos(id: number, archivos: File[]) {
  const formulario = new FormData()
  archivos.forEach((archivo) => formulario.append('archivos', archivo))
  return api.postForm<Archivo[]>(`/api/propiedades/${id}/documentos`, formulario)
}

export const eliminarDocumento = (documentoId: number) =>
  api.delete<{ mensaje: string }>(`/api/propiedades/documentos/${documentoId}`)

export const exportarPropiedades = (filtros: FiltrosPropiedades, opciones: OpcionesExportacion) =>
  exportarTabla('/api/propiedades', 'inmuebles', filtros, opciones)
