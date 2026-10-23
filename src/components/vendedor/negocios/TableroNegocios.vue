<template>
  <div class="tablero" role="list" aria-label="Tablero de negocios por etapa">
    <section
      v-for="columna in columnas"
      :key="columna.etapaId"
      role="listitem"
      :class="['columna', { cerrada: !columna.abierta, destino: destino === columna.etapaId }]"
      :aria-label="`${columna.etapa}: ${columna.cantidad} negocios`"
      @dragover.prevent="alPasar(columna.etapaId)"
      @dragleave="alSalir(columna.etapaId, $event)"
      @drop.prevent="alSoltar(columna.etapaId)"
    >
      <header class="columna-cabecera">
        <span :class="['badge', claseEtapa(columna.etapaId)]">{{ columna.etapa }}</span>
        <span class="contador">{{ columna.cantidad }}</span>
      </header>

      <ol class="tarjetas">
        <li
          v-for="negocio in columna.negocios"
          :key="negocio.id"
          :class="['tarjeta', { arrastrando: arrastrado?.id === negocio.id, bloqueada: !puedeMover(negocio) }]"
          :draggable="puedeMover(negocio)"
          tabindex="0"
          @dragstart="alEmpezar(negocio, $event)"
          @dragend="limpiar"
          @click="emit('abrir', negocio)"
          @keyup.enter="emit('abrir', negocio)"
        >
          <strong class="tarjeta-nombre">{{ negocio.nombre }}</strong>
          <span class="tarjeta-valor">{{ formatearPrecio(negocio.valor) }}</span>
          <span class="tarjeta-linea">{{ negocio.codigoInmueble }} · {{ negocio.tipoInmueble }}</span>
          <span class="tarjeta-linea">{{ negocio.nombreContacto }}</span>
          <span class="tarjeta-pie">
            <span :class="{ tenue: !negocio.nombrePropietario }">{{ negocio.nombrePropietario ?? 'Sin asignar' }}</span>
            <span v-if="negocio.fechaCierreEstimada">Cierre {{ formatearFecha(negocio.fechaCierreEstimada) }}</span>
          </span>
          <!-- Alternativa al arrastre para teclado y pantallas tactiles -->
          <label v-if="puedeMover(negocio)" class="mover" @click.stop>
            <span class="sr-only">Mover {{ negocio.nombre }} a</span>
            <select
              :value="negocio.etapaId"
              @keyup.enter.stop
              @change="emit('mover', negocio, Number(($event.target as HTMLSelectElement).value))"
            >
              <option v-for="c in columnasTodas" :key="c.etapaId" :value="c.etapaId">{{ c.etapa }}</option>
            </select>
          </label>
        </li>
        <li v-if="columna.cantidad > columna.negocios.length" class="mas">
          + {{ columna.cantidad - columna.negocios.length }} más (use la vista de tabla o los filtros para verlos)
        </li>
        <li v-if="columna.cantidad === 0" class="vacia">Sin negocios</li>
      </ol>

      <footer class="columna-total">
        <span>Total</span>
        <strong>{{ formatearPrecio(columna.total) }}</strong>
      </footer>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

import { claseEtapa, type ColumnaTablero, type EtapaNegocio, type Negocio } from '@/services/negocios'
import { esAdmin, sesion } from '@/services/sesion'
import { formatearFecha } from '@/utils/fechas'
import { formatearPrecio } from '@/utils/formato'

/* Vista de tablero (pipeline): una columna por etapa con el monto total acumulado abajo. Las tarjetas se arrastran de
   una etapa a otra (o se mueven con el select); el padre llama al backend y actualiza las columnas */
const props = defineProps<{ columnas: ColumnaTablero[]; etapas: EtapaNegocio[] }>()
const emit = defineEmits<{ mover: [negocio: Negocio, etapaId: number]; abrir: [negocio: Negocio] }>()

// El select de cada tarjeta ofrece todas las etapas aunque el tablero este filtrado por algunas
const columnasTodas = computed(() =>
  props.etapas.length ? props.etapas.map((e) => ({ etapaId: e.id, etapa: e.nombre })) : props.columnas)

// Mismas reglas que el backend: propietario, negocio sin asignar o administrador
const puedeMover = (n: Negocio) => esAdmin.value || n.propietarioId === null || n.propietarioId === sesion.id

const arrastrado = ref<Negocio | null>(null)
const destino = ref<number | null>(null)

function alEmpezar(negocio: Negocio, evento: DragEvent) {
  arrastrado.value = negocio
  evento.dataTransfer?.setData('text/plain', String(negocio.id))
  if (evento.dataTransfer) evento.dataTransfer.effectAllowed = 'move'
}

function alPasar(etapaId: number) {
  if (arrastrado.value) destino.value = etapaId
}

function alSalir(etapaId: number, evento: DragEvent) {
  // dragleave tambien salta al pasar sobre una tarjeta hija: solo se limpia al salir de la columna
  const columna = evento.currentTarget as HTMLElement
  if (destino.value === etapaId && !columna.contains(evento.relatedTarget as Node)) destino.value = null
}

function alSoltar(etapaId: number) {
  const negocio = arrastrado.value
  limpiar()
  if (negocio && negocio.etapaId !== etapaId) emit('mover', negocio, etapaId)
}

function limpiar() {
  arrastrado.value = null
  destino.value = null
}
</script>

<style scoped src="../contactos/estatus.css"></style>
<style scoped>
.tablero {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: minmax(250px, 1fr);
  gap: 14px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.columna {
  display: flex;
  flex-direction: column;
  min-height: 360px;
  background-color: #e6ebf2;
  border-radius: 12px;
  border: 2px solid transparent;
  transition: border-color 0.15s, background-color 0.15s;
}

.columna.cerrada {
  background-color: #eceff3;
}

.columna.destino {
  border-color: #004cb5;
  background-color: #dbe7ff;
}

.columna-cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 12px 8px;
}

.contador {
  background-color: #ffffff;
  color: #0b1f46;
  border-radius: 999px;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 600;
}

.tarjetas {
  list-style: none;
  margin: 0;
  padding: 4px 10px 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  overflow-y: auto;
  max-height: 62vh;
}

.tarjeta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  background-color: #ffffff;
  border-radius: 10px;
  padding: 12px;
  box-shadow: 0 2px 6px rgba(11, 31, 70, 0.08);
  cursor: grab;
  font-size: 13px;
  color: #1c1c1c;
}

.tarjeta.bloqueada {
  cursor: pointer;
}

.tarjeta:focus-visible {
  outline: 2px solid #004cb5;
}

.tarjeta.arrastrando {
  opacity: 0.5;
}

.tarjeta-nombre {
  color: #0b1f46;
  font-size: 14px;
}

.tarjeta-valor {
  font-weight: 600;
  color: #004cb5;
}

.tarjeta-linea {
  color: #4b5563;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tarjeta-pie {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin-top: 4px;
  font-size: 12px;
  color: #636363;
}

.tenue {
  color: #9ca3af;
}

.mover select {
  width: 100%;
  margin-top: 6px;
  border: 1px solid #d5dbe6;
  border-radius: 6px;
  padding: 4px 6px;
  font-size: 12px;
  background-color: #f8fafc;
  color: #0b1f46;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
}

.mas,
.vacia {
  font-size: 12px;
  color: #636363;
  text-align: center;
  padding: 8px;
}

.columna-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-top: 1px solid #d5dbe6;
  font-size: 13px;
  color: #0b1f46;
}
</style>
