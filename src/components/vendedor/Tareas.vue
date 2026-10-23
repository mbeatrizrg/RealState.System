<template>
  <div class="crm-contactos">
    <MenuCrm activo="Tareas" />

    <main class="main-content">
      <header class="header">
        <div>
          <h1 class="greeting">Tareas</h1>
          <p class="subtitulo">Llamadas, visitas guiadas, envíos y trámites que hay que hacer en una fecha concreta</p>
        </div>
        <div class="acciones">
          <button type="button" class="btn-secundario" @click="mostrarColumnas = true">Editar columnas</button>
          <button type="button" class="btn-secundario" :disabled="totalElementos === 0" @click="mostrarExportar = true">Exportar</button>
          <button type="button" class="btn-filtros" @click="abrirFormulario(null)">Crear tarea</button>
        </div>
      </header>

      <nav class="pestanas" aria-label="Vistas de tareas">
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
          <input v-model="q" type="search" maxlength="100" placeholder="Buscar por título, descripción o contacto" class="search-input"
                 aria-label="Buscar tareas" @input="recargarConPausa" @keyup.enter="recargar" />
        </div>
      </section>

      <section class="barra-filtros" aria-label="Filtros">
        <FiltroLista v-model="propietarios" etiqueta="Propietario de la tarea" :opciones="opcionesPropietario" />
        <FiltroLista v-model="tipos" etiqueta="Tipo de tarea" :opciones="TIPOS_TAREA" />
        <FiltroLista v-model="estados" etiqueta="Estado de la tarea" :opciones="ESTADOS_TAREA" />
        <FiltroLista v-model="prioridades" etiqueta="Prioridad" :opciones="PRIORIDADES" />
        <FiltroLista v-model="vencimiento" etiqueta="Fecha de vencimiento" :opciones="RANGOS_VENCIMIENTO" :multiple="false">
          <template v-if="vencimiento[0] === 'personalizado'" #extra>
            <div class="rango">
              <label>Desde <input v-model="venceDesde" type="date" :max="venceHasta || undefined" /></label>
              <label>Hasta <input v-model="venceHasta" type="date" :min="venceDesde || undefined" /></label>
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

      <section class="tabla-seccion">
        <p v-if="error" class="estado-msg error" role="alert">No se pudieron cargar las tareas: {{ error }}</p>

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
              <tr v-for="t in tareas" :key="t.id" :class="['fila', { hecha: t.estado === 'completada' }]" tabindex="0"
                  @click="abrir(t)" @keyup.enter="abrir(t)">
                <td v-for="columna in columnasVisibles" :key="columna.clave">
                  <button v-if="columna.clave === 'titulo'" type="button" class="nombre boton-enlace" @click.stop="abrir(t)">{{ t.titulo }}</button>
                  <span v-else-if="columna.clave === 'estado'" :class="['badge', claseEstadoTarea(t.estado)]">{{ textoEstadoTarea(t.estado) }}</span>
                  <span v-else-if="columna.clave === 'prioridad'" :class="['badge', clasePrioridad(t.prioridad)]">{{ textoPrioridad(t.prioridad) }}</span>
                  <router-link v-else-if="columna.clave === 'contacto' && t.contactoId" :to="`/contact/${t.contactoId}`" class="enlace-celda" @click.stop>
                    {{ t.nombreContacto }}
                  </router-link>
                  <router-link v-else-if="columna.clave === 'vinculo' && t.negocioId" :to="`/negocios/${t.negocioId}`" class="enlace-celda" @click.stop>
                    {{ t.nombreNegocio }}
                  </router-link>
                  <router-link v-else-if="columna.clave === 'vinculo' && t.inmuebleId" :to="`/inmuebles/${t.inmuebleId}`" class="enlace-celda" @click.stop>
                    {{ t.codigoInmueble }} · {{ t.tituloInmueble }}
                  </router-link>
                  <span v-else-if="columna.clave === 'vencimiento'" :class="{ vencida: t.estado === 'vencida' }">{{ formatearFechaHora(t.fechaVencimiento) }}</span>
                  <span v-else :class="{ tenue: valorCelda(t, columna.clave) === '—' }">{{ valorCelda(t, columna.clave) }}</span>
                </td>
              </tr>
            </tbody>
          </table>

          <p v-if="cargando && tareas.length === 0" class="estado-msg">Cargando tareas...</p>
          <p v-else-if="!cargando && tareas.length === 0" class="estado-msg">{{ mensajeVacio }}</p>
        </div>

        <div v-if="!error && totalElementos > 0" class="pie-tabla">
          <label class="tamano">
            Filas por página
            <select v-model.number="tamano" @change="recargar">
              <option v-for="n in [10, 25, 50, 100]" :key="n" :value="n">{{ n }}</option>
            </select>
          </label>
          <Paginacion :pagina="pagina" :total-paginas="totalPaginas" :total-elementos="totalElementos" @cambiar="cargar" />
        </div>
      </section>
    </main>

    <ModalColumnas v-if="mostrarColumnas" :seleccion="columnas" :columnas="COLUMNAS_TAREA" :por-defecto="COLUMNAS_TAREA_POR_DEFECTO"
                   fija="titulo" @cerrar="mostrarColumnas = false" @aplicar="aplicarColumnas" />
    <ModalExportar v-if="mostrarExportar" :filtros="filtrosConPagina" :columnas="columnas" :disponibles="COLUMNAS_TAREA" unidad="tareas"
                   :exportar="exportarTareas" :total-elementos="totalElementos" :filas-pagina="tareas.length" @cerrar="mostrarExportar = false" />
    <PanelAvanzado v-if="mostrarAvanzados" v-model="avanzados" :campos="CAMPOS_AVANZADOS" ayuda="una tarea debe cumplir todos."
                   @cerrar="mostrarAvanzados = false" />
    <DetalleTarea v-if="seleccionada" :tarea="seleccionada" @cerrar="cerrarDetalle" @cambio="alCambiar" @editar="abrirFormulario" />
    <ModalTarea v-if="mostrarFormulario" :propietarios="listaPropietarios" :tarea="editando" @cerrar="mostrarFormulario = false" @guardada="alGuardar" />
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
import DetalleTarea from './tareas/DetalleTarea.vue'
import ModalTarea from './tareas/ModalTarea.vue'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import { obtenerPropietarios } from '@/services/contactos'
import type { ConteoPestanas } from '@/services/negocios'
import { guardarColumnas, leerColumnasGuardadas, type ColumnaTabla } from '@/services/tablas'
import {
  buscarTareas, claseEstadoTarea, clasePrioridad, COLUMNAS_TAREA, COLUMNAS_TAREA_POR_DEFECTO, contarTareas, ESTADOS_TAREA,
  exportarTareas, obtenerTarea, PRIORIDADES, SIN_PROPIETARIO, textoEstadoTarea, textoPrioridad, textoTipoTarea, TIPOS_TAREA,
  type ClaveColumnaTarea, type EstadoTarea, type FiltrosTareas, type OrdenTareas, type Prioridad, type Tarea, type TipoTarea,
  type VistaTareas,
} from '@/services/tareas'
import { aFechaIso, formatearFecha, formatearFechaHora } from '@/utils/fechas'

const route = useRoute()
const router = useRouter()

// ----- Pestañas en la URL (/tareas?vista=mias_pendientes); ?tarea=ID abre el detalle -----
const pestanas: { vista: VistaTareas; texto: string; conteo: keyof ConteoPestanas }[] = [
  { vista: 'todas', texto: 'Todas las tareas', conteo: 'todos' },
  { vista: 'mias_pendientes', texto: 'Mis tareas pendientes', conteo: 'mios' },
  { vista: 'completadas_vencidas', texto: 'Tareas completadas / vencidas', conteo: 'terceraPestana' },
]
const vista = computed<VistaTareas>(() => {
  const valor = route.query.vista
  return valor === 'mias_pendientes' || valor === 'completadas_vencidas' ? valor : 'todas'
})
const cambiarVista = (nueva: VistaTareas) =>
  router.replace({ query: { ...route.query, vista: nueva === 'todas' ? undefined : nueva } })

// ----- Filtros -----
const RANGOS_VENCIMIENTO = [
  { valor: 'vencidas', texto: 'Antes de hoy' },
  { valor: 'hoy', texto: 'Hoy' },
  { valor: 'manana', texto: 'Mañana' },
  { valor: 'semana', texto: 'Esta semana' },
  { valor: 'proximos7', texto: 'Próximos 7 días' },
  { valor: 'mes', texto: 'Este mes' },
  { valor: 'personalizado', texto: 'Rango personalizado' },
]
const CAMPOS_AVANZADOS: CampoAvanzado[] = [
  { clave: 'contacto', etiqueta: 'Contacto contiene', placeholder: 'Ej: María López' },
  { clave: 'codigoInmueble', etiqueta: 'Código RRBR contiene', placeholder: 'Ej: RRBR-120' },
  { clave: 'negocio', etiqueta: 'Negocio contiene', placeholder: 'Ej: Venta Apto.' },
  { clave: 'sinContacto', etiqueta: 'Contacto asociado', opciones: [{ valor: 'true', texto: 'Solo tareas sin contacto' }] },
]
const avanzadosVacios = () => ({ contacto: '', codigoInmueble: '', negocio: '', sinContacto: '' })

const q = ref('')
const propietarios = ref<(string | number)[]>([])
const tipos = ref<(string | number)[]>([])
const estados = ref<(string | number)[]>([])
const prioridades = ref<(string | number)[]>([])
const vencimiento = ref<(string | number)[]>([])
const venceDesde = ref('')
const venceHasta = ref('')
const avanzados = ref<Record<string, string>>(avanzadosVacios())

const listaPropietarios = ref<Catalogo[]>([])
const opcionesPropietario = computed(() => [
  { valor: SIN_PROPIETARIO, texto: 'Sin asignar' },
  ...listaPropietarios.value.map((p) => ({ valor: p.id, texto: p.nombre })),
])

const cantidadAvanzados = computed(() => Object.values(avanzados.value).filter((v) => v.trim() !== '').length)
const hayFiltros = computed(() =>
  q.value.trim() !== '' || propietarios.value.length > 0 || tipos.value.length > 0 || estados.value.length > 0
  || prioridades.value.length > 0 || vencimiento.value.length > 0 || cantidadAvanzados.value > 0)

function limpiarFiltros() {
  q.value = ''
  propietarios.value = []
  tipos.value = []
  estados.value = []
  prioridades.value = []
  vencimiento.value = []
  venceDesde.value = ''
  venceHasta.value = ''
  avanzados.value = avanzadosVacios()
}

function rangoVencimiento(opcion: string): { desde?: string; hasta?: string } {
  const hoy = new Date()
  const dia = (n: number) => aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate() + n))
  const lunes = (hoy.getDay() + 6) % 7
  switch (opcion) {
    case 'vencidas': return { hasta: dia(-1) }
    case 'hoy': return { desde: dia(0), hasta: dia(0) }
    case 'manana': return { desde: dia(1), hasta: dia(1) }
    case 'semana': return { desde: dia(-lunes), hasta: dia(6 - lunes) }
    case 'proximos7': return { desde: dia(0), hasta: dia(6) }
    case 'mes': return {
      desde: aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth(), 1)),
      hasta: aFechaIso(new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0)),
    }
    case 'personalizado': return { desde: venceDesde.value || undefined, hasta: venceHasta.value || undefined }
    default: return {}
  }
}

const filtros = computed<FiltrosTareas>(() => {
  const fechas = rangoVencimiento(String(vencimiento.value[0] ?? ''))
  const a = avanzados.value
  return {
    vista: vista.value,
    q: q.value.trim(),
    propietarioIds: propietarios.value.map(Number),
    tiposTarea: tipos.value.map(String) as TipoTarea[],
    estados: estados.value.map(String) as EstadoTarea[],
    prioridades: prioridades.value.map(String) as Prioridad[],
    venceDesde: fechas.desde,
    venceHasta: fechas.hasta,
    contacto: a.contacto,
    codigoInmueble: a.codigoInmueble,
    negocio: a.negocio,
    sinContacto: a.sinContacto === 'true' ? true : undefined,
    orden: orden.value,
    direccion: direccion.value,
  }
})

// ----- Orden: por defecto lo que vence primero -----
const orden = ref<OrdenTareas>('fechaProgramada')
const direccion = ref<'asc' | 'desc'>('asc')
function ordenarPor(campo: OrdenTareas) {
  if (orden.value === campo) {
    direccion.value = direccion.value === 'asc' ? 'desc' : 'asc'
  } else {
    orden.value = campo
    direccion.value = campo === 'prioridad' || campo === 'fechaCreacion' ? 'desc' : 'asc'
  }
}
const indicadorOrden = (columna: ColumnaTabla) =>
  columna.orden === orden.value ? (direccion.value === 'asc' ? '▲' : '▼') : '↕'
const ariaOrden = (columna: ColumnaTabla) =>
  columna.orden === orden.value ? (direccion.value === 'asc' ? 'ascending' : 'descending') : undefined

// ----- Columnas visibles -----
const CLAVE_COLUMNAS = 'rr_columnas_tareas'
const columnas = ref<ClaveColumnaTarea[]>(leerColumnasGuardadas(CLAVE_COLUMNAS, COLUMNAS_TAREA, COLUMNAS_TAREA_POR_DEFECTO, 'titulo'))
const columnasVisibles = computed(() => columnas.value.map((clave) => COLUMNAS_TAREA.find((c) => c.clave === clave)!).filter(Boolean))
const mostrarColumnas = ref(false)
function aplicarColumnas(nuevas: string[]) {
  columnas.value = nuevas as ClaveColumnaTarea[]
  mostrarColumnas.value = false
  guardarColumnas(CLAVE_COLUMNAS, nuevas)
}

function valorCelda(t: Tarea, clave: ClaveColumnaTarea): string {
  switch (clave) {
    case 'tipo': return textoTipoTarea(t.tipoTarea)
    case 'contacto': return t.nombreContacto ?? '—'
    case 'vinculo': return '—'
    case 'propietario': return t.nombrePropietario ?? 'Sin asignar'
    case 'fechaCreacion': return formatearFecha(t.fechaCreacion)
    case 'descripcion': return t.descripcion ? (t.descripcion.length > 80 ? `${t.descripcion.slice(0, 80)}…` : t.descripcion) : '—'
    default: return ''
  }
}

// ----- Carga -----
const tareas = ref<Tarea[]>([])
const conteos = ref<ConteoPestanas | null>(null)
const pagina = ref(1)
const tamano = ref(25)
const totalPaginas = ref(0)
const totalElementos = ref(0)
const cargando = ref(false)
const error = ref('')
const aviso = ref('')
let ultimaPeticion = 0

const filtrosConPagina = computed<FiltrosTareas>(() => ({ ...filtros.value, pagina: pagina.value - 1, tamano: tamano.value }))

async function cargar(numeroPagina = 1) {
  const peticion = ++ultimaPeticion
  cargando.value = true
  error.value = ''
  try {
    const resultado = await buscarTareas({ ...filtros.value, pagina: numeroPagina - 1, tamano: tamano.value })
    if (peticion !== ultimaPeticion) return
    tareas.value = resultado.contenido
    pagina.value = resultado.pagina + 1
    totalPaginas.value = resultado.totalPaginas
    totalElementos.value = resultado.totalElementos
  } catch (e) {
    if (peticion === ultimaPeticion) error.value = mensajeDeError(e)
  } finally {
    if (peticion === ultimaPeticion) cargando.value = false
  }
}

const claveConteos = computed(() => JSON.stringify({ ...filtros.value, vista: undefined, orden: undefined, direccion: undefined }))
async function cargarConteos() {
  try {
    conteos.value = await contarTareas(filtros.value)
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
watch(claveConteos, cargarConteos)

const mensajeVacio = computed(() => {
  if (hayFiltros.value) return 'Ninguna tarea coincide con los filtros.'
  if (vista.value === 'mias_pendientes') return 'No tienes tareas pendientes. ¡Todo al día!'
  if (vista.value === 'completadas_vencidas') return 'No hay tareas completadas ni vencidas.'
  return 'Aún no hay tareas. Crea la primera con "Crear tarea".'
})

// ----- Detalle (?tarea=ID para poder compartir el enlace) -----
const seleccionada = ref<Tarea | null>(null)
const abrir = (t: Tarea) => router.replace({ query: { ...route.query, tarea: String(t.id) } })
const cerrarDetalle = () => router.replace({ query: { ...route.query, tarea: undefined } })

watch(() => route.query.tarea, async (id) => {
  if (!id) {
    seleccionada.value = null
    return
  }
  const enTabla = tareas.value.find((t) => String(t.id) === String(id))
  try {
    seleccionada.value = enTabla ?? await obtenerTarea(String(id))
  } catch (e) {
    mostrarAviso(mensajeDeError(e))
    cerrarDetalle()
  }
}, { immediate: true })

function alCambiar(tarea: Tarea | null) {
  if (tarea) {
    seleccionada.value = tarea
    mostrarAviso(tarea.estado === 'completada' ? 'Tarea completada.' : 'Estado actualizado.')
  } else {
    cerrarDetalle()
    mostrarAviso('Tarea eliminada.')
  }
  cargar(pagina.value)
  cargarConteos()
}

// ----- Crear / editar -----
const mostrarFormulario = ref(false)
const editando = ref<Tarea | null>(null)
function abrirFormulario(tarea: Tarea | null) {
  editando.value = tarea
  mostrarFormulario.value = true
}
function alGuardar(tarea: Tarea) {
  mostrarFormulario.value = false
  mostrarAviso(editando.value ? 'Tarea actualizada.' : 'Tarea creada.')
  if (seleccionada.value?.id === tarea.id) seleccionada.value = tarea
  editando.value = null
  cargar(pagina.value)
  cargarConteos()
}

const mostrarExportar = ref(false)
const mostrarAvanzados = ref(false)

onMounted(() => {
  recargar()
  cargarConteos()
  obtenerPropietarios().then((lista) => { listaPropietarios.value = lista }).catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
<style scoped src="./contactos/modulos.css"></style>
<style scoped>
.boton-enlace {
  background: none;
  border: none;
  padding: 0;
  font: inherit;
  text-align: left;
  cursor: pointer;
}

.fila.hecha .nombre {
  text-decoration: line-through;
  color: #636363;
}

.vencida {
  color: #991b1b;
  font-weight: 600;
}
</style>
