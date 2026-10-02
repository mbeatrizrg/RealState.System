<template>
  <div class="menu-container">
    <!-- Navegación superior -->
    <header class="navbar">
      <router-link to="/" class="nav-link">Salir</router-link>
      <img src="/brand2.png" alt="Rifat Richani" class="brand" />
      <button @click="signIn" class="inc-btn">
          <span>Iniciar Sesión</span>
      </button> 
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

<script setup>
import {useRouter} from 'vue-router'
import SignIn from './sign-in.vue'

const router = useRouter()
const signIn = ()=> {
  router.push('/signin')
}
// Lógica de JavaScript / Vue (si es necesaria)
</script>

<style scoped src= "./menu-style.css"></style>