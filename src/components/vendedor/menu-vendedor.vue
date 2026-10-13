<template>
  <div class="dashboard-layout">
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
            <button class="btn-menu" @click="Leads">Control</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Calendario</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Perfil</button>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- Contenido Principal -->
    <main class="main-content">
      <!-- Cabecera con saludo y botón de filtros -->
      <header class="header">
        <!-- Usamos interpolación de Vue para el nombre dinámico -->
        <h1 class="greeting">Hola, nombreUsuario </h1>
        <button class="btn-filtros">
          Filtros
          <svg class="icon-chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </header>

      <!-- Barra de búsqueda -->
      <section class="search-section">
        <div class="search-input-wrapper">
          <svg class="icon-search" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            type="text"
            placeholder="Buscar inmuebles"
            class="search-input"
          >
        </div>
      </section>
      <section>

       <!-- Contenedor del Grid/Carrusel de tarjetas -->
        <div class="cards-grid">
            <article class="card" v-for="property in properties" :key="property.id">
            <div class="card-image-wrapper">
              <img :src="property.image" :alt="property.title" class="building-img" />
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="/location.png" alt="Ubicación" />
                <span>{{ property.location }}</span>
              </div>

              <button class="favorite-btn" :class="{ 'active': isFavorite }" @click="property.isFavorite = !property.isFavorite" aria-label="Añadir a favoritos">
                <img :src="property.isFavorite ? '/filledheart.png' : '/heart.png'" alt="Favorito" />
              </button>

              <h3 class="card-title">{{ property.title }}</h3>

              <div class="card-details">
                <span>{{ property.bedrooms }} habitaciones | {{ property.bathrooms }} baños</span>
                <span>{{ property.area }} m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">${{ property.price }}</span>
                <button @click="HomeDetails" class="details-btn">
                  <span>Ver Detalles &gt;</span>
                </button> 
              </div>
            </div>
          </article>

        </div>

        <!-- Botón de Navegación del Carrusel -->
        <button class="next-btn" aria-label="Siguiente">
          <img src="/arrow.png" alt="Siguiente" />
        </button>
      </section>

    </main>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router'
import {ref} from 'vue'

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

const isFavorite = ref(false)
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
}
const properties = ref([
  {
    id: 1,
    title: 'Residencias Altos Del Mirador',
    location: 'Valencia, Via Guataparo',
    bedrooms: 4,
    bathrooms: 3,
    area: 67,
    price: 150000,
    image: '/building.png',
    isFavorite: false,
  },
  {
    id: 2,
    title: 'Residencias Altos Del Mirador',
    location: 'Valencia, Via Guataparo',
    bedrooms: 4,
    bathrooms: 3,
    area: 67,
    price: 150000,
    image: '/building.png',
    isFavorite: false,
  },
  {
    id: 3,
    title: 'Residencias Altos Del Mirador',
    location: 'Valencia, Via Guataparo',
    bedrooms: 4,
    bathrooms: 3,
    area: 67,
    price: 150000,
    image: '/building.png',
    isFavorite: false,
  }
])


</script>

<style scoped src= "./vendedor.css"></style>