<template>
  <div class="item">
    <button type="button" class="item-cabecera" :aria-expanded="abierta" @click="emit('alternar')">
      <span :class="['icono', actividad.tipo.toLowerCase()]" aria-hidden="true">{{ ICONOS[actividad.tipo] }}</span>
      <span class="item-titulo">
        <strong>{{ titulo }}</strong>
        <small>
          {{ ETIQUETAS[actividad.tipo] }}
          <template v-if="actividad.nombreEmpleado"> · {{ actividad.nombreEmpleado }}</template>
          <template v-if="actividad.nombreNegocio"> · {{ actividad.nombreNegocio }}</template>
        </small>
      </span>
      <span class="item-fecha">
        <template v-if="programada">{{ actividad.tipo === 'TAREA' ? 'Vence' : 'El' }} {{ formatearFechaHora(actividad.fechaProgramada) }}</template>
        <template v-else>{{ formatearFechaHora(actividad.fechaCreacion) }}</template>
      </span>
    </button>

    <div v-if="abierta" class="item-detalle">
      <dl v-if="actividad.tipo === 'LLAMADA' || actividad.duracionMinutos" class="datos-llamada">
        <template v-if="actividad.resultado"><dt>Resultado</dt><dd>{{ actividad.resultado }}</dd></template>
        <template v-if="actividad.duracionMinutos"><dt>Duración</dt><dd>{{ actividad.duracionMinutos }} min</dd></template>
        <template v-if="actividad.tipo === 'LLAMADA' && actividad.fechaProgramada">
          <dt>Fecha</dt><dd>{{ formatearFechaHora(actividad.fechaProgramada) }}</dd>
        </template>
      </dl>
      <p v-if="actividad.descripcion" class="descripcion">{{ actividad.descripcion }}</p>
      <p v-else class="descripcion tenue">Sin detalles.</p>

      <ul v-if="actividad.archivos.length" class="adjuntos">
        <li v-for="archivo in actividad.archivos" :key="archivo.id">
          <audio v-if="urlsAudio[archivo.id]" :src="urlsAudio[archivo.id]" controls preload="none" />
          <button type="button" class="enlace" @click="descargarArchivo(archivo)">
            📎 {{ archivo.nombre }} <small>({{ formatearTamano(archivo.tamano) }})</small>
          </button>
          <button v-if="esAudio(archivo) && !urlsAudio[archivo.id]" type="button" class="enlace" @click="escuchar(archivo)">
            ▶ Escuchar
          </button>
        </li>
      </ul>

      <div v-if="editable" class="item-acciones">
        <button v-if="actividad.tipo === 'TAREA' || actividad.tipo === 'REUNION'" type="button" class="btn-mini"
                :disabled="ocupado" @click="emit('completar')">
          {{ actividad.completada ? 'Marcar como pendiente' : actividad.tipo === 'TAREA' ? 'Marcar como hecha' : 'Marcar como realizada' }}
        </button>
        <button v-if="actividad.tipo !== 'ETAPA'" type="button" class="btn-mini peligro" :disabled="ocupado" @click="emit('eliminar')">
          Eliminar
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, reactive } from 'vue'

import { descargarArchivo, formatearTamano, type Actividad, type Archivo, type TipoHistorial } from '@/services/actividades'
import { api } from '@/services/api'
import { formatearFechaHora } from '@/utils/fechas'

const props = defineProps<{ actividad: Actividad; abierta: boolean; ocupado: boolean }>()
const emit = defineEmits<{ alternar: []; completar: []; eliminar: [] }>()

const ICONOS: Record<TipoHistorial, string> = {
  NOTA: '✎', CORREO: '✉', LLAMADA: '☎', TAREA: '✓', REUNION: '◷', ETAPA: '➜', COMUNICACION: '💬',
}
const ETIQUETAS: Record<TipoHistorial, string> = {
  NOTA: 'Nota', CORREO: 'Correo', LLAMADA: 'Llamada', TAREA: 'Tarea', REUNION: 'Reunión', ETAPA: 'Cambio de etapa',
  COMUNICACION: 'Comunicación del lead',
}

const a = computed(() => props.actividad)
const titulo = computed(() => {
  if (a.value.asunto) return a.value.asunto
  if (a.value.tipo === 'NOTA') return (a.value.descripcion ?? '').slice(0, 80) || 'Nota'
  return ETIQUETAS[a.value.tipo]
})
const programada = computed(() => (a.value.tipo === 'TAREA' || a.value.tipo === 'REUNION') && !!a.value.fechaProgramada)
// Las comunicaciones de los leads se gestionan en el lead; los cambios de etapa no se editan
const editable = computed(() => a.value.tipo !== 'COMUNICACION')

// Las grabaciones de llamadas se reproducen aqui mismo (el archivo se pide con el token, no es publico)
const esAudio = (archivo: Archivo) => archivo.tipoContenido.startsWith('audio/')
const urlsAudio = reactive<Record<number, string>>({})
async function escuchar(archivo: Archivo) {
  const { blob } = await api.archivo(`/api/archivos/${archivo.id}`)
  urlsAudio[archivo.id] = URL.createObjectURL(blob)
}
onBeforeUnmount(() => Object.values(urlsAudio).forEach((url) => URL.revokeObjectURL(url)))
</script>

<style scoped>
.item-cabecera {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  background: none;
  border: none;
  padding: 12px 14px;
  text-align: left;
  cursor: pointer;
  font: inherit;
  color: inherit;
}

.icono {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 15px;
  background-color: #e3e8f4;
  color: #0b1f46;
}

.icono.correo { background-color: #dbe7ff; color: #1d4ed8; }
.icono.llamada { background-color: #d9f5e1; color: #166534; }
.icono.tarea { background-color: #fdf0cf; color: #92400e; }
.icono.reunion { background-color: #ece3ff; color: #6d28d9; }
.icono.etapa { background-color: #ffe4d1; color: #c2410c; }
.icono.comunicacion { background-color: #d5f3f6; color: #0e7490; }

.item-titulo {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.item-titulo strong {
  color: #0b1f46;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-titulo small,
.item-fecha {
  color: #636363;
  font-size: 12px;
}

.item-fecha {
  white-space: nowrap;
}

.item-detalle {
  padding: 0 14px 14px 58px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  font-size: 14px;
  color: #1c1c1c;
}

.descripcion {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

.tenue {
  color: #9ca3af;
}

.datos-llamada {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 4px 12px;
  margin: 0;
  font-size: 13px;
}

.datos-llamada dt {
  color: #636363;
}

.datos-llamada dd {
  margin: 0;
}

.adjuntos {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.adjuntos li {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.adjuntos audio {
  height: 32px;
  max-width: 100%;
}

.enlace {
  background: none;
  border: none;
  padding: 0;
  color: #004cb5;
  cursor: pointer;
  font-size: 13px;
  text-align: left;
}

.enlace small {
  color: #636363;
}

.item-acciones {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-mini {
  background-color: #ffffff;
  border: 1px solid #0b1f46;
  color: #0b1f46;
  border-radius: 6px;
  padding: 6px 10px;
  font-size: 12px;
  cursor: pointer;
}

.btn-mini.peligro {
  border-color: #991b1b;
  color: #991b1b;
}

.btn-mini:disabled {
  opacity: 0.5;
  cursor: default;
}

@media (max-width: 640px) {
  .item-cabecera {
    flex-wrap: wrap;
  }

  .item-fecha {
    width: 100%;
    padding-left: 44px;
  }

  .item-detalle {
    padding-left: 14px;
  }
}
</style>
