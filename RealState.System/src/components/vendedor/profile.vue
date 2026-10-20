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
            <button class="btn-menu" @click="control">Control</button>
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
        <!-- Círculo de la foto -->
        <div class="profile-avatar" @click="seleccionarFoto">
          <img v-if="fotoUrl" :src="fotoUrl" alt="Foto de perfil" class="avatar-img" />
          <div v-else class="avatar-placeholder">
            <span>Subir foto</span>
          </div>
        </div>

          <!-- Botón para quitar la foto (solo se muestra si hay foto) -->
        <button 
          v-if="fotoUrl" 
          type="button" 
          class="btn-remove-photo" 
          @click="eliminarFoto"
          >
          Quitar foto
        </button>

        <!-- Input oculto para cargar archivo de imagen -->
        <input 
          ref="fileInput" 
          type="file" 
          accept="image/*" 
          style="display: none;" 
          @change="alCambiarFoto" 
          />
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
              placeholder="ID: ET5652" 
              class="input-dark"
              readonly
            />
          </div>
          <div class="form-group flex-grow">
              <label for="gmail">Gmail</label>
            <input
              type="email"  
              placeholder="rifatrichani@gmail.com"
              id="gmail" 
              class="input-dark"
              readonly
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
              placeholder="0412-12345678" 
              class="input-dark" 
              readonly
            />
          </div>

          <div class="form-group short-width">
            <label for="password">Cotraseña</label>
            <input
              class="input-dark" 
              type="password" 
              id="password" 
              placeholder="Scott" 
              readonly
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
  nombre: 'Rifat Richani',
  id: 'ET5652',
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

const control = ()=> {
  router.push('/control')
}

const profile = () => {
  router.push('/profile')
}

const fotoUrl = ref(null)
const fileInput = ref(null)

// Abrir el selector de archivos al hacer clic en el círculo
const seleccionarFoto = () => {
  fileInput.value.click()
}

// Cargar la foto seleccionada
const alCambiarFoto = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      fotoUrl.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// Quitar / borrar la foto
const eliminarFoto = () => {
  fotoUrl.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

</script>

<style scoped src="./profile.css"></style>
