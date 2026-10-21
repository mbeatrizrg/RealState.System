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
          <li class="menu-item active">
            <button class="btn-menu" @click="Leads">Leads</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="comunication">Comunicaciones</button>
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
    </aside>

    <!-- Contenido Principal -->
    <main class="main-content">
      <!-- Cabecera con saludo y botón de filtros -->
      <header class="header">
        <!-- Usamos interpolación de Vue para el nombre dinámico -->
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
            class="search-input"
          >
        </div>
        <div class="table-container">
        <table class="user-table">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>ID</th>
                    <th>Estatus</th>
                    <th>Inmueble ID</th>
                </tr>
            </thead>
            <tbody>
            <tr>
              <td>
                <router-link to="/clientProfile" class="link-page">
                  Elena
                </router-link>
              </td>
              <td>167CD</td>
              <td><span class="badge in-progress">En progreso</span></td>
              <td>190V2</td>
            </tr>
            <tr>
              <td>
                <router-link to="/clientProfile" class="link-page">
                Elena
                </router-link>
              </td>
              <td>167CD</td>
              <td><span class="badge finished">Finalizada</span></td>
              <td>2OED5</td>
            </tr>
            <tr>
              <td>
                <router-link to="/clientProfile" class="link-page">
                Elena
                </router-link>
              </td>
              <td>167CD</td>
              <td><span class="badge starting">Iniciando</span></td>
              <td>U876J</td>
            </tr>
            <tr>
              <td>
                <router-link to="/clientProfile" class="link-page">
                Elena
                </router-link>
              </td>
              <td>167CD</td>
              <td><span class="badge in-progress">En progreso</span></td>
              <td>B3EF</td>
            </tr>
            <tr>
                <td>
                <router-link to="/clientProfile" class="link-page">
                Elena
                </router-link>
              </td>
                <td>167CD</td>
                <td><span class="badge in-progress">En progreso</span></td>
                <td>235TD</td>
            </tr>
            <tr>
              <td>
                <router-link to="/clientProfile" class="link-page">
                Elena
                </router-link>
              </td>
                <td>167CD</td>
                <td><span class="badge in-progress">En progreso</span></td>
                <td>GSC56</td>
            </tr>
          </tbody>
        </table>
        </div>
    </section>

    </main>
</div>
</template>

<script setup>
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
const comunication = ()=> {
  router.push('/comunication')
}

const profile = ()=> {
  router.push('/profile')
}

const clientProfile = ()=> {
  router.push('/clientProfile')
}


</script>

<style scoped src= "./Leads.css"></style>