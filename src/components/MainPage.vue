<template>
  <div class="page1">
    <!-- primera pagina header -->
    <header class="CommonHeader">
      <div class="logo">
        <img src="/icon.png" alt="Icono" class="icon" />
        <img src="/brand.png" alt="Rifat Richani" class="brand" />
      </div>
       <!-- arreglar -->
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

    <!-- Sección principal -->
      <section class="background">
        <!-- Fondo 1 -->
        <div class="imagen">
          <img src="/PhotoPrincipal.png" alt="foto de portada" />
        </div>

        <!-- Menú flotante -->
        <aside class="side-menu">
          <button class="icon-btn toggle-btn" @click="toggleSidemenu">
            <img src="/arrow.png" alt="flecha" class="icon-arrow" :class="{ 'rotated': !isOpen }"/>
          </button>
          <div class="extra-btn" :class="{ 'is-closed': !isOpen }">
            <a href="tel:+584145936070" class="icon-btn">
              <img src="/phone.png" alt="telefono" class="icon-phone" />
            </a>
            <a href="mailto:rrbrventas01@gmail.com" class="icon-btn">
              <img src="/gmail.png" alt="gmail" class="icon-gmail" />
            </a>
            <a href="https://wa.me/584145936070" target="_blank" rel="noopener noreferrer" class="icon-btn">
              <img src="/whatsapp.svg" alt="whatsapp" class="icon-whatsapp" />
            </a>
          </div>
        </aside>

        <!-- Barra de filtros -->
        <div class="filter-bar">
          <div class="filter-group">
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

          <div class="filter-group">
            <label for="tipo">Tipo de Propiedad</label>
            <select id="tipo" name="tipo">
              <option value="casa">Casa</option>
              <option value="apartamento">Apartamento</option>
              <option value="townhouse">Townhouse</option>
              <option value="terreno">Terreno</option>
              <option value="local comercial">Local comercial</option>
              <option value="galpon">Galpon</option>
              <option value="oficina">Oficina</option>
              <option value="consultorio medico">Consultorio medico</option>
              <option value="club">Club</option>
              <option value="negocio">Negocio</option>
            </select>
          </div>

          <div class="filter-group">
            <label for="precio">Precio</label>
            <input 
            class="filter-input"
              type="number" 
              id="precio" 
              min="0"
              max="100000000"
              name="precio" 
              placeholder="Ej: 15000"
            />
          </div>

          <div class="filter-group">
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
  </div>
  
      <!-- DESTACADOS 2DA PAGINA -->
      <section class="featured-section">
        <!-- Encabezado de la sección -->
        <header class="section-header">
          <h2>Destacados</h2>
          <p>Selecciona tu locación</p>
        </header>

        <!-- Contenedor del Grid/Carrusel de tarjetas -->
        <div class="cards-grid">
          <p v-if="cargando" class="estado-msg">Cargando propiedades...</p>
          <p v-else-if="error" class="estado-msg">No se pudieron cargar las propiedades: {{ error }}</p>
          <p v-else-if="destacados.length === 0" class="estado-msg">Aún no hay propiedades publicadas.</p>

          <article v-for="inmueble in destacados" :key="inmueble.id" class="card">
            <div class="card-image-wrapper">
              <img :src="urlImagen(inmueble.urlImagenPrincipal)" :alt="inmueble.titulo" class="building-img" />
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="/location.png" alt="Ubicación" />
                <span>{{ inmueble.ciudad || 'Carabobo' }}</span>
              </div>

              <!-- PENDIENTE: favoritos requiere inicio de sesion (JWT) -->
              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">{{ inmueble.titulo }}</h3>

              <div class="card-details">
                <span>{{ inmueble.tipoInmueble }}</span>
              </div>

              <div class="card-footer">
                <span class="card-price">{{ formatearPrecio(inmueble.precio) }}</span>
                <router-link :to="`/details/${inmueble.id}`" class="details-btn">Ver Detalles &gt;</router-link>
              </div>
            </div>
          </article>
        </div>

        <!-- Botón de Navegación del Carrusel -->
        <button class="next-btn" aria-label="Siguiente">
          <img src="/arrow.png" alt="Siguiente" />
        </button>
      </section>

      <!-- PÁGINA 3 -->
       <section class="info-section">
        <div class="section-content">
          <h1 class="principal-text">24 Años Transformando Espacios</h1>
          
          <p class="little-text">
            "Brindar asesoría inmobiliaria de alto nivel en la compra, venta y alquiler de propiedades, resguardando el patrimonio de nuestros clientes con transparencia, criterio estratégico y una estricta seguridad jurídica, garantizando que cada operación sea una decisión rentable y segura."
          </p>
        </div>
      </section>

  <!-- PÁGINA 4-->
  <section class = "page-4">
        <div class="vision-container">
          <article class="image-article">
            <div class="image-4">
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>
          </article>
          <article class="text-article">
            <h1 class="text-4">Nuestra vision</h1>
            <p class="text-little-4">
            "Consolidarnos como la firma inmobiliaria referente y de mayor confianza en, reconocida por nuestra trayectoria, innovación y excelencia en la gestión personalizada de activos residenciales y comerciales en las zonas de mayor valorización en el estado."
            </p>
          </article>
        </div>
  </section>
  <!-- PÁGINA 5-->

  <section class="container">
    <!-- Banner Superior de datos -->
    <div class="banner">
      <div class="stat-item">
        <div class="stat-number">600+</div>
        <div class="stat-label">VIVIENDAS</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">24+</div>
        <div class="stat-label">AÑOS APOYANDO A CARABOBO</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">350+</div>
        <div class="stat-label">CLIENTES</div>
      </div>
    </div>

    <!-- Sección why us? -->
    <div class="content">
      <!-- Imagen de la persona -->
      <div class="profile-image">
        <img src="/profile.png" alt="Perfil" class="profile-img" />
      </div>

      <!-- Texto descriptivo -->
      <div class="info-text">
        <h2 class="section-title">¿Por qué nosotros?</h2>
        <p class="description">
          Rifat Richani is an artful mosaic of luxury residences that blend
          minimalist details with modern living. A serene community where
          thoughtful design and exceptional amenities invites you to embrace an
          elevated lifestyle in Long Island City.
        </p>

        <!-- Botón flotante opcional (si aplica en la sección) -->
      <button class="next-btn" aria-label="Siguiente">
          <img src="/arrow.png" alt="Siguiente" />
      </button>
      </div>
    </div>
  </section>
  

  <!-- PÁGINA 6-->
  <footer class="page-6">
    <!-- boton superior derecho-->
    <!--contenido central-->
    <div class="footer-content">
      <div class="logo">
        <img src="/icon.png" alt="Rifat Richani Bienes Raices" class="footer-brand-img" />
      </div>
      <!-- Direccion-->
      <p class="footer-address">
        C.C.P. Otama, urb. El Viñedo, PB<br />
        Local 10, Valencia, Carabobo
      </p>
      <!--Titulo de contacto-->

      <h3 class="contact-title">CONTACTANOS</h3>
      <!--Iconos de redes sociales-->
      <div class="social-icons">
        <a href="https://x.com/rifatrichanibr"><img src="/x.png" alt="X (Twitter)" /></a>
        <a href="https://www.instagram.com/rifatrichanibienesraices?stkn=c2YydWl4c3Q4anNr"><img src="/inst.png" alt="Instagram" /></a>
        <a href="https://www.facebook.com/share/19A5MuKs4X/?mibextid=wwXIfr"><img src="/facebook.png" alt="Facebook" /></a>
        <a href="https://wa.me/584145936070"><img src="/whatsapp.png" alt="WhatsApp"></a>
      </div>


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
</footer>

</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { useFiltros } from '@/composables/useFiltros'
import { mensajeDeError } from '@/services/api'
import { buscarInmuebles } from '@/services/inmuebles'
import { formatearPrecio, urlImagen } from '@/utils/formato'

const router = useRouter()
const { ciudades, tiposInmueble, errorCatalogos, filtros, buscar } = useFiltros()

// Destacados: las 4 propiedades disponibles mas recientes
const destacados = ref([])
const cargando = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    destacados.value = (await buscarInmuebles({ tamano: 4 })).contenido
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
})

const isFavorite = ref(false)
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
}


const Search = ()=> {
  router.push('/search')
}

const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const Home = ()=> {
  router.push('/')
}

const Contact = ()=> {
  router.push('/contacts')
}

const signIn = ()=> {
  router.push('/signin')
}

const isOpen = ref(true) // Estado para controlar la visibilidad del menú
const toggleSidemenu = () => {
  isOpen.value = !isOpen.value // Cambia el estado al hacer clic
}
// Lógica de JavaScript / Vue (si es necesaria)


</script>

<style scoped src= "./style.css"></style>
<style scoped>


/* Misma apariencia que .inc-btn del encabezado, fijo arriba a la derecha */
.inc-flotante {
  position: fixed;
  /* A la altura del boton "Menu" del encabezado */
  top: 46px;
  right: 40px;
  z-index: 60;
  box-shadow: 0 8px 20px rgba(11, 31, 70, 0.3);
}

.flotante-enter-active,
.flotante-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.flotante-enter-from,
.flotante-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 600px) {
  .primary-nav {
    margin-right: 150px;
  }

  .inc-flotante {
    top: 12px;
    right: 16px;
    font-size: 15px;
  }
}
</style>