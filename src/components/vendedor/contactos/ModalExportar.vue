<template>
  <ModalCrm titulo="Exportar contactos" @cerrar="emit('cerrar')">
    <fieldset class="grupo">
      <legend>Formato</legend>
      <label :class="['opcion', { marcada: formato === 'xlsx' }]">
        <input v-model="formato" type="radio" value="xlsx" />
        <span><strong>Excel</strong> (.xlsx) para filtrar y editar</span>
      </label>
      <label :class="['opcion', { marcada: formato === 'pdf' }]">
        <input v-model="formato" type="radio" value="pdf" />
        <span><strong>PDF</strong> para imprimir o compartir</span>
      </label>
    </fieldset>

    <fieldset class="grupo">
      <legend>Contactos</legend>
      <label :class="['opcion', { marcada: alcance === 'todos' }]">
        <input v-model="alcance" type="radio" value="todos" />
        <span>Todos los resultados con los filtros actuales ({{ totalElementos }})</span>
      </label>
      <label :class="['opcion', { marcada: alcance === 'pagina' }]">
        <input v-model="alcance" type="radio" value="pagina" />
        <span>Solo la página actual ({{ filasPagina }})</span>
      </label>
      <p v-if="alcance === 'todos' && totalElementos > MAXIMO" class="aviso">
        Se pueden exportar como máximo {{ MAXIMO }} contactos. Agrega filtros o exporta solo la página actual.
      </p>
    </fieldset>

    <fieldset class="grupo">
      <legend>Columnas</legend>
      <label class="opcion">
        <input v-model="todasLasColumnas" type="checkbox" />
        <span>Incluir todas las columnas (no solo las visibles)</span>
      </label>
      <p class="columnas">{{ titulosColumnas }}</p>
    </fieldset>

    <p v-if="error" class="error" role="alert">{{ error }}</p>

    <template #pie>
      <button type="button" class="btn-secundario" @click="emit('cerrar')">Cancelar</button>
      <button type="button" class="btn-primario" :disabled="exportando || bloqueado" @click="exportar">
        {{ exportando ? 'Generando...' : 'Exportar' }}
      </button>
    </template>
  </ModalCrm>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

import ModalCrm from './ModalCrm.vue'
import { mensajeDeError } from '@/services/api'
import { COLUMNAS, exportarContactos, type ClaveColumna, type FiltrosContactos } from '@/services/contactos'

// ContactoService.MAXIMO_EXPORTACION en el backend
const MAXIMO = 5000

const props = defineProps<{
  filtros: FiltrosContactos
  columnas: ClaveColumna[]
  totalElementos: number
  filasPagina: number
}>()
const emit = defineEmits<{ cerrar: [] }>()

const formato = ref<'xlsx' | 'pdf'>('xlsx')
const alcance = ref<'todos' | 'pagina'>('todos')
const todasLasColumnas = ref(false)
const exportando = ref(false)
const error = ref('')

const columnasExportadas = computed(() => (todasLasColumnas.value ? COLUMNAS.map((c) => c.clave) : props.columnas))
const titulosColumnas = computed(() =>
  columnasExportadas.value.map((clave) => COLUMNAS.find((c) => c.clave === clave)?.titulo).join(' · '),
)
const bloqueado = computed(() => alcance.value === 'todos' && props.totalElementos > MAXIMO)

async function exportar() {
  exportando.value = true
  error.value = ''
  try {
    await exportarContactos(props.filtros, {
      formato: formato.value,
      alcance: alcance.value,
      columnas: columnasExportadas.value,
    })
    emit('cerrar')
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    exportando.value = false
  }
}
</script>

<style scoped>
.grupo {
  border: none;
  margin: 0 0 16px;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.grupo legend {
  font-size: 13px;
  font-weight: 700;
  color: #0b1f46;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-bottom: 8px;
}

.opcion {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border: 1px solid #e3e8f4;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.opcion.marcada {
  border-color: #004cb5;
  background-color: #f0f4fc;
}

.opcion input {
  accent-color: #004cb5;
}

.columnas {
  margin: 0;
  font-size: 13px;
  color: #636363;
  line-height: 1.5;
}

.aviso {
  margin: 0;
  font-size: 13px;
  color: #92400e;
}

.error {
  margin: 0;
  color: #991b1b;
  font-size: 14px;
}
</style>
