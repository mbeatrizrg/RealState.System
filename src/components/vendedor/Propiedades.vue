<template>
  <div class="crm-contactos">
    <MenuCrm activo="Inmuebles" />

    <main class="main-content">
      <header class="header">
        <div>
          <h1 class="greeting">Inmuebles</h1>
          <p class="subtitulo">Propiedades del catálogo de la inmobiliaria para vender o alquilar</p>
        </div>
        <div class="acciones">
          <button type="button" class="btn-secundario" @click="mostrarColumnas = true">Editar columnas</button>
          <button type="button" class="btn-secundario" :disabled="totalElementos === 0" @click="mostrarExportar = true">Exportar</button>
          <router-link to="/control?seccion=inmueble" class="btn-filtros enlace-boton">Registrar inmueble</router-link>
        </div>
      </header>

      <nav class="pestanas" aria-label="Vistas de inmuebles">
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
            placeholder="Buscar por título, código RRBR o ubicación"
            class="search-input"
            aria-label="Buscar inmuebles"
            @input="recargarConPausa"
            @keyup.enter="recargar"
          />
        </div>
      </section>

      <section class="barra-filtros" aria-label="Filtros">
        <FiltroLista v-model="asesores" etiqueta="Asesor captador" :opciones="opcionesAsesor" />
        <FiltroLista v-model="tipos" etiqueta="Tipo de inmueble" :opciones="opcionesTipo" />
        <FiltroLista v-model="ciudades" etiqueta="Zona / ubicación" :opciones="opcionesCiudad">
          <template #extra>
            <div class="rango">
              <label>Dirección contiene <input v-model="zonaTexto" type="text" maxlength="100" placeholder="Ej: El Bosque" /></label>
            </div>
          </template>
        </FiltroLista>
        <FiltroLista v-model="precio" etiqueta="Rango de precio" :opciones="RANGOS_PRECIO" :multiple="false">
          <template v-if="precio[0] === 'personalizado'" #extra>
            <div class="rango">
              <label>Mínimo <input v-model="precioMin" type="number" min="0" placeholder="USD" /></label>
              <label>Máximo <input v-model="precioMax" type="number" min="0" placeholder="USD" /></label>
            </div>
          </template>
        </FiltroLista>
        <FiltroLista v-model="estados" etiqueta="Estado del inmueble" :opciones="ESTADOS_INMUEBLE" />
        <FiltroLista v-model="captado" etiqueta="Fecha de captación" :opciones="RANGOS_FECHA" :multiple="false">
          <template v-if="captado[0] === 'personalizado'" #extra>
            <div class="rango">
              <label>Desde <input v-model="captadoDesde" type="date" :max="captadoHasta || undefined" /></label>
              <label>Hasta <input v-model="captadoHasta" type="date" :min="captadoDesde || undefined" /></label>
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

      <section class="tabla-seccion">
        <p v-if="error" class="estado-msg error" role="alert">No se pudieron cargar los inmuebles: {{ error }}</p>

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
              <tr v-for="p in propiedades" :key="p.id" class="fila" tabindex="0" @click="abrir(p)" @keyup.enter="abrir(p)">
                <td v-for="columna in columnasVisibles" :key="columna.clave">
                  <router-link v-if="columna.clave === 'codigo'" :to="`/inmuebles/${p.id}`" class="nombre" @click.stop>{{ p.codigo }}</router-link>
                  <div v-else-if="columna.clave === 'titulo'" class="celda-inmueble">
                    <img :src="urlImagen(p.urlImagenPrincipal)" alt="" class="miniatura" loading="lazy" />
                    <span>{{ p.titulo }}</span>
                  </div>
                  <span v-else-if="columna.clave === 'estado'" :class="['badge', claseEstado(p.estado)]">{{ textoEstado(p.estado) }}</span>
                  <span v-else-if="columna.clave === 'dueno'" :class="{ tenue: !p.duenoNombre }">{{ p.duenoNombre ?? 'Sin registrar' }}</span>
                  <span v-else>{{ valorCelda(p, columna.clave) }}</span>
                </td>
              </tr>
            </tbody>
          </table>

          <p v-if="cargando && propiedades.length === 0" class="estado-msg">Cargando inmuebles...</p>
          <p v-else-if="!cargando && propiedades.length === 0" class="estado-msg">{{ mensajeVacio }}</p>
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
      :columnas="COLUMNAS_PROPIEDAD"
      :por-defecto="COLUMNAS_PROPIEDAD_POR_DEFECTO"
      fija="codigo"
      @cerrar="mostrarColumnas = false"
      @aplicar="aplicarColumnas"
    />
    <ModalExportar
      v-if="mostrarExportar"
      :filtros="filtrosConPagina"
      :columnas="columnas"
      :disponibles="COLUMNAS_PROPIEDAD"
      unidad="inmuebles"
      :exportar="exportarPropiedades"
      :total-elementos="totalElementos"
      :filas-pagina="propiedades.length"
      @cerrar="mostrarExportar = false"
    />
    <PanelAvanzado
      v-if="mostrarAvanzados"
      v-model="avanzados"
      :campos="CAMPOS_AVANZADOS"
      :rangos="[['metrosMin', 'metrosMax']]"
      ayuda="un inmueble debe cumplir todos."
      @cerrar="mostrarAvanzados = false"
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
import { mensajeDeError } from '@/services/api'
import { obtenerCiudades, obtenerTiposInmueble, type Catalogo } from '@/services/catalogos'
import { obtenerPropietarios } from '@/services/contactos'
import type { ConteoPestanas } from '@/services/negocios'
import {
  buscarPropiedades, claseEstado, COLUMNAS_PROPIEDAD, COLUMNAS_PROPIEDAD_POR_DEFECTO, contarPropiedades, ESTADOS_INMUEBLE,
  exportarPropiedades, textoEstado,
  type ClaveColumnaPropiedad, type EstadoInmueble, type FiltrosPropiedades, type OrdenPropiedades, type Propiedad,
  type VistaPropiedades,
} from '@/services/propiedades'
import { guardarColumnas, leerColumnasGuardadas, type ColumnaTabla } from '@/services/tablas'
import { formatearFecha, RANGOS_FECHA, rangoAFechas } from '@/utils/fechas'
import { formatearPrecio, urlImagen } from '@/utils/formato'

const route = useRoute()
const router = useRouter()

const pestanas: { vista: VistaPropiedades; texto: string; conteo: keyof ConteoPestanas }[] = [
  { vista: 'todos', texto: 'Todos los inmuebles', conteo: 'todos' },
  { vista: 'mios', texto: 'Mis inmuebles captados', conteo: 'mios' },
  { vista: 'disponibles', texto: 'Inmuebles disponibles', conteo: 'terceraPestana' },
]
const vista = computed<VistaPropiedades>(() => {
  const valor = route.query.vista
  return valor === 'mios' || valor === 'disponibles' ? valor : 'todos'
})
const cambiarVista = (nueva: VistaPropiedades) =>
  router.replace({ query: { ...route.query, vista: nueva === 'todos' ? undefined : nueva } })

// ----- Filtros -----
const RANGOS_PRECIO = [
  { valor: '0-50000', texto: 'Hasta $50.000' },
  { valor: '50000-150000', texto: '$50.000 a $150.000' },
  { valor: '150000-300000', texto: '$150.000 a $300.000' },
  { valor: '300000-', texto: 'Más de $300.000' },
  { valor: 'personalizado', texto: 'Rango personalizado' },
]
const CAMPOS_AVANZADOS: CampoAvanzado[] = [
  { clave: 'codigo', etiqueta: 'Código RRBR contiene', placeholder: 'Ej: RRBR-120' },
  { clave: 'dueno', etiqueta: 'Dueño contiene', placeholder: 'Nombre del propietario' },
  { clave: 'operacion', etiqueta: 'Tipo de operación', opciones: [{ valor: 'venta', texto: 'Venta' }, { valor: 'alquiler', texto: 'Alquiler' }] },
  { clave: 'habitacionesMin', etiqueta: 'Habitaciones (mínimo)', numerico: true },
  { clave: 'banosMin', etiqueta: 'Baños (mínimo)', numerico: true },
  { clave: 'metrosMin', etiqueta: 'Metros cuadrados (mínimo)', numerico: true },
  { clave: 'metrosMax', etiqueta: 'Metros cuadrados (máximo)', numerico: true },
]
const avanzadosVacios = () =>
  ({ codigo: '', dueno: '', operacion: '', habitacionesMin: '', banosMin: '', metrosMin: '', metrosMax: '' })

const q = ref('')
const asesores = ref<(string | number)[]>([])
const tipos = ref<(string | number)[]>([])
const ciudades = ref<(string | number)[]>([])
const zonaTexto = ref('')
const precio = ref<(string | number)[]>([])
const precioMin = ref('')
const precioMax = ref('')
const estados = ref<(string | number)[]>([])
const captado = ref<(string | number)[]>([])
const captadoDesde = ref('')
const captadoHasta = ref('')
const avanzados = ref<Record<string, string>>(avanzadosVacios())

const listaAsesores = ref<Catalogo[]>([])
const listaTipos = ref<Catalogo[]>([])
const listaCiudades = ref<Catalogo[]>([])
const opcionesAsesor = computed(() => listaAsesores.value.map((a) => ({ valor: a.id, texto: a.nombre })))
const opcionesTipo = computed(() => listaTipos.value.map((t) => ({ valor: t.id, texto: t.nombre })))
const opcionesCiudad = computed(() => listaCiudades.value.map((c) => ({ valor: c.id, texto: c.nombre })))

const cantidadAvanzados = computed(() => Object.values(avanzados.value).filter((v) => v.trim() !== '').length)
const hayFiltros = computed(() =>
  q.value.trim() !== '' || asesores.value.length > 0 || tipos.value.length > 0 || ciudades.value.length > 0
  || zonaTexto.value.trim() !== '' || precio.value.length > 0 || estados.value.length > 0 || captado.value.length > 0
  || cantidadAvanzados.value > 0)

function limpiarFiltros() {
  q.value = ''
  asesores.value = []
  tipos.value = []
  ciudades.value = []
  zonaTexto.value = ''
  precio.value = []
  precioMin.value = ''
  precioMax.value = ''
  estados.value = []
  captado.value = []
  captadoDesde.value = ''
  captadoHasta.value = ''
  avanzados.value = avanzadosVacios()
}

// "50000-150000" -> { min: '50000', max: '150000' }
function rangoPrecio(): { min?: string; max?: string } {
  const opcion = String(precio.value[0] ?? '')
  if (opcion === 'personalizado') return { min: String(precioMin.value ?? '').trim(), max: String(precioMax.value ?? '').trim() }
  if (!opcion) return {}
  const [min, max] = opcion.split('-')
  return { min: min || undefined, max: max || undefined }
}

const filtros = computed<FiltrosPropiedades>(() => {
  const fechas = rangoAFechas(String(captado.value[0] ?? ''), captadoDesde.value, captadoHasta.value)
  const { min, max } = rangoPrecio()
  const a = avanzados.value
  return {
    vista: vista.value,
    q: q.value.trim(),
    asesorIds: asesores.value.map(Number),
    tipoInmuebleIds: tipos.value.map(Number),
    ciudadIds: ciudades.value.map(Number),
    zona: zonaTexto.value.trim(),
    precioMin: min,
    precioMax: max,
    estados: estados.value.map(String) as EstadoInmueble[],
    captadoDesde: fechas.desde,
    captadoHasta: fechas.hasta,
    codigo: a.codigo,
    dueno: a.dueno,
    operacion: (a.operacion || undefined) as FiltrosPropiedades['operacion'],
    habitacionesMin: a.habitacionesMin,
    banosMin: a.banosMin,
    metrosMin: a.metrosMin,
    metrosMax: a.metrosMax,
    orden: orden.value,
    direccion: direccion.value,
  }
})

// ----- Orden -----
const orden = ref<OrdenPropiedades>('fechaCaptacion')
const direccion = ref<'asc' | 'desc'>('desc')
function ordenarPor(campo: OrdenPropiedades) {
  if (orden.value === campo) {
    direccion.value = direccion.value === 'asc' ? 'desc' : 'asc'
  } else {
    orden.value = campo
    direccion.value = campo === 'fechaCaptacion' || campo === 'precio' ? 'desc' : 'asc'
  }
}
const indicadorOrden = (columna: ColumnaTabla) =>
  columna.orden === orden.value ? (direccion.value === 'asc' ? '▲' : '▼') : '↕'
const ariaOrden = (columna: ColumnaTabla) =>
  columna.orden === orden.value ? (direccion.value === 'asc' ? 'ascending' : 'descending') : undefined

// ----- Columnas visibles -----
const CLAVE_COLUMNAS = 'rr_columnas_inmuebles'
const columnas = ref<ClaveColumnaPropiedad[]>(
  leerColumnasGuardadas(CLAVE_COLUMNAS, COLUMNAS_PROPIEDAD, COLUMNAS_PROPIEDAD_POR_DEFECTO, 'codigo'))
const columnasVisibles = computed(() =>
  columnas.value.map((clave) => COLUMNAS_PROPIEDAD.find((c) => c.clave === clave)!).filter(Boolean))
const mostrarColumnas = ref(false)
function aplicarColumnas(nuevas: string[]) {
  columnas.value = nuevas as ClaveColumnaPropiedad[]
  mostrarColumnas.value = false
  guardarColumnas(CLAVE_COLUMNAS, nuevas)
}

function valorCelda(p: Propiedad, clave: ClaveColumnaPropiedad): string {
  switch (clave) {
    case 'tipo': return p.tipoInmueble
    case 'operacion': return p.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Venta'
    case 'zona': return p.ciudad ? `${p.ubicacion}, ${p.ciudad}` : p.ubicacion
    case 'precio': return formatearPrecio(p.precio)
    case 'asesor': return p.nombreAsesor ?? '—'
    case 'fechaCaptacion': return formatearFecha(p.fechaCaptacion)
    case 'habitaciones': return p.habitaciones != null ? String(p.habitaciones) : '—'
    case 'banos': return p.banos != null ? String(p.banos) : '—'
    case 'metros': return p.metrosCuadrados != null ? `${p.metrosCuadrados} m²` : '—'
    case 'estacionamientos': return p.estacionamientos != null ? String(p.estacionamientos) : '—'
    case 'piso': return p.piso != null ? (p.piso === 0 ? 'PB' : String(p.piso)) : '—'
    case 'negocios': return String(p.negociosAbiertos)
    default: return ''
  }
}

// ----- Carga de datos -----
const propiedades = ref<Propiedad[]>([])
const conteos = ref<ConteoPestanas | null>(null)
const pagina = ref(1)
const tamano = ref(25)
const totalPaginas = ref(0)
const totalElementos = ref(0)
const cargando = ref(false)
const error = ref('')
let ultimaPeticion = 0

const filtrosConPagina = computed<FiltrosPropiedades>(() => ({ ...filtros.value, pagina: pagina.value - 1, tamano: tamano.value }))

async function cargar(numeroPagina = 1) {
  const peticion = ++ultimaPeticion
  cargando.value = true
  error.value = ''
  try {
    const resultado = await buscarPropiedades({ ...filtros.value, pagina: numeroPagina - 1, tamano: tamano.value })
    if (peticion !== ultimaPeticion) return
    propiedades.value = resultado.contenido
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
    conteos.value = await contarPropiedades(filtros.value)
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

// El texto libre y los campos escritos se consultan con pausa; el resto de los filtros al cambiar
const filtrosSinTexto = computed(() =>
  JSON.stringify({ ...filtros.value, q: undefined, zona: undefined, precioMin: undefined, precioMax: undefined, opcionPrecio: precio.value }))
watch(filtrosSinTexto, recargar)
watch([zonaTexto, precioMin, precioMax], recargarConPausa)
watch(claveConteos, cargarConteos)

const mensajeVacio = computed(() => {
  if (hayFiltros.value) return 'Ningún inmueble coincide con los filtros.'
  if (vista.value === 'mios') return 'Todavía no has captado inmuebles. Regístralos con "Registrar inmueble".'
  if (vista.value === 'disponibles') return 'No hay inmuebles disponibles en este momento.'
  return 'Aún no hay inmuebles registrados.'
})

const abrir = (p: Propiedad) => router.push(`/inmuebles/${p.id}`)

const mostrarExportar = ref(false)
const mostrarAvanzados = ref(false)

onMounted(() => {
  recargar()
  cargarConteos()
  obtenerPropietarios().then((lista) => { listaAsesores.value = lista }).catch(() => {})
  obtenerTiposInmueble().then((lista) => { listaTipos.value = lista }).catch(() => {})
  obtenerCiudades().then((lista) => { listaCiudades.value = lista }).catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
<style scoped src="./contactos/modulos.css"></style>
<style scoped>
.enlace-boton {
  text-decoration: none;
  display: inline-block;
}
</style>
