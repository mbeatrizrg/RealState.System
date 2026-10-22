<template>
  <div ref="raiz" class="filtro-lista">
    <button
      type="button"
      :class="['filtro-boton', { activo: seleccion.length > 0 }]"
      :aria-expanded="abierto"
      aria-haspopup="listbox"
      @click="alternar"
    >
      <span>{{ resumen }}</span>
      <svg class="flecha" :class="{ abierto }" viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 9l6 6 6-6" />
      </svg>
    </button>

    <div v-if="abierto" class="filtro-panel" @keydown.esc="cerrar">
      <input
        ref="buscador"
        v-model="texto"
        type="search"
        class="filtro-buscar"
        :placeholder="`Buscar ${etiqueta.toLowerCase()}`"
        maxlength="60"
      />
      <ul class="filtro-opciones" role="listbox" :aria-multiselectable="multiple">
        <li v-for="opcion in visibles" :key="opcion.valor">
          <label :class="['filtro-opcion', { marcada: estaMarcada(opcion.valor) }]">
            <input
              :type="multiple ? 'checkbox' : 'radio'"
              :checked="estaMarcada(opcion.valor)"
              :name="etiqueta"
              @change="alternarOpcion(opcion.valor)"
            />
            <span>{{ opcion.texto }}</span>
          </label>
        </li>
        <li v-if="visibles.length === 0" class="filtro-vacio">Sin coincidencias</li>
      </ul>
      <slot name="extra" />
      <div class="filtro-pie">
        <button type="button" class="enlace" :disabled="seleccion.length === 0" @click="emit('update:modelValue', [])">Quitar filtro</button>
        <button type="button" class="enlace" @click="cerrar">Listo</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'

type Valor = string | number
export interface OpcionLista {
  valor: Valor
  texto: string
}

/* Filtro desplegable con buscador: se escribe para acotar la lista y se marcan una (multiple=false) o varias opciones.
   v-model es siempre un arreglo de valores */
const props = withDefaults(defineProps<{
  etiqueta: string
  opciones: OpcionLista[]
  modelValue: Valor[]
  multiple?: boolean
}>(), { multiple: true })
const emit = defineEmits<{ 'update:modelValue': [valor: Valor[]] }>()

const abierto = ref(false)
const texto = ref('')
const raiz = ref<HTMLElement | null>(null)
const buscador = ref<HTMLInputElement | null>(null)

const seleccion = computed(() => props.modelValue)

// Sin distinguir mayusculas ni acentos: "maria" encuentra "María"
const normalizar = (s: string) => s.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase()
const visibles = computed(() => {
  const busqueda = normalizar(texto.value.trim())
  return busqueda ? props.opciones.filter((o) => normalizar(o.texto).includes(busqueda)) : props.opciones
})

const resumen = computed(() => {
  if (seleccion.value.length === 0) return props.etiqueta
  if (seleccion.value.length === 1) {
    const opcion = props.opciones.find((o) => o.valor === seleccion.value[0])
    return `${props.etiqueta}: ${opcion?.texto ?? seleccion.value[0]}`
  }
  return `${props.etiqueta} (${seleccion.value.length})`
})

const estaMarcada = (valor: Valor) => seleccion.value.includes(valor)

function alternarOpcion(valor: Valor) {
  if (!props.multiple) {
    emit('update:modelValue', estaMarcada(valor) ? [] : [valor])
    return
  }
  emit('update:modelValue', estaMarcada(valor) ? seleccion.value.filter((v) => v !== valor) : [...seleccion.value, valor])
}

async function alternar() {
  abierto.value = !abierto.value
  if (abierto.value) {
    texto.value = ''
    await nextTick()
    buscador.value?.focus()
  }
}

const cerrar = () => { abierto.value = false }

// Clic fuera del filtro: se cierra
function alHacerClic(evento: MouseEvent) {
  if (abierto.value && raiz.value && !raiz.value.contains(evento.target as Node)) cerrar()
}
onMounted(() => document.addEventListener('mousedown', alHacerClic))
onBeforeUnmount(() => document.removeEventListener('mousedown', alHacerClic))
</script>

<style scoped>
.filtro-lista {
  position: relative;
}

.filtro-boton {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #ffffff;
  color: #0b1f46;
  border: 1px solid #c9d2e3;
  border-radius: 8px;
  padding: 9px 14px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  max-width: 260px;
}

.filtro-boton span {
  overflow: hidden;
  text-overflow: ellipsis;
}

.filtro-boton:hover {
  border-color: #0b1f46;
}

.filtro-boton.activo {
  background-color: #e3e8f4;
  border-color: #004cb5;
  color: #004cb5;
  font-weight: 600;
}

.flecha {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.flecha.abierto {
  transform: rotate(180deg);
}

.filtro-panel {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  z-index: 30;
  width: 270px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(11, 31, 70, 0.18);
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filtro-buscar {
  background-color: #0c1452;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 9px 12px;
  font-size: 14px;
  outline: none;
}

.filtro-buscar::placeholder {
  color: #92abde;
}

.filtro-opciones {
  list-style: none;
  margin: 0;
  padding: 0;
  max-height: 240px;
  overflow-y: auto;
}

.filtro-opcion {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 6px;
  font-size: 14px;
  color: #1c1c1c;
  cursor: pointer;
}

.filtro-opcion:hover {
  background-color: #f1f4f6;
}

.filtro-opcion.marcada {
  color: #004cb5;
  font-weight: 600;
}

.filtro-opcion input {
  accent-color: #004cb5;
}

.filtro-vacio {
  padding: 8px;
  font-size: 13px;
  color: #636363;
}

.filtro-pie {
  display: flex;
  justify-content: space-between;
  border-top: 1px solid #e3e8f4;
  padding-top: 8px;
}

.enlace {
  background: none;
  border: none;
  color: #004cb5;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  padding: 4px;
}

.enlace:disabled {
  color: #9aa4b8;
  cursor: default;
}
</style>
