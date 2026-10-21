import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { mensajeDeError } from '@/services/api'
import { obtenerCiudades, obtenerTiposInmueble, type Catalogo } from '@/services/catalogos'
import type { FiltrosBusqueda } from '@/services/inmuebles'

// Logica compartida de la barra de filtros (MainPage y SearchHome)
export function useFiltros() {
  const route = useRoute()
  const router = useRouter()

  const ciudades = ref<Catalogo[]>([])
  const tiposInmueble = ref<Catalogo[]>([])
  const errorCatalogos = ref('')

  const filtros = reactive({
    ciudadId: '',
    tipoInmuebleId: '',
    maxPrecio: '',
    // PENDIENTE: el backend aun no tiene el campo is_alquiler, por eso no se envia en la busqueda
    modalidad: 'compra',
  })

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
    filtros.ciudadId = valor('ciudadId')
    filtros.tipoInmuebleId = valor('tipoInmuebleId')
    filtros.maxPrecio = valor('maxPrecio')
    filtros.modalidad = valor('modalidad') || 'compra'
  }

  // Filtros en el formato del backend
  function filtrosParaApi(): FiltrosBusqueda {
    return {
      ciudadId: filtros.ciudadId,
      tipoInmuebleId: filtros.tipoInmuebleId,
      maxPrecio: filtros.maxPrecio,
    }
  }

  // Navega a la pagina de resultados con los filtros en la URL
  function buscar() {
    const query: Record<string, string> = {}
    Object.entries(filtros).forEach(([clave, valor]) => {
      if (valor !== '' && valor !== null && valor !== undefined) query[clave] = String(valor)
    })
    router.push({ path: '/search', query })
  }

  return { ciudades, tiposInmueble, errorCatalogos, filtros, leerFiltrosDeUrl, filtrosParaApi, buscar }
}
