<template>
  <div class="pastel">
    <div class="pastel-grafica">
      <svg :viewBox="`0 0 ${TAM} ${TAM}`" role="img" :aria-label="descripcion">
        <g v-if="total > 0">
          <path
            v-for="(s, i) in segmentos"
            :key="s.porcion.clave"
            :d="s.ruta"
            :fill="colores[i] ?? GRIS"
            :class="['segmento', { activo: s.porcion.clave === seleccion, atenuado: seleccion && s.porcion.clave !== seleccion }]"
            tabindex="0"
            role="button"
            :aria-label="`${s.porcion.etiqueta}: ${s.porcion.cantidad} (${porcentaje(s.porcion.cantidad)})`"
            @pointermove="mostrar(s.porcion, $event)"
            @pointerleave="ocultar"
            @focus="mostrar(s.porcion)"
            @blur="ocultar"
            @click="emit('elegir', s.porcion)"
            @keyup.enter="emit('elegir', s.porcion)"
          />
        </g>
        <circle v-else :cx="TAM / 2" :cy="TAM / 2" :r="(R_EXT + R_INT) / 2" fill="none" stroke="#e3e8f4" :stroke-width="R_EXT - R_INT" />
        <text :x="TAM / 2" :y="TAM / 2 - 4" text-anchor="middle" class="centro-valor">{{ total.toLocaleString('es-VE') }}</text>
        <text :x="TAM / 2" :y="TAM / 2 + 16" text-anchor="middle" class="centro-texto">{{ unidad }}</text>
      </svg>
      <div v-if="tooltip" class="tooltip" :style="{ left: `${tooltip.x}px`, top: `${tooltip.y}px` }" role="status">
        <strong>{{ tooltip.porcion.cantidad.toLocaleString('es-VE') }} · {{ porcentaje(tooltip.porcion.cantidad) }}</strong>
        <span>{{ tooltip.porcion.etiqueta }}</span>
      </div>
    </div>

    <!-- Leyenda con el valor y el porcentaje de cada porcion: la identidad nunca depende solo del color -->
    <ul class="leyenda">
      <li v-for="(p, i) in porciones" :key="p.clave">
        <button type="button" :class="['leyenda-item', { activo: p.clave === seleccion }]" :aria-pressed="p.clave === seleccion"
                :disabled="p.cantidad === 0" @click="emit('elegir', p)">
          <span class="muestra" :style="{ backgroundColor: colores[i] ?? GRIS }" aria-hidden="true" />
          <span class="etiqueta">{{ p.etiqueta }}</span>
          <span class="valor">{{ p.cantidad.toLocaleString('es-VE') }}</span>
          <span class="pct">{{ porcentaje(p.cantidad) }}</span>
        </button>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

import type { Porcion } from '@/services/estadisticas'

/* Grafica de pastel (dona) de un reparto en porcentajes, como mucho 6 porciones (el backend agrupa el resto en "Otros").
   Cada porcion y cada fila de la leyenda se pueden elegir para filtrar el panel. Separacion de 2px del color de fondo
   entre porciones; los colores vienen del padre (rampa ordinal o paleta categorica validadas) */
const props = withDefaults(defineProps<{
  porciones: Porcion[]
  colores: string[]
  unidad?: string
  seleccion?: string | null
  descripcion?: string
}>(), { unidad: 'clientes', seleccion: null, descripcion: 'Gráfica de pastel' })
const emit = defineEmits<{ elegir: [porcion: Porcion] }>()

const TAM = 220
const R_EXT = 104
const R_INT = 62
const GRIS = '#b8bcc4'

const total = computed(() => props.porciones.reduce((s, p) => s + p.cantidad, 0))
const porcentaje = (n: number) => (total.value === 0 ? '0 %' : `${Math.round((n * 1000) / total.value) / 10} %`)

function punto(angulo: number, r: number) {
  return [TAM / 2 + r * Math.sin(angulo), TAM / 2 - r * Math.cos(angulo)]
}

// Arco de dona entre dos angulos (radianes, 0 = arriba, sentido horario)
function arco(desde: number, hasta: number) {
  //Una sola porcion del 100 %: dos medias lunas para que el arco no se anule
  if (hasta - desde >= Math.PI * 2 - 1e-6) hasta = desde + Math.PI * 2 - 1e-4
  const grande = hasta - desde > Math.PI ? 1 : 0
  const [x1, y1] = punto(desde, R_EXT)
  const [x2, y2] = punto(hasta, R_EXT)
  const [x3, y3] = punto(hasta, R_INT)
  const [x4, y4] = punto(desde, R_INT)
  return `M${x1} ${y1} A${R_EXT} ${R_EXT} 0 ${grande} 1 ${x2} ${y2} L${x3} ${y3} A${R_INT} ${R_INT} 0 ${grande} 0 ${x4} ${y4} Z`
}

const segmentos = computed(() => {
  let angulo = 0
  return props.porciones
    .filter((p) => p.cantidad > 0)
    .map((p) => {
      const barrido = (p.cantidad / total.value) * Math.PI * 2
      const s = { porcion: p, ruta: arco(angulo, angulo + barrido) }
      angulo += barrido
      return s
    })
})

const tooltip = ref<{ porcion: Porcion; x: number; y: number } | null>(null)
function mostrar(porcion: Porcion, evento?: PointerEvent) {
  const caja = (evento?.currentTarget as SVGElement | undefined)?.ownerSVGElement?.parentElement?.getBoundingClientRect()
  tooltip.value = {
    porcion,
    x: evento && caja ? evento.clientX - caja.left + 12 : TAM / 2,
    y: evento && caja ? evento.clientY - caja.top + 12 : TAM / 2,
  }
}
const ocultar = () => { tooltip.value = null }
</script>

<style scoped>
.pastel {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.pastel-grafica {
  position: relative;
  width: 220px;
  max-width: 100%;
  flex-shrink: 0;
}

.pastel-grafica svg {
  display: block;
  width: 100%;
  height: auto;
}

.segmento {
  stroke: #ffffff;
  stroke-width: 2;
  cursor: pointer;
  transition: opacity 0.15s;
  outline: none;
}

.segmento:hover,
.segmento:focus-visible {
  opacity: 0.85;
}

.segmento.activo {
  stroke: #0b1f46;
}

.segmento.atenuado {
  opacity: 0.35;
}

.centro-valor {
  font-size: 26px;
  font-weight: 600;
  fill: #0b1f46;
}

.centro-texto {
  font-size: 12px;
  fill: #636363;
}

.tooltip {
  position: absolute;
  z-index: 5;
  pointer-events: none;
  display: flex;
  flex-direction: column;
  gap: 2px;
  background-color: #ffffff;
  border: 1px solid #e3e8f4;
  border-radius: 8px;
  padding: 8px 10px;
  box-shadow: 0 8px 20px rgba(11, 31, 70, 0.15);
  white-space: nowrap;
  font-size: 12px;
  color: #636363;
}

.tooltip strong {
  color: #0b1f46;
  font-size: 14px;
}

.leyenda {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  min-width: 220px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.leyenda-item {
  display: grid;
  grid-template-columns: 12px 1fr auto 52px;
  align-items: center;
  gap: 10px;
  width: 100%;
  background: none;
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 6px 8px;
  font-size: 13px;
  color: #1c1c1c;
  text-align: left;
  cursor: pointer;
}

.leyenda-item:hover:not(:disabled),
.leyenda-item.activo {
  background-color: #eef3fb;
  border-color: #c7d5f0;
}

.leyenda-item:disabled {
  cursor: default;
  color: #9ca3af;
}

.muestra {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.valor {
  font-weight: 600;
  color: #0b1f46;
  font-variant-numeric: tabular-nums;
}

.pct {
  color: #636363;
  text-align: right;
  font-variant-numeric: tabular-nums;
}
</style>
