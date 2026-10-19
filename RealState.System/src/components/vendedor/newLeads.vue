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
          <li class="menu-item active">
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
          <li class="menu-item">
            <button class="btn-menu" @click="profile">Perfil</button>
          </li>
        </ul>
      </nav>
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
    
      <section>
        <div class="form-container">
          <!-- Switch de opciones -->
          <div class="switch">
            <button 
              :class="['btn-action', { active: activeSection === 'usuario' }]"
              @click="activeSection = 'usuario'">
              Datos del Cliente
            </button>
            <button 
              :class="['btn-action', { active: activeSection === 'inmueble' }]"
              @click="activeSection = 'inmueble'">
              Datos del Inmueble
            </button>
          </div>

          <!-- Formulario Usuario -->
          <div v-if="activeSection === 'usuario'" class="form-section">
            <div class="group">
              <label>Nombre</label>
              <input type="text" placeholder="Rifat" />
            </div>

            <div class="group">
              <label>Apellido</label>
              <input type="text" placeholder="Richani" />
            </div>

            <div class="group">
              <label>Nro de Teléfono</label>
              <input type="text" placeholder="414-496577" />
            </div>

            <div class="group">
              <label>Inmueble deseado</label>
              <input type="text" placeholder="ID: ET5652" />
            </div>

            <div class="group full-width">
              <label>Gmail</label>
              <input type="text" placeholder="ej: ejemplo@gmail.com" />
            </div>
          </div>

          <!-- Formulario Inmueble (Añadido) -->
          <div v-if="activeSection === 'inmueble'" class="inmueble-grid">
            <!-- Columna 1: Foto y Usuario Vinculado -->

            <!-- Círculo de la foto -->
            <div class="profile-avatar" @click="seleccionarFoto">
              <img v-if="fotoUrl" :src="fotoUrl" alt="Foto de perfil" class="avatar-img" />
              <div v-else class="avatar-placeholder">
                <span>Subir foto</span>
              </div>
            </div>

              <!-- Botón para quitar la foto (solo se muestra si hay foto) -->
            <div class="img-btn">
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
            </div>
              <!-- Columna 2: Tipo, Código RRBR, Dirección -->
              <div class="col-middle">
                <div class="group">
                  <label>Tipo</label>
                  <div class="select-wrapper">
                    <select>
                      <option value="compra">Compra</option>
                      <option value="venta">Venta</option>
                    </select>
                    <span class="chevron">&#x203A;</span>
                  </div>
                </div>
              </div>

              <div class="group">
                <label>Codigo RRBR</label>
                <input type="text" placeholder="ej: 414-496577" />
              </div>

              <div class="group">
                <label>Direccion</label>
                <input type="text" placeholder="Informacion" />
              </div>

            <!-- Columna 3: Inmueble, Precio, Extra -->
            <div class="col-right">
              <div class="group">
                <label>Inmueble</label>
                <div class="select-wrapper">
                  <select>
                    <option value="apartamento">Apartamento</option>
                    <option value="casa">Casa</option>
                    <option value="local">Local</option>
                  </select>
                    <span class="chevron">&#x203A;</span>
                </div>
              </div>

              <div class="group">
                <label>Precio</label>
                <input type="text" placeholder="ej: 50000$" />
              </div>

              <div class="group">
                <label>Extra</label>
                <div class="select-wrapper">
                  <select>
                    <option value="planta-piscina">Planta, Piscina</option>
                  </select>
                    <span class="chevron">&#x203A;</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <button class="btn-añadir">
        Editar
        <svg class="icon-chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </main>
  </div>
</template>

<script setup>

import {useRouter} from 'vue-router'
import{ref} from 'vue'
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

const profile = ()=> {
  router.push('/profile')
}

const activeSection = ref('usuario')

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

<style scoped src= "./newLeads.css"></style>