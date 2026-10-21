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
