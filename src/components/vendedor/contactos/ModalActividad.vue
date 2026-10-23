<template>
  <ModalCrm :titulo="TITULOS[tipo]" @cerrar="emit('cerrar')">
    <form :id="idFormulario" class="form-grid" novalidate @submit.prevent="guardar">
      <p v-if="tipo === 'CORREO'" class="campo ancho destinatario">
        <span>Para</span>
        <strong>{{ nombreContacto }} &lt;{{ correoContacto }}&gt;</strong>
      </p>

      <label v-if="tipo !== 'NOTA' && tipo !== 'LLAMADA'" class="campo ancho">
        <span>{{ tipo === 'CORREO' ? 'Asunto' : tipo === 'TAREA' ? 'Título de la tarea' : 'Título de la reunión' }}</span>
        <input ref="primero" v-model="datos.asunto" type="text" maxlength="200" required
               :placeholder="tipo === 'TAREA' ? 'Ej: Enviar contrato de arrendamiento' : tipo === 'REUNION' ? 'Ej: Visita al apartamento' : ''" />
      </label>

      <template v-if="tipo === 'LLAMADA'">
        <label class="campo">
          <span>Resultado</span>
          <select v-model="datos.resultado" required>
            <option v-for="r in RESULTADOS_LLAMADA" :key="r" :value="r">{{ r }}</option>
          </select>
        </label>
        <label class="campo">
          <span>Duración (minutos)</span>
          <input v-model.number="datos.duracionMinutos" type="number" min="0" max="1440" />
        </label>
        <label class="campo ancho">
          <span>Fecha y hora de la llamada</span>
          <input v-model="datos.fechaProgramada" type="datetime-local" />
        </label>
      </template>

      <template v-if="tipo === 'TAREA' || tipo === 'REUNION'">
        <label class="campo">
          <span>{{ tipo === 'TAREA' ? 'Vence el' : 'Fecha y hora' }}</span>
          <input v-model="datos.fechaProgramada" type="datetime-local" required />
        </label>
        <label v-if="tipo === 'REUNION'" class="campo">
          <span>Duración (minutos)</span>
          <input v-model.number="datos.duracionMinutos" type="number" min="0" max="1440" />
        </label>
      </template>

      <label class="campo ancho">
        <span>{{ ETIQUETA_TEXTO[tipo] }}</span>
        <textarea
          :ref="tipo === 'NOTA' ? 'primero' : undefined"
          v-model="datos.descripcion"
          rows="5"
          maxlength="5000"
          :required="tipo === 'NOTA' || tipo === 'CORREO'"
          :placeholder="PLACEHOLDER_TEXTO[tipo]"
        />
      </label>

      <label v-if="negocios.length" class="campo ancho">
        <span>Negocio relacionado (opcional)</span>
        <select v-model="datos.negocioId">
          <option :value="null">Ninguno</option>
          <option v-for="n in negocios" :key="n.id" :value="n.id">{{ n.nombre }}</option>
        </select>
      </label>

      <div v-if="tipo !== 'TAREA'" class="campo ancho">
        <span>{{ tipo === 'LLAMADA' ? 'Grabación o archivos' : 'Adjuntos' }}</span>
        <ul v-if="archivos.length" class="lista-archivos">
          <li v-for="(archivo, i) in archivos" :key="`${archivo.name}-${i}`">
            <span>{{ archivo.name }} · {{ formatearTamano(archivo.size) }}</span>
            <button type="button" :aria-label="`Quitar ${archivo.name}`" @click="archivos.splice(i, 1)">Quitar</button>
          </li>
        </ul>
        <input ref="selector" type="file" multiple hidden :accept="ACEPTA" @change="agregarArchivos" />
        <button v-if="archivos.length < MAXIMO_ADJUNTOS" type="button" class="btn-adjuntar" @click="selector?.click()">
          + Adjuntar archivo
        </button>
        <p class="ayuda-campo">PDF, imágenes, Office o audio. Máximo {{ MAXIMO_ADJUNTOS }} archivos de 10 MB.</p>
      </div>

      <label v-if="tipo === 'CORREO'" class="casilla ancho">
        <input v-model="datos.enviarCorreo" type="checkbox" />
        Enviar al contacto por correo (si se desmarca, solo se registra en el historial)
      </label>
    </form>
    <p v-if="error" class="error-form" role="alert">{{ error }}</p>
    <template #pie>
      <button type="button" class="btn-secundario" @click="emit('cerrar')">Cancelar</button>
      <button type="submit" :form="idFormulario" class="btn-primario" :disabled="guardando">
        {{ guardando ? 'Guardando...' : BOTON[tipo] }}
      </button>
    </template>
  </ModalCrm>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, useId } from 'vue'

import ModalCrm from './ModalCrm.vue'
import {
  crearActividad, formatearTamano, MAXIMO_ADJUNTOS, PESO_MAXIMO_ARCHIVO, RESULTADOS_LLAMADA,
  type Actividad, type NuevaActividad, type TipoActividad,
} from '@/services/actividades'
import { mensajeDeError } from '@/services/api'

// Accesos rapidos del registro del contacto: crear nota, enviar correo, registrar llamada, programar tarea o reunion
const props = withDefaults(defineProps<{
  tipo: TipoActividad
  contactoId: number
  nombreContacto: string
  correoContacto: string
  negocios?: { id: number; nombre: string }[]
  negocioId?: number | null
}>(), { negocios: () => [], negocioId: null })
const emit = defineEmits<{ cerrar: []; guardada: [actividad: Actividad] }>()

const TITULOS: Record<TipoActividad, string> = {
  NOTA: 'Crear nota', CORREO: 'Enviar correo', LLAMADA: 'Registrar llamada', TAREA: 'Programar tarea', REUNION: 'Programar reunión',
}
const BOTON: Record<TipoActividad, string> = {
  NOTA: 'Guardar nota', CORREO: 'Enviar', LLAMADA: 'Registrar llamada', TAREA: 'Crear tarea', REUNION: 'Programar reunión',
}
const ETIQUETA_TEXTO: Record<TipoActividad, string> = {
  NOTA: 'Nota', CORREO: 'Mensaje', LLAMADA: 'Notas de la llamada', TAREA: 'Descripción (opcional)', REUNION: 'Detalles y lugar (opcional)',
}
const PLACEHOLDER_TEXTO: Record<TipoActividad, string> = {
  NOTA: '¿Qué hay que recordar de este contacto?',
  CORREO: 'Escriba el mensaje para el contacto',
  LLAMADA: '¿Qué se habló?',
  TAREA: '',
  REUNION: 'Ej: En la oficina de Av. Bolívar',
}
const ACEPTA = '.pdf,.jpg,.jpeg,.png,.webp,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.mp3,.wav,.ogg,.m4a'

const idFormulario = useId()

// "2026-10-22T15:30" en la hora local (formato de <input type="datetime-local"> y de LocalDateTime)
function ahoraLocal(minutosExtra = 0) {
  const d = new Date(Date.now() + minutosExtra * 60000)
  d.setSeconds(0, 0)
  return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
}

const datos = reactive({
  asunto: '',
  descripcion: '',
  resultado: RESULTADOS_LLAMADA[0]!,
  duracionMinutos: null as number | null,
  // Llamadas: cuando ocurrio (ahora). Tareas y reuniones: mañana a esta hora como propuesta
  fechaProgramada: props.tipo === 'LLAMADA' ? ahoraLocal() : props.tipo === 'TAREA' || props.tipo === 'REUNION' ? ahoraLocal(24 * 60) : '',
  negocioId: props.negocioId,
  enviarCorreo: true,
})

const archivos = ref<File[]>([])
const selector = ref<HTMLInputElement | null>(null)
const primero = ref<HTMLInputElement | HTMLTextAreaElement | null>(null)
const guardando = ref(false)
const error = ref('')
onMounted(() => primero.value?.focus())

function agregarArchivos(evento: Event) {
  const input = evento.target as HTMLInputElement
  const elegidos = Array.from(input.files ?? [])
  input.value = ''
  const grandes = elegidos.filter((a) => a.size > PESO_MAXIMO_ARCHIVO)
  const validos = elegidos.filter((a) => a.size <= PESO_MAXIMO_ARCHIVO)
  const espacio = MAXIMO_ADJUNTOS - archivos.value.length
  archivos.value.push(...validos.slice(0, espacio))
  error.value = grandes.length
    ? `${grandes.map((a) => a.name).join(', ')} pesa más de 10 MB.`
    : validos.length > espacio ? `Solo se pueden adjuntar ${MAXIMO_ADJUNTOS} archivos.` : ''
}

function validar() {
  const tipo = props.tipo
  if ((tipo === 'NOTA' || tipo === 'CORREO') && !datos.descripcion.trim()) {
    return tipo === 'NOTA' ? 'Escriba el contenido de la nota.' : 'Escriba el mensaje del correo.'
  }
  if (tipo !== 'NOTA' && tipo !== 'LLAMADA' && !datos.asunto.trim()) return 'Escriba el título o asunto.'
  if ((tipo === 'TAREA' || tipo === 'REUNION') && !datos.fechaProgramada) return 'Indique la fecha.'
  return ''
}

async function guardar() {
  error.value = validar()
  if (error.value) return
  const tipo = props.tipo
  const cuerpo: NuevaActividad = {
    tipo,
    contactoId: props.contactoId,
    negocioId: datos.negocioId,
    asunto: tipo === 'LLAMADA' ? `Llamada: ${datos.resultado}` : datos.asunto.trim(),
    descripcion: datos.descripcion.trim(),
    fechaProgramada: datos.fechaProgramada ? `${datos.fechaProgramada}:00` : null,
    resultado: tipo === 'LLAMADA' ? datos.resultado : undefined,
    duracionMinutos: tipo === 'LLAMADA' || tipo === 'REUNION' ? datos.duracionMinutos : null,
    enviarCorreo: tipo === 'CORREO' && datos.enviarCorreo,
  }
  guardando.value = true
  try {
    emit('guardada', await crearActividad(cuerpo, tipo === 'TAREA' ? [] : archivos.value))
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    guardando.value = false
  }
}
</script>

<style scoped src="./formulario.css"></style>
<style scoped>
.destinatario {
  margin: 0;
}

.destinatario strong {
  font-weight: 500;
  color: #1c1c1c;
  font-size: 14px;
  word-break: break-all;
}
</style>
