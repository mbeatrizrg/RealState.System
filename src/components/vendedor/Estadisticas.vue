<template>
  <div class="crm-contactos">
    <MenuCrm activo="Estadísticas" />

    <main class="main-content">
      <header class="header">
        <div>
          <h1 class="greeting">Estadísticas e informes</h1>
          <p class="subtitulo">Cómo interactúan los clientes con los inmuebles y cuáles son los resultados comerciales</p>
        </div>
        <div class="acciones">
          <button type="button" class="btn-secundario" @click="mostrarPaneles = true">Personalizar gráficas</button>
          <button type="button" class="btn-filtros" :disabled="!datos" @click="mostrarInforme = true">Exportar informe</button>
        </div>
      </header>

      <nav class="pestanas" aria-label="Secciones de estadísticas">
        <button v-for="p in PESTANAS" :key="p.valor" type="button" :class="['pestana', { activa: pestana === p.valor }]"
                :aria-current="pestana === p.valor ? 'page' : undefined" @click="cambiarPestana(p.valor)">
          {{ p.texto }}
        </button>
      </nav>

      <section class="barra-filtros" aria-label="Filtros">
        <FiltroLista v-model="vendedores" etiqueta="Propietario / vendedor" :opciones="opcionesVendedor" />
        <FiltroLista v-model="periodo" etiqueta="Rango de fechas" :opciones="RANGOS_FECHA" :multiple="false">
          <template v-if="periodo[0] === 'personalizado'" #extra>
            <div class="rango">
              <label>Desde <input v-model="desde" type="date" :max="hasta || undefined" /></label>
              <label>Hasta <input v-model="hasta" type="date" :min="desde || undefined" /></label>
            </div>
          </template>
        </FiltroLista>
        <FiltroLista v-model="tipos" etiqueta="Tipo de inmueble" :opciones="opcionesTipo" />
        <FiltroLista v-model="ciudades" etiqueta="Zona / ubicación" :opciones="opcionesCiudad">
          <template #extra>
            <div class="rango">
              <label>Dirección contiene <input v-model="zonaTexto" type="text" maxlength="100" placeholder="Ej: El Bosque" /></label>
            </div>
          </template>
        </FiltroLista>
        <FiltroLista v-model="operacion" etiqueta="Tipo de operación" :opciones="OPERACIONES" :multiple="false" />
        <button type="button" :class="['btn-avanzados', { activo: cantidadAvanzados > 0 }]" @click="mostrarAvanzados = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M7 12h10M10 18h4" />
          </svg>
          Filtros avanzados{{ cantidadAvanzados ? ` (${cantidadAvanzados})` : '' }}
        </button>
        <button v-if="hayFiltros" type="button" class="btn-limpiar" @click="limpiarFiltros">Limpiar filtros</button>
      </section>

      <p v-if="nivel" class="filtro-activo" role="status">
        Tabla filtrada por clientes que <strong>{{ etiquetaNivel.toLowerCase() }}</strong>
        <button type="button" class="enlace-celda" @click="nivel = null">Quitar</button>
      </p>

      <p v-if="error" class="estado-msg error" role="alert">No se pudieron calcular las estadísticas: {{ error }}</p>
      <p v-else-if="!datos" class="estado-msg">Calculando estadísticas...</p>

      <template v-else>
        <!-- ===================== Resumen general ===================== -->
        <template v-if="pestana === 'resumen'">
          <section v-if="visible.resumen" class="tarjetas" aria-label="Métricas">
            <article class="tarjeta-metrica">
              <h2>Consultas recibidas</h2>
              <p class="cifra">{{ numero(datos.resumen.consultas) }}</p>
              <p class="detalle">{{ numero(datos.resumen.mensajes) }} mensajes en los leads</p>
            </article>
            <article class="tarjeta-metrica">
              <h2>Visitas a inmuebles realizadas</h2>
              <p class="cifra">{{ numero(datos.resumen.visitas) }}</p>
              <p class="detalle">Tareas de visita y reuniones completadas</p>
            </article>
            <article class="tarjeta-metrica">
              <h2>Negocios abiertos</h2>
              <p class="cifra">{{ numero(datos.resumen.negociosAbiertos) }}</p>
              <p class="detalle">{{ numero(datos.resumen.clientesInteresados) }} clientes interactuaron en el período</p>
            </article>
            <article class="tarjeta-metrica">
              <h2>Cierres del mes</h2>
              <p class="cifra">{{ numero(datos.resumen.cierresMes) }}</p>
              <p class="detalle">{{ numero(datos.resumen.cierresPeriodo) }} en el período · {{ formatearPrecio(datos.resumen.montoCerradoPeriodo) }}</p>
            </article>
          </section>

          <div class="graficas">
            <section v-if="visible.pastel" class="tarjeta-panel">
              <header class="panel-cabecera">
                <h2>{{ modoPastel === 'nivel' ? 'Comportamiento de los clientes' : 'Tipo de inmueble más demandado' }}</h2>
                <div class="alternar-vista pequeno" role="group" aria-label="Ver la gráfica según">
                  <button type="button" :class="{ activo: modoPastel === 'nivel' }" :aria-pressed="modoPastel === 'nivel'" @click="modoPastel = 'nivel'">
                    Estado del cliente
                  </button>
                  <button type="button" :class="{ activo: modoPastel === 'tipo' }" :aria-pressed="modoPastel === 'tipo'" @click="modoPastel = 'tipo'">
                    Tipo de inmueble
                  </button>
                </div>
              </header>
              <GraficoPastel v-if="modoPastel === 'nivel'" :porciones="datos.porNivel" :colores="coloresNivel(datos.porNivel)" :seleccion="nivel"
                             descripcion="Clientes según su interacción con los inmuebles" @elegir="elegirNivel" />
              <GraficoPastel v-else :porciones="datos.porTipo" :colores="coloresTipo(datos.porTipo)" :seleccion="tipoSeleccionado"
                             descripcion="Clientes interesados por tipo de inmueble" @elegir="elegirTipo" />
              <p class="nota">Clic en una porción para filtrar el panel.</p>
            </section>

            <section v-if="visible.serie" class="tarjeta-panel ancho">
              <header class="panel-cabecera">
                <h2>Interacciones {{ TITULO_AGRUPACION[agrupacion] }}</h2>
                <div class="alternar-vista pequeno" role="group" aria-label="Agrupar por">
                  <button v-for="a in AGRUPACIONES" :key="a.valor" type="button" :class="{ activo: agrupacion === a.valor }"
                          :aria-pressed="agrupacion === a.valor" @click="agrupacion = a.valor">
                    {{ a.texto }}
                  </button>
                </div>
              </header>
              <GraficoSerie v-if="datos.serie.puntos.length" :etiquetas="datos.serie.puntos.map((p) => p.etiqueta)" :series="seriesInteraccion"
                            :tipo="agrupacion === 'tiempo' ? 'lineas' : 'barras'"
                            :descripcion="`Consultas, visitas y cierres ${TITULO_AGRUPACION[agrupacion]}`" @elegir="elegirPunto" />
              <p v-else class="vacio">No hay interacciones en el período con estos filtros.</p>
            </section>
          </div>

          <TablaInmuebles v-if="visible.inmuebles" :filas="datos.inmuebles" :nivel="nivel" @abrir="abrirInmueble" />
        </template>

        <!-- ===================== Interaccion con inmuebles ===================== -->
        <template v-else-if="pestana === 'interaccion'">
          <div v-if="visible.pastel" class="graficas dos">
            <section class="tarjeta-panel">
              <header class="panel-cabecera"><h2>Estado del cliente frente al inmueble</h2></header>
              <GraficoPastel :porciones="datos.porNivel" :colores="coloresNivel(datos.porNivel)" :seleccion="nivel"
                             descripcion="Clientes según su interacción con los inmuebles" @elegir="elegirNivel" />
            </section>
            <section class="tarjeta-panel">
              <header class="panel-cabecera"><h2>Tipo de inmueble más demandado</h2></header>
              <GraficoPastel :porciones="datos.porTipo" :colores="coloresTipo(datos.porTipo)" :seleccion="tipoSeleccionado"
                             descripcion="Clientes interesados por tipo de inmueble" @elegir="elegirTipo" />
            </section>
          </div>
          <section v-if="visible.serie" class="tarjeta-panel">
            <header class="panel-cabecera">
              <h2>Interacciones {{ TITULO_AGRUPACION[agrupacion] }}</h2>
              <div class="alternar-vista pequeno" role="group" aria-label="Agrupar por">
                <button v-for="a in AGRUPACIONES" :key="a.valor" type="button" :class="{ activo: agrupacion === a.valor }"
                        :aria-pressed="agrupacion === a.valor" @click="agrupacion = a.valor">
                  {{ a.texto }}
                </button>
              </div>
            </header>
            <GraficoSerie v-if="datos.serie.puntos.length" :etiquetas="datos.serie.puntos.map((p) => p.etiqueta)" :series="seriesInteraccion"
                          :tipo="agrupacion === 'tiempo' ? 'lineas' : 'barras'"
                          :descripcion="`Consultas, visitas y cierres ${TITULO_AGRUPACION[agrupacion]}`" @elegir="elegirPunto" />
            <p v-else class="vacio">No hay interacciones en el período con estos filtros.</p>
          </section>
          <TablaInmuebles v-if="visible.inmuebles" :filas="datos.inmuebles" :nivel="nivel" @abrir="abrirInmueble" />
        </template>

        <!-- ===================== Rendimiento de ventas ===================== -->
        <template v-else>
          <section v-if="visible.resumen" class="tarjetas" aria-label="Métricas de ventas">
            <article class="tarjeta-metrica">
              <h2>Cierres en el período</h2>
              <p class="cifra">{{ numero(totalVentas.ganados) }}</p>
              <p class="detalle">{{ numero(totalVentas.perdidos) }} perdidos · tasa de cierre {{ tasaTotal }}</p>
            </article>
            <article class="tarjeta-metrica">
              <h2>Monto cerrado</h2>
              <p class="cifra">{{ formatearPrecio(totalVentas.monto) }}</p>
              <p class="detalle">Ventas y alquileres ganados en el período</p>
            </article>
            <article class="tarjeta-metrica">
              <h2>Negocios abiertos</h2>
              <p class="cifra">{{ numero(totalVentas.abiertos) }}</p>
              <p class="detalle">{{ formatearPrecio(totalVentas.pipeline) }} en el pipeline</p>
            </article>
            <article class="tarjeta-metrica">
              <h2>Tareas vencidas</h2>
              <p class="cifra">{{ numero(totalVentas.vencidas) }}</p>
              <p class="detalle">{{ numero(totalVentas.tareas) }} tareas completadas en el período</p>
            </article>
          </section>

          <div v-if="visible.serie" class="graficas dos">
            <section class="tarjeta-panel">
              <header class="panel-cabecera"><h2>Monto cerrado por período</h2></header>
              <GraficoSerie :etiquetas="datos.ventasPorPeriodo.map((p) => p.etiqueta)" tipo="barras" moneda
                            :series="[{ clave: 'monto', nombre: 'Monto cerrado', color: COLOR_SERIE[0]!, valores: datos.ventasPorPeriodo.map((p) => Number(p.monto)) }]"
                            descripcion="Monto de los negocios ganados por período" />
            </section>
            <section class="tarjeta-panel">
              <header class="panel-cabecera"><h2>Pipeline por etapa</h2></header>
              <ul class="pipeline">
                <li v-for="e in datos.pipeline" :key="e.etapaId">
                  <span class="pipeline-etapa">{{ e.etapa }}</span>
                  <span class="pipeline-barra" aria-hidden="true">
                    <span :style="{ width: `${maximoPipeline ? (Number(e.valor) / maximoPipeline) * 100 : 0}%` }" />
                  </span>
                  <span class="pipeline-valor">{{ formatearPrecio(Number(e.valor)) }} <small>· {{ e.cantidad }}</small></span>
                </li>
              </ul>
            </section>
          </div>

          <section v-if="visible.vendedores" class="tarjeta-panel">
            <header class="panel-cabecera"><h2>Rendimiento por vendedor</h2></header>
            <div class="table-container">
              <table class="user-table">
                <thead>
                  <tr>
                    <th>Vendedor</th><th class="num">Negocios creados</th><th class="num">Abiertos</th><th class="num">Ganados</th>
                    <th class="num">Perdidos</th><th class="num">Monto ganado</th><th class="num">Valor en pipeline</th>
                    <th class="num">Tasa de cierre</th><th class="num">Tareas completadas</th><th class="num">Tareas vencidas</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="v in datos.vendedores" :key="v.id ?? 'sin'">
                    <td :class="{ tenue: v.id === null }">{{ v.nombre }}</td>
                    <td class="num">{{ v.creados }}</td>
                    <td class="num">{{ v.abiertos }}</td>
                    <td class="num">{{ v.ganados }}</td>
                    <td class="num">{{ v.perdidos }}</td>
                    <td class="num">{{ formatearPrecio(Number(v.montoGanado)) }}</td>
                    <td class="num">{{ formatearPrecio(Number(v.valorPipeline)) }}</td>
                    <td class="num">{{ v.tasaCierre === null ? '—' : `${Math.round(v.tasaCierre * 100)} %` }}</td>
                    <td class="num">{{ v.tareasCompletadas }}</td>
                    <td :class="['num', { alerta: v.tareasVencidas > 0 }]">{{ v.tareasVencidas }}</td>
                  </tr>
                </tbody>
              </table>
              <p v-if="!datos.vendedores.length" class="estado-msg">No hay negocios ni tareas con estos filtros.</p>
            </div>
          </section>
        </template>
      </template>
    </main>

    <PanelAvanzado v-if="mostrarAvanzados" v-model="avanzados" :campos="CAMPOS_AVANZADOS" :rangos="[['precioMin', 'precioMax']]"
                   ayuda="se tienen en cuenta solo los inmuebles que cumplen todos." @cerrar="mostrarAvanzados = false" />

    <ModalCrm v-if="mostrarPaneles" titulo="Personalizar gráficas" @cerrar="mostrarPaneles = false">
      <p class="ayuda-modal">Elige qué partes del panel quieres ver. La elección se guarda en este navegador.</p>
      <label v-for="s in SECCIONES" :key="s.valor" class="opcion-modal">
        <input v-model="visible[s.valor]" type="checkbox" @change="guardarPaneles" />
        {{ s.texto }}
      </label>
      <template #pie>
        <button type="button" class="btn-primario" @click="mostrarPaneles = false">Listo</button>
      </template>
    </ModalCrm>

    <ModalCrm v-if="mostrarInforme" titulo="Exportar informe" @cerrar="mostrarInforme = false">
      <fieldset class="grupo-modal">
        <legend>Formato</legend>
        <label class="opcion-modal"><input v-model="formatoInforme" type="radio" value="xlsx" /> <span><strong>Excel</strong> (una hoja por sección)</span></label>
        <label class="opcion-modal"><input v-model="formatoInforme" type="radio" value="pdf" /> <span><strong>PDF</strong> para imprimir o compartir</span></label>
      </fieldset>
      <fieldset class="grupo-modal">
        <legend>Secciones</legend>
        <label v-for="s in SECCIONES" :key="s.valor" class="opcion-modal">
          <input v-model="seccionesInforme" type="checkbox" :value="s.valor" /> {{ s.texto }}
        </label>
      </fieldset>
      <p class="ayuda-modal">El informe incluye los datos de cada gráfica en tablas, con los filtros actuales.</p>
      <p v-if="errorInforme" class="error-modal" role="alert">{{ errorInforme }}</p>
      <template #pie>
        <button type="button" class="btn-secundario" @click="mostrarInforme = false">Cancelar</button>
        <button type="button" class="btn-primario" :disabled="exportando || !seccionesInforme.length" @click="exportar">
          {{ exportando ? 'Generando...' : 'Exportar' }}
        </button>
      </template>
    </ModalCrm>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import MenuCrm from './MenuCrm.vue'
import FiltroLista from './contactos/FiltroLista.vue'
import ModalCrm from './contactos/ModalCrm.vue'
import PanelAvanzado, { type CampoAvanzado } from './contactos/PanelAvanzado.vue'
import GraficoPastel from './estadisticas/GraficoPastel.vue'
import GraficoSerie, { type SerieGrafico } from './estadisticas/GraficoSerie.vue'
import TablaInmuebles from './estadisticas/TablaInmuebles.vue'
import { COLOR_NIVEL, COLOR_SERIE, COLOR_TIPO, GRIS_OTROS } from './estadisticas/colores'
import { mensajeDeError } from '@/services/api'
import { obtenerCiudades, obtenerTiposInmueble, type Catalogo } from '@/services/catalogos'
import { obtenerPropietarios } from '@/services/contactos'
import {
  exportarInforme, obtenerEstadisticas, SECCIONES,
  type Agrupacion, type Estadisticas, type FiltrosEstadisticas, type NivelInteraccion, type Porcion, type SeccionInforme,
} from '@/services/estadisticas'
import { ESTADOS_INMUEBLE } from '@/services/propiedades'
import { RANGOS_FECHA, rangoAFechas } from '@/utils/fechas'
import { formatearPrecio } from '@/utils/formato'

const route = useRoute()
const router = useRouter()

type Pestana = 'resumen' | 'interaccion' | 'ventas'
const PESTANAS: { valor: Pestana; texto: string }[] = [
  { valor: 'resumen', texto: 'Resumen general' },
  { valor: 'interaccion', texto: 'Interacción con inmuebles' },
  { valor: 'ventas', texto: 'Rendimiento de ventas' },
]
const pestana = computed<Pestana>(() => {
  const valor = route.query.pestana
  return valor === 'interaccion' || valor === 'ventas' ? valor : 'resumen'
})
const cambiarPestana = (p: Pestana) => router.replace({ query: { ...route.query, pestana: p === 'resumen' ? undefined : p } })

const AGRUPACIONES: { valor: Agrupacion; texto: string }[] = [
  { valor: 'tiempo', texto: 'En el tiempo' },
  { valor: 'zona', texto: 'Por zona' },
  { valor: 'propiedad', texto: 'Por inmueble' },
]
const TITULO_AGRUPACION: Record<Agrupacion, string> = { tiempo: 'en el tiempo', zona: 'por zona', propiedad: 'por inmueble' }
const OPERACIONES = [{ valor: 'venta', texto: 'Venta' }, { valor: 'alquiler', texto: 'Alquiler' }]
const CAMPOS_AVANZADOS: CampoAvanzado[] = [
  { clave: 'precioMin', etiqueta: 'Precio mínimo (USD)', numerico: true },
  { clave: 'precioMax', etiqueta: 'Precio máximo (USD)', numerico: true },
  { clave: 'estado', etiqueta: 'Estado del inmueble', opciones: ESTADOS_INMUEBLE },
  { clave: 'codigo', etiqueta: 'Código RRBR contiene', placeholder: 'Ej: RRBR-120' },
]
const avanzadosVacios = () => ({ precioMin: '', precioMax: '', estado: '', codigo: '' })

// ----- Filtros (por defecto: este año) -----
const vendedores = ref<(string | number)[]>([])
const periodo = ref<(string | number)[]>(['esteAnio'])
const desde = ref('')
const hasta = ref('')
const tipos = ref<(string | number)[]>([])
const ciudades = ref<(string | number)[]>([])
const zonaTexto = ref('')
const operacion = ref<(string | number)[]>([])
const avanzados = ref<Record<string, string>>(avanzadosVacios())
const nivel = ref<NivelInteraccion | null>(null)
const agrupacion = ref<Agrupacion>('tiempo')
const modoPastel = ref<'nivel' | 'tipo'>('nivel')

const listaVendedores = ref<Catalogo[]>([])
const listaTipos = ref<Catalogo[]>([])
const listaCiudades = ref<Catalogo[]>([])
const opcionesVendedor = computed(() => listaVendedores.value.map((v) => ({ valor: v.id, texto: v.nombre })))
const opcionesTipo = computed(() => listaTipos.value.map((t) => ({ valor: t.id, texto: t.nombre })))
const opcionesCiudad = computed(() => listaCiudades.value.map((c) => ({ valor: c.id, texto: c.nombre })))

const cantidadAvanzados = computed(() => Object.values(avanzados.value).filter((v) => v.trim() !== '').length)
const hayFiltros = computed(() =>
  vendedores.value.length > 0 || periodo.value[0] !== 'esteAnio' || tipos.value.length > 0 || ciudades.value.length > 0
  || zonaTexto.value.trim() !== '' || operacion.value.length > 0 || cantidadAvanzados.value > 0 || nivel.value !== null)

function limpiarFiltros() {
  vendedores.value = []
  periodo.value = ['esteAnio']
  desde.value = ''
  hasta.value = ''
  tipos.value = []
  ciudades.value = []
  zonaTexto.value = ''
  operacion.value = []
  avanzados.value = avanzadosVacios()
  nivel.value = null
}

const filtros = computed<FiltrosEstadisticas>(() => {
  const fechas = rangoAFechas(String(periodo.value[0] ?? ''), desde.value, hasta.value)
  const a = avanzados.value
  return {
    vendedorIds: vendedores.value.map(Number),
    desde: fechas.desde,
    hasta: fechas.hasta,
    tipoInmuebleIds: tipos.value.map(Number),
    ciudadIds: ciudades.value.map(Number),
    zona: zonaTexto.value.trim(),
    operacion: (operacion.value[0] || undefined) as FiltrosEstadisticas['operacion'],
    precioMin: a.precioMin,
    precioMax: a.precioMax,
    estados: a.estado ? [a.estado] : [],
    codigo: a.codigo,
    nivel: nivel.value ?? undefined,
    agrupacion: agrupacion.value,
  }
})

// ----- Paneles visibles (preferencia del navegador) -----
const CLAVE_PANELES = 'rr_paneles_estadisticas'
const visible = reactive<Record<SeccionInforme, boolean>>({ resumen: true, pastel: true, serie: true, inmuebles: true, vendedores: true })
try {
  Object.assign(visible, JSON.parse(localStorage.getItem(CLAVE_PANELES) ?? '{}'))
} catch {
  // Sin localStorage: todos los paneles visibles
}
function guardarPaneles() {
  try {
    localStorage.setItem(CLAVE_PANELES, JSON.stringify(visible))
  } catch {
    // Se mantiene solo hasta recargar la pagina
  }
}
const mostrarPaneles = ref(false)

// ----- Carga -----
const datos = ref<Estadisticas | null>(null)
const error = ref('')
let ultimaPeticion = 0
async function cargar() {
  const peticion = ++ultimaPeticion
  error.value = ''
  try {
    const resultado = await obtenerEstadisticas(filtros.value)
    if (peticion === ultimaPeticion) datos.value = resultado
  } catch (e) {
    if (peticion === ultimaPeticion) error.value = mensajeDeError(e)
  }
}
let pausa: ReturnType<typeof setTimeout> | undefined
const cargarConPausa = () => {
  clearTimeout(pausa)
  pausa = setTimeout(cargar, 350)
}
onBeforeUnmount(() => clearTimeout(pausa))
watch(() => JSON.stringify({ ...filtros.value, zona: undefined }), cargar)
watch(zonaTexto, cargarConPausa)

// ----- Graficas -----
const numero = (n: number) => n.toLocaleString('es-VE')
const coloresNivel = (porciones: Porcion[]) => porciones.map((p) => COLOR_NIVEL[p.clave as NivelInteraccion] ?? GRIS_OTROS)
// El color sigue al tipo (por id), no a su posicion en el ranking: filtrar no repinta las porciones
const coloresTipo = (porciones: Porcion[]) => porciones.map((p) => {
  if (p.clave === 'otros') return GRIS_OTROS
  const indice = listaTipos.value.findIndex((t) => String(t.id) === p.clave)
  return COLOR_TIPO[indice >= 0 ? indice % COLOR_TIPO.length : 0]!
})

const seriesInteraccion = computed<SerieGrafico[]>(() => {
  const puntos = datos.value?.serie.puntos ?? []
  return [
    { clave: 'consultas', nombre: 'Consultas y mensajes', color: COLOR_SERIE[0]!, valores: puntos.map((p) => p.consultas) },
    { clave: 'visitas', nombre: 'Visitas presenciales', color: COLOR_SERIE[1]!, valores: puntos.map((p) => p.visitas) },
    { clave: 'cierres', nombre: 'Cierres', color: COLOR_SERIE[2]!, valores: puntos.map((p) => p.cierres) },
  ]
})

const etiquetaNivel = computed(() => datos.value?.porNivel.find((p) => p.clave === nivel.value)?.etiqueta ?? '')
const tipoSeleccionado = computed(() => (tipos.value.length === 1 ? String(tipos.value[0]) : null))

// Porcion del pastel: filtra la tabla por ese nivel (o deja de filtrar si ya estaba elegido)
function elegirNivel(p: Porcion) {
  nivel.value = nivel.value === p.clave ? null : (p.clave as NivelInteraccion)
}
function elegirTipo(p: Porcion) {
  if (p.clave === 'otros') return
  tipos.value = tipoSeleccionado.value === p.clave ? [] : [Number(p.clave)]
}
// Punto de la grafica: un inmueble abre su detalle; una zona filtra por esa ciudad
function elegirPunto(indice: number) {
  const punto = datos.value?.serie.puntos[indice]
  if (!punto || punto.clave === 'otros') return
  if (agrupacion.value === 'propiedad') abrirInmueble(Number(punto.clave))
  else if (agrupacion.value === 'zona' && punto.clave !== 'sin') ciudades.value = [Number(punto.clave)]
}

const abrirInmueble = (id: number) =>
  router.push({ path: `/estadisticas/inmuebles/${id}`, query: nivel.value ? { nivel: nivel.value } : {} })

// ----- Rendimiento de ventas -----
const totalVentas = computed(() => {
  const v = datos.value?.vendedores ?? []
  return {
    ganados: v.reduce((s, x) => s + x.ganados, 0),
    perdidos: v.reduce((s, x) => s + x.perdidos, 0),
    monto: v.reduce((s, x) => s + Number(x.montoGanado), 0),
    abiertos: v.reduce((s, x) => s + x.abiertos, 0),
    pipeline: v.reduce((s, x) => s + Number(x.valorPipeline), 0),
    tareas: v.reduce((s, x) => s + x.tareasCompletadas, 0),
    vencidas: v.reduce((s, x) => s + x.tareasVencidas, 0),
  }
})
const tasaTotal = computed(() => {
  const { ganados, perdidos } = totalVentas.value
  return ganados + perdidos === 0 ? '—' : `${Math.round((ganados / (ganados + perdidos)) * 100)} %`
})
const maximoPipeline = computed(() => Math.max(0, ...(datos.value?.pipeline ?? []).map((e) => Number(e.valor))))

// ----- Informe -----
const mostrarInforme = ref(false)
const formatoInforme = ref<'xlsx' | 'pdf'>('xlsx')
const seccionesInforme = ref<SeccionInforme[]>([])
const exportando = ref(false)
const errorInforme = ref('')
watch(mostrarInforme, (abierto) => {
  if (!abierto) return
  errorInforme.value = ''
  // Por defecto, las secciones que se estan viendo
  seccionesInforme.value = SECCIONES.map((s) => s.valor).filter((s) => visible[s])
})
async function exportar() {
  exportando.value = true
  errorInforme.value = ''
  try {
    await exportarInforme(filtros.value, formatoInforme.value, seccionesInforme.value)
    mostrarInforme.value = false
  } catch (e) {
    errorInforme.value = mensajeDeError(e)
  } finally {
    exportando.value = false
  }
}

const mostrarAvanzados = ref(false)

onMounted(() => {
  cargar()
  obtenerPropietarios().then((lista) => { listaVendedores.value = lista }).catch(() => {})
  obtenerTiposInmueble().then((lista) => { listaTipos.value = lista }).catch(() => {})
  obtenerCiudades().then((lista) => { listaCiudades.value = lista }).catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/modulos.css"></style>
<style scoped src="./estadisticas/estadisticas.css"></style>
