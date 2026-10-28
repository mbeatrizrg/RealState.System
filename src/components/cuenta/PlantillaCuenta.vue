<template>
  <!-- Estructura comun de las pantallas de cuenta (login, registro, verificar, recuperar, CRM y Mi cuenta):
       encabezado, tarjeta con la imagen de la marca a la izquierda y el formulario (slot) a la derecha, y menu desplegable -->
  <div class="layout-container">
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

    <main class="main-content">
      <section class="auth-card" :class="{ ancha }">
        <aside class="vision-container" aria-hidden="true">
          <img src="/PhotoPrincipal.png" alt="" class="vision-img" />
          <div class="vision-texto">
            <img src="/brand2.png" alt="" class="vision-marca" />
            <p class="vision-frase">Encuentra el lugar donde empieza tu próxima historia.</p>
            <ul class="vision-lista">
              <li>Propiedades verificadas en Carabobo</li>
              <li>Guarda tus favoritas y recibe asesoría</li>
              <li>Atención personalizada de nuestros asesores</li>
            </ul>
          </div>
        </aside>

        <div class="form-section" :class="{ 'con-scroll': scroll }">
          <h1 class="form-title">{{ titulo }}</h1>
          <p v-if="subtitulo" class="form-subtitulo">{{ subtitulo }}</p>
          <slot />
        </div>
      </section>
    </main>

    <div class="menu-container" :class="{ 'open': isMenuOpen }">
      <header class="navbar">
        <button @click="toggleMenu" class="nav-link">Salir</button>
        <img src="/brand2.png" alt="Rifat Richani" class="brand" />
        <button @click="ir(accionCabecera.ruta)" class="inc-btn">
          <span>{{ accionCabecera.texto }}</span>
        </button>
      </header>

      <main class="menu-content">
        <nav class="menu-list">
          <button @click="ir('/')" class="menu-item active"><span>Principal</span></button>
          <button @click="ir('/search')" class="menu-item"><span>Disponibles</span></button>
          <a href="#" class="menu-item">Favoritos</a>
          <button @click="ir('/contacts')" class="menu-item"><span>Contactanos</span></button>
        </nav>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'

import { esUsuario } from '@/services/sesion'

const props = defineProps({
  titulo: { type: String, required: true },
  subtitulo: { type: String, default: '' },
  // true en pantallas con varios formularios (Mi cuenta)
  scroll: { type: Boolean, default: false },
  // Formularios con mas campos (registro): la tarjeta es mas ancha
  ancha: { type: Boolean, default: false },
  // Boton del encabezado, ej. { texto: 'Crear cuenta', ruta: '/create' }. Por defecto "Mi cuenta" o "Iniciar Sesión"
  accion: { type: Object, default: null },
})

const router = useRouter()

const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const ir = (ruta) => {
  isMenuOpen.value = false
  router.push(ruta)
}

const accionCabecera = computed(() =>
  props.accion ?? (esUsuario.value ? { texto: 'Mi cuenta', ruta: '/cuenta' } : { texto: 'Iniciar Sesión', ruta: '/signin' })
)

const signIn = ()=> {
  router.push('/signin')
}

</script>

<style scoped src="../sign-in-style.css"></style>
<style scoped src="./cuenta-style.css"></style>
