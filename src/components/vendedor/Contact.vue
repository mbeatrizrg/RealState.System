<template>
  <div class="crm-contactos">
    <MenuCrm activo="Contactos" />

    <main class="main-content">
      <header class="header">
        <div>
          <h1 class="greeting">Contactos</h1>
          <p class="subtitulo">Personas con las que la empresa tiene o puede tener una relación comercial</p>
        </div>
        <div class="acciones">
          <button type="button" class="btn-secundario" @click="mostrarColumnas = true">Editar columnas</button>
          <button type="button" class="btn-filtros" :disabled="totalElementos === 0" @click="mostrarExportar = true">
            Exportar
          </button>
        </div>
      </header>

      <!-- Barra superior de navegación: las tres páginas del módulo -->
      <nav class="pestanas" aria-label="Vistas de contactos">
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
            placeholder="Buscar por nombre, correo o teléfono"
            class="search-input"
            aria-label="Buscar contactos"
            @input="recargarConPausa"
            @keyup.enter="recargar"
          />
        </div>
      </section>

      <!-- Filtros de la tabla: cada uno abre una lista con buscador -->
      <section class="barra-filtros" aria-label="Filtros">
        <FiltroLista v-model="propietarios" etiqueta="Propietario del contacto" :opciones="opcionesPropietario" />
        <FiltroLista v-model="creado" etiqueta="Fecha de creación" :opciones="RANGOS_FECHA" :multiple="false">
          <template v-if="creado[0] === 'personalizado'" #extra>
            <div class="rango">
              <label>Desde <input v-model="creadoDesde" type="date" :max="creadoHasta || undefined" /></label>
              <label>Hasta <input v-model="creadoHasta" type="date" :min="creadoDesde || undefined" /></label>
            </div>
          </template>
        </FiltroLista>
        <FiltroLista v-model="actividad" etiqueta="Última actividad" :opciones="opcionesActividad" :multiple="false">
          <template v-if="actividad[0] === 'personalizado'" #extra>
            <div class="rango">
              <label>Desde <input v-model="actividadDesde" type="date" :max="actividadHasta || undefined" /></label>
              <label>Hasta <input v-model="actividadHasta" type="date" :min="actividadDesde || undefined" /></label>
            </div>
          </template>
        </FiltroLista>
        <FiltroLista v-model="estatus" etiqueta="Estado del lead" :opciones="opcionesEstatus" />
        <button type="button" :class="['btn-avanzados', { activo: cantidadAvanzados > 0 }]" @click="mostrarAvanzados = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M7 12h10M10 18h4" />
          </svg>
          Filtros avanzados{{ cantidadAvanzados ? ` (${cantidadAvanzados})` : '' }}
        </button>
        <button v-if="hayFiltros" type="button" class="btn-limpiar" @click="limpiarFiltros">Limpiar filtros</button>
      </section>

      <section class="tabla-seccion">
        <p v-if="error" class="estado-msg error" role="alert">No se pudieron cargar los contactos: {{ error }}</p>

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
              <tr
                v-for="contacto in contactos"
                :key="contacto.id"
                class="fila"
                tabindex="0"
                @click="abrir(contacto)"
                @keyup.enter="abrir(contacto)"
              >
                <td v-for="columna in columnasVisibles" :key="columna.clave">
                  <div v-if="columna.clave === 'nombre'" class="celda-nombre">
                    <span class="avatar" aria-hidden="true">{{ iniciales(contacto) }}</span>
                    <router-link :to="`/contact/${contacto.id}`" class="nombre" @click.stop>
                      {{ contacto.nombre }} {{ contacto.apellido }}
                    </router-link>
                  </div>
                  <span v-else-if="columna.clave === 'estatus'" :class="['badge', claseEstatus(contacto.estatusId)]">
                    {{ contacto.estatus ?? 'Sin lead' }}
                  </span>
                  <span v-else-if="columna.clave === 'propietario'" :class="{ tenue: !contacto.nombrePropietario }">
                    {{ contacto.nombrePropietario ?? 'Sin asignar' }}
                  </span>
                  <span v-else>{{ valorCelda(contacto, columna.clave) }}</span>
                </td>
              </tr>
            </tbody>
          </table>

          <p v-if="cargando && contactos.length === 0" class="estado-msg">Cargando contactos...</p>
          <p v-else-if="!cargando && contactos.length === 0" class="estado-msg">{{ mensajeVacio }}</p>
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

    <ModalColumnas v-if="mostrarColumnas" :seleccion="columnas" @cerrar="mostrarColumnas = false" @aplicar="aplicarColumnas" />
    <ModalExportar
      v-if="mostrarExportar"
      :filtros="filtrosConPagina"
      :columnas="columnas"
      :total-elementos="totalElementos"
      :filas-pagina="contactos.length"
      @cerrar="mostrarExportar = false"
    />
    <PanelFiltrosAvanzados v-if="mostrarAvanzados" v-model="avanzados" @cerrar="mostrarAvanzados = false" />
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
import PanelFiltrosAvanzados from './contactos/PanelFiltrosAvanzados.vue'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import {
  buscarContactos, COLUMNAS, COLUMNAS_POR_DEFECTO, contarContactos, filtrosAvanzadosVacios, obtenerEstatusLead,
  obtenerPropietarios, SIN_VALOR,
  type ClaveColumna, type Columna, type Contacto, type ConteoContactos, type FiltrosContactos, type OrdenContactos,
  type VistaContactos,
} from '@/services/contactos'
import { guardarColumnas, leerColumnasGuardadas } from '@/services/tablas'
import { formatearFecha, formatearFechaHora, RANGOS_FECHA, rangoAFechas } from '@/utils/fechas'
import { claseEstatus } from './contactos/estatus'

const route = useRoute()
const router = useRouter()

// ----- Pestañas: la vista va en la URL (/contact?vista=mios) para poder compartirla o recargar -----
const pestanas: { vista: VistaContactos; texto: string; conteo: keyof ConteoContactos }[] = [
  { vista: 'todos', texto: 'Todos los contactos', conteo: 'todos' },
  { vista: 'mios', texto: 'Mis contactos', conteo: 'mios' },
  { vista: 'sin_asignar', texto: 'Contactos sin asignar', conteo: 'sinAsignar' },
]
const vista = computed<VistaContactos>(() => {
  const valor = route.query.vista
  return valor === 'mios' || valor === 'sin_asignar' ? valor : 'todos'
})
const cambiarVista = (nueva: VistaContactos) =>
  router.replace({ query: { ...route.query, vista: nueva === 'todos' ? undefined : nueva } })

// ----- Filtros -----
const q = ref('')
const propietarios = ref<(string | number)[]>([])
const estatus = ref<(string | number)[]>([])
const creado = ref<(string | number)[]>([])
const creadoDesde = ref('')
const creadoHasta = ref('')
const actividad = ref<(string | number)[]>([])
const actividadDesde = ref('')
const actividadHasta = ref('')
const avanzados = ref(filtrosAvanzadosVacios())

const listaPropietarios = ref<Catalogo[]>([])
const listaEstatus = ref<Catalogo[]>([])
const opcionesPropietario = computed(() => [
  { valor: SIN_VALOR, texto: 'Sin asignar' },
  ...listaPropietarios.value.map((p) => ({ valor: p.id, texto: p.nombre })),
])
const opcionesEstatus = computed(() => [
  ...listaEstatus.value.map((e) => ({ valor: e.id, texto: e.nombre })),
  { valor: SIN_VALOR, texto: 'Sin lead' },
])
const opcionesActividad = [...RANGOS_FECHA, { valor: 'sin', texto: 'Sin actividad' }]

const cantidadAvanzados = computed(() => Object.values(avanzados.value).filter((v) => v.trim() !== '').length)
const hayFiltros = computed(() =>
  q.value.trim() !== '' || propietarios.value.length > 0 || estatus.value.length > 0 || creado.value.length > 0
  || actividad.value.length > 0 || cantidadAvanzados.value > 0)

function limpiarFiltros() {
  q.value = ''
  propietarios.value = []
  estatus.value = []
  creado.value = []
  creadoDesde.value = ''
  creadoHasta.value = ''
  actividad.value = []
  actividadDesde.value = ''
  actividadHasta.value = ''
  avanzados.value = filtrosAvanzadosVacios()
}

// Filtros tal como los recibe el backend (sin paginacion: se usan tambien para los conteos)
const filtros = computed<FiltrosContactos>(() => {
  const fechasCreacion = rangoAFechas(String(creado.value[0] ?? ''), creadoDesde.value, creadoHasta.value)
  const opcionActividad = String(actividad.value[0] ?? '')
  const fechasActividad = opcionActividad === 'sin' ? {} : rangoAFechas(opcionActividad, actividadDesde.value, actividadHasta.value)
  const a = avanzados.value
  return {
    vista: vista.value,
    q: q.value.trim(),
    propietarioIds: propietarios.value.map(Number),
    estatus: estatus.value.map(Number),
    creadoDesde: fechasCreacion.desde,
    creadoHasta: fechasCreacion.hasta,
    actividadDesde: fechasActividad.desde,
    actividadHasta: fechasActividad.hasta,
    sinActividad: opcionActividad === 'sin' ? true : undefined,
    nombre: a.nombre.trim(),
    correo: a.correo.trim(),
    telefono: a.telefono.trim(),
    cedula: a.cedula.trim(),
    tipoNegocio: a.tipoNegocio.trim(),
    verificado: a.verificado === '' ? undefined : a.verificado === 'true',
    orden: orden.value,
    direccion: direccion.value,
  }
})

// ----- Orden -----
const orden = ref<OrdenContactos>('fechaCreacion')
const direccion = ref<'asc' | 'desc'>('desc')
function ordenarPor(campo: OrdenContactos) {
  if (orden.value === campo) {
    direccion.value = direccion.value === 'asc' ? 'desc' : 'asc'
  } else {
    orden.value = campo
    // Las fechas empiezan por la mas reciente; el texto en orden alfabetico
    direccion.value = campo === 'fechaCreacion' || campo === 'ultimaActividad' ? 'desc' : 'asc'
  }
}
const indicadorOrden = (columna: Columna) =>
  columna.orden === orden.value ? (direccion.value === 'asc' ? '▲' : '▼') : '↕'
const ariaOrden = (columna: Columna) =>
  columna.orden === orden.value ? (direccion.value === 'asc' ? 'ascending' : 'descending') : undefined

// ----- Columnas visibles (preferencia de cada navegador) -----
const CLAVE_COLUMNAS = 'rr_columnas_contactos'
const columnas = ref<ClaveColumna[]>(leerColumnasGuardadas(CLAVE_COLUMNAS, COLUMNAS, COLUMNAS_POR_DEFECTO, 'nombre'))
const columnasVisibles = computed(() => columnas.value.map((clave) => COLUMNAS.find((c) => c.clave === clave)!).filter(Boolean))
const mostrarColumnas = ref(false)
function aplicarColumnas(nuevas: string[]) {
  columnas.value = nuevas as ClaveColumna[]
  mostrarColumnas.value = false
  guardarColumnas(CLAVE_COLUMNAS, nuevas)
}

function valorCelda(c: Contacto, clave: ClaveColumna): string {
  switch (clave) {
    case 'correo': return c.correo
    case 'telefono': return c.telefono
    case 'fechaCreacion': return formatearFecha(c.fechaCreacion)
    case 'ultimaActividad': return c.ultimaActividad ? formatearFechaHora(c.ultimaActividad) : 'Sin actividad'
    case 'cedula': return c.cedula != null ? String(c.cedula) : '—'
    case 'tipoNegocio': return c.tipoNegocio || '—'
    case 'leads': return String(c.cantidadLeads)
    case 'verificado': return c.verificado ? 'Sí' : 'No'
    default: return ''
  }
}

const iniciales = (c: Contacto) => `${c.nombre.charAt(0)}${c.apellido.charAt(0)}`.toUpperCase()

// ----- Carga de datos -----
const contactos = ref<Contacto[]>([])
const conteos = ref<ConteoContactos | null>(null)
const pagina = ref(1)
const tamano = ref(25)
const totalPaginas = ref(0)
const totalElementos = ref(0)
const cargando = ref(false)
const error = ref('')
let ultimaPeticion = 0

const filtrosConPagina = computed<FiltrosContactos>(() => ({ ...filtros.value, pagina: pagina.value - 1, tamano: tamano.value }))

// numeroPagina en base 1. Si llegan respuestas fuera de orden solo se usa la de la ultima peticion
async function cargar(numeroPagina = 1) {
  const peticion = ++ultimaPeticion
  cargando.value = true
  error.value = ''
  try {
    const resultado = await buscarContactos({ ...filtros.value, pagina: numeroPagina - 1, tamano: tamano.value })
    if (peticion !== ultimaPeticion) return
    contactos.value = resultado.contenido
    pagina.value = resultado.pagina + 1
    totalPaginas.value = resultado.totalPaginas
    totalElementos.value = resultado.totalElementos
  } catch (e) {
    if (peticion === ultimaPeticion) error.value = mensajeDeError(e)
  } finally {
    if (peticion === ultimaPeticion) cargando.value = false
  }
}

// Los conteos de las pestañas no dependen de la pestaña activa ni del orden
const claveConteos = computed(() => JSON.stringify({ ...filtros.value, vista: undefined, orden: undefined, direccion: undefined }))
async function cargarConteos() {
  try {
    conteos.value = await contarContactos(filtros.value)
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
onBeforeUnmount(() => clearTimeout(pausa))

// El texto se consulta con pausa (recargarConPausa); el resto de los filtros al cambiar
watch(() => JSON.stringify({ ...filtros.value, q: undefined }), recargar)
watch(claveConteos, cargarConteos)

const mensajeVacio = computed(() => {
  if (hayFiltros.value) return 'Ningún contacto coincide con los filtros.'
  if (vista.value === 'mios') return 'Todavía no tienes contactos asignados. Puedes tomar uno desde "Contactos sin asignar".'
  if (vista.value === 'sin_asignar') return 'Todos los contactos tienen un propietario asignado.'
  return 'Aún no hay contactos registrados.'
})

const abrir = (c: Contacto) => router.push(`/contact/${c.id}`)

const mostrarExportar = ref(false)
const mostrarAvanzados = ref(false)

onMounted(() => {
  recargar()
  cargarConteos()
  Promise.all([obtenerPropietarios(), obtenerEstatusLead()])
    .then(([lista, catalogo]) => {
      listaPropietarios.value = lista
      listaEstatus.value = catalogo
    })
    .catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
