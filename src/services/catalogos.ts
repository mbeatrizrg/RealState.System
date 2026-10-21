import { api } from './api'

// Formato comun de las tablas auxiliares (ciudad, tipo_inmueble, extras...)
export interface Catalogo {
  id: number
  nombre: string
}

export const obtenerCiudades = () => api.get<Catalogo[]>('/api/ciudad')
export const obtenerTiposInmueble = () => api.get<Catalogo[]>('/api/tipo_inmueble')
export const obtenerEstatusInmueble = () => api.get<Catalogo[]>('/api/estatus_inmueble')
export const obtenerExtras = () => api.get<Catalogo[]>('/api/extras')
