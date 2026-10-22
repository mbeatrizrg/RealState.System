<template>
  <div class="galeria-fotos">
    <div class="galeria-cabecera">
      <label>Fotos</label>
      <span class="galeria-contador">{{ total }} / {{ MAX_IMAGENES }}</span>
    </div>

    <div class="galeria-grid">
      <!-- Fotos ya guardadas en el servidor -->
      <div v-for="(imagen, indice) in existentes" :key="`e-${imagen.id}`" class="galeria-item">
        <img :src="urlImagen(imagen.url)" :alt="`Foto ${indice + 1}`" />
        <span v-if="indice === 0" class="galeria-principal">Principal</span>
        <button type="button" class="galeria-quitar" :disabled="deshabilitado" aria-label="Eliminar foto" @click="emit('eliminar', imagen)">&times;</button>
      </div>

      <!-- Fotos elegidas que aun no se han subido -->
      <div v-for="(previa, indice) in previas" :key="previa.url" class="galeria-item pendiente">
        <img :src="previa.url" :alt="previa.archivo.name" />
        <span v-if="existentes.length === 0 && indice === 0" class="galeria-principal">Principal</span>
        <button type="button" class="galeria-quitar" :disabled="deshabilitado" aria-label="Quitar foto" @click="quitar(indice)">&times;</button>
      </div>

      <button v-if="total < MAX_IMAGENES" type="button" class="galeria-agregar" :disabled="deshabilitado" @click="entrada?.click()">
        <span class="galeria-mas">+</span>
        <span>{{ total === 0 ? 'Subir fotos' : 'Agregar' }}</span>
      </button>
    </div>

    <input
      ref="entrada"
      type="file"
      accept="image/jpeg,image/png,image/webp"
      multiple
      hidden
      @change="alElegir"
    />
    <p class="galeria-ayuda">JPG, PNG o WEBP · máx. 5 MB c/u · hasta {{ MAX_IMAGENES }} fotos</p>
    <ul v-if="errores.length" class="galeria-errores">
      <li v-for="mensaje in errores" :key="mensaje">{{ mensaje }}</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue'

import { MAX_IMAGENES, validarImagen, type Imagen } from '@/services/inmuebles'
import { urlImagen } from '@/utils/formato'

/* existentes: fotos ya guardadas (modo "gestionar fotos"); v-model: archivos elegidos pendientes de subir.
   El componente solo valida y muestra; subir y eliminar en el servidor lo hace la pagina que lo usa */
const props = withDefaults(defineProps<{ existentes?: Imagen[]; modelValue: File[]; deshabilitado?: boolean }>(), {
  existentes: () => [],
  deshabilitado: false,
})
const emit = defineEmits<{ 'update:modelValue': [archivos: File[]]; eliminar: [imagen: Imagen] }>()

const entrada = ref<HTMLInputElement | null>(null)
const errores = ref<string[]>([])
const total = computed(() => props.existentes.length + props.modelValue.length)

// Vista previa local de cada archivo; las URL temporales se liberan cuando el archivo sale de la lista
const previas = ref<{ archivo: File; url: string }[]>([])
watch(
  () => props.modelValue,
  (archivos) => {
    previas.value.filter((p) => !archivos.includes(p.archivo)).forEach((p) => URL.revokeObjectURL(p.url))
    previas.value = archivos.map((archivo) => previas.value.find((p) => p.archivo === archivo) ?? { archivo, url: URL.createObjectURL(archivo) })
  },
  { immediate: true },
)
onBeforeUnmount(() => previas.value.forEach((p) => URL.revokeObjectURL(p.url)))

function alElegir(evento: Event) {
  const input = evento.target as HTMLInputElement
  const elegidos = Array.from(input.files ?? [])
  input.value = '' // permite volver a elegir el mismo archivo
  errores.value = []

  const validos: File[] = []
  elegidos.forEach((archivo) => {
    const error = validarImagen(archivo)
    if (error) errores.value.push(error)
    else validos.push(archivo)
  })
  const disponibles = MAX_IMAGENES - total.value
  if (validos.length > disponibles) {
    errores.value.push(`Solo se pueden agregar ${disponibles} foto(s) más (máximo ${MAX_IMAGENES} por inmueble).`)
  }
  if (validos.length) emit('update:modelValue', [...props.modelValue, ...validos.slice(0, disponibles)])
}

function quitar(indice: number) {
  errores.value = []
  emit('update:modelValue', props.modelValue.filter((_, i) => i !== indice))
}
</script>

<style scoped>
.galeria-fotos {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.galeria-cabecera {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.galeria-cabecera label {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.galeria-contador {
  font-size: 12px;
  color: #636363;
}

.galeria-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.galeria-item,
.galeria-agregar {
  position: relative;
  aspect-ratio: 1 / 1;
  border-radius: 12px;
  overflow: hidden;
}

.galeria-item {
  background-color: var(--inactivo, #2d325a);
}

.galeria-item.pendiente {
  outline: 2px dashed var(--activo, #3b4cca);
  outline-offset: -2px;
}

.galeria-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.galeria-principal {
  position: absolute;
  left: 6px;
  bottom: 6px;
  background-color: var(--activo, #3b4cca);
  color: #ffffff;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 8px;
}

.galeria-quitar {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background-color: rgba(239, 68, 68, 0.9);
  color: #ffffff;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
}

.galeria-agregar {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background-color: var(--inactivo, #2d325a);
  border: 2px solid var(--activo, #3b4cca);
  color: #ffffff;
  font-size: 13px;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease;
}

.galeria-agregar:hover:not(:disabled) {
  transform: scale(1.03);
  border-color: #60a5fa;
}

.galeria-mas {
  font-size: 28px;
  line-height: 1;
}

button:disabled {
  opacity: 0.5;
  cursor: default;
}

.galeria-ayuda {
  margin: 0;
  font-size: 11px;
  color: #636363;
}

.galeria-errores {
  margin: 0;
  padding-left: 16px;
  font-size: 12px;
  color: #dc2626;
}
</style>
