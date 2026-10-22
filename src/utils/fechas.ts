// Fechas del CRM. El backend envia LocalDateTime sin zona ("2026-10-21T14:30:00") y recibe fechas yyyy-MM-dd

const formatoFecha = new Intl.DateTimeFormat('es-VE', { day: '2-digit', month: 'short', year: 'numeric' })
const formatoFechaHora = new Intl.DateTimeFormat('es-VE', {
  day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit',
})

// "2026-10-21T14:30:00" -> "21 oct 2026"
export const formatearFecha = (valor: string | null | undefined) => (valor ? formatoFecha.format(new Date(valor)) : '—')

export const formatearFechaHora = (valor: string | null | undefined) =>
  valor ? formatoFechaHora.format(new Date(valor)) : '—'

// Date -> "2026-10-21" (en la hora local, no en UTC)
export function aFechaIso(fecha: Date) {
  const mes = String(fecha.getMonth() + 1).padStart(2, '0')
  const dia = String(fecha.getDate()).padStart(2, '0')
  return `${fecha.getFullYear()}-${mes}-${dia}`
}

export type RangoPredefinido = 'hoy' | 'ayer' | 'ultimos7' | 'ultimos30' | 'esteMes' | 'mesPasado' | 'esteAnio' | 'personalizado'

export const RANGOS_FECHA: { valor: RangoPredefinido; texto: string }[] = [
  { valor: 'hoy', texto: 'Hoy' },
  { valor: 'ayer', texto: 'Ayer' },
  { valor: 'ultimos7', texto: 'Últimos 7 días' },
  { valor: 'ultimos30', texto: 'Últimos 30 días' },
  { valor: 'esteMes', texto: 'Este mes' },
  { valor: 'mesPasado', texto: 'Mes pasado' },
  { valor: 'esteAnio', texto: 'Este año' },
  { valor: 'personalizado', texto: 'Rango personalizado' },
]

// Convierte un rango predefinido en { desde, hasta } (ambos incluidos). "personalizado" usa las fechas escritas
export function rangoAFechas(rango: string, desde = '', hasta = '', hoy = new Date()): { desde?: string; hasta?: string } {
  const dia = (desplazamiento: number) => {
    const d = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate() + desplazamiento)
    return aFechaIso(d)
  }
  switch (rango) {
    case 'hoy': return { desde: dia(0), hasta: dia(0) }
    case 'ayer': return { desde: dia(-1), hasta: dia(-1) }
    case 'ultimos7': return { desde: dia(-6), hasta: dia(0) }
    case 'ultimos30': return { desde: dia(-29), hasta: dia(0) }
    case 'esteMes': return { desde: aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth(), 1)), hasta: dia(0) }
    case 'mesPasado': return {
      desde: aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth() - 1, 1)),
      hasta: aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth(), 0)),
    }
    case 'esteAnio': return { desde: aFechaIso(new Date(hoy.getFullYear(), 0, 1)), hasta: dia(0) }
    case 'personalizado': return { desde: desde || undefined, hasta: hasta || undefined }
    default: return {}
  }
}
