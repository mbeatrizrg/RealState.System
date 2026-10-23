<template>
  <div ref="contenedor" class="serie">
    <ul v-if="series.length > 1" class="leyenda" aria-label="Series">
      <li v-for="s in series" :key="s.clave">
        <span :class="['clave', tipo === 'lineas' ? 'linea' : 'caja']" :style="{ backgroundColor: s.color }" aria-hidden="true" />
        {{ s.nombre }}
      </li>
    </ul>

    <div class="lienzo">
      <svg :width="ancho" :height="ALTO" role="img" :aria-label="descripcion">
        <!-- Lineas de guia y eje Y -->
        <g class="eje">
          <template v-for="t in ticks" :key="t">
            <line :x1="M.izq" :x2="ancho - M.der" :y1="y(t)" :y2="y(t)" />
            <text :x="M.izq - 8" :y="y(t) + 4" text-anchor="end">{{ formatoEje(t) }}</text>
          </template>
          <text v-for="(e, i) in etiquetas" v-show="mostrarEtiqueta(i)" :key="`x${i}`" :x="xCentro(i)" :y="ALTO - 10" text-anchor="middle">
            {{ recortar(e) }}
          </text>
        </g>

        <!-- Columnas agrupadas: <= 24px, extremo redondeado arriba y recto en la base, 2px de separacion -->
        <g v-if="tipo === 'barras'">
          <template v-for="(_, i) in etiquetas" :key="`g${i}`">
            <path v-for="(s, j) in series" :key="s.clave" :d="columna(i, j, s.valores[i] ?? 0)" :fill="s.color"
                  :class="{ atenuada: activo !== null && activo !== i }" />
          </template>
        </g>

        <!-- Lineas de 2px con marcadores de 8px y anillo del color de fondo -->
        <g v-else>
          <path v-for="s in series" :key="s.clave" :d="linea(s.valores)" fill="none" :stroke="s.color" stroke-width="2"
                stroke-linejoin="round" stroke-linecap="round" />
          <line v-if="activo !== null" class="cruz" :x1="xCentro(activo)" :x2="xCentro(activo)" :y1="M.arriba" :y2="ALTO - M.abajo" />
          <template v-for="s in series" :key="`m${s.clave}`">
            <circle v-for="(v, i) in s.valores" v-show="activo === i || s.valores.length <= 16" :key="i" :cx="xCentro(i)" :cy="y(v)"
                    r="4" :fill="s.color" stroke="#ffffff" stroke-width="2" />
          </template>
        </g>

        <!-- Zonas de contacto: toda la banda de cada punto (mas grande que la marca) -->
        <rect v-for="(e, i) in etiquetas" :key="`h${i}`" :x="xCentro(i) - banda / 2" :y="M.arriba" :width="banda"
              :height="ALTO - M.arriba - M.abajo" fill="transparent" class="zona" tabindex="0" :aria-label="textoAccesible(i)"
              @pointerenter="activo = i" @pointerleave="activo = null" @focus="activo = i" @blur="activo = null"
              @click="emit('elegir', i)" @keyup.enter="emit('elegir', i)" />
      </svg>

      <div v-if="activo !== null" class="tooltip" :style="posicionTooltip" role="status">
        <span class="tooltip-titulo">{{ etiquetas[activo] }}</span>
        <span v-for="s in series" :key="s.clave" class="tooltip-fila">
          <span class="tooltip-clave" :style="{ backgroundColor: s.color }" aria-hidden="true" />
          <strong>{{ formatoValor(s.valores[activo] ?? 0) }}</strong>
          <span>{{ s.nombre }}</span>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

export interface SerieGrafico {
  clave: string
  nombre: string
  color: string
  valores: number[]
}

/* Grafica de un solo eje: lineas (evolucion en el tiempo) o columnas agrupadas (comparar zonas o inmuebles).
   El tooltip muestra todas las series del punto bajo el cursor; con el teclado se recorre con Tab */
const props = withDefaults(defineProps<{
  etiquetas: string[]
  series: SerieGrafico[]
  tipo?: 'lineas' | 'barras'
  moneda?: boolean
  descripcion?: string
}>(), { tipo: 'lineas', moneda: false, descripcion: 'Gráfica' })
const emit = defineEmits<{ elegir: [indice: number] }>()

const ALTO = 280
const M = { izq: 56, der: 16, arriba: 14, abajo: 34 }

const contenedor = ref<HTMLElement | null>(null)
const ancho = ref(720)
let observador: ResizeObserver | undefined
onMounted(() => {
  if (!contenedor.value) return
  ancho.value = Math.max(320, contenedor.value.clientWidth || 720)
  if (typeof ResizeObserver !== 'undefined') {
    observador = new ResizeObserver(([e]) => { if (e) ancho.value = Math.max(320, Math.floor(e.contentRect.width)) })
    observador.observe(contenedor.value)
  }
})
onBeforeUnmount(() => observador?.disconnect())

const activo = ref<number | null>(null)

// Maximo redondeado a un numero limpio (1, 2, 2.5 o 5 por potencia de 10) y 4 divisiones
const maximo = computed(() => {
  const mayor = Math.max(0, ...props.series.flatMap((s) => s.valores))
  if (mayor === 0) return 4
  const paso = mayor / 4
  const potencia = 10 ** Math.floor(Math.log10(paso))
  const limpio = [1, 2, 2.5, 5, 10].find((m) => m * potencia >= paso) ?? 10
  return limpio * potencia * 4
})
const ticks = computed(() => [0, 1, 2, 3, 4].map((i) => (maximo.value / 4) * i))

const anchoUtil = computed(() => ancho.value - M.izq - M.der)
const banda = computed(() => anchoUtil.value / Math.max(1, props.etiquetas.length))
const xCentro = (i: number) => M.izq + banda.value * (i + 0.5)
const y = (v: number) => M.arriba + (1 - v / maximo.value) * (ALTO - M.arriba - M.abajo)

function linea(valores: number[]) {
  return valores.map((v, i) => `${i === 0 ? 'M' : 'L'}${xCentro(i)} ${y(v)}`).join(' ')
}

function columna(i: number, j: number, valor: number) {
  const n = props.series.length
  const grosor = Math.min(24, (banda.value * 0.7 - (n - 1) * 2) / n)
  const inicio = xCentro(i) - (n * grosor + (n - 1) * 2) / 2 + j * (grosor + 2)
  const base = y(0)
  const alto = base - y(valor)
  if (alto <= 0) return ''
  const r = Math.min(4, alto, grosor / 2)
  return `M${inicio} ${base} V${base - alto + r} Q${inicio} ${base - alto} ${inicio + r} ${base - alto}`
    + ` H${inicio + grosor - r} Q${inicio + grosor} ${base - alto} ${inicio + grosor} ${base - alto + r} V${base} Z`
}

// Con muchos puntos se muestran solo algunas etiquetas del eje X para que no se encimen
const cadaCuanto = computed(() => Math.max(1, Math.ceil(props.etiquetas.length / Math.max(1, Math.floor(anchoUtil.value / 76)))))
// La ultima siempre se muestra; si no cae en el salto, reemplaza a la anterior para que no se encimen
function mostrarEtiqueta(i: number) {
  const ultima = props.etiquetas.length - 1
  const c = cadaCuanto.value
  if (i === ultima) return true
  return i % c === 0 && !(ultima % c !== 0 && i === ultima - (ultima % c))
}
const recortar = (texto: string) => {
  const maximoLetras = Math.max(6, Math.floor((banda.value * cadaCuanto.value) / 7))
  return texto.length > maximoLetras ? `${texto.slice(0, maximoLetras - 1)}…` : texto
}

const compacto = new Intl.NumberFormat('es-VE', { notation: 'compact', maximumFractionDigits: 1 })
const formatoEje = (v: number) => (props.moneda ? `$${compacto.format(v)}` : compacto.format(v))
const formatoValor = (v: number) =>
  props.moneda ? `$${Math.round(v).toLocaleString('es-VE')}` : v.toLocaleString('es-VE')

const textoAccesible = (i: number) =>
  `${props.etiquetas[i]}: ${props.series.map((s) => `${s.nombre} ${formatoValor(s.valores[i] ?? 0)}`).join(', ')}`

const posicionTooltip = computed(() => {
  if (activo.value === null) return {}
  const x = xCentro(activo.value)
  const izquierda = x > ancho.value * 0.6
  return izquierda ? { right: `${ancho.value - x + 12}px`, top: `${M.arriba}px` } : { left: `${x + 12}px`, top: `${M.arriba}px` }
})
</script>

<style scoped>
.serie {
  width: 100%;
  min-width: 0;
}

.leyenda {
  list-style: none;
  margin: 0 0 8px;
  padding: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #1c1c1c;
}

.leyenda li {
  display: flex;
  align-items: center;
  gap: 6px;
}

.clave.linea {
  width: 16px;
  height: 2px;
  border-radius: 1px;
}

.clave.caja {
  width: 10px;
  height: 10px;
  border-radius: 2px;
}

.lienzo {
  position: relative;
}

svg {
  display: block;
  overflow: visible;
}

.eje line {
  stroke: #e3e8f4;
  stroke-width: 1;
}

.eje text {
  font-size: 11px;
  fill: #636363;
  font-variant-numeric: tabular-nums;
}

.cruz {
  stroke: #9ca3af;
  stroke-width: 1;
}

.atenuada {
  opacity: 0.4;
}

.zona {
  cursor: pointer;
  outline: none;
}

.zona:focus-visible {
  stroke: #004cb5;
  stroke-width: 1;
}

.tooltip {
  position: absolute;
  z-index: 5;
  pointer-events: none;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 150px;
  background-color: #ffffff;
  border: 1px solid #e3e8f4;
  border-radius: 8px;
  padding: 8px 10px;
  box-shadow: 0 8px 20px rgba(11, 31, 70, 0.15);
  font-size: 12px;
  color: #636363;
}

.tooltip-titulo {
  color: #0b1f46;
  font-weight: 600;
}

.tooltip-fila {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tooltip-fila strong {
  color: #0b1f46;
  font-size: 13px;
  font-variant-numeric: tabular-nums;
}

.tooltip-clave {
  width: 12px;
  height: 2px;
  border-radius: 1px;
}
</style>
