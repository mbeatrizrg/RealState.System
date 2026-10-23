import { api } from './api'
import { aQuery, descargar } from './tablas'

// Modulo de Estadisticas e Informes del CRM (EstadisticaController). Ver EstadisticasDTO para las definiciones

export type NivelInteraccion = 'INFORMACION' | 'AGENDO' | 'VISITO' | 'OFERTA' | 'CERRO' | 'DESCARTO'
export type Agrupacion = 'tiempo' | 'zona' | 'propiedad'
export type SeccionInforme = 'resumen' | 'pastel' | 'serie' | 'inmuebles' | 'vendedores'

export const SECCIONES: { valor: SeccionInforme; texto: string }[] = [
  { valor: 'resumen', texto: 'Tarjetas de métricas' },
  { valor: 'pastel', texto: 'Gráfica de pastel (interacción / demanda)' },
  { valor: 'serie', texto: 'Gráfica de interacciones (consultas, visitas y cierres)' },
  { valor: 'inmuebles', texto: 'Tabla de detalle por inmueble' },
  { valor: 'vendedores', texto: 'Rendimiento de ventas por vendedor' },
]

export interface Resumen {
  consultas: number
  mensajes: number
  visitas: number
  negociosAbiertos: number
  cierresMes: number
  cierresPeriodo: number
  montoCerradoPeriodo: number
  clientesInteresados: number
}

export interface Porcion {
  clave: string
  etiqueta: string
  cantidad: number
}

export interface PuntoSerie {
  clave: string
  etiqueta: string
  consultas: number
  visitas: number
  cierres: number
}

export interface FilaInmueble {
  id: number
  codigo: string
  titulo: string
  tipo: string
  zona: string
  clientes: number
  consultas: number
  visitas: number
  ofertas: number
  vendedor: string | null
}

export interface FilaVendedor {
  id: number | null
  nombre: string
  creados: number
  abiertos: number
  ganados: number
  perdidos: number
  montoGanado: number
  valorPipeline: number
  tasaCierre: number | null
  tareasCompletadas: number
  tareasVencidas: number
}

export interface PuntoVentas {
  clave: string
  etiqueta: string
  cierres: number
  monto: number
}

export interface EtapaValor {
  etapaId: number
  etapa: string
  cantidad: number
  valor: number
}

export interface Estadisticas {
  desde: string | null
  hasta: string | null
  resumen: Resumen
  porNivel: Porcion[]
  porTipo: Porcion[]
  serie: { agrupacion: Agrupacion; puntos: PuntoSerie[] }
  inmuebles: FilaInmueble[]
  vendedores: FilaVendedor[]
  ventasPorPeriodo: PuntoVentas[]
  pipeline: EtapaValor[]
}

export interface Accion {
  fecha: string | null
  tipo: string
  descripcion: string
}

export interface ContactoInteraccion {
  contactoId: number
  nombre: string
  correo: string
  telefono: string
  nivel: NivelInteraccion
  etiquetaNivel: string
  consultas: number
  visitas: number
  negocioId: number | null
  negocio: string | null
  etapa: string | null
  ultimaInteraccion: string | null
  acciones: Accion[]
}

export interface InteraccionInmueble {
  id: number
  codigo: string
  titulo: string
  tipo: string
  zona: string
  vendedor: string | null
  contactos: ContactoInteraccion[]
}

export interface FiltrosEstadisticas {
  vendedorIds?: number[]
  desde?: string
  hasta?: string
  tipoInmuebleIds?: number[]
  ciudadIds?: number[]
  zona?: string
  operacion?: 'venta' | 'alquiler'
  precioMin?: string
  precioMax?: string
  estados?: string[]
  codigo?: string
  nivel?: NivelInteraccion
  agrupacion?: Agrupacion
}

export const obtenerEstadisticas = (filtros: FiltrosEstadisticas) =>
  api.get<Estadisticas>(`/api/estadisticas${aQuery(filtros)}`)

export const obtenerInteraccionInmueble = (id: number | string, nivel?: string) =>
  api.get<InteraccionInmueble>(`/api/estadisticas/inmuebles/${id}${aQuery({ nivel })}`)

export const exportarInforme = (filtros: FiltrosEstadisticas, formato: 'xlsx' | 'pdf', secciones: SeccionInforme[]) =>
  descargar(`/api/estadisticas/exportar${aQuery({ ...filtros, formato, secciones })}`, `informe-estadisticas.${formato}`)
