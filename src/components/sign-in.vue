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
        <button @click="Menu" class="menu-btn">
          <span>Menu</span>
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
            <label for="phone">teléfono</label>
            <input 
              type="tel" 
              id="phone" 
              placeholder="+58 414-555 5555" 
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
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router'

const router = useRouter()

const Menu = ()=> {
  router.push('/menu')
}

const Create = ()=> {
  router.push('/create')
}


// Lógica de JavaScript / Vue (si es necesaria)




</script>
<style scoped src= "./sign-in-style.css"></style>