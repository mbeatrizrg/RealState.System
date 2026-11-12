<template>
  <div class="home-search">
    <!-- Header Principal -->
    <header class="CommonHeader">
      <div class="logo">
        <img src="/icon.png" alt="Icono" class="icon" />
        <img src="/brand.png" alt="Rifat Richani" class="brand" />
      </div>
      
      <nav class="primary-nav">
        <!-- cambiar para que se vea como el search btn -->
        <button @click="toggleMenu" class="menu-btn">
          <span>{{ isMenuOpen ? 'Cerrar' : 'Menu' }}</span>
        </button> 
      </nav>
    </header>
    <!-- Barra de Filtros -->
     <div class="filter-bar">
      <FilterBar />
     </div>
      <!-- Grid de Tarjetas de Propiedades -->
      <section class="properties-grid">
        <p v-if="cargando" class="estado-msg">Buscando propiedades...</p>
        <p v-else-if="error" class="estado-msg">No se pudo realizar la búsqueda: {{ error }}</p>
        <p v-else-if="inmuebles.length === 0" class="estado-msg">No encontramos propiedades con esos filtros.</p>
        <p v-else class="estado-msg resumen">{{ totalElementos }} {{ totalElementos === 1 ? 'propiedad encontrada' : 'propiedades encontradas' }}</p>

        <article v-for="inmueble in inmuebles" :key="inmueble.id" class="card2">

        <div class="img-card">
          <img :src="urlImagen(inmueble.urlImagenPrincipal)" :alt="inmueble.titulo" class="building2-img" />
        </div>

        <div class="content2">
          <div class="location-card">
            <img src="/location.png" alt="Ubicación" />
            <span>{{ inmueble.ciudad || 'Carabobo' }}</span>
          </div>

            <h3 class="card-title">{{ inmueble.titulo }}</h3>

          <div class="details-card">
    <span class="card-rooms" v-if="inmueble.habitaciones != null || inmueble.banos != null">
        {{ inmueble.habitaciones || 0 }} {{ inmueble.habitaciones == 1 ? 'habitación' : 'habitaciones' }} 
        <template v-if="inmueble.banos != null">
            | {{ inmueble.banos }} {{ inmueble.banos == 1 ? 'baño' : 'baños' }}
        </template>
    </span>
    <span class="card-area" v-if="inmueble.metrosCuadrados != null">
        {{ Number(inmueble.metrosCuadrados).toLocaleString('es-VE') }} m² Totales
    </span>
</div>

        <div class="footer-card">
          <span class="card-price">{{ formatearPrecio(inmueble.precio) }}</span>
          <router-link :to="`/details/${inmueble.id}`" class="details-btn">Ver Detalles &gt;</router-link>
        </div>
        </div>
      </article>
          
      </section>

      <Paginacion
        v-if="!cargando && !error"
        :pagina="pagina"
        :total-paginas="totalPaginas"
        @cambiar="irAPagina"
      />

        <div class="menu-container" :class="{ 'open': isMenuOpen }">
    <!-- Navegación superior -->
    <header class="navbar">
      <button @click="toggleMenu" class="nav-link">
        Salir
      </button>
      <img src="/brand2.png" alt="Rifat Richani" class="brand" />
 
    </header>

    <!-- Menú principal centrado -->
    <main class="menu-content">
      <nav class="menu-list">
        <button @click="Home" class="menu-item"><span>Principal</span></button> 
        <button @click="Search" class="menu-item active"><span>Disponibles</span></button> 
        <!--favoritos
        <a href="#" class="menu-item">Favoritos</a> -->
        <button @click="Contact" class="menu-item"><span>Contactanos</span></button> 
      </nav>
    </main>
  </div>

  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import Paginacion from '@/components/Paginacion.vue'
import { useFiltros } from '@/composables/useFiltros'
import { mensajeDeError } from '@/services/api'
import { buscarInmuebles } from '@/services/inmuebles'
import { formatearPrecio, urlImagen } from '@/utils/formato'

const route = useRoute()
const router = useRouter()
const { pagina, leerFiltrosDeUrl, filtrosParaApi, irAPagina } = useFiltros()

const inmuebles = ref([])
const totalPaginas = ref(0)
const totalElementos = ref(0)
const cargando = ref(false)
const error = ref('')

// Cada vez que cambian los filtros o la pagina en la URL se repite la busqueda
watch(() => route.query, async () => {
  leerFiltrosDeUrl()
  cargando.value = true
  error.value = ''
  try {
    const resultado = await buscarInmuebles(filtrosParaApi())
    inmuebles.value = resultado.contenido
    totalPaginas.value = resultado.totalPaginas
    totalElementos.value = resultado.totalElementos
    window.scrollTo?.({ top: 0, behavior: 'smooth' })
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
}, { immediate: true })

const Home = ()=> {
  router.push('/')
}
const Search = ()=> {
  router.push('/search')
}

const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const Contact = ()=> {
  router.push('/contacts')
}

// Lógica de JavaScript / Vue (si es necesaria)
</script>

<style scoped src= "./search.css"></style>
