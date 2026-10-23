<template>
  <!-- Barra lateral del CRM (mismas opciones que menu-vendedor.vue) -->
  <aside class="sidebar">
    <div class="logo2-container">
      <img src="/Logo2.png" alt="Icono2" class="logo" />
    </div>

    <nav class="menu">
      <ul>
        <li v-for="opcion in opciones" :key="opcion.texto" :class="['menu-item', { active: opcion.texto === activo }]">
          <button class="btn-menu" @click="router.push(opcion.ruta)">{{ opcion.texto }}</button>
        </li>
      </ul>
    </nav>

    <BotonCerrarSesion />
  </aside>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

import BotonCerrarSesion from './BotonCerrarSesion.vue'

defineProps<{ activo: string }>()

const router = useRouter()

// Comunicaciones y Calendario aun no tienen pantalla propia: igual que en el resto del CRM llevan a Leads
const opciones = [
  { texto: 'Inventario', ruta: '/vendedor' },
  { texto: 'Contactos', ruta: '/contact' },
  { texto: 'Negocios', ruta: '/negocios' },
  { texto: 'Inmuebles', ruta: '/inmuebles' },
  { texto: 'Tareas', ruta: '/tareas' },
  { texto: 'Estadísticas', ruta: '/estadisticas' },
  { texto: 'Leads', ruta: '/leads' },
  { texto: 'Comunicaciones', ruta: '/leads' },
  { texto: 'Control', ruta: '/control' },
  { texto: 'Calendario', ruta: '/leads' },
  { texto: 'Perfil', ruta: '/profile' },
]
</script>

<style scoped>
.sidebar {
  width: 260px;
  flex-shrink: 0;
  background-color: #0b1f46;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  padding-top: 50px;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}

.logo2-container {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
  width: 90px;
  height: 90px;
  padding-left: 80px;
  padding-bottom: 30px;
}

.menu ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  padding: 16px 32px;
  font-size: 16px;
  transition: background-color 0.2s ease;
}

.menu-item.active {
  background-color: #004cb5;
  border-radius: 0 4px 4px 0;
}

.menu-item:hover:not(.active) {
  background-color: rgba(255, 255, 255, 0.05);
  cursor: pointer;
}

.btn-menu {
  background: transparent;
  border: none;
  font-size: 17px;
  cursor: pointer;
  color: #ffffff;
  display: block;
  width: 100%;
  text-align: left;
}

@media (max-width: 900px) {
  .sidebar {
    width: 100%;
    padding-top: 16px;
    border-radius: 0;
  }

  .logo2-container {
    display: none;
  }

  .menu ul {
    flex-direction: row;
    overflow-x: auto;
    gap: 0;
  }

  .menu-item {
    padding: 12px 16px;
    white-space: nowrap;
  }
}
</style>
