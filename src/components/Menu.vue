<template>
  <div class="menu-container">
    <!-- Navegación superior -->
    <header class="navbar">
      <router-link to="/" class="nav-link">Salir</router-link>
      <img src="/brand2.png" alt="Rifat Richani" class="brand" />
      <router-link to="/signin" class="nav-link">Iniciar Sesion</router-link>
    </header>

    <!-- Menú principal centrado -->
    <main class="menu-content">
      <nav class="menu-list">
        <router-link to="/" class="menu-item" exact-active-class="active">Principal</router-link>
        <router-link to="/search" class="menu-item" exact-active-class="active">Disponibles</router-link>
        <!-- PENDIENTE: pagina de favoritos (requiere inicio de sesion) -->
        <a href="#" class="menu-item">Favoritos</a>
        <a href="#" class="menu-item">Contactanos</a>
      </nav>
    </main>
  </div>
</template>


<style scoped src= "./menu-style.css"></style>
