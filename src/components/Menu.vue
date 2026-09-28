<template>
  <div class="menu-container">
    <!-- Navegación superior -->
    <header class="navbar">
      <router-link to="/" class="nav-link">Salir</router-link>
      <img src="/brand2.png" alt="Rifat Richani" class="brand" />
      <button class="nav-link">Iniciar Sesion</button>
    </header>

    <!-- Menú principal centrado -->
    <main class="menu-content">
      <nav class="menu-list">
        <a href="#" class="menu-item active">Principal</a>
        <a href="#" class="menu-item">Disponibles</a>
        <a href="#" class="menu-item">Favoritos</a>
        <a href="#" class="menu-item">Contactanos</a>
      </nav>
    </main>
  </div>
</template>


<style scoped src= "./menu-style.css"></style>