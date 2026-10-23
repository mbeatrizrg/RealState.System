import { api } from './api'

// Piezas comunes de las tablas del CRM (Contactos, Negocios, Inmuebles): query params, columnas y exportacion

// Columna de una tabla. clave = la misma del enum Columna* del backend; orden = campo por el que ordena (si se puede)
export interface ColumnaTabla<K extends string = string, O extends string = string> {
  clave: K
  titulo: string
  orden?: O
}

export interface OpcionesExportacion<K extends string = string> {
  formato: 'xlsx' | 'pdf'
  alcance: 'todos' | 'pagina'
  columnas: K[]
}

// Maximo de filas de una exportacion (ContactoService.MAXIMO_EXPORTACION en el backend)
export const MAXIMO_EXPORTACION = 5000

// Los filtros vacios no se envian; las listas se envian separadas por coma (propietarioIds=2,3)
export function aQuery(filtros: object) {
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

// Descarga un archivo del backend (exportaciones, adjuntos) con el nombre que propone en Content-Disposition
export async function descargar(ruta: string, nombrePorDefecto: string) {
  const { blob, nombre } = await api.archivo(ruta)
  const enlace = document.createElement('a')
  enlace.href = URL.createObjectURL(blob)
  enlace.download = nombre ?? nombrePorDefecto
  document.body.appendChild(enlace)
  enlace.click()
  enlace.remove()
  setTimeout(() => URL.revokeObjectURL(enlace.href), 1000)
}

// Exportacion Excel/PDF de una tabla con sus filtros: GET {base}/exportar?...
export const exportarTabla = (base: string, unidad: string, filtros: object, opciones: OpcionesExportacion) =>
  descargar(`${base}/exportar${aQuery({ ...filtros, ...opciones })}`, `${unidad}.${opciones.formato}`)

// Columnas visibles guardadas en el navegador (preferencia de cada usuario). La columna fija siempre debe estar
export function leerColumnasGuardadas<K extends string>(clave: string, disponibles: ColumnaTabla<K>[], porDefecto: K[], fija: K): K[] {
  try {
    const guardadas = JSON.parse(localStorage.getItem(clave) ?? 'null') as K[] | null
    const validas = guardadas?.filter((c) => disponibles.some((d) => d.clave === c))
    if (validas?.includes(fija)) return validas
  } catch {
    // Sin localStorage o con datos corruptos: columnas por defecto
  }
  return [...porDefecto]
}

export function guardarColumnas(clave: string, columnas: string[]) {
  try {
    localStorage.setItem(clave, JSON.stringify(columnas))
  } catch {
    // Se mantiene solo hasta recargar la pagina
  }
}
