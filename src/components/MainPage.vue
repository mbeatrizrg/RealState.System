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
          <transition name="fade">
            <img 
              :key="imagenActual" 
              :src="imagenesFondo[imagenActual]" 
              alt="foto de portada" 
            />
          </transition> 
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
       <!-- Nivel Superior: Pestañas -->
           <div class="filter-tabs">
             <input
                type="radio"
                id="modalidad-compra"
                name="modalidad"
                value="compra"
                v-model="modalidad"
                class="hidden-input"
             />
             <label for="modalidad-compra" class="tab-label">Comprar</label>
             <input
                type="radio"
                id="modalidad-alquiler"
                name="modalidad"
                value="alquiler"
                v-model="modalidad"
                class="hidden-input"
             />
             <label for="modalidad-alquiler" class="tab-label">Alquilar</label>
           </div>

           <div class="filter-inputs">
        <!-- Locación / Ciudades -->
              <div class="filter-group">
                <label for="locacion">Locación</label>
                <select id="locacion" name="locacion" v-model="ciudadSeleccionada">
                  <option value="">Todas las ciudades</option>
                    <option v-for="ciudad in ciudades" :key="ciudad.id" :value="ciudad.id">
                    {{ ciudad.nombre }}
                  </option>
                </select>
              </div>
      
             <div class="divider"></div>
             <!-- Tipo de Propiedad -->
             <div class="filter-group">
               <label for="tipo">Tipo de Propiedad</label>
               <select id="tipo" name="tipo" v-model="tipoSeleccionado">
                 <option value="">Todos los tipos</option>
                 <option v-for="tipo in tiposInmueble" :key="tipo.id" :value="tipo.id">
                   {{ tipo.nombre }}
                 </option>
               </select>
             </div>

             <div class="divider"></div>

            <button @click="Search" class="search-btn">
              <img src="/lupa.svg" alt="" />
              <span>Buscar Propiedad</span>
            </button>
           </div>
        </div>
      </section>
    </div>
  
    <!-- DESTACADOS 2DA PAGINA -->
    <section class="featured-section">
      <!-- Encabezado de la sección -->
      <header class="section-header">
        <h2>Destacados</h2>
          <p></p>
        </header>

        <!-- Contenedor del Grid/Carrusel de tarjetas -->
        <div class="cards-grid">
          <p v-if="cargando" class="estado-msg">Cargando propiedades...</p>
          <p v-else-if="error" class="estado-msg">No se pudieron cargar las propiedades: {{ error }}</p>
          <p v-else-if="destacados.length === 0" class="estado-msg">Aún no hay propiedades publicadas.</p>

          <article v-for="inmueble in destacadosVisibles" :key="inmueble.id" class="card">
            <div class="card-image-wrapper">
              <img :src="urlImagen(inmueble.urlImagenPrincipal)" :alt="inmueble.titulo" class="building-img" />
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="/location.png" alt="Ubicación" />
                <span>{{ inmueble.ciudad || 'Carabobo' }}</span>
              </div>

              <h3 class="card-title">{{ inmueble.titulo }}</h3>

              <div class="card-details">
                <span class="card-rooms" v-if="inmueble.habitaciones != null || inmueble.banos != null">
                {{ inmueble.habitaciones || 0 }} habitaciones <template v-if="inmueble.banos != null">| {{ inmueble.banos }} baños</template>
                </span>
                <span class="card-area" v-if="inmueble.metrosCuadrados != null">
                {{ Number(inmueble.metrosCuadrados).toLocaleString('es-VE') }} m² Totales
                </span>
              </div>

              <div class="card-footer">
                <span class="card-price">{{ formatearPrecio(inmueble.precio) }}</span>
                <router-link :to="`/details/${inmueble.id}`" class="details-btn">Ver Detalles &gt;</router-link>
              </div>
            </div>
          </article>
        </div>

        <!-- Botón de Navegación del Carrusel -->
        <button class="next-btn" aria-label="Siguiente" @click="siguienteDestacado">
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
            <h1 class="text-4">Nuestra visión</h1>
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
  </section>
  
 <!--
    ==========================
    <div class="content">
      
      <div class="profile-image">
        <img src="/profile.png" alt="Perfil" class="profile-img" />
      </div>

      <div class="info-text">
        <h2 class="section-title">¿Por qué nosotros?</h2>
        <p class="description">
          Rifat Richani is an artful mosaic of luxury residences that blend
          minimalist details with modern living. A serene community where
          thoughtful design and exceptional amenities invites you to embrace an
          elevated lifestyle in Long Island City.
        </p>

      <button class="next-btn" aria-label="Siguiente">
          <img src="/arrow.png" alt="Siguiente" />
      </button>
      </div>
    </div>
  </section>
=========================
-->

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
          <!--favoritos
          <a href="#" class="menu-item">Favoritos</a> -->
          <button @click="Contact" class="menu-item"><span>Contactanos</span></button> 
        </nav>
      </main>
    </div>
  </div>
</footer>

</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useFiltros } from '@/composables/useFiltros'
import { mensajeDeError } from '@/services/api'
import { buscarInmuebles } from '@/services/inmuebles'
import { formatearPrecio, urlImagen } from '@/utils/formato'

const router = useRouter()
const { ciudades, tiposInmueble, errorCatalogos, filtros, buscar } = useFiltros()

// Destacados: propiedades disponibles para rotar en el carrusel (4 visibles a la vez)
const destacados = ref([])
const indiceCarrusel = ref(0)
const TARJETAS_VISIBLES = 4
const cargando = ref(true)
const error = ref('')

const destacadosVisibles = computed(() => {
  const total = destacados.value.length
  if (total <= TARJETAS_VISIBLES) {
    return destacados.value
  }
  const resultado = []
  for (let i = 0; i < TARJETAS_VISIBLES; i++) {
    resultado.push(destacados.value[(indiceCarrusel.value + i) % total])
  }
  return resultado
})

const siguienteDestacado = () => {
  const total = destacados.value.length
  if (total <= TARJETAS_VISIBLES) return
  indiceCarrusel.value = (indiceCarrusel.value + 1) % total
}

const modalidad = ref('compra')
const ciudadSeleccionada = ref('')
const tipoSeleccionado = ref('')

onMounted(async () => {
  try {
    destacados.value = (await buscarInmuebles({ tamano: 20 })).contenido
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
})

// Envía los parámetros a la ruta /search
const Search = () => {
  const query = {}

  // modalidad: 'alquiler' -> isAlquiler = true, 'compra' -> isAlquiler = false
  query.isAlquiler = modalidad.value === 'alquiler' ? 'true' : 'false'

  if (ciudadSeleccionada.value) {
    query.ciudadId = ciudadSeleccionada.value
  }

  if (tipoSeleccionado.value) {
    query.tipoInmuebleId = tipoSeleccionado.value
  }

  // Redirecciona enviando los parametros en la URL
  // Ejemplo: /search?isAlquiler=false&ciudadId=1&tipoInmuebleId=3
  router.push({ path: '/search', query })
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

const isOpen = ref(true) 
const toggleSidemenu = () => {
  isOpen.value = !isOpen.value // Cambia el estado al hacer clic
}

const imagenesFondo = ref([
  '/PhotoPrincipal.png',
  '/building.png', // Reemplaza con los nombres reales de tus fotos
  '/building2.png'
])
const imagenActual = ref(0)
let temporizadorFondo = null

onMounted(() => {
  // Cambia la imagen cada 4 segundos (4000 milisegundos)
  temporizadorFondo = setInterval(() => {
    imagenActual.value = (imagenActual.value + 1) % imagenesFondo.value.length
  }, 4000)
})

onUnmounted(() => {
  // Limpia el temporizador si el usuario sale de la página
  if (temporizadorFondo) clearInterval(temporizadorFondo)
})

// Lógica de JavaScript / Vue (si es necesaria)

</script>

<style scoped src= "./main.css"></style>
<style scoped>


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
