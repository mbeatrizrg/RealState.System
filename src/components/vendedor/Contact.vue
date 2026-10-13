<template>
  <div class="dashboard-layout">
    <!-- Barra lateral (Sidebar) -->
    <aside class="sidebar">
      <div class="logo2-container">
        <img src="/Logo2.png" alt="Icono2" class="logo" />
      </div>

      <nav class="menu">
        <ul>
          <!-- Elemento activo resaltado -->
          <li class="menu-item">
            <a href="#">Inventario</a>
          </li>
          <li class="menu-item active">
            <a href="#">Contactos</a>
          </li>
          <li class="menu-item">
            <a href="#">Leads</a>
          </li>
          <li class="menu-item">
            <a href="#">Comunicaciones</a>
          </li>
          <li class="menu-item">
            <a href="#">Control</a>
          </li>
          <li class="menu-item">
            <a href="#">Calendario</a>
          </li>
          <li class="menu-item">
            <a href="#">Perfil</a>
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

        <table class="user-table">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>ID</th>
                    <th>Estatus</th>
                    <th>Vendedor</th>
                </tr>
            </thead>
            <tbody>
            <tr>
              <td>Elena</td>
              <td>167CD</td>
              <td><span class="badge in-progress">En progreso</span></td>
              <td>Lucas</td>
            </tr>
            <tr>
              <td>Elena</td>
              <td>167CD</td>
              <td><span class="badge finished">Finalizada</span></td>
              <td>Carlos</td>
            </tr>
            <tr>
              <td>Elena</td>
              <td>167CD</td>
              <td><span class="badge starting">Iniciando</span></td>
              <td>Lucas</td>
            </tr>
            <tr>
               <td>Elena</td>
                <td>167CD</td>
                <td><span class="badge in-progress">En progreso</span></td>
                <td>Lucrecia</td>
            </tr>
            <tr>
                <td>Elena</td>
                <td>167CD</td>
                <td><span class="badge in-progress">En progreso</span></td>
                <td>Miguel</td>
            </tr>
            <tr>
                <td>Elena</td>
                <td>167CD</td>
                <td><span class="badge in-progress">En progreso</span></td>
                <td>Carlos</td>
            </tr>
          </tbody>
        </table>
    </section>

    </main>
</div>
</template>

<script setup>
</script>

<style scoped src= "./contact.css"></style>