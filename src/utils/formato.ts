import { BASE_URL } from '@/services/api'

const formatoPrecio = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 0,
})

// 150000 -> "$150,000"
export const formatearPrecio = (precio: number | null | undefined) =>
  precio == null ? 'Consultar' : formatoPrecio.format(precio)

// Imagen que se muestra cuando el inmueble aun no tiene fotos en la galeria
export const IMAGEN_POR_DEFECTO = '/building.png'

// Las fotos subidas desde el CRM se guardan como /uploads/...: se sirven desde el backend, no desde el frontend
export const urlImagen = (url: string | null | undefined) => {
  if (!url) return IMAGEN_POR_DEFECTO
  return url.startsWith('/uploads/') ? `${BASE_URL}${url}` : url
}
