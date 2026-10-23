<template>
  <!-- Al pie de la barra lateral del CRM: cierra la sesion del empleado y vuelve a la pagina principal -->
  <div class="cerrar-sesion">
    <p v-if="sesion.nombre" class="usuario" :title="sesion.nombre">{{ sesion.nombre }}</p>
    <button type="button" class="btn-salir" :disabled="saliendo" @click="salir">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12H3m0 0l4-4m-4 4l4 4M10 5V4a1 1 0 011-1h8a1 1 0 011 1v16a1 1 0 01-1 1h-8a1 1 0 01-1-1v-1" />
      </svg>
      {{ saliendo ? 'Cerrando...' : 'Cerrar sesión' }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { cerrarSesion } from '@/services/auth'
import { sesion } from '@/services/sesion'

const router = useRouter()
const saliendo = ref(false)

// El backend invalida el token; aunque falle (sin conexion) la sesion se borra del navegador igual
async function salir() {
  saliendo.value = true
  await cerrarSesion()
  saliendo.value = false
  router.push('/')
}
</script>

<style scoped>
.cerrar-sesion {
  margin-top: auto;
  padding: 24px 24px 32px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
}

.usuario {
  margin: 0 0 10px;
  font-size: 13px;
  color: #c7d5f0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-salir {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  background: transparent;
  color: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.35);
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 15px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-salir:hover:not(:disabled) {
  background-color: rgba(255, 255, 255, 0.08);
}

.btn-salir:disabled {
  opacity: 0.6;
  cursor: default;
}

.btn-salir svg {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .cerrar-sesion {
    margin-top: 0;
    padding: 8px 16px 12px;
    border-top: none;
  }

  .usuario {
    display: none;
  }

  .btn-salir {
    width: auto;
  }
}
</style>
