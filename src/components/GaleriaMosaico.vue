<template>
  <div :class="['mosaico', `fotos-${Math.min(fotos.length, 5)}`]">
    <button
      v-for="(url, i) in visibles"
      :key="`${url}-${i}`"
      type="button"
      :class="['celda', { principal: i === 0 }]"
      :aria-label="`Ver foto ${i + 1} de ${fotos.length}`"
      @click="abrir(i)"
    >
      <img :src="url" :alt="`${titulo} - foto ${i + 1}`" :loading="i === 0 ? 'eager' : 'lazy'" />
      <span v-if="i === visibles.length - 1 && restantes > 0" class="mas">+{{ restantes }} fotos</span>
    </button>
  </div>

  <!-- Visor a pantalla completa: flechas, teclado y contador -->
  <Teleport to="body">
    <div v-if="abierta !== null" class="visor-fondo" role="dialog" aria-modal="true" :aria-label="`Fotos de ${titulo}`"
         tabindex="-1" @click.self="cerrar" @keydown.esc="cerrar" @keydown.left="mover(-1)" @keydown.right="mover(1)">
      <button ref="botonCerrar" type="button" class="visor-cerrar" aria-label="Cerrar" @click="cerrar">&times;</button>
      <button v-if="fotos.length > 1" type="button" class="visor-flecha izquierda" aria-label="Foto anterior" @click="mover(-1)">&#8249;</button>
      <img :src="fotos[abierta]" :alt="`${titulo} - foto ${abierta + 1}`" class="visor-foto" />
      <button v-if="fotos.length > 1" type="button" class="visor-flecha derecha" aria-label="Foto siguiente" @click="mover(1)">&#8250;</button>
      <span class="visor-contador">{{ abierta + 1 }} / {{ fotos.length }}</span>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'

/* Galeria del inmueble en mosaico (portal y CRM). La distribucion cambia segun la cantidad de fotos para que no
   queden huecos: 1 foto ocupa todo, 2 van a la mitad, 3 o mas usan una principal grande y las demas a un lado.
   Se muestran como mucho 5; la ultima indica cuantas faltan y todas se ven en el visor */
const props = withDefaults(defineProps<{ fotos: string[]; titulo?: string }>(), { titulo: 'Inmueble' })

const MAXIMO = 5
const visibles = computed(() => props.fotos.slice(0, MAXIMO))
const restantes = computed(() => Math.max(0, props.fotos.length - MAXIMO))

const abierta = ref<number | null>(null)
const botonCerrar = ref<HTMLButtonElement | null>(null)

async function abrir(i: number) {
  abierta.value = i
  await nextTick()
  botonCerrar.value?.focus()
}
const cerrar = () => { abierta.value = null }
function mover(paso: number) {
  if (abierta.value === null) return
  abierta.value = (abierta.value + paso + props.fotos.length) % props.fotos.length
}
</script>

<style scoped>
.mosaico {
  display: grid;
  gap: 8px;
  height: clamp(240px, 38vw, 480px);
  border-radius: 14px;
  overflow: hidden;
}

/* 1 foto: ocupa todo el ancho */
.mosaico.fotos-1 {
  grid-template-columns: 1fr;
}

/* 2 fotos: mitad y mitad */
.mosaico.fotos-2 {
  grid-template-columns: 1fr 1fr;
}

/* 3 fotos: principal a la izquierda y dos apiladas */
.mosaico.fotos-3 {
  grid-template-columns: 2fr 1fr;
  grid-template-rows: 1fr 1fr;
}

/* 4 fotos: principal y tres en columna */
.mosaico.fotos-4 {
  grid-template-columns: 2fr 1fr;
  grid-template-rows: repeat(3, 1fr);
}

/* 5 o mas: principal y una cuadricula de 2 x 2 */
.mosaico.fotos-5 {
  grid-template-columns: 2fr 1fr 1fr;
  grid-template-rows: 1fr 1fr;
}

.mosaico.fotos-3 .principal,
.mosaico.fotos-5 .principal {
  grid-row: 1 / 3;
}

.mosaico.fotos-4 .principal {
  grid-row: 1 / 4;
}

.celda {
  position: relative;
  padding: 0;
  border: none;
  background-color: #e3e8f4;
  cursor: zoom-in;
  overflow: hidden;
  min-height: 0;
}

.celda img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.3s ease;
}

.celda:hover img,
.celda:focus-visible img {
  transform: scale(1.03);
}

.celda:focus-visible {
  outline: 3px solid #004cb5;
  outline-offset: -3px;
}

.mas {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  background-color: rgba(11, 31, 70, 0.55);
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
}

/* ----- Visor ----- */
.visor-fondo {
  position: fixed;
  inset: 0;
  z-index: 10000;
  background-color: rgba(5, 12, 30, 0.92);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 72px;
  outline: none;
}

.visor-foto {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
}

.visor-cerrar,
.visor-flecha {
  position: absolute;
  border: none;
  background-color: rgba(255, 255, 255, 0.12);
  color: #ffffff;
  cursor: pointer;
  border-radius: 50%;
  display: grid;
  place-items: center;
}

.visor-cerrar {
  top: 16px;
  right: 16px;
  width: 44px;
  height: 44px;
  font-size: 30px;
}

.visor-flecha {
  top: 50%;
  transform: translateY(-50%);
  width: 52px;
  height: 52px;
  font-size: 34px;
}

.visor-flecha.izquierda {
  left: 12px;
}

.visor-flecha.derecha {
  right: 12px;
}

.visor-cerrar:hover,
.visor-flecha:hover {
  background-color: rgba(255, 255, 255, 0.25);
}

.visor-contador {
  position: absolute;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  color: #ffffff;
  font-size: 14px;
  font-variant-numeric: tabular-nums;
}

/* En el celular: la principal arriba y las demas en una fila debajo */
@media (max-width: 640px) {
  .mosaico {
    height: auto;
  }

  .mosaico.fotos-1 .celda {
    aspect-ratio: 4 / 3;
  }

  .mosaico.fotos-2,
  .mosaico.fotos-3,
  .mosaico.fotos-4,
  .mosaico.fotos-5 {
    grid-template-columns: repeat(4, 1fr);
    grid-template-rows: auto;
  }

  .mosaico .principal {
    grid-column: 1 / -1;
    grid-row: auto !important;
    aspect-ratio: 4 / 3;
  }

  .mosaico .celda:not(.principal) {
    aspect-ratio: 1;
  }

  .mosaico.fotos-2 .celda:not(.principal) {
    grid-column: 1 / -1;
    aspect-ratio: 4 / 3;
  }

  .visor-fondo {
    padding: 56px 8px;
  }

  .visor-flecha {
    width: 40px;
    height: 40px;
    font-size: 26px;
  }
}
</style>
