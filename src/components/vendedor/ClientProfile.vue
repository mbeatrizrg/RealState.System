<template>
  <div class="leads-page">
    <!-- Barra lateral (Sidebar) -->
    <aside class="sidebar">
      <div class="logo2-container">
        <img src="/Logo2.png" alt="Icono2" class="logo" />
      </div>

      <nav class="menu">
        <ul>
          <li class="menu-item">
            <button class="btn-menu" @click="vendedor">Inventario</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="contactos">Contactos</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="$router.push('/negocios')">Negocios</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="$router.push('/inmuebles')">Inmuebles</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="$router.push('/tareas')">Tareas</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="$router.push('/estadisticas')">Estadísticas</button>
          </li>
          <li class="menu-item active">
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

      <BotonCerrarSesion />
    </aside>

    <!-- Contenido Principal -->
    <main class="main-content">
      <header class="header">
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
            placeholder="Buscar usuarios"
            class="search-input">
        </div>
      </section>
    </main>
    </div>
</template>


<script setup>
import BotonCerrarSesion from './BotonCerrarSesion.vue'
import {useRouter} from 'vue-router'

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

</script>

<style scoped src= "./clientProfile.css"></style>