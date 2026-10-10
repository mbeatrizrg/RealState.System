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
            <select id="locacion" name="locacion">
              <option value="" disabled selected>Ciudades</option>
              <option value="valencia">Valencia</option>
              <option value="naguanagua">Naguanagua</option>
              <option value="san_diego">San Diego</option>
              <option value="puerto_cabello">Puerto Cabello</option>
              <option value="guacara">Guacara</option>
              <option value="los_guayos">Los Guayos</option>
              <option value="tocuyito">Tocuyito (Libertador)</option>
              <option value="san_joaquin">San Joaquín</option>
              <option value="diego_ibarra">Mariara (Diego Ibarra)</option>
              <option value="bejuma">Bejuma</option>
              <option value="montalban">Montalbán</option>
              <option value="miranda">Miranda</option>
              <option value="carlos_arvelo">Güigüe (Carlos Arvelo)</option>
              <option value="juan_jose_mora">Morón (Juan José Mora)</option>
            </select>
          </div>

          <div class="filter-group2">
            <label for="tipo">Tipo de Propiedad</label>
            <select id="tipo" name="tipo">
              <option value="casa">Casa</option>
              <option value="apartamento">Apartamento</option>
              <option value="townhouse">Townhouse</option>
              <option value="terreno">Terreno</option>
              <option value="local comercial">local comercial</option>
              <option value="galpon">Galpon</option>
              <option value="oficina">Oficina</option>
              <option value="consultorio medico">Consultorio medico</option>
              <option value="club">Club</option>
              <option value="negocio">Negocio</option>
            </select>
          </div>

          <div class="filter-group2">
            <label for="precio">Precio</label>
            <input
            class="filter-input2"
              type="number" 
              id="precio" 
              min="0"
              max="100000000"
              name="precio" 
              placeholder="Ej: 15000"
              />
          </div>

          <div class="filter-group2">
            <label for="modalidad">Alquiler o Compra</label>
            <select id="modalidad" name="modalidad">
              <option value="compra">Compra</option>
              <option value="alquiler">Alquiler</option>
            </select>
          </div>

          <button @click="Search" class="search-btn">
            <img src="/lupa.svg" alt="" />
            <span>Buscar Propiedad</span>
          </button>
        </div> 
      </section>
    </main>
      <!-- Grid de Tarjetas de Propiedades -->
      <section class="properties-grid">
        <!-- Tarjeta 1 -->
        <article class="card2" v-for="property in properties" :key="property.id">
            <div class="img-card">
              <img :src="property.image" :alt="property.title" class="building2-img" />
            </div>

            <div class="content2">
              <div class="location-card">
                <img src="/location.png" alt="Ubicación" />
                <span>{{ property.location }}</span>
              </div>

              <button class="favorite-btn" :class="{ 'active': isFavorite }" @click="property.isFavorite = !property.isFavorite" aria-label="Añadir a favoritos">
                <img :src="property.isFavorite ? '/filledheart.png' : '/heart.png'" alt="Favorito" />
              </button>

              <h3 class="card-title">{{ property.title }}</h3>

              <div class="details-card">
                <span>{{ property.bedrooms }} habitaciones | {{ property.bathrooms }} baños</span>
                <span>{{ property.area }} m² Totales</span>
              </div>

              <div class="footer-card">
                <span class="card-price">${{ property.price.toLocaleString() }}</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
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
        <button @click="Home" class="menu-item"><span>Principal</span></button> 
        <button @click="Search" class="menu-item active"><span>Disponibles</span></button> 
        <a href="#" class="menu-item">Favoritos</a>
        <a href="#" class="menu-item">Contactanos</a>
      </nav>
    </main>
  </div>


  </div>
</template>

<script setup>
import { ref } from 'vue';
import {useRouter} from 'vue-router'

const router = useRouter()

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
    image: '/building2.png',
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
    image: '/building2.png',
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
    image: '/building2.png',
    isFavorite: false,
  },
  {
    id: 4,
    title: 'Residencias Altos Del Mirador',
    location: 'Valencia, Via Guataparo',
    bedrooms: 4,
    bathrooms: 3,
    area: 67,
    price: 150000,
    image: '/building2.png',
    isFavorite: false,
  },
])

const Search = ()=> {
  router.push('/search')
}

const Home = ()=> {
  router.push('/')
}

const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const signIn = ()=> {
  router.push('/signin')
}
// Lógica de JavaScript / Vue (si es necesaria)
</script>

<style scoped src= "./style2.css"></style>