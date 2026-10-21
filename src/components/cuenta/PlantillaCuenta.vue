<template>
  <!-- Estructura comun de las pantallas de cuenta (mismo diseño que sign-in.vue): encabezado, imagen y menu desplegable.
       El formulario de cada pantalla va en el slot -->
  <div class="layout-container">
    <header class="CommonHeader">
      <div class="logo">
        <img src="/icon.png" alt="Icono" class="icon" />
        <img src="/brand.png" alt="Rifat Richani" class="brand" />
      </div>

      <nav class="primary-nav">
        <button @click="toggleMenu" class="menu-btn">
          <span>{{ isMenuOpen ? 'Cerrar' : 'Menu' }}</span>
        </button>
        <button @click="botonCuenta" class="inc-btn">
          <span>{{ esUsuario ? 'Mi cuenta' : 'Iniciar Sesión' }}</span>
        </button>
      </nav>
    </header>

    <main class="main-content">
      <div class="vision-container">
        <article class="image-article">
          <div class="image-4">
            <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
          </div>
        </article>
      </div>

      <div class="form-section" :class="{ 'con-scroll': scroll }">
        <h1 class="form-title">{{ titulo }}</h1>
        <slot />
      </div>
    </main>

    <div class="menu-container" :class="{ 'open': isMenuOpen }">
      <header class="navbar">
        <button @click="toggleMenu" class="nav-link">Salir</button>
        <img src="/brand2.png" alt="Rifat Richani" class="brand" />
        <button @click="botonCuenta" class="inc-btn">
          <span>{{ esUsuario ? 'Mi cuenta' : 'Iniciar Sesión' }}</span>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { esUsuario } from '@/services/sesion'

defineProps({
  titulo: { type: String, required: true },
  // true en pantallas con varios formularios (Mi cuenta)
  scroll: { type: Boolean, default: false },
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

const botonCuenta = () => ir(esUsuario.value ? '/cuenta' : '/signin')
</script>

<style scoped src="../sign-in-style.css"></style>
<style scoped src="./cuenta-style.css"></style>
