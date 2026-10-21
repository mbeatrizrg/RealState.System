import { api } from './api'

// InmuebleListResponseDTO
export interface InmuebleTarjeta {
  id: number
  titulo: string
  precio: number
  urlImagenPrincipal: string | null
  ciudad: string | null
  tipoInmuebleId: number
  tipoInmueble: string
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
  nombreEmpleado: string
  extras: string[]
  urlsGaleria: string[]
}

export interface FiltrosBusqueda {
  ciudadId?: string | number | null
  tipoInmuebleId?: string | number | null
  minPrecio?: string | number | null
  maxPrecio?: string | number | null
}

// GET /api/inmueble/buscar, los filtros vacios no se envian
export function buscarInmuebles(filtros: FiltrosBusqueda = {}) {
  const params = new URLSearchParams()
  Object.entries(filtros).forEach(([clave, valor]) => {
    if (valor !== undefined && valor !== null && valor !== '') params.append(clave, String(valor))
  })
  const query = params.toString()
  return api.get<InmuebleTarjeta[]>(`/api/inmueble/buscar${query ? `?${query}` : ''}`)
}

export const obtenerInmueble = (id: string | number) => api.get<InmuebleDetalle>(`/api/inmueble/${id}`)

// Telemetria para el CRM: registra que se abrio el detalle de un inmueble
export const registrarVista = (inmuebleId: number) => api.post<void>('/api/visto', { inmueble: inmuebleId })
