import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { mensajeDeError } from '@/services/api'
import { obtenerCiudades, obtenerTiposInmueble, type Catalogo } from '@/services/catalogos'
import type { FiltrosBusqueda, Orden } from '@/services/inmuebles'

// Propiedades por pagina en los resultados del portal
export const TAMANO_PAGINA = 12

// Convierte la modalidad del select al filtro isAlquiler del backend ('' = ambas)
export function modalidadAIsAlquiler(modalidad: string) {
  if (modalidad === 'alquiler') return true
  if (modalidad === 'compra') return false
  return null
}

// Logica compartida de la barra de filtros (MainPage y SearchHome). Los filtros viven en la URL (/search?q=...&pagina=2)
// para que la busqueda se pueda compartir, recargar y volver atras sin perderla
export function useFiltros() {
  const route = useRoute()
  const router = useRouter()

  const ciudades = ref<Catalogo[]>([])
  const tiposInmueble = ref<Catalogo[]>([])
  const errorCatalogos = ref('')

  const filtros = reactive({
    q: '',
    ciudadId: '',
    tipoInmuebleId: '',
    minPrecio: '',
    maxPrecio: '',
    // '' (todas) | 'compra' | 'alquiler'
    modalidad: '',
    orden: 'recientes' as Orden,
  })
  // Pagina actual en base 1, como se muestra al usuario
  const pagina = ref(1)

  onMounted(async () => {
    try {
      const [listaCiudades, listaTipos] = await Promise.all([obtenerCiudades(), obtenerTiposInmueble()])
      ciudades.value = listaCiudades
      tiposInmueble.value = listaTipos
    } catch (error) {
      errorCatalogos.value = mensajeDeError(error)
    }
  })

  // Copia los filtros de la URL (/search?ciudadId=1...) a la barra
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
    filtros.modalidad = ['compra', 'alquiler'].includes(valor('modalidad')) ? valor('modalidad') : ''
    filtros.orden = (['precio_asc', 'precio_desc'].includes(valor('orden')) ? valor('orden') : 'recientes') as Orden
    pagina.value = Math.max(1, Number.parseInt(valor('pagina'), 10) || 1)
  }

  // Filtros en el formato del backend (FiltroInmuebleDTO, pagina en base 0)
  function filtrosParaApi(): FiltrosBusqueda {
    return {
      q: filtros.q.trim(),
      ciudadId: filtros.ciudadId,
      tipoInmuebleId: filtros.tipoInmuebleId,
      minPrecio: filtros.minPrecio,
      maxPrecio: filtros.maxPrecio,
      isAlquiler: modalidadAIsAlquiler(filtros.modalidad),
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

  // Nueva busqueda: siempre empieza en la primera pagina
  const buscar = () => navegar(1)
  const irAPagina = (numeroPagina: number) => navegar(numeroPagina)

  function limpiarFiltros() {
    Object.assign(filtros, { q: '', ciudadId: '', tipoInmuebleId: '', minPrecio: '', maxPrecio: '', modalidad: '', orden: 'recientes' })
    navegar(1)
  }

  return { ciudades, tiposInmueble, errorCatalogos, filtros, pagina, leerFiltrosDeUrl, filtrosParaApi, buscar, irAPagina, limpiarFiltros }
}
