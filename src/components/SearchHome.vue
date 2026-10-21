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
        <button @click="signIn" class="inc-btn">
          <span>Iniciar Sesión</span>
        </button> 
      </nav>
    </header>

    <main class="bar">
      <!-- Barra de Filtros / Búsqueda -->
      <section class="background">
        <div class="filter-bar2">
          <div class="filter-group2">
            <label for="locacion">Locación</label>
            <select id="locacion" name="locacion" v-model="filtros.ciudadId">
              <option value="">Ciudades</option>
              <option v-for="ciudad in ciudades" :key="ciudad.id" :value="String(ciudad.id)">{{ ciudad.nombre }}</option>
            </select>
          </div>

          <div class="filter-group2">
            <label for="tipo">Tipo de Propiedad</label>
            <select id="tipo" name="tipo" v-model="filtros.tipoInmuebleId">
              <option value="">Todos</option>
              <option v-for="tipo in tiposInmueble" :key="tipo.id" :value="String(tipo.id)">{{ tipo.nombre }}</option>
            </select>
          </div>

          <div class="filter-group2">
            <label for="precio">Precio máximo</label>
            <input
              class="filter-input2"
              type="number"
              id="precio"
              min="0"
              max="100000000"
              name="precio"
              placeholder="Ej: 15000"
              v-model="filtros.maxPrecio"
            />
          </div>

          <div class="filter-group2">
            <label for="modalidad">Alquiler o Compra</label>
            <select id="modalidad" name="modalidad" v-model="filtros.modalidad">
              <option value="compra">Compra</option>
              <option value="alquiler">Alquiler</option>
            </select>
          </div>
          <button @click="buscar" class="search-btn">
            <img src="/lupa.svg" alt="" />
            <span>Buscar Propiedad</span>
          </button>
        </div>
        <p v-if="errorCatalogos" class="estado-msg">No se pudieron cargar los filtros: {{ errorCatalogos }}</p> 
      </section>
    </main>
      <!-- Grid de Tarjetas de Propiedades -->
      <section class="properties-grid">
        <p v-if="cargando" class="estado-msg">Buscando propiedades...</p>
        <p v-else-if="error" class="estado-msg">No se pudo realizar la búsqueda: {{ error }}</p>
        <p v-else-if="inmuebles.length === 0" class="estado-msg">No encontramos propiedades con esos filtros.</p>

        <article v-for="inmueble in inmuebles" :key="inmueble.id" class="card2">
            <div class="img-card">
              <img :src="inmueble.urlImagenPrincipal || IMAGEN_POR_DEFECTO" :alt="inmueble.titulo" class="building2-img" />
            </div>

            <div class="content2">
              <div class="location-card">
                <img src="/location.png" alt="Ubicación" />
                <span>{{ inmueble.ciudad || 'Carabobo' }}</span>
              </div>

              <!-- PENDIENTE: favoritos requiere inicio de sesion (JWT) -->
              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">{{ inmueble.titulo }}</h3>

              <div class="details-card">
                <span>{{ inmueble.tipoInmueble }}</span>
              </div>

              <div class="footer-card">
                <span class="card-price">{{ formatearPrecio(inmueble.precio) }}</span>
                <router-link :to="`/details/${inmueble.id}`" class="details-btn">Ver Detalles &gt;</router-link>
              </div>
            </div>
          </article>
      </section>

        <div class="menu-container" :class="{ 'open': isMenuOpen }">
    <!-- Navegación superior -->
    <header class="navbar">
      <button @click="toggleMenu" class="nav-link">
        Salir
      </button>
      <img src="/brand2.png" alt="Rifat Richani" class="brand" />
      <button @click="signIn" class="inc-btn">
          <span>Iniciar Sesión</span>
      </button> 
    </header>

    <!-- Menú principal centrado -->
    <main class="menu-content">
      <nav class="menu-list">
        <button @click="Home" class="menu-item active"><span>Principal</span></button> 
        <button @click="Search" class="menu-item"><span>Disponibles</span></button> 
        <a href="#" class="menu-item">Favoritos</a>
        <button @click="Contact" class="menu-item"><span>Contactanos</span></button> 
      </nav>
    </main>
  </div>


  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useFiltros } from '@/composables/useFiltros'
import { mensajeDeError } from '@/services/api'
import { buscarInmuebles } from '@/services/inmuebles'
import { formatearPrecio, IMAGEN_POR_DEFECTO } from '@/utils/formato'

const route = useRoute()
const router = useRouter()
const { ciudades, tiposInmueble, errorCatalogos, filtros, leerFiltrosDeUrl, filtrosParaApi, buscar } = useFiltros()

const inmuebles = ref([])
const cargando = ref(false)
const error = ref('')

// Cada vez que cambian los filtros en la URL se repite la busqueda
watch(() => route.query, async () => {
  leerFiltrosDeUrl()
  cargando.value = true
  error.value = ''
  try {
    inmuebles.value = await buscarInmuebles(filtrosParaApi())
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

const signIn = ()=> {
  router.push('/signin')
}
// Lógica de JavaScript / Vue (si es necesaria)
</script>

<style scoped src= "./style2.css"></style>
