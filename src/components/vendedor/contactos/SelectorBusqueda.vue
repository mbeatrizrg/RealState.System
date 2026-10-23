<template>
  <div ref="raiz" class="selector">
    <input
      :id="id"
      v-model="texto"
      type="search"
      class="selector-input"
      :placeholder="placeholder"
      maxlength="100"
      autocomplete="off"
      role="combobox"
      :aria-expanded="abierto"
      :aria-controls="`${id}-lista`"
      :aria-invalid="requerido && modelValue === null ? 'true' : undefined"
      @focus="abrir"
      @input="buscarConPausa"
      @keydown.down.prevent="mover(1)"
      @keydown.up.prevent="mover(-1)"
      @keydown.enter.prevent="elegir(opciones[resaltada])"
      @keydown.esc="abierto = false"
    />
    <ul v-if="abierto" :id="`${id}-lista`" class="selector-lista" role="listbox">
      <li v-if="buscando" class="selector-vacio">Buscando...</li>
      <li v-else-if="error" class="selector-vacio error">{{ error }}</li>
      <li v-else-if="opciones.length === 0" class="selector-vacio">Sin coincidencias</li>
      <li
        v-for="(opcion, i) in opciones"
        v-else
        :key="opcion.id"
        role="option"
        :aria-selected="opcion.id === modelValue"
        :class="['selector-opcion', { resaltada: i === resaltada }]"
        @mousedown.prevent="elegir(opcion)"
      >
        <span>{{ opcion.texto }}</span>
        <small v-if="opcion.detalle">{{ opcion.detalle }}</small>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'

import { mensajeDeError } from '@/services/api'

export interface OpcionSelector<T = unknown> {
  id: number
  texto: string
  detalle?: string
  // El registro completo (ej. el inmueble) para que el formulario pueda usar sus datos
  dato?: T
}

/* Campo con busqueda en el backend para elegir un registro (inmueble o contacto). v-model es el id elegido;
   al elegir tambien emite "elegido" con la opcion completa */
const props = withDefaults(defineProps<{
  modelValue: number | null
  buscar: (q: string) => Promise<OpcionSelector[]>
  textoInicial?: string
  placeholder?: string
  id?: string
  requerido?: boolean
}>(), { textoInicial: '', placeholder: 'Escriba para buscar', id: 'selector', requerido: false })
const emit = defineEmits<{ 'update:modelValue': [id: number | null]; elegido: [opcion: OpcionSelector] }>()

const texto = ref(props.textoInicial)
const opciones = ref<OpcionSelector[]>([])
const abierto = ref(false)
const buscando = ref(false)
const error = ref('')
const resaltada = ref(0)
const raiz = ref<HTMLElement | null>(null)
let ultimaPeticion = 0
let pausa: ReturnType<typeof setTimeout> | undefined

watch(() => props.textoInicial, (nuevo) => { texto.value = nuevo })

async function cargar() {
  const peticion = ++ultimaPeticion
  buscando.value = true
  error.value = ''
  try {
    const resultado = await props.buscar(texto.value.trim())
    if (peticion !== ultimaPeticion) return
    opciones.value = resultado
    resaltada.value = 0
  } catch (e) {
    if (peticion === ultimaPeticion) error.value = mensajeDeError(e)
  } finally {
    if (peticion === ultimaPeticion) buscando.value = false
  }
}

function abrir() {
  abierto.value = true
  cargar()
}

function buscarConPausa() {
  abierto.value = true
  // Si se borra o cambia el texto, la eleccion anterior deja de valer
  if (props.modelValue !== null) emit('update:modelValue', null)
  clearTimeout(pausa)
  pausa = setTimeout(cargar, 300)
}

function mover(paso: number) {
  if (!abierto.value) return abrir()
  const total = opciones.value.length
  if (total) resaltada.value = (resaltada.value + paso + total) % total
}

function elegir(opcion: OpcionSelector | undefined) {
  if (!opcion) return
  texto.value = opcion.texto
  abierto.value = false
  emit('update:modelValue', opcion.id)
  emit('elegido', opcion)
}

function clicFuera(evento: MouseEvent) {
  if (raiz.value && !raiz.value.contains(evento.target as Node)) abierto.value = false
}
onMounted(() => document.addEventListener('mousedown', clicFuera))
onBeforeUnmount(() => {
  document.removeEventListener('mousedown', clicFuera)
  clearTimeout(pausa)
})
</script>

<style scoped>
.selector {
  position: relative;
}

.selector-input {
  width: 100%;
  box-sizing: border-box;
  background-color: #0c1452;
  color: #ffffff;
  border: none;
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
}

.selector-input::placeholder {
  color: #92abde;
}

.selector-input[aria-invalid='true'] {
  box-shadow: 0 0 0 2px #f3b4b4;
}

.selector-lista {
  position: absolute;
  z-index: 20;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  max-height: 240px;
  overflow-y: auto;
  margin: 0;
  padding: 6px;
  list-style: none;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 12px 28px rgba(11, 31, 70, 0.18);
}

.selector-opcion {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  color: #0b1f46;
  font-size: 14px;
}

.selector-opcion small {
  color: #636363;
  font-size: 12px;
}

.selector-opcion.resaltada,
.selector-opcion:hover {
  background-color: #eef3fb;
}

.selector-vacio {
  padding: 8px 10px;
  color: #636363;
  font-size: 13px;
}

.selector-vacio.error {
  color: #991b1b;
}
</style>
