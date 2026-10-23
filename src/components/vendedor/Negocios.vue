<template>
  <div class="crm-contactos">
    <MenuCrm activo="Negocios" />

    <main class="main-content">
      <header class="header">
        <div>
          <h1 class="greeting">Negocios</h1>
          <p class="subtitulo">Oportunidades de venta o alquiler de un inmueble con un cliente interesado, por etapas hasta el cierre</p>
        </div>
        <div class="acciones">
          <div class="alternar-vista" role="group" aria-label="Modo de vista">
            <button type="button" :class="{ activo: modo === 'tabla' }" :aria-pressed="modo === 'tabla'" @click="cambiarModo('tabla')">
              Vista de tabla
            </button>
            <button type="button" :class="{ activo: modo === 'tablero' }" :aria-pressed="modo === 'tablero'" @click="cambiarModo('tablero')">
              Vista de tablero
            </button>
          </div>
          <button v-if="modo === 'tabla'" type="button" class="btn-secundario" @click="mostrarColumnas = true">Editar columnas</button>
          <button type="button" class="btn-secundario" :disabled="totalElementos === 0" @click="mostrarExportar = true">Exportar</button>
          <button type="button" class="btn-filtros" @click="mostrarFormulario = true">Crear negocio</button>
        </div>
      </header>

      <nav class="pestanas" aria-label="Vistas de negocios">
        <button
          v-for="pestana in pestanas"
          :key="pestana.vista"
          type="button"
          :class="['pestana', { activa: vista === pestana.vista }]"
          :aria-current="vista === pestana.vista ? 'page' : undefined"
          @click="cambiarVista(pestana.vista)"
        >
          {{ pestana.texto }}
          <span v-if="conteos" class="contador">{{ conteos[pestana.conteo] }}</span>
        </button>
      </nav>

      <section class="search-section">
        <div class="search-input-wrapper">
          <svg class="icon-search" viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            v-model="q"
            type="search"
            maxlength="100"
            placeholder="Buscar por negocio, cliente, título o código del inmueble"
            class="search-input"
            aria-label="Buscar negocios"
            @input="recargarConPausa"
            @keyup.enter="recargar"
          />
        </div>
      </section>

      <section class="barra-filtros" aria-label="Filtros">
        <FiltroLista v-model="propietarios" etiqueta="Propietario del negocio" :opciones="opcionesPropietario" />
        <FiltroLista v-model="etapasFiltro" etiqueta="Etapa del negocio" :opciones="opcionesEtapa" />
        <FiltroLista v-model="operaciones" etiqueta="Tipo de operación" :opciones="OPCIONES_OPERACION" />
        <FiltroLista v-model="tiposInmueble" etiqueta="Tipo de inmueble" :opciones="opcionesTipo" />
        <FiltroLista v-model="creado" etiqueta="Fecha de creación" :opciones="RANGOS_FECHA" :multiple="false">
          <template v-if="creado[0] === 'personalizado'" #extra>
            <div class="rango">
              <label>Desde <input v-model="creadoDesde" type="date" :max="creadoHasta || undefined" /></label>
              <label>Hasta <input v-model="creadoHasta" type="date" :min="creadoDesde || undefined" /></label>
            </div>
          </template>
        </FiltroLista>
        <FiltroLista v-model="cierre" etiqueta="Fecha de cierre" :opciones="RANGOS_CIERRE" :multiple="false">
          <template v-if="cierre[0] === 'personalizado'" #extra>
            <div class="rango">
              <label>Desde <input v-model="cierreDesde" type="date" :max="cierreHasta || undefined" /></label>
              <label>Hasta <input v-model="cierreHasta" type="date" :min="cierreDesde || undefined" /></label>
            </div>
          </template>
        </FiltroLista>
        <button type="button" :class="['btn-avanzados', { activo: cantidadAvanzados > 0 }]" @click="mostrarAvanzados = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M7 12h10M10 18h4" />
          </svg>
          Filtros avanzados{{ cantidadAvanzados ? ` (${cantidadAvanzados})` : '' }}
        </button>
        <button v-if="hayFiltros" type="button" class="btn-limpiar" @click="limpiarFiltros">Limpiar filtros</button>
      </section>

      <p v-if="aviso" class="aviso-flotante" role="status">{{ aviso }}</p>

      <!-- Vista de tablero (pipeline) -->
      <section v-if="modo === 'tablero'" class="tabla-seccion">
        <p v-if="error" class="estado-msg error" role="alert">No se pudo cargar el tablero: {{ error }}</p>
        <p v-else-if="cargando && !tablero" class="estado-msg">Cargando tablero...</p>
        <template v-else-if="tablero">
          <p class="resumen-tablero">
            {{ totalTablero.cantidad }} negocios · Valor total {{ formatearPrecio(totalTablero.monto) }}
          </p>
          <TableroNegocios :columnas="tablero.columnas" :etapas="etapas" @mover="mover" @abrir="abrir" />
        </template>
      </section>

      <!-- Vista de tabla -->
      <section v-else class="tabla-seccion">
        <p v-if="error" class="estado-msg error" role="alert">No se pudieron cargar los negocios: {{ error }}</p>

        <div v-else class="table-container">
          <table class="user-table" :aria-busy="cargando">
            <thead>
              <tr>
                <th v-for="columna in columnasVisibles" :key="columna.clave" :aria-sort="ariaOrden(columna)">
                  <button v-if="columna.orden" type="button" class="th-orden" @click="ordenarPor(columna.orden)">
                    {{ columna.titulo }}
                    <span class="flecha-orden" aria-hidden="true">{{ indicadorOrden(columna) }}</span>
                  </button>
                  <span v-else>{{ columna.titulo }}</span>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="negocio in negocios" :key="negocio.id" class="fila" tabindex="0" @click="abrir(negocio)" @keyup.enter="abrir(negocio)">
                <td v-for="columna in columnasVisibles" :key="columna.clave">
                  <router-link v-if="columna.clave === 'nombre'" :to="`/negocios/${negocio.id}`" class="nombre" @click.stop>
                    {{ negocio.nombre }}
                  </router-link>
                  <span v-else-if="columna.clave === 'etapa'" :class="['badge', claseEtapa(negocio.etapaId)]">{{ negocio.etapa }}</span>
                  <router-link v-else-if="columna.clave === 'contacto'" :to="`/contact/${negocio.contactoId}`" class="enlace-celda" @click.stop>
                    {{ negocio.nombreContacto }}
                  </router-link>
                  <router-link v-else-if="columna.clave === 'inmueble'" :to="`/inmuebles/${negocio.inmuebleId}`" class="enlace-celda" @click.stop>
                    {{ negocio.codigoInmueble }} · {{ negocio.tipoInmueble }}
                  </router-link>
                  <span v-else-if="columna.clave === 'propietario'" :class="{ tenue: !negocio.nombrePropietario }">
                    {{ negocio.nombrePropietario ?? 'Sin asignar' }}
                  </span>
                  <span v-else>{{ valorCelda(negocio, columna.clave) }}</span>
                </td>
              </tr>
            </tbody>
          </table>

          <p v-if="cargando && negocios.length === 0" class="estado-msg">Cargando negocios...</p>
          <p v-else-if="!cargando && negocios.length === 0" class="estado-msg">{{ mensajeVacio }}</p>
        </div>

        <div v-if="!error && totalElementos > 0" class="pie-tabla">
          <label class="tamano">
            Filas por página
            <select v-model.number="tamano" @change="recargar">
              <option v-for="t in [10, 25, 50, 100]" :key="t" :value="t">{{ t }}</option>
            </select>
          </label>
          <Paginacion :pagina="pagina" :total-paginas="totalPaginas" :total-elementos="totalElementos" @cambiar="cargar" />
        </div>
      </section>
    </main>

    <ModalColumnas
      v-if="mostrarColumnas"
      :seleccion="columnas"
      :columnas="COLUMNAS_NEGOCIO"
      :por-defecto="COLUMNAS_NEGOCIO_POR_DEFECTO"
      @cerrar="mostrarColumnas = false"
      @aplicar="aplicarColumnas"
    />
    <ModalExportar
      v-if="mostrarExportar"
      :filtros="filtrosConPagina"
      :columnas="columnas"
      :disponibles="COLUMNAS_NEGOCIO"
      unidad="negocios"
      :exportar="exportarNegocios"
      :total-elementos="totalElementos"
      :filas-pagina="modo === 'tabla' ? negocios.length : Math.min(tamano, totalElementos)"
      @cerrar="mostrarExportar = false"
    />
    <PanelAvanzado
      v-if="mostrarAvanzados"
      v-model="avanzados"
      :campos="CAMPOS_AVANZADOS"
      :rangos="[['valorMin', 'valorMax']]"
      ayuda="un negocio debe cumplir todos."
      @cerrar="mostrarAvanzados = false"
    />
    <ModalNegocio
      v-if="mostrarFormulario"
      :etapas="etapas"
      :propietarios="listaPropietarios"
      @cerrar="mostrarFormulario = false"
      @guardado="alGuardar"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import Paginacion from '@/components/Paginacion.vue'
import MenuCrm from './MenuCrm.vue'
import FiltroLista from './contactos/FiltroLista.vue'
import ModalColumnas from './contactos/ModalColumnas.vue'
import ModalExportar from './contactos/ModalExportar.vue'
import PanelAvanzado, { type CampoAvanzado } from './contactos/PanelAvanzado.vue'
import ModalNegocio from './negocios/ModalNegocio.vue'
import TableroNegocios from './negocios/TableroNegocios.vue'
import { mensajeDeError } from '@/services/api'
import { obtenerTiposInmueble, type Catalogo } from '@/services/catalogos'
import { obtenerPropietarios } from '@/services/contactos'
import {
  buscarNegocios, cambiarEtapaNegocio, claseEtapa, COLUMNAS_NEGOCIO, COLUMNAS_NEGOCIO_POR_DEFECTO, contarNegocios,
  exportarNegocios, obtenerEtapas, obtenerTablero, SIN_PROPIETARIO,
  type ClaveColumnaNegocio, type ConteoPestanas, type EtapaNegocio, type FiltrosNegocios, type Negocio,
  type OrdenNegocios, type TableroNegocios as Tablero, type TipoOperacion, type VistaNegocios,
} from '@/services/negocios'
import { guardarColumnas, leerColumnasGuardadas, type ColumnaTabla } from '@/services/tablas'
import { aFechaIso, formatearFecha, RANGOS_FECHA, rangoAFechas } from '@/utils/fechas'
import { formatearPrecio } from '@/utils/formato'

const route = useRoute()
const router = useRouter()

// ----- Pestañas y modo (tabla o tablero) en la URL: /negocios?vista=mios&modo=tablero -----
const pestanas: { vista: VistaNegocios; texto: string; conteo: keyof ConteoPestanas }[] = [
  { vista: 'todos', texto: 'Todos los negocios', conteo: 'todos' },
  { vista: 'mios', texto: 'Mis negocios', conteo: 'mios' },
  { vista: 'sin_asignar', texto: 'Negocios sin asignar', conteo: 'terceraPestana' },
]
const vista = computed<VistaNegocios>(() => {
  const valor = route.query.vista
  return valor === 'mios' || valor === 'sin_asignar' ? valor : 'todos'
})
const modo = computed<'tabla' | 'tablero'>(() => (route.query.modo === 'tablero' ? 'tablero' : 'tabla'))
const cambiarVista = (nueva: VistaNegocios) =>
  router.replace({ query: { ...route.query, vista: nueva === 'todos' ? undefined : nueva } })
const cambiarModo = (nuevo: 'tabla' | 'tablero') =>
  router.replace({ query: { ...route.query, modo: nuevo === 'tabla' ? undefined : nuevo } })

// ----- Filtros -----
const OPCIONES_OPERACION = [{ valor: 'venta', texto: 'Venta' }, { valor: 'alquiler', texto: 'Alquiler' }]
// La fecha de cierre estimada suele estar en el futuro
const RANGOS_CIERRE = [
  { valor: 'vencidos', texto: 'Vencidos (antes de hoy)' },
  { valor: 'esteMes', texto: 'Este mes' },
  { valor: 'proximos30', texto: 'Próximos 30 días' },
  { valor: 'proximos90', texto: 'Próximos 90 días' },
  { valor: 'personalizado', texto: 'Rango personalizado' },
]
const CAMPOS_AVANZADOS: CampoAvanzado[] = [
  { clave: 'valorMin', etiqueta: 'Valor mínimo (USD)', numerico: true, placeholder: 'Ej: 50000' },
  { clave: 'valorMax', etiqueta: 'Valor máximo (USD)', numerico: true, placeholder: 'Ej: 200000' },
  { clave: 'contacto', etiqueta: 'Cliente contiene', placeholder: 'Ej: María López' },
  { clave: 'codigoInmueble', etiqueta: 'Código RRBR contiene', placeholder: 'Ej: RRBR-120' },
  {
    clave: 'abiertos', etiqueta: 'Estado del negocio',
    opciones: [{ valor: 'true', texto: 'En marcha (etapas abiertas)' }, { valor: 'false', texto: 'Cerrados (ganados o perdidos)' }],
  },
]
const avanzadosVacios = () => ({ valorMin: '', valorMax: '', contacto: '', codigoInmueble: '', abiertos: '' })

const q = ref('')
const propietarios = ref<(string | number)[]>([])
const etapasFiltro = ref<(string | number)[]>([])
const operaciones = ref<(string | number)[]>([])
const tiposInmueble = ref<(string | number)[]>([])
const creado = ref<(string | number)[]>([])
const creadoDesde = ref('')
const creadoHasta = ref('')
const cierre = ref<(string | number)[]>([])
const cierreDesde = ref('')
const cierreHasta = ref('')
const avanzados = ref<Record<string, string>>(avanzadosVacios())

const listaPropietarios = ref<Catalogo[]>([])
const etapas = ref<EtapaNegocio[]>([])
const listaTipos = ref<Catalogo[]>([])
const opcionesPropietario = computed(() => [
  { valor: SIN_PROPIETARIO, texto: 'Sin asignar' },
  ...listaPropietarios.value.map((p) => ({ valor: p.id, texto: p.nombre })),
])
const opcionesEtapa = computed(() => etapas.value.map((e) => ({ valor: e.id, texto: e.nombre })))
const opcionesTipo = computed(() => listaTipos.value.map((t) => ({ valor: t.id, texto: t.nombre })))

const cantidadAvanzados = computed(() => Object.values(avanzados.value).filter((v) => v.trim() !== '').length)
const hayFiltros = computed(() =>
  q.value.trim() !== '' || propietarios.value.length > 0 || etapasFiltro.value.length > 0 || operaciones.value.length > 0
  || tiposInmueble.value.length > 0 || creado.value.length > 0 || cierre.value.length > 0 || cantidadAvanzados.value > 0)

function limpiarFiltros() {
  q.value = ''
  propietarios.value = []
  etapasFiltro.value = []
  operaciones.value = []
  tiposInmueble.value = []
  creado.value = []
  creadoDesde.value = ''
  creadoHasta.value = ''
  cierre.value = []
  cierreDesde.value = ''
  cierreHasta.value = ''
  avanzados.value = avanzadosVacios()
}

function rangoCierre(opcion: string): { desde?: string; hasta?: string } {
  const hoy = new Date()
  const enDias = (dias: number) => aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate() + dias))
  switch (opcion) {
    case 'vencidos': return { hasta: enDias(-1) }
    case 'esteMes': return {
      desde: aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth(), 1)),
      hasta: aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0)),
    }
    case 'proximos30': return { desde: enDias(0), hasta: enDias(30) }
    case 'proximos90': return { desde: enDias(0), hasta: enDias(90) }
    case 'personalizado': return { desde: cierreDesde.value || undefined, hasta: cierreHasta.value || undefined }
    default: return {}
  }
}

const filtros = computed<FiltrosNegocios>(() => {
  const fechasCreacion = rangoAFechas(String(creado.value[0] ?? ''), creadoDesde.value, creadoHasta.value)
  const fechasCierre = rangoCierre(String(cierre.value[0] ?? ''))
  const a = avanzados.value
  return {
    vista: vista.value,
    q: q.value.trim(),
    propietarioIds: propietarios.value.map(Number),
    etapas: etapasFiltro.value.map(Number),
    tiposOperacion: operaciones.value.map(String) as TipoOperacion[],
    tipoInmuebleIds: tiposInmueble.value.map(Number),
    creadoDesde: fechasCreacion.desde,
    creadoHasta: fechasCreacion.hasta,
    cierreDesde: fechasCierre.desde,
    cierreHasta: fechasCierre.hasta,
    valorMin: a.valorMin,
    valorMax: a.valorMax,
    contacto: a.contacto,
    codigoInmueble: a.codigoInmueble,
    abiertos: a.abiertos === '' ? undefined : a.abiertos === 'true',
    orden: orden.value,
    direccion: direccion.value,
  }
})

// ----- Orden -----
const orden = ref<OrdenNegocios>('fechaCreacion')
const direccion = ref<'asc' | 'desc'>('desc')
function ordenarPor(campo: OrdenNegocios) {
  if (orden.value === campo) {
    direccion.value = direccion.value === 'asc' ? 'desc' : 'asc'
  } else {
    orden.value = campo
    direccion.value = campo === 'fechaCreacion' || campo === 'valor' ? 'desc' : 'asc'
  }
}
const indicadorOrden = (columna: ColumnaTabla) =>
  columna.orden === orden.value ? (direccion.value === 'asc' ? '▲' : '▼') : '↕'
const ariaOrden = (columna: ColumnaTabla) =>
  columna.orden === orden.value ? (direccion.value === 'asc' ? 'ascending' : 'descending') : undefined

// ----- Columnas visibles -----
const CLAVE_COLUMNAS = 'rr_columnas_negocios'
const columnas = ref<ClaveColumnaNegocio[]>(
  leerColumnasGuardadas(CLAVE_COLUMNAS, COLUMNAS_NEGOCIO, COLUMNAS_NEGOCIO_POR_DEFECTO, 'nombre'))
const columnasVisibles = computed(() =>
  columnas.value.map((clave) => COLUMNAS_NEGOCIO.find((c) => c.clave === clave)!).filter(Boolean))
const mostrarColumnas = ref(false)
function aplicarColumnas(nuevas: string[]) {
  columnas.value = nuevas as ClaveColumnaNegocio[]
  mostrarColumnas.value = false
  guardarColumnas(CLAVE_COLUMNAS, nuevas)
}

function valorCelda(n: Negocio, clave: ClaveColumnaNegocio): string {
  switch (clave) {
    case 'operacion': return n.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Venta'
    case 'valor': return formatearPrecio(n.valor)
    case 'fechaCierre': return n.fechaCierreEstimada ? formatearFecha(`${n.fechaCierreEstimada}T00:00:00`) : '—'
    case 'fechaCreacion': return formatearFecha(n.fechaCreacion)
    case 'fechaActualizacion': return formatearFecha(n.fechaActualizacion)
    case 'tituloInmueble': return n.tituloInmueble
    default: return ''
  }
}

// ----- Carga de datos -----
const negocios = ref<Negocio[]>([])
const tablero = ref<Tablero | null>(null)
const conteos = ref<ConteoPestanas | null>(null)
const pagina = ref(1)
const tamano = ref(25)
const totalPaginas = ref(0)
const totalElementos = ref(0)
const cargando = ref(false)
const error = ref('')
const aviso = ref('')
let ultimaPeticion = 0

const filtrosConPagina = computed<FiltrosNegocios>(() => ({ ...filtros.value, pagina: pagina.value - 1, tamano: tamano.value }))

const totalTablero = computed(() => ({
  cantidad: tablero.value?.columnas.reduce((s, c) => s + c.cantidad, 0) ?? 0,
  monto: tablero.value?.columnas.reduce((s, c) => s + Number(c.total), 0) ?? 0,
}))

async function cargar(numeroPagina = 1) {
  const peticion = ++ultimaPeticion
  cargando.value = true
  error.value = ''
  try {
    if (modo.value === 'tablero') {
      const resultado = await obtenerTablero(filtros.value)
      if (peticion !== ultimaPeticion) return
      tablero.value = resultado
      totalElementos.value = resultado.columnas.reduce((s, c) => s + c.cantidad, 0)
    } else {
      const resultado = await buscarNegocios({ ...filtros.value, pagina: numeroPagina - 1, tamano: tamano.value })
      if (peticion !== ultimaPeticion) return
      negocios.value = resultado.contenido
      pagina.value = resultado.pagina + 1
      totalPaginas.value = resultado.totalPaginas
      totalElementos.value = resultado.totalElementos
    }
  } catch (e) {
    if (peticion === ultimaPeticion) error.value = mensajeDeError(e)
  } finally {
    if (peticion === ultimaPeticion) cargando.value = false
  }
}

const claveConteos = computed(() => JSON.stringify({ ...filtros.value, vista: undefined, orden: undefined, direccion: undefined }))
async function cargarConteos() {
  try {
    conteos.value = await contarNegocios(filtros.value)
  } catch {
    conteos.value = null
  }
}

const recargar = () => cargar(1)
let pausa: ReturnType<typeof setTimeout> | undefined
function recargarConPausa() {
  clearTimeout(pausa)
  pausa = setTimeout(recargar, 350)
}
let pausaAviso: ReturnType<typeof setTimeout> | undefined
function mostrarAviso(texto: string) {
  aviso.value = texto
  clearTimeout(pausaAviso)
  pausaAviso = setTimeout(() => { aviso.value = '' }, 4000)
}
onBeforeUnmount(() => {
  clearTimeout(pausa)
  clearTimeout(pausaAviso)
})

watch(() => JSON.stringify({ ...filtros.value, q: undefined }), recargar)
watch(modo, recargar)
watch(claveConteos, cargarConteos)

const mensajeVacio = computed(() => {
  if (hayFiltros.value) return 'Ningún negocio coincide con los filtros.'
  if (vista.value === 'mios') return 'Todavía no tienes negocios a tu cargo. Puedes crear uno o tomar uno sin asignar.'
  if (vista.value === 'sin_asignar') return 'Todos los negocios tienen un propietario asignado.'
  return 'Aún no hay negocios. Crea el primero con "Crear negocio".'
})

const abrir = (n: Negocio) => router.push(`/negocios/${n.id}`)

// Arrastrar y soltar: se mueve la tarjeta de inmediato y se revierte si el backend lo rechaza
async function mover(negocio: Negocio, etapaId: number) {
  const columnasTablero = tablero.value?.columnas
  const origen = columnasTablero?.find((c) => c.etapaId === negocio.etapaId)
  const destino = columnasTablero?.find((c) => c.etapaId === etapaId)
  if (origen && destino) {
    origen.negocios = origen.negocios.filter((n) => n.id !== negocio.id)
    origen.cantidad -= 1
    origen.total = Number(origen.total) - Number(negocio.valor)
    destino.negocios = [{ ...negocio, etapaId, etapa: destino.etapa, etapaAbierta: destino.abierta }, ...destino.negocios]
    destino.cantidad += 1
    destino.total = Number(destino.total) + Number(negocio.valor)
  }
  try {
    await cambiarEtapaNegocio(negocio.id, etapaId)
    mostrarAviso(`"${negocio.nombre}" pasó a ${destino?.etapa ?? 'la nueva etapa'}.`)
  } catch (e) {
    mostrarAviso(`No se pudo mover el negocio: ${mensajeDeError(e)}`)
  }
  await cargar(pagina.value)
  cargarConteos()
}

// ----- Crear negocio -----
const mostrarFormulario = ref(false)
function alGuardar(id: number) {
  mostrarFormulario.value = false
  router.push(`/negocios/${id}`)
}

const mostrarExportar = ref(false)
const mostrarAvanzados = ref(false)

onMounted(() => {
  recargar()
  cargarConteos()
  obtenerPropietarios().then((lista) => { listaPropietarios.value = lista }).catch(() => {})
  obtenerEtapas().then((lista) => { etapas.value = lista }).catch(() => {})
  obtenerTiposInmueble().then((lista) => { listaTipos.value = lista }).catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
<style scoped src="./contactos/modulos.css"></style>
