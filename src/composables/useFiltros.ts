import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { mensajeDeError } from '@/services/api'
import { obtenerCiudades, obtenerTiposInmueble, type Catalogo } from '@/services/catalogos'
import type { FiltrosBusqueda, Orden } from '@/services/inmuebles'

export const TAMANO_PAGINA = 12

export function modalidadAIsAlquiler(modalidad: string): boolean | null {
  if (modalidad === 'alquiler') return true
  if (modalidad === 'compra') return false
  return null
}

const ciudades = ref<Catalogo[]>([])
const tiposInmueble = ref<Catalogo[]>([])
const errorCatalogos = ref('')

const filtros = reactive({
  q: '',
  ciudadId: '',
  tipoInmuebleId: '',
  minPrecio: '',
  maxPrecio: '',
  orden: 'recientes' as Orden,
  isAlquiler: false 
})

// Pagina actual
const pagina = ref(1)

export function useFiltros() {
  const route = useRoute()
  const router = useRouter()

  onMounted(async () => {
    // Evitamos volver a consultar la API de catálogos si ya están llenos
    if (ciudades.value.length === 0) {
      try {
        const [listaCiudades, listaTipos] = await Promise.all([obtenerCiudades(), obtenerTiposInmueble()])
        ciudades.value = listaCiudades
        tiposInmueble.value = listaTipos
      } catch (error) {
        errorCatalogos.value = mensajeDeError(error)
      }
    }
  })

  function leerFiltrosDeUrl() {
    const valor = (clave: string) => {
      const dato = route.query[clave]
      return typeof dato === 'string' ? dato : ''
    }
    filtros.q = valor('q')
    filtros.ciudadId = valor('ciudadId')
    filtros.tipoInmuebleId = valor('tipoInmuebleId')
    filtros.minPrecio = valor('minPrecio')
    filtros.maxPrecio = valor('maxPrecio')
    

    if (route.query.isAlquiler !== undefined) {
      filtros.isAlquiler = route.query.isAlquiler === 'true'
    }

    filtros.orden = (['precio_asc', 'precio_desc'].includes(valor('orden')) ? valor('orden') : 'recientes') as Orden
    pagina.value = Math.max(1, Number.parseInt(valor('pagina'), 10) || 1)
  }

  function filtrosParaApi(): FiltrosBusqueda {
    return {
      q: filtros.q.trim(),
      ciudadId: filtros.ciudadId,
      tipoInmuebleId: filtros.tipoInmuebleId,
      minPrecio: filtros.minPrecio,
      maxPrecio: filtros.maxPrecio,
      isAlquiler: filtros.isAlquiler,
      orden: filtros.orden === 'recientes' ? '' : filtros.orden,
      pagina: pagina.value - 1,
      tamano: TAMANO_PAGINA,
    }
  }

  function navegar(numeroPagina: number) {
    const query: Record<string, string> = {}
    Object.entries(filtros).forEach(([clave, valor]) => {
      const texto = String(valor ?? '').trim()
      if (texto !== '' && !(clave === 'orden' && texto === 'recientes')) query[clave] = texto
    })
    if (numeroPagina > 1) query.pagina = String(numeroPagina)
    router.push({ path: '/search', query })
  }

  const buscar = () => navegar(1)
  const irAPagina = (numeroPagina: number) => navegar(numeroPagina)

  function limpiarFiltros() {
    Object.assign(filtros, { q: '', ciudadId: '', tipoInmuebleId: '', minPrecio: '', maxPrecio: '', orden: 'recientes', isAlquiler: false })
    navegar(1)
  }

  return { ciudades, tiposInmueble, errorCatalogos, filtros, pagina, leerFiltrosDeUrl, filtrosParaApi, buscar, irAPagina, limpiarFiltros }
}