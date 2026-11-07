<template>
  <div class="leads-page">
    <MenuCrm activo="Contactos" />

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
import MenuCrm from './MenuCrm.vue'
</script>

<style scoped src= "./clientProfile.css"></style>