<template>
  <div class="crm-vendedor">
    <!-- Barra lateral (Sidebar) -->
    <aside class="sidebar">
      <div class="logo2-container">
        <img src="/Logo2.png" alt="Icono2" class="logo" />
      </div>

      <nav class="menu">
        <ul>
          <!-- Elemento activo resaltado -->
          <li class="menu-item active">
            <button class="btn-menu" @click="vendedor">Inventario</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="contactos">Contactos</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Leads</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Comunicaciones</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="control">Control</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Calendario</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="profile">Perfil</button>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- Contenido Principal -->
    <main class="main-content">
      <!-- Cabecera con saludo y botón de filtros -->
      <header class="header">
        <h1 class="greeting">Hola, {{ sesion.nombre }}</h1>
        <button class="btn-filtros" :aria-expanded="mostrarFiltros" @click="mostrarFiltros = !mostrarFiltros">
          {{ filtrosActivos ? `Filtros (${filtrosActivos})` : 'Filtros' }}
          <svg class="icon-chevron" :class="{ abierto: mostrarFiltros }" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </header>

      <!-- Barra de búsqueda: busca en título, dirección, descripción, código RRBR y ciudad -->
      <section class="search-section">
        <div class="search-input-wrapper">
          <svg class="icon-search" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            v-model="filtros.q"
            type="search"
            maxlength="100"
            placeholder="Buscar inmuebles por título, dirección, ciudad o código RRBR"
            class="search-input"
            @input="buscarConPausa"
            @keyup.enter="buscarDesdeInicio"
          >
        </div>
      </section>

      <!-- Panel de filtros -->
      <section v-if="mostrarFiltros" class="filtros-panel">
        <div class="filtro">
          <label for="f-ciudad">Ciudad</label>
          <select id="f-ciudad" v-model="filtros.ciudadId" @change="buscarDesdeInicio">
            <option value="">Todas</option>
            <option v-for="ciudad in ciudades" :key="ciudad.id" :value="String(ciudad.id)">{{ ciudad.nombre }}</option>
          </select>
        </div>
        <div class="filtro">
          <label for="f-tipo">Tipo de inmueble</label>
          <select id="f-tipo" v-model="filtros.tipoInmuebleId" @change="buscarDesdeInicio">
            <option value="">Todos</option>
            <option v-for="tipo in tiposInmueble" :key="tipo.id" :value="String(tipo.id)">{{ tipo.nombre }}</option>
          </select>
        </div>
        <div class="filtro">
          <label for="f-modalidad">Modalidad</label>
          <select id="f-modalidad" v-model="filtros.modalidad" @change="buscarDesdeInicio">
            <option value="">Venta y alquiler</option>
            <option value="compra">Venta</option>
            <option value="alquiler">Alquiler</option>
          </select>
        </div>
        <div class="filtro">
          <label for="f-estado">Estado</label>
          <select id="f-estado" v-model="filtros.vendido" @change="buscarDesdeInicio">
            <option value="">Todos</option>
            <option value="false">Disponibles</option>
            <option value="true">Vendidos</option>
          </select>
        </div>
        <div class="filtro">
          <label for="f-min">Precio mínimo</label>
          <input id="f-min" v-model="filtros.minPrecio" type="number" min="0" placeholder="0" @change="buscarDesdeInicio" />
        </div>
        <div class="filtro">
          <label for="f-max">Precio máximo</label>
          <input id="f-max" v-model="filtros.maxPrecio" type="number" min="0" placeholder="Sin límite" @change="buscarDesdeInicio" />
        </div>
        <div class="filtro">
          <label for="f-orden">Ordenar por</label>
          <select id="f-orden" v-model="filtros.orden" @change="buscarDesdeInicio">
            <option value="recientes">Más recientes</option>
            <option value="precio_asc">Menor precio</option>
            <option value="precio_desc">Mayor precio</option>
          </select>
        </div>
        <label class="filtro-check">
          <input v-model="filtros.soloMios" type="checkbox" @change="buscarDesdeInicio" />
          Solo mis inmuebles
        </label>
        <button class="btn-limpiar" @click="limpiarFiltros">Limpiar filtros</button>
      </section>

      <section>
        <p v-if="cargando" class="estado-msg">Cargando inventario...</p>
        <p v-else-if="error" class="estado-msg">No se pudo cargar el inventario: {{ error }}</p>
        <p v-else-if="inmuebles.length === 0" class="estado-msg">No hay inmuebles que coincidan con la búsqueda.</p>

       <!-- Grid de tarjetas -->
        <div class="cards-grid">
            <article class="card" v-for="inmueble in inmuebles" :key="inmueble.id">
            <div class="card-image-wrapper">
              <img :src="urlImagen(inmueble.urlImagenPrincipal)" :alt="inmueble.titulo" class="building-img" />
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="/location.png" alt="Ubicación" />
                <span>{{ inmueble.ciudad }}</span>
              </div>

              <h3 class="card-title">{{ inmueble.titulo }}</h3>

              <div class="card-details">
                <span>{{ inmueble.tipoInmueble }}</span>
                <span :class="['estatus', { vendido: inmueble.isVendido }]">{{ inmueble.estatusInmueble }}</span>
              </div>

              <div class="card-footer">
                <span class="card-price">{{ formatearPrecio(inmueble.precio) }}</span>
                <div class="card-acciones">
                  <router-link v-if="puedeEditar(inmueble)" :to="`/control?inmueble=${inmueble.id}`" class="details-btn secundario">Fotos</router-link>
                  <router-link :to="`/details/${inmueble.id}`" class="details-btn">Ver Detalles &gt;</router-link>
                </div>
              </div>
            </div>
          </article>

        </div>

        <Paginacion
          v-if="!cargando && !error"
          :pagina="pagina"
          :total-paginas="totalPaginas"
          :total-elementos="totalElementos"
          @cambiar="cargar"
        />
      </section>

    </main>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import Paginacion from '@/components/Paginacion.vue'
import { modalidadAIsAlquiler } from '@/composables/useFiltros'
import { mensajeDeError } from '@/services/api'
import { obtenerCiudades, obtenerTiposInmueble } from '@/services/catalogos'
import { buscarInmueblesCrm } from '@/services/inmuebles'
import { esAdmin, sesion } from '@/services/sesion'
import { formatearPrecio, urlImagen } from '@/utils/formato'

const router = useRouter()

const Leads = ()=> {
  router.push('/leads')
}

const vendedor = ()=> {
  router.push('/vendedor')
}

const contactos = ()=> {
  router.push('/contact')
}

const control = ()=> {
  router.push('/control')
}

const profile = ()=> {
  router.push('/profile')
}

const TAMANO_PAGINA = 8

const ciudades = ref([])
const tiposInmueble = ref([])
const mostrarFiltros = ref(false)

const filtrosVacios = () => ({
  q: '',
  ciudadId: '',
  tipoInmuebleId: '',
  modalidad: '',
  vendido: '',
  minPrecio: '',
  maxPrecio: '',
  orden: 'recientes',
  soloMios: false,
})
const filtros = reactive(filtrosVacios())

// Cuantos filtros del panel estan aplicados (se muestra en el boton "Filtros")
const filtrosActivos = computed(() =>
  ['ciudadId', 'tipoInmuebleId', 'modalidad', 'vendido', 'minPrecio', 'maxPrecio'].filter((clave) => filtros[clave] !== '').length
  + (filtros.soloMios ? 1 : 0) + (filtros.orden !== 'recientes' ? 1 : 0)
)

const inmuebles = ref([])
const pagina = ref(1)
const totalPaginas = ref(0)
const totalElementos = ref(0)
const cargando = ref(false)
const error = ref('')
let ultimaPeticion = 0

// numeroPagina en base 1. Si llegan respuestas fuera de orden solo se usa la de la ultima peticion
async function cargar(numeroPagina = 1) {
  const peticion = ++ultimaPeticion
  cargando.value = true
  error.value = ''
  try {
    const resultado = await buscarInmueblesCrm({
      q: filtros.q.trim(),
      ciudadId: filtros.ciudadId,
      tipoInmuebleId: filtros.tipoInmuebleId,
      isAlquiler: modalidadAIsAlquiler(filtros.modalidad),
      vendido: filtros.vendido,
      minPrecio: filtros.minPrecio,
      maxPrecio: filtros.maxPrecio,
      orden: filtros.orden === 'recientes' ? '' : filtros.orden,
      soloMios: filtros.soloMios || undefined,
      pagina: numeroPagina - 1,
      tamano: TAMANO_PAGINA,
    })
    if (peticion !== ultimaPeticion) return
    inmuebles.value = resultado.contenido
    pagina.value = resultado.pagina + 1
    totalPaginas.value = resultado.totalPaginas
    totalElementos.value = resultado.totalElementos
  } catch (e) {
    if (peticion === ultimaPeticion) error.value = mensajeDeError(e)
  } finally {
    if (peticion === ultimaPeticion) cargando.value = false
  }
}

const buscarDesdeInicio = () => cargar(1)

// Mientras se escribe se espera a que el usuario haga una pausa antes de consultar
let pausa
function buscarConPausa() {
  clearTimeout(pausa)
  pausa = setTimeout(buscarDesdeInicio, 350)
}
onBeforeUnmount(() => clearTimeout(pausa))

function limpiarFiltros() {
  Object.assign(filtros, filtrosVacios())
  buscarDesdeInicio()
}

// El backend vuelve a validar: solo el vendedor encargado o un administrador puede cambiar las fotos
const puedeEditar = (inmueble) => esAdmin.value || inmueble.empleadoId === sesion.id

onMounted(() => {
  cargar(1)
  Promise.all([obtenerCiudades(), obtenerTiposInmueble()])
    .then(([listaCiudades, listaTipos]) => {
      ciudades.value = listaCiudades
      tiposInmueble.value = listaTipos
    })
    .catch(() => {})
})

</script>

<style scoped src= "./vendedor.css"></style>
