<template>
  <div class="leads-page">
    <!-- Barra lateral (Sidebar) -->
    <aside class="sidebar">
      <div class="logo2-container">
        <img src="/Logo2.png" alt="Icono2" class="logo" />
      </div>

      <nav class="menu">
        <ul>
          <!-- Elemento activo resaltado -->
          <li class="menu-item">
            <button class="btn-menu" @click="vendedor">Inventario</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="contactos">Contactos</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Leads</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Comunicaciones</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Control</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Calendario</button>
          </li>
          <li class="menu-item active">
            <button class="btn-menu" @click="profile">Perfil</button>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- Contenido Principal -->
    <main class="main-content">
      <!-- Cabecera con saludo y botón de filtros -->
      <div class="header-top">
        <!-- Usamos interpolación de Vue para el nombre dinámico -->
        <div class="profile-avatar"></div>
        <button class="btn-filtros">
          Filtros
          <svg class="icon-chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>

      <h1 class="greeting"> Hola, {{ datosPerfil.saludo }}</h1>
      <section class="profile-form">
        <!--Fila 1-->
        <div class="form-row">
          <div class="form-group full-width">
            <label>Nombre</label>
            <input 
              type="text" 
              v-model="datosPerfil.nombre" 
              class="input-dark" 
              placeholder="Rifat Richaniß" 
              readonly
            />
          </div>
        </div>

        <!--FIla 2-->
        <div class="form-row">
          <div class="form-group short-width">
            <label>ID</label>
            <input
              type="text"  
              class="input-dark" 
              placeholder="ID: ET5652" 
              class="input-dark"/>
          </div>
          <div class="form-group flex-grow">
              <label for="gmail">Gmail</label>
            <input
              type="email"  
              placeholder="ejemplo@gmail.com"
              id="gmail" 
              class="input-dark"
            />
        </div>
      </div>

      <!--Fila 3-->
      <div class="form-row">
          <div class="form-group short-width">
            <label>Telefono</label>
            <input 
              type="text" 
              id="phone" 
              placeholder="414-496577" 
              class="input-dark" 
            />
          </div>

          <div class="form-group short-width">
            <label for="password">Cotraseña</label>
            <input
              class="input-dark" 
              type="password" 
              id="password" 
              placeholder="8 caracteres" 
            />
          </div>
        </div>
      </section>
    </main>
  </div>
</template>


<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// datos iniciales
const datosPerfil = ref({
  saludo: 'rifat',
  nombre: 'Buscar inmuebles',
  id: '12345',
  gmail: 'rifatrichani@gmail.com',
  telefono: '0412-12345678',
  password: 'Scott'
})

const Leads = () => {
  router.push('/leads')
}

const vendedor = () => {
  router.push('/vendedor')
}

const contactos = () => {
  router.push('/contact')
}

const profile = () => {
  router.push('/profile')
}
</script>

<style scoped src="./profile.css"></style>
