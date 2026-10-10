<template>
  <div class="layout-container">
    <!-- Encabezado[cite: 1] -->
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
        <button @click="Create" class="inc-btn">
          <span>Crear cuenta</span>
        </button>

      </nav>
    </header>

    <!-- Contenido principal-->
    <main class="main-content">
      <div class="vision-container">
          <article class="image-article">
            <div class="image-4">
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>
          </article>
        </div>

      <div class="form-section">
        <h1 class="form-title">Iniciar sesion</h1>

        <form class="login-form">
          <div class="form-group">
            <label for="gmail">Gmail</label>
            <input 
              type="email" 
              id="gmail" 
              placeholder="example@gmail.com" 
            />
          </div>

          <div class="form-group">
            <label for="password">Cotraseña</label>
            <input 
              type="password" 
              id="password" 
              placeholder="8 caracteres" 
            />
          </div>

          <button type="submit" class="btn btn-submit">Iniciar sesion</button>
        </form>
      </div>
    </main>


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
        <button @click="Search" class="menu-item"><span>Disponibles</span></button> 
        <a href="#" class="menu-item">Favoritos</a>
        <a href="#" class="menu-item">Contactanos</a>
      </nav>
    </main>
  </div>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router'
import { ref } from 'vue';
const router = useRouter()

const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}
const signIn = ()=> {
  router.push('/signin')
}

const Create = ()=> {
  router.push('/create')
}

const Home = ()=> {
  router.push('/')
}
const Search = ()=> {
  router.push('/search')
}

// Lógica de JavaScript / Vue (si es necesaria)




</script>
<style scoped src= "./sign-in-style.css"></style>