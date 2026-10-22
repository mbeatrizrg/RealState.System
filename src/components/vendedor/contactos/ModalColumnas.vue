<template>
  <ModalCrm titulo="Editar columnas" @cerrar="emit('cerrar')">
    <p class="ayuda">Marca las columnas que quieres ver y usa las flechas para cambiar su orden. El nombre siempre se muestra.</p>
    <ol class="lista-columnas">
      <li v-for="(columna, indice) in orden" :key="columna.clave" :class="{ oculta: !visibles.has(columna.clave) }">
        <label>
          <input
            type="checkbox"
            :checked="visibles.has(columna.clave)"
            :disabled="columna.clave === 'nombre'"
            @change="alternar(columna.clave)"
          />
          {{ columna.titulo }}
        </label>
        <div class="flechas">
          <button type="button" :disabled="indice === 0" :aria-label="`Subir ${columna.titulo}`" @click="mover(indice, -1)">&uarr;</button>
          <button type="button" :disabled="indice === orden.length - 1" :aria-label="`Bajar ${columna.titulo}`" @click="mover(indice, 1)">&darr;</button>
        </div>
      </li>
    </ol>

    <template #pie>
      <button type="button" class="btn-secundario" @click="restablecer">Restablecer</button>
      <button type="button" class="btn-primario" @click="aplicar">Aplicar</button>
    </template>
  </ModalCrm>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import ModalCrm from './ModalCrm.vue'
import { COLUMNAS, COLUMNAS_POR_DEFECTO, type ClaveColumna, type Columna } from '@/services/contactos'

// Recibe las columnas visibles en su orden y devuelve la nueva seleccion al aplicar
const props = defineProps<{ seleccion: ClaveColumna[] }>()
const emit = defineEmits<{ cerrar: []; aplicar: [columnas: ClaveColumna[]] }>()

// Primero las visibles (en su orden) y despues las ocultas
function ordenInicial(seleccion: ClaveColumna[]): Columna[] {
  const visiblesPrimero = seleccion.map((clave) => COLUMNAS.find((c) => c.clave === clave)).filter((c): c is Columna => !!c)
  return [...visiblesPrimero, ...COLUMNAS.filter((c) => !seleccion.includes(c.clave))]
}

const orden = ref<Columna[]>(ordenInicial(props.seleccion))
const visibles = ref(new Set<ClaveColumna>(props.seleccion))

function alternar(clave: ClaveColumna) {
  const nuevas = new Set(visibles.value)
  if (nuevas.has(clave)) nuevas.delete(clave)
  else nuevas.add(clave)
  visibles.value = nuevas
}

function mover(indice: number, desplazamiento: number) {
  const lista = [...orden.value]
  const [columna] = lista.splice(indice, 1)
  if (!columna) return
  lista.splice(indice + desplazamiento, 0, columna)
  orden.value = lista
}

function restablecer() {
  orden.value = ordenInicial(COLUMNAS_POR_DEFECTO)
  visibles.value = new Set(COLUMNAS_POR_DEFECTO)
}

function aplicar() {
  emit('aplicar', orden.value.filter((c) => visibles.value.has(c.clave)).map((c) => c.clave))
}
</script>

<style scoped>
.ayuda {
  margin: 0 0 12px;
  font-size: 14px;
  color: #636363;
}

.lista-columnas {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.lista-columnas li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #e3e8f4;
  border-radius: 8px;
  font-size: 14px;
}

.lista-columnas li.oculta {
  color: #9aa4b8;
  background-color: #f8f9fb;
}

.lista-columnas label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.lista-columnas input {
  accent-color: #004cb5;
}

.flechas {
  display: flex;
  gap: 4px;
}

.flechas button {
  width: 30px;
  height: 30px;
  border: 1px solid #c9d2e3;
  background-color: #ffffff;
  color: #0b1f46;
  border-radius: 6px;
  cursor: pointer;
}

.flechas button:disabled {
  opacity: 0.35;
  cursor: default;
}
</style>
