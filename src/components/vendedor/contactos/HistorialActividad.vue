<template>
  <div class="historial">
    <div class="filtros-historial" role="group" aria-label="Filtrar actividad">
      <button type="button" :class="['chip', { activo: filtro === '' }]" @click="filtro = ''">
        Toda la actividad <span>{{ actividades.length }}</span>
      </button>
      <button
        v-for="t in tiposPresentes"
        :key="t.valor"
        type="button"
        :class="['chip', { activo: filtro === t.valor }]"
        @click="filtro = t.valor"
      >
        {{ t.texto }} <span>{{ t.cantidad }}</span>
      </button>
    </div>

    <!-- Tareas pendientes y reuniones futuras primero -->
    <section v-if="pendientes.length" class="bloque">
      <h3>Próximas y pendientes</h3>
      <ol class="eventos">
        <li v-for="a in pendientes" :key="clave(a)" :class="['evento', 'pendiente', { vencida: estaVencida(a) }]">
          <ItemActividad :actividad="a" :abierta="abiertas.has(clave(a))" :ocupado="ocupado" @alternar="alternar(a)"
                         @completar="completar(a)" @eliminar="eliminar(a)" />
        </li>
      </ol>
    </section>

    <section class="bloque">
      <h3 v-if="pendientes.length">Historial</h3>
      <ol v-if="pasadas.length" class="eventos">
        <li v-for="a in pasadas" :key="clave(a)" class="evento">
          <ItemActividad :actividad="a" :abierta="abiertas.has(clave(a))" :ocupado="ocupado" @alternar="alternar(a)"
                         @completar="completar(a)" @eliminar="eliminar(a)" />
        </li>
      </ol>
      <p v-else-if="!pendientes.length" class="vacio">{{ filtro ? 'No hay actividad de este tipo.' : vacio }}</p>
    </section>
    <p v-if="error" class="error" role="alert">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

import ItemActividad from './ItemActividad.vue'
import { eliminarActividad, marcarCompletada, TIPOS_HISTORIAL, type Actividad, type TipoHistorial } from '@/services/actividades'
import { mensajeDeError } from '@/services/api'

/* Historial continuo de actividad (correos, notas, llamadas con grabacion, reuniones y tareas). Se filtra por tipo y cada
   evento se despliega para ver el detalle y sus adjuntos. Emite "cambio" para que el padre recargue */
const props = withDefaults(defineProps<{ actividades: Actividad[]; vacio?: string }>(), {
  vacio: 'Todavía no hay actividad registrada. Usa los accesos rápidos para crear la primera.',
})
const emit = defineEmits<{ cambio: [] }>()

const filtro = ref<TipoHistorial | ''>('')
const abiertas = ref(new Set<string>())
const ocupado = ref(false)
const error = ref('')

// Las comunicaciones de los leads tienen ids de otra tabla: la clave incluye el tipo
const clave = (a: Actividad) => `${a.tipo}-${a.id}`

const tiposPresentes = computed(() =>
  TIPOS_HISTORIAL
    .map((t) => ({ ...t, cantidad: props.actividades.filter((a) => a.tipo === t.valor).length }))
    .filter((t) => t.cantidad > 0))

const filtradas = computed(() => (filtro.value ? props.actividades.filter((a) => a.tipo === filtro.value) : props.actividades))
const esPendiente = (a: Actividad) => (a.tipo === 'TAREA' || a.tipo === 'REUNION') && !a.completada
// Pendientes ordenadas por fecha programada (la mas proxima primero)
const pendientes = computed(() =>
  filtradas.value.filter(esPendiente).sort((x, y) => (x.fechaProgramada ?? '').localeCompare(y.fechaProgramada ?? '')))
const pasadas = computed(() => filtradas.value.filter((a) => !esPendiente(a)))
const estaVencida = (a: Actividad) => !!a.fechaProgramada && new Date(a.fechaProgramada).getTime() < Date.now()

function alternar(a: Actividad) {
  const nuevas = new Set(abiertas.value)
  if (nuevas.has(clave(a))) nuevas.delete(clave(a))
  else nuevas.add(clave(a))
  abiertas.value = nuevas
}

async function ejecutar(accion: () => Promise<unknown>) {
  ocupado.value = true
  error.value = ''
  try {
    await accion()
    emit('cambio')
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    ocupado.value = false
  }
}

const completar = (a: Actividad) => ejecutar(() => marcarCompletada(a.id, !a.completada))

function eliminar(a: Actividad) {
  if (!window.confirm('¿Eliminar esta actividad? También se borran sus adjuntos.')) return
  ejecutar(() => eliminarActividad(a.id))
}
</script>

<style scoped>
.historial {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.filtros-historial {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  background-color: #ffffff;
  border: 1px solid #d5dbe6;
  color: #0b1f46;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
}

.chip span {
  margin-left: 4px;
  color: #636363;
}

.chip.activo {
  background-color: #0b1f46;
  border-color: #0b1f46;
  color: #ffffff;
}

.chip.activo span {
  color: #c7d5f0;
}

.bloque h3 {
  margin: 0 0 8px;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #636363;
}

.eventos {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.evento {
  background-color: #ffffff;
  border-radius: 10px;
  border: 1px solid #e3e8f4;
}

.evento.pendiente {
  border-left: 4px solid #004cb5;
}

.evento.vencida {
  border-left-color: #c2410c;
}

.vacio {
  margin: 0;
  color: #636363;
  font-size: 14px;
}

.error {
  margin: 0;
  color: #991b1b;
  font-size: 13px;
}
</style>
