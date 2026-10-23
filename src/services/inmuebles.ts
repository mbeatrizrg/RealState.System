import { api } from './api'

// Maximo de fotos por inmueble (GaleriaService.MAX_IMAGENES_POR_INMUEBLE en el backend)
export const MAX_IMAGENES = 6
// Formatos y peso que acepta el backend (AlmacenImagenesLocalServiceImpl)
export const FORMATOS_IMAGEN = ['image/jpeg', 'image/png', 'image/webp']
export const PESO_MAXIMO_IMAGEN = 5 * 1024 * 1024

// InmuebleListResponseDTO
export interface InmuebleTarjeta {
  id: number
  titulo: string
  precio: number
  urlImagenPrincipal: string | null
  ciudad: string | null
  ubicacion: string | null
  isAlquiler: boolean
  isVendido: boolean
  tipoInmuebleId: number
  tipoInmueble: string
  estatusInmueble: string
  empleadoId: number | null
}

// ImagenResponseDTO
export interface Imagen {
  id: number
  url: string
}

// InmuebleDetalleResponseDTO
export interface InmuebleDetalle {
  id: number
  titulo: string
  precio: number
  descripcion: string | null
  rrbr: string
  ubicacion: string | null
  nombreCiudad: string | null
  nombreTipoInmueble: string
  nombreEstatus: string
  isAlquiler: boolean
  isVendido: boolean
  nombreEmpleado: string
  // Caracteristicas (null = sin registrar)
  habitaciones: number | null
  banos: number | null
  metrosCuadrados: number | null
  estacionamientos: number | null
  piso: number | null
  empleadoId: number
  extras: string[]
  urlsGaleria: string[]
  imagenes: Imagen[]
}

// PaginaResponseDTO: "pagina" empieza en 0
export interface Pagina<T> {
  contenido: T[]
  pagina: number
  tamano: number
  totalElementos: number
  totalPaginas: number
}

export type Orden = 'recientes' | 'precio_asc' | 'precio_desc'

type Valor = string | number | boolean | null | undefined

// FiltroInmuebleDTO: todos opcionales
export interface FiltrosBusqueda {
  q?: string | null
  ciudadId?: Valor
  tipoInmuebleId?: Valor
  extraId?: Valor
  isAlquiler?: Valor
  minPrecio?: Valor
  maxPrecio?: Valor
  orden?: Orden | ''
  pagina?: number
  tamano?: number
}

// Solo para el CRM
export interface FiltrosCrm extends FiltrosBusqueda {
  vendido?: Valor
  soloMios?: boolean
}

// InmuebleRequestDTO
export interface InmuebleNuevo {
  titulo: string
  precio: number
  descripcion: string
  rrbr?: string
  ubicacion: string
  isAlquiler: boolean
  ciudadId: number
  tipoInmuebleId: number
  extraId: number
  empleadoId?: number
  // Caracteristicas opcionales del inmueble
  habitaciones?: number
  banos?: number
  metrosCuadrados?: number
  estacionamientos?: number
  piso?: number
}

// "3 habitaciones", "1 baño", "120 m²"... para mostrar las caracteristicas de un inmueble (sin las que faltan)
export function textosCaracteristicas(c: {
  habitaciones?: number | null; banos?: number | null; metrosCuadrados?: number | null
  estacionamientos?: number | null; piso?: number | null
}) {
  const plural = (n: number, uno: string, varios: string) => `${n} ${n === 1 ? uno : varios}`
  const textos: { clave: string; texto: string }[] = []
  if (c.habitaciones != null) textos.push({ clave: 'habitaciones', texto: plural(c.habitaciones, 'habitación', 'habitaciones') })
  if (c.banos != null) textos.push({ clave: 'banos', texto: plural(c.banos, 'baño', 'baños') })
  if (c.metrosCuadrados != null) textos.push({ clave: 'metros', texto: `${Number(c.metrosCuadrados).toLocaleString('es-VE')} m²` })
  if (c.estacionamientos != null) {
    textos.push({ clave: 'estacionamientos', texto: plural(c.estacionamientos, 'puesto de estacionamiento', 'puestos de estacionamiento') })
  }
  if (c.piso != null) textos.push({ clave: 'piso', texto: c.piso === 0 ? 'Planta baja' : `Piso ${c.piso}` })
  return textos
}

// Los filtros vacios no se envian (false si se envia: isAlquiler=false significa "en venta")
function aQuery(filtros: object) {
  const params = new URLSearchParams()
  Object.entries(filtros).forEach(([clave, valor]) => {
    if (valor !== undefined && valor !== null && valor !== '') params.append(clave, String(valor))
  })
  const query = params.toString()
  return query ? `?${query}` : ''
}

// GET /api/inmueble/buscar (portal publico, nunca devuelve vendidos)
export const buscarInmuebles = (filtros: FiltrosBusqueda = {}) =>
  api.get<Pagina<InmuebleTarjeta>>(`/api/inmueble/buscar${aQuery(filtros)}`)

// GET /api/inmueble/crm (inventario del CRM, incluye vendidos)
export const buscarInmueblesCrm = (filtros: FiltrosCrm = {}) =>
  api.get<Pagina<InmuebleTarjeta>>(`/api/inmueble/crm${aQuery(filtros)}`)

export const obtenerInmueble = (id: string | number) => api.get<InmuebleDetalle>(`/api/inmueble/${id}`)

export const registrarInmueble = (datos: InmuebleNuevo) =>
  api.post<{ id: number; mensaje: string }>('/api/inmueble', datos)

// Telemetria para el CRM: registra que se abrio el detalle de un inmueble
export const registrarVista = (inmuebleId: number) => api.post<void>('/api/visto', { inmueble: inmuebleId })

// ----- Galeria (CRM) -----

// POST /api/galeria/inmueble/{id} como multipart con el campo "archivos" repetido
export function subirImagenes(inmuebleId: number, archivos: File[]) {
  const formulario = new FormData()
  archivos.forEach((archivo) => formulario.append('archivos', archivo))
  return api.postForm<Imagen[]>(`/api/galeria/inmueble/${inmuebleId}`, formulario)
}

export const eliminarImagen = (imagenId: number) => api.delete<{ mensaje: string }>(`/api/galeria/${imagenId}`)

// Validacion previa en el navegador (el backend vuelve a validar todo)
export function validarImagen(archivo: File): string | null {
  if (!FORMATOS_IMAGEN.includes(archivo.type)) return `${archivo.name}: solo se aceptan imágenes JPG, PNG o WEBP`
  if (archivo.size > PESO_MAXIMO_IMAGEN) return `${archivo.name}: pesa más de 5 MB`
  return null
}
