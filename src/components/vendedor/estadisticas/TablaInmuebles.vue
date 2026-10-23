<template>
  <section class="tarjeta-panel">
    <header class="panel-cabecera">
      <h2>Detalle por inmueble <span class="contador">{{ filas.length }}</span></h2>
      <p class="nota">Clic en un inmueble para ver qué contactos interactuaron y qué hicieron.</p>
    </header>
    <div class="table-container">
      <table class="user-table">
        <thead>
          <tr>
            <th>Inmueble</th><th>Tipo</th><th>Zona</th>
            <th class="num">{{ nivel ? 'Clientes en este nivel' : 'Clientes que consultaron' }}</th>
            <th class="num">Visitas realizadas</th><th class="num">Ofertas recibidas</th><th>Vendedor encargado</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="f in filas" :key="f.id" class="fila" tabindex="0" @click="emit('abrir', f.id)" @keyup.enter="emit('abrir', f.id)">
            <td>
              <span class="nombre">{{ f.codigo }}</span>
              <span class="subtexto">{{ f.titulo }}</span>
            </td>
            <td>{{ f.tipo }}</td>
            <td>{{ f.zona }}</td>
            <td class="num">{{ f.clientes }}</td>
            <td class="num">{{ f.visitas }}</td>
            <td class="num">{{ f.ofertas }}</td>
            <td :class="{ tenue: !f.vendedor }">{{ f.vendedor ?? '—' }}</td>
          </tr>
        </tbody>
      </table>
      <p v-if="!filas.length" class="estado-msg">Ningún inmueble tuvo interacción en el período con estos filtros.</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { FilaInmueble, NivelInteraccion } from '@/services/estadisticas'

// Tabla inferior del panel de estadisticas (un inmueble por fila, los mas consultados primero)
defineProps<{ filas: FilaInmueble[]; nivel: NivelInteraccion | null }>()
const emit = defineEmits<{ abrir: [id: number] }>()
</script>

<style scoped src="../contact.css"></style>
<style scoped src="./estadisticas.css"></style>
<style scoped>
.subtexto {
  display: block;
  font-size: 12px;
  color: #636363;
}
</style>
