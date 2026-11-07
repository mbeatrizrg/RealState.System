<template>
  <div class="crm-contactos">
    <MenuCrm activo="Comunicaciones" />

    <main class="main-content">
      <header class="header">
        <div>
          <h1 class="greeting">Comunicaciones y reportes</h1>
          <p class="subtitulo">Seguimiento de conversaciones con clientes y reportes detallados sobre cómo estuvieron las visitas a inmuebles</p>
        </div>
        <div class="acciones">
          <button type="button" class="btn-secundario" @click="abrirModalComunicacion">
            Registrar comunicación
          </button>
          <button type="button" class="btn-filtros" @click="abrirModalReporte">
            Reportes de visita
          </button>
        </div>
      </header>

      <!-- Pestañas del módulo -->
      <nav class="pestanas" aria-label="Secciones de comunicaciones">
        <button
          type="button"
          :class="['pestana', { activa: pestanaActiva === 'comunicaciones' }]"
          :aria-current="pestanaActiva === 'comunicaciones' ? 'page' : undefined"
          @click="cambiarPestana('comunicaciones')"
        >
          Historial de comunicaciones
          <span class="contador">{{ comunicacionesFiltradas.length }}</span>
        </button>
        <button
          type="button"
          :class="['pestana', { activa: pestanaActiva === 'reportes' }]"
          :aria-current="pestanaActiva === 'reportes' ? 'page' : undefined"
          @click="cambiarPestana('reportes')"
        >
          Reportes de visita a inmuebles
          <span class="contador">{{ reportesFiltrados.length }}</span>
        </button>
      </nav>

      <!-- Buscador -->
      <section class="search-section">
        <div class="search-input-wrapper">
          <svg class="icon-search" viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            v-model="q"
            type="search"
            maxlength="100"
            :placeholder="pestanaActiva === 'comunicaciones'
              ? 'Buscar por cliente, inmueble, vendedor, canal o mensaje'
              : 'Buscar reportes por inmueble, cliente, vendedor o comentarios de la visita'"
            class="search-input"
            aria-label="Buscar en comunicaciones y reportes"
          />
        </div>
      </section>

      <!-- Barra de filtros rápidos -->
      <section class="barra-filtros" aria-label="Filtros">
        <template v-if="pestanaActiva === 'comunicaciones'">
          <div class="chips-filtro">
            <button
              type="button"
              :class="['chip-filtro', { activo: filtroCanal === '' }]"
              @click="filtroCanal = ''"
            >
              Todos los canales
            </button>
            <button
              v-for="c in canalesDisponibles"
              :key="c"
              type="button"
              :class="['chip-filtro', { activo: filtroCanal === c }]"
              @click="filtroCanal = c"
            >
              {{ c }}
            </button>
          </div>
        </template>
        <template v-else>
          <div class="chips-filtro">
            <button
              type="button"
              :class="['chip-filtro', { activo: filtroValoracion === '' }]"
              @click="filtroValoracion = ''"
            >
              Todas las valoraciones
            </button>
            <button
              v-for="v in VALORACIONES_VISITA"
              :key="v.valor"
              type="button"
              :class="['chip-filtro', { activo: filtroValoracion === v.valor }]"
              @click="filtroValoracion = v.valor"
            >
              {{ v.texto }}
            </button>
          </div>
        </template>

        <button v-if="hayFiltros" type="button" class="btn-limpiar" @click="limpiarFiltros">
          Limpiar filtros
        </button>
      </section>

      <p v-if="aviso" class="aviso-flotante" role="status">{{ aviso }}</p>

      <!-- Tarjetas de resumen rápido -->
      <section class="tarjetas-resumen" aria-label="Resumen de actividad">
        <article class="tarjeta-kpi">
          <span class="kpi-etiqueta">Comunicaciones registradas</span>
          <strong class="kpi-valor">{{ comunicaciones.length }}</strong>
          <small class="kpi-detalle">{{ canalesResumen }}</small>
        </article>
        <article class="tarjeta-kpi">
          <span class="kpi-etiqueta">Reportes de visita realizados</span>
          <strong class="kpi-valor">{{ reportes.length }}</strong>
          <small class="kpi-detalle">Visitas a inmuebles con informe del vendedor</small>
        </article>
        <article class="tarjeta-kpi">
          <span class="kpi-etiqueta">Visitas con alto interés</span>
          <strong class="kpi-valor">{{ visitasInteresadas }}</strong>
          <small class="kpi-detalle">Clientes interesados o con posible oferta</small>
        </article>
      </section>

      <!-- ===================== PESTAÑA 1: HISTORIAL DE COMUNICACIONES ===================== -->
      <section v-if="pestanaActiva === 'comunicaciones'" class="tabla-seccion">
        <p v-if="errorComunicaciones" class="estado-msg error" role="alert">
          No se pudieron cargar las comunicaciones: {{ errorComunicaciones }}
        </p>

        <div v-else class="table-container">
          <table class="user-table" :aria-busy="cargandoComunicaciones">
            <thead>
              <tr>
                <th><span>Cliente</span></th>
                <th><span>Inmueble consultado</span></th>
                <th><span>Canal</span></th>
                <th><span>Resumen de la comunicación</span></th>
                <th><span>Asesor</span></th>
                <th><span>Fecha</span></th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="item in comunicacionesFiltradas"
                :key="item.id"
                class="fila"
                tabindex="0"
                @click="seleccionarComunicacion(item)"
                @keyup.enter="seleccionarComunicacion(item)"
              >
                <td>
                  <div class="celda-nombre">
                    <span class="avatar" aria-hidden="true">{{ inicialesTexto(item.nombreCliente) }}</span>
                    <router-link
                      v-if="item.contactoId"
                      :to="`/contact/${item.contactoId}`"
                      class="nombre"
                      @click.stop
                    >
                      {{ item.nombreCliente }}
                    </router-link>
                    <span v-else class="nombre">{{ item.nombreCliente }}</span>
                  </div>
                </td>
                <td>
                  <router-link
                    v-if="item.inmuebleId"
                    :to="`/inmuebles/${item.inmuebleId}`"
                    class="enlace-celda"
                    @click.stop
                  >
                    {{ item.nombreInmueble || `Lead #${item.leadId}` }}
                  </router-link>
                  <span v-else>{{ item.nombreInmueble || `Lead #${item.leadId}` }}</span>
                </td>
                <td>
                  <span :class="['badge', claseCanal(item.canal)]">{{ item.canal }}</span>
                </td>
                <td class="celda-descripcion">{{ recortar(item.descripcion, 85) }}</td>
                <td>{{ item.nombreVendedor }}</td>
                <td>{{ formatearFechaHora(item.fechaCreacion) }}</td>
              </tr>
            </tbody>
          </table>

          <p v-if="cargandoComunicaciones && comunicaciones.length === 0" class="estado-msg">
            Cargando comunicaciones...
          </p>
          <p v-else-if="!cargandoComunicaciones && comunicacionesFiltradas.length === 0" class="estado-msg">
            {{ hayFiltros ? 'Ninguna comunicación coincide con los filtros.' : 'Aún no hay comunicaciones registradas.' }}
          </p>
        </div>
      </section>

      <!-- ===================== PESTAÑA 2: REPORTES DE VISITA A INMUEBLES ===================== -->
      <section v-else class="tabla-seccion">
        <p v-if="errorReportes" class="estado-msg error" role="alert">
          No se pudieron cargar los reportes de visita: {{ errorReportes }}
        </p>

        <div v-else class="table-container">
          <table class="user-table" :aria-busy="cargandoReportes">
            <thead>
              <tr>
                <th><span>Inmueble visitado</span></th>
                <th><span>Cliente</span></th>
                <th><span>¿Cómo estuvo la visita?</span></th>
                <th><span>Reporte del vendedor</span></th>
                <th><span>Vendedor</span></th>
                <th><span>Fecha de visita</span></th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="rep in reportesFiltrados"
                :key="rep.id"
                class="fila"
                tabindex="0"
                @click="reporteSeleccionado = rep"
                @keyup.enter="reporteSeleccionado = rep"
              >
                <td>
                  <router-link
                    v-if="rep.inmuebleId"
                    :to="`/inmuebles/${rep.inmuebleId}`"
                    class="nombre"
                    @click.stop
                  >
                    {{ rep.codigoInmueble }} · {{ rep.tituloInmueble }}
                  </router-link>
                  <span v-else class="tenue">Sin inmueble</span>
                </td>
                <td>
                  <router-link
                    v-if="rep.contactoId"
                    :to="`/contact/${rep.contactoId}`"
                    class="enlace-celda"
                    @click.stop
                  >
                    {{ rep.nombreContacto }}
                  </router-link>
                  <span v-else class="tenue">Sin contacto</span>
                </td>
                <td>
                  <span :class="['badge', claseValoracion(extraerValoracion(rep))]">
                    {{ extraerValoracion(rep) }}
                  </span>
                </td>
                <td class="celda-descripcion">{{ recortar(limpiarDescripcionReporte(rep.descripcion), 90) }}</td>
                <td>{{ rep.nombrePropietario ?? 'Sin asignar' }}</td>
                <td>{{ formatearFechaHora(rep.fechaVencimiento ?? rep.fechaCreacion) }}</td>
              </tr>
            </tbody>
          </table>

          <p v-if="cargandoReportes && reportes.length === 0" class="estado-msg">
            Cargando reportes de visita...
          </p>
          <p v-else-if="!cargandoReportes && reportesFiltrados.length === 0" class="estado-msg">
            {{ hayFiltros ? 'Ningún reporte coincide con los filtros.' : 'Aún no hay reportes de visita registrados. Usa el botón "Reportes de visita" para crear el primero.' }}
          </p>
        </div>
      </section>
    </main>

    <!-- ===================== MODAL: CREAR REPORTE DE VISITA ===================== -->
    <ModalCrm
      v-if="mostrarModalReporte"
      titulo="Reporte de visita al inmueble"
      @cerrar="mostrarModalReporte = false"
    >
      <form :id="idFormReporte" class="form-grid" novalidate @submit.prevent="guardarReporte">
        <div class="campo ancho">
          <span>Inmueble visitado</span>
          <SelectorBusqueda
            id="reporte-inmueble"
            v-model="formReporte.inmuebleId"
            :buscar="buscarInmueblesSelector"
            :texto-inicial="textoInmuebleReporte"
            placeholder="Buscar por código RRBR o título del inmueble"
          />
        </div>

        <div class="campo ancho">
          <span>Cliente que realizó la visita</span>
          <SelectorBusqueda
            id="reporte-contacto"
            v-model="formReporte.contactoId"
            :buscar="buscarContactosSelector"
            :texto-inicial="textoContactoReporte"
            placeholder="Buscar por nombre, correo o teléfono del cliente"
          />
        </div>

        <div class="campo ancho">
          <span>Negocio vinculado (opcional)</span>
          <SelectorBusqueda
            id="reporte-negocio"
            v-model="formReporte.negocioId"
            :buscar="buscarNegociosSelector"
            :texto-inicial="textoNegocioReporte"
            placeholder="Seleccionar negocio si aplica"
            @elegido="alElegirNegocioReporte"
          />
          <small class="ayuda-campo">Si eliges un negocio, se vinculan automáticamente su cliente y su inmueble.</small>
        </div>

        <label class="campo">
          <span>Fecha y hora de la visita</span>
          <input v-model="formReporte.fechaVisita" type="datetime-local" required />
        </label>

        <label class="campo">
          <span>¿Cómo estuvo la visita? (Valoración)</span>
          <select v-model="formReporte.calificacion" required>
            <option v-for="v in VALORACIONES_VISITA" :key="v.valor" :value="v.valor">
              {{ v.texto }}
            </option>
          </select>
        </label>

        <label class="campo ancho">
          <span>Reporte de la visita (impresión del cliente)</span>
          <textarea
            v-model="formReporte.impresionCliente"
            rows="4"
            maxlength="3000"
            required
            placeholder="Describe cómo estuvo la visita: qué le pareció el inmueble al cliente, qué áreas le gustaron más, dudas o comentarios..."
          />
        </label>

        <label class="campo ancho">
          <span>Estado físico y observaciones del inmueble (opcional)</span>
          <textarea
            v-model="formReporte.observaciones"
            rows="3"
            maxlength="2000"
            placeholder="Ej: El inmueble estaba en óptimas condiciones, iluminación adecuada, detalles de mantenimiento..."
          />
        </label>

        <label class="campo ancho">
          <span>Próximos pasos o acuerdos con el cliente (opcional)</span>
          <input
            v-model="formReporte.proximosPasos"
            type="text"
            maxlength="500"
            placeholder="Ej: Enviar borrador de oferta el lunes / Agendar segunda visita con la familia"
          />
        </label>
      </form>
      <p v-if="errorFormReporte" class="error-form" role="alert">{{ errorFormReporte }}</p>
      <template #pie>
        <button type="button" class="btn-secundario" @click="mostrarModalReporte = false">Cancelar</button>
        <button type="submit" :form="idFormReporte" class="btn-primario" :disabled="guardandoReporte">
          {{ guardandoReporte ? 'Guardando reporte...' : 'Guardar reporte de visita' }}
        </button>
      </template>
    </ModalCrm>

    <!-- ===================== MODAL: REGISTRAR COMUNICACIÓN ===================== -->
    <ModalCrm
      v-if="mostrarModalComunicacion"
      titulo="Registrar comunicación con cliente"
      @cerrar="mostrarModalComunicacion = false"
    >
      <form :id="idFormComunicacion" class="form-grid" novalidate @submit.prevent="guardarComunicacion">
        <label class="campo ancho">
          <span>Consulta / Lead del cliente</span>
          <select v-model="formComunicacion.leadId" required>
            <option :value="0" disabled>Seleccione el cliente e inmueble consultado</option>
            <option v-for="l in leads" :key="l.id" :value="l.id">
              {{ l.nombreCliente }} · {{ l.rrbrInmueble }} ({{ l.tituloInmueble }})
            </option>
          </select>
          <small class="ayuda-campo">Selecciona el lead sobre el que conversaste con el cliente.</small>
        </label>

        <label class="campo ancho">
          <span>Canal de comunicación</span>
          <select v-model="formComunicacion.canalId" required>
            <option v-for="c in canales" :key="c.id" :value="c.id">{{ c.nombre }}</option>
          </select>
        </label>

        <label class="campo ancho">
          <span>Resumen de la conversación</span>
          <textarea
            v-model="formComunicacion.descripcion"
            rows="4"
            maxlength="3000"
            required
            placeholder="Escribe los detalles de lo conversado con el cliente, dudas resueltas o acuerdos..."
          />
        </label>
      </form>
      <p v-if="errorFormComunicacion" class="error-form" role="alert">{{ errorFormComunicacion }}</p>
      <template #pie>
        <button type="button" class="btn-secundario" @click="mostrarModalComunicacion = false">Cancelar</button>
        <button type="submit" :form="idFormComunicacion" class="btn-primario" :disabled="guardandoComunicacion">
          {{ guardandoComunicacion ? 'Guardando...' : 'Registrar comunicación' }}
        </button>
      </template>
    </ModalCrm>

    <!-- ===================== MODAL: DETALLE DE REPORTE DE VISITA ===================== -->
    <ModalCrm
      v-if="reporteSeleccionado"
      :titulo="reporteSeleccionado.titulo"
      @cerrar="reporteSeleccionado = null"
    >
      <div class="detalle-modal">
        <div class="detalle-badges">
          <span :class="['badge', claseValoracion(extraerValoracion(reporteSeleccionado))]">
            {{ extraerValoracion(reporteSeleccionado) }}
          </span>
          <span class="badge contactado">Visita realizada</span>
        </div>

        <dl class="detalle-lista">
          <dt>Inmueble visitado</dt>
          <dd>
            <router-link
              v-if="reporteSeleccionado.inmuebleId"
              :to="`/inmuebles/${reporteSeleccionado.inmuebleId}`"
              class="enlace-celda"
            >
              {{ reporteSeleccionado.codigoInmueble }} · {{ reporteSeleccionado.tituloInmueble }}
            </router-link>
            <span v-else>—</span>
          </dd>

          <dt>Cliente</dt>
          <dd>
            <router-link
              v-if="reporteSeleccionado.contactoId"
              :to="`/contact/${reporteSeleccionado.contactoId}`"
              class="enlace-celda"
            >
              {{ reporteSeleccionado.nombreContacto }}
            </router-link>
            <span v-else>—</span>
          </dd>

          <dt>Negocio asociado</dt>
          <dd>
            <router-link
              v-if="reporteSeleccionado.negocioId"
              :to="`/negocios/${reporteSeleccionado.negocioId}`"
              class="enlace-celda"
            >
              {{ reporteSeleccionado.nombreNegocio }}
            </router-link>
            <span v-else>—</span>
          </dd>

          <dt>Asesor responsable</dt>
          <dd>{{ reporteSeleccionado.nombrePropietario ?? 'Sin asignar' }}</dd>

          <dt>Fecha de la visita</dt>
          <dd>{{ formatearFechaHora(reporteSeleccionado.fechaVencimiento ?? reporteSeleccionado.fechaCreacion) }}</dd>
        </dl>

        <section class="detalle-cuerpo">
          <h3>Informe detallado de la visita</h3>
          <p class="texto-pre">{{ reporteSeleccionado.descripcion || 'Sin observaciones adicionales.' }}</p>
        </section>
      </div>
      <template #pie>
        <button type="button" class="btn-primario" @click="reporteSeleccionado = null">Cerrar</button>
      </template>
    </ModalCrm>

    <!-- ===================== MODAL: DETALLE DE COMUNICACIÓN ===================== -->
    <ModalCrm
      v-if="comunicacionSeleccionada"
      :titulo="`Comunicación por ${comunicacionSeleccionada.canal}`"
      @cerrar="comunicacionSeleccionada = null"
    >
      <div class="detalle-modal">
        <div class="detalle-badges">
          <span :class="['badge', claseCanal(comunicacionSeleccionada.canal)]">
            {{ comunicacionSeleccionada.canal }}
          </span>
        </div>

        <dl class="detalle-lista">
          <dt>Cliente</dt>
          <dd>
            <router-link
              v-if="comunicacionSeleccionada.contactoId"
              :to="`/contact/${comunicacionSeleccionada.contactoId}`"
              class="enlace-celda"
            >
              {{ comunicacionSeleccionada.nombreCliente }}
            </router-link>
            <span v-else>{{ comunicacionSeleccionada.nombreCliente }}</span>
          </dd>

          <dt>Inmueble consultado</dt>
          <dd>
            <router-link
              v-if="comunicacionSeleccionada.inmuebleId"
              :to="`/inmuebles/${comunicacionSeleccionada.inmuebleId}`"
              class="enlace-celda"
            >
              {{ comunicacionSeleccionada.nombreInmueble || `Lead #${comunicacionSeleccionada.leadId}` }}
            </router-link>
            <span v-else>{{ comunicacionSeleccionada.nombreInmueble || `Lead #${comunicacionSeleccionada.leadId}` }}</span>
          </dd>

          <dt>Asesor</dt>
          <dd>{{ comunicacionSeleccionada.nombreVendedor }}</dd>

          <dt>Fecha y hora</dt>
          <dd>{{ formatearFechaHora(comunicacionSeleccionada.fechaCreacion) }}</dd>
        </dl>

        <section class="detalle-cuerpo">
          <h3>Resumen de la conversación</h3>
          <p class="texto-pre">{{ comunicacionSeleccionada.descripcion }}</p>
        </section>
      </div>
      <template #pie>
        <button
          type="button"
          class="btn-secundario"
          @click="abrirReporteDesdeComunicacion(comunicacionSeleccionada)"
        >
          Crear reporte de visita para este cliente
        </button>
        <button type="button" class="btn-primario" @click="comunicacionSeleccionada = null">Cerrar</button>
      </template>
    </ModalCrm>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, useId } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import MenuCrm from './MenuCrm.vue'
import ModalCrm from './contactos/ModalCrm.vue'
import SelectorBusqueda, { type OpcionSelector } from './contactos/SelectorBusqueda.vue'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import {
  crearReporteVisita,
  obtenerCanalesComunicacion,
  obtenerComunicacionesCrm,
  obtenerLeadsCrm,
  obtenerReportesVisita,
  registrarComunicacionCrm,
  VALORACIONES_VISITA,
  type ComunicacionCrm,
  type LeadCrm,
} from '@/services/comunicaciones'
import { buscarContactos } from '@/services/contactos'
import { buscarNegocios, type Negocio } from '@/services/negocios'
import { buscarPropiedades } from '@/services/propiedades'
import type { Tarea } from '@/services/tareas'
import { formatearFechaHora } from '@/utils/fechas'

const route = useRoute()
const router = useRouter()

const idFormReporte = useId()
const idFormComunicacion = useId()

// Pestaña activa en la URL (?pestana=reportes)
const pestanaActiva = computed<'comunicaciones' | 'reportes'>(() =>
  route.query.pestana === 'reportes' ? 'reportes' : 'comunicaciones',
)

function cambiarPestana(nueva: 'comunicaciones' | 'reportes') {
  router.replace({ query: { ...route.query, pestana: nueva === 'comunicaciones' ? undefined : nueva } })
}

// Estado de datos
const comunicaciones = ref<ComunicacionCrm[]>([])
const reportes = ref<Tarea[]>([])
const canales = ref<Catalogo[]>([])
const leads = ref<LeadCrm[]>([])

const cargandoComunicaciones = ref(false)
const cargandoReportes = ref(false)
const errorComunicaciones = ref('')
const errorReportes = ref('')
const aviso = ref('')

// Filtros
const q = ref('')
const filtroCanal = ref('')
const filtroValoracion = ref('')

const hayFiltros = computed(
  () => q.value.trim() !== '' || filtroCanal.value !== '' || filtroValoracion.value !== '',
)

function limpiarFiltros() {
  q.value = ''
  filtroCanal.value = ''
  filtroValoracion.value = ''
}

const canalesDisponibles = computed(() => {
  const nombres = new Set<string>(canales.value.map((c) => c.nombre))
  comunicaciones.value.forEach((c) => {
    if (c.canal) nombres.add(c.canal)
  })
  return Array.from(nombres)
})

const comunicacionesFiltradas = computed(() => {
  const texto = q.value.trim().toLowerCase()
  return comunicaciones.value.filter((c) => {
    if (filtroCanal.value && c.canal !== filtroCanal.value) return false
    if (!texto) return true
    return (
      c.nombreCliente?.toLowerCase().includes(texto) ||
      c.nombreInmueble?.toLowerCase().includes(texto) ||
      c.nombreVendedor?.toLowerCase().includes(texto) ||
      c.canal?.toLowerCase().includes(texto) ||
      c.descripcion?.toLowerCase().includes(texto)
    )
  })
})

function extraerValoracion(tarea: Tarea): string {
  if (tarea.titulo.includes('·')) {
    return tarea.titulo.split('·').slice(1).join('·').trim()
  }
  const match = tarea.descripcion?.match(/Valoración de la visita:\s*(.+)/i)
  if (match?.[1]) return match[1].trim()
  return 'Visita realizada'
}

function limpiarDescripcionReporte(descripcion: string | null): string {
  if (!descripcion) return 'Sin observaciones detalladas.'
  return descripcion
    .replace(/Resultado:\s*/gi, '')
    .replace(/Valoración de la visita:\s*.+\n?/gi, '')
    .trim()
}

const reportesFiltrados = computed(() => {
  const texto = q.value.trim().toLowerCase()
  return reportes.value.filter((r) => {
    const val = extraerValoracion(r)
    if (filtroValoracion.value && val !== filtroValoracion.value) return false
    if (!texto) return true
    return (
      r.titulo?.toLowerCase().includes(texto) ||
      r.descripcion?.toLowerCase().includes(texto) ||
      r.nombreContacto?.toLowerCase().includes(texto) ||
      r.tituloInmueble?.toLowerCase().includes(texto) ||
      r.codigoInmueble?.toLowerCase().includes(texto) ||
      r.nombrePropietario?.toLowerCase().includes(texto)
    )
  })
})

const canalesResumen = computed(() => {
  if (comunicaciones.value.length === 0) return 'WhatsApp, llamadas, correos y visitas'
  const conteo: Record<string, number> = {}
  comunicaciones.value.forEach((c) => {
    conteo[c.canal] = (conteo[c.canal] ?? 0) + 1
  })
  return Object.entries(conteo)
    .slice(0, 3)
    .map(([canal, n]) => `${n} por ${canal}`)
    .join(' · ')
})

const visitasInteresadas = computed(() =>
  reportes.value.filter((r) => {
    const v = extraerValoracion(r).toLowerCase()
    return v.includes('interesado') || v.includes('segunda visita') || v.includes('visita realizada')
  }).length,
)

// Carga de datos
async function cargarComunicaciones() {
  cargandoComunicaciones.value = true
  errorComunicaciones.value = ''
  try {
    comunicaciones.value = await obtenerComunicacionesCrm()
  } catch (e) {
    errorComunicaciones.value = mensajeDeError(e)
  } finally {
    cargandoComunicaciones.value = false
  }
}

async function cargarReportes() {
  cargandoReportes.value = true
  errorReportes.value = ''
  try {
    const pagina = await obtenerReportesVisita()
    reportes.value = pagina.contenido
  } catch (e) {
    errorReportes.value = mensajeDeError(e)
  } finally {
    cargandoReportes.value = false
  }
}

async function cargarCatalogosAuxiliares() {
  try {
    const [listaCanales, listaLeads] = await Promise.all([
      obtenerCanalesComunicacion(),
      obtenerLeadsCrm(),
    ])
    canales.value = listaCanales
    leads.value = listaLeads
  } catch {
    // Los catálogos se recargan al abrir los modales si hicieron falta
  }
}

let pausaAviso: ReturnType<typeof setTimeout> | undefined
function mostrarAviso(texto: string) {
  aviso.value = texto
  clearTimeout(pausaAviso)
  pausaAviso = setTimeout(() => {
    aviso.value = ''
  }, 4000)
}
onBeforeUnmount(() => clearTimeout(pausaAviso))

// Detalle seleccionado
const comunicacionSeleccionada = ref<ComunicacionCrm | null>(null)
const reporteSeleccionado = ref<Tarea | null>(null)

function seleccionarComunicacion(item: ComunicacionCrm) {
  comunicacionSeleccionada.value = item
}

// Modal Reporte de Visita
const mostrarModalReporte = ref(false)
const guardandoReporte = ref(false)
const errorFormReporte = ref('')

function ahoraLocal() {
  const d = new Date()
  d.setSeconds(0, 0)
  return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
}

const formReporte = reactive({
  inmuebleId: null as number | null,
  contactoId: null as number | null,
  negocioId: null as number | null,
  fechaVisita: ahoraLocal(),
  calificacion: VALORACIONES_VISITA[0]!.valor,
  impresionCliente: '',
  observaciones: '',
  proximosPasos: '',
})

const textoInmuebleReporte = ref('')
const textoContactoReporte = ref('')
const textoNegocioReporte = ref('')

function abrirModalReporte() {
  formReporte.inmuebleId = null
  formReporte.contactoId = null
  formReporte.negocioId = null
  formReporte.fechaVisita = ahoraLocal()
  formReporte.calificacion = VALORACIONES_VISITA[0]!.valor
  formReporte.impresionCliente = ''
  formReporte.observaciones = ''
  formReporte.proximosPasos = ''
  textoInmuebleReporte.value = ''
  textoContactoReporte.value = ''
  textoNegocioReporte.value = ''
  errorFormReporte.value = ''
  mostrarModalReporte.value = true
}

function abrirReporteDesdeComunicacion(c: ComunicacionCrm) {
  comunicacionSeleccionada.value = null
  abrirModalReporte()
  if (c.inmuebleId) {
    formReporte.inmuebleId = c.inmuebleId
    textoInmuebleReporte.value = c.nombreInmueble ?? ''
  }
  if (c.contactoId) {
    formReporte.contactoId = c.contactoId
    textoContactoReporte.value = c.nombreCliente
  }
}

const buscarInmueblesSelector = async ( busq: string ): Promise<OpcionSelector[]> =>
  (await buscarPropiedades({ q: busq, tamano: 10, orden: 'titulo', direccion: 'asc' })).contenido.map((p) => ({
    id: p.id,
    texto: `${p.codigo} · ${p.titulo}`,
    detalle: `${p.tipoInmueble} · ${p.ubicacion}`,
  }))

const buscarContactosSelector = async ( busq: string ): Promise<OpcionSelector[]> =>
  (await buscarContactos({ q: busq, tamano: 10, orden: 'nombre', direccion: 'asc' })).contenido.map((c) => ({
    id: c.id,
    texto: `${c.nombre} ${c.apellido}`,
    detalle: `${c.correo} · ${c.telefono}`,
  }))

const buscarNegociosSelector = async ( busq: string ): Promise<OpcionSelector[]> =>
  (await buscarNegocios({ q: busq, tamano: 10, abiertos: true, orden: 'nombre', direccion: 'asc' })).contenido.map((n) => ({
    id: n.id,
    texto: n.nombre,
    detalle: `${n.nombreContacto} · ${n.codigoInmueble} · ${n.etapa}`,
    dato: n,
  }))

function alElegirNegocioReporte(opcion: OpcionSelector) {
  const n = opcion.dato as Negocio
  formReporte.contactoId = n.contactoId
  formReporte.inmuebleId = n.inmuebleId
  textoContactoReporte.value = n.nombreContacto
  textoInmuebleReporte.value = `${n.codigoInmueble} · ${n.tituloInmueble}`
}

async function guardarReporte() {
  if (!formReporte.inmuebleId && !formReporte.contactoId && !formReporte.negocioId) {
    errorFormReporte.value = 'Selecciona al menos el inmueble visitado o el cliente.'
    return
  }
  if (!formReporte.fechaVisita) {
    errorFormReporte.value = 'Indica la fecha y hora en que se realizó la visita.'
    return
  }
  if (!formReporte.impresionCliente.trim()) {
    errorFormReporte.value = 'Escribe el reporte de cómo estuvo la visita.'
    return
  }
  errorFormReporte.value = ''
  guardandoReporte.value = true
  try {
    await crearReporteVisita({
      contactoId: formReporte.contactoId,
      inmuebleId: formReporte.inmuebleId,
      negocioId: formReporte.negocioId,
      fechaVisita: formReporte.fechaVisita,
      calificacion: formReporte.calificacion,
      impresionCliente: formReporte.impresionCliente,
      observaciones: formReporte.observaciones,
      proximosPasos: formReporte.proximosPasos,
    })
    mostrarModalReporte.value = false
    cambiarPestana('reportes')
    mostrarAviso('Reporte de visita registrado correctamente.')
    await cargarReportes()
  } catch (e) {
    errorFormReporte.value = mensajeDeError(e)
  } finally {
    guardandoReporte.value = false
  }
}

// Modal Registrar Comunicación
const mostrarModalComunicacion = ref(false)
const guardandoComunicacion = ref(false)
const errorFormComunicacion = ref('')

const formComunicacion = reactive({
  leadId: 0,
  canalId: 1,
  descripcion: '',
})

async function abrirModalComunicacion() {
  errorFormComunicacion.value = ''
  formComunicacion.descripcion = ''
  if (canales.value.length === 0 || leads.value.length === 0) {
    await cargarCatalogosAuxiliares()
  }
  formComunicacion.canalId = canales.value[0]?.id ?? 1
  formComunicacion.leadId = leads.value[0]?.id ?? 0
  mostrarModalComunicacion.value = true
}

async function guardarComunicacion() {
  if (!formComunicacion.leadId) {
    errorFormComunicacion.value = 'Selecciona la consulta (lead) del cliente.'
    return
  }
  if (!formComunicacion.canalId) {
    errorFormComunicacion.value = 'Selecciona el canal utilizado.'
    return
  }
  if (!formComunicacion.descripcion.trim()) {
    errorFormComunicacion.value = 'Escribe el resumen de la comunicación.'
    return
  }
  errorFormComunicacion.value = ''
  guardandoComunicacion.value = true
  try {
    await registrarComunicacionCrm({
      leadId: formComunicacion.leadId,
      canalId: formComunicacion.canalId,
      descripcion: formComunicacion.descripcion.trim(),
    })
    mostrarModalComunicacion.value = false
    cambiarPestana('comunicaciones')
    mostrarAviso('Comunicación registrada exitosamente.')
    await cargarComunicaciones()
  } catch (e) {
    errorFormComunicacion.value = mensajeDeError(e)
  } finally {
    guardandoComunicacion.value = false
  }
}

// Utilidades de formato visual
function inicialesTexto(nombreCompleto: string): string {
  const partes = (nombreCompleto || '').trim().split(/\s+/)
  return ((partes[0]?.[0] ?? '') + (partes[1]?.[0] ?? '')).toUpperCase() || 'C'
}

function recortar(texto: string | null | undefined, max = 80): string {
  if (!texto) return '—'
  const limpio = texto.replace(/\s+/g, ' ').trim()
  return limpio.length > max ? `${limpio.slice(0, max)}…` : limpio
}

function claseCanal(canal: string): string {
  const c = (canal || '').toLowerCase()
  if (c.includes('whatsapp')) return 'ganado'
  if (c.includes('llamada')) return 'contactado'
  if (c.includes('correo')) return 'nuevo'
  if (c.includes('presencial')) return 'visita'
  return 'calificado'
}

function claseValoracion(valoracion: string): string {
  const encontrada = VALORACIONES_VISITA.find((v) => v.valor === valoracion)
  if (encontrada) return encontrada.clase
  const v = valoracion.toLowerCase()
  if (v.includes('muy interesado') || v.includes('oferta')) return 'ganado'
  if (v.includes('descart')) return 'perdido'
  if (v.includes('objecion')) return 'negociacion'
  if (v.includes('segunda')) return 'visita'
  return 'calificado'
}

onMounted(() => {
  cargarComunicaciones()
  cargarReportes()
  cargarCatalogosAuxiliares()
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
<style scoped src="./contactos/modulos.css"></style>
<style scoped src="./contactos/formulario.css"></style>
<style scoped>
.chips-filtro {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip-filtro {
  background-color: #ffffff;
  border: 1px solid #d5dbe6;
  color: #0b1f46;
  border-radius: 999px;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
}

.chip-filtro:hover {
  border-color: #004cb5;
  color: #004cb5;
}

.chip-filtro.activo {
  background-color: #0b1f46;
  border-color: #0b1f46;
  color: #ffffff;
  font-weight: 600;
}

.tarjetas-resumen {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.tarjeta-kpi {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 18px 22px;
  box-shadow: 0 1px 3px rgba(11, 31, 70, 0.08);
  display: flex;
  flex-direction: column;
  gap: 4px;
  border-left: 4px solid #004cb5;
}

.kpi-etiqueta {
  font-size: 13px;
  font-weight: 600;
  color: #4a5670;
}

.kpi-valor {
  font-size: 28px;
  font-weight: 700;
  color: #0b1f46;
  line-height: 1.15;
}

.kpi-detalle {
  font-size: 12px;
  color: #636363;
}

.celda-descripcion {
  max-width: 360px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detalle-modal {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detalle-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detalle-lista {
  display: grid;
  grid-template-columns: 150px 1fr;
  gap: 8px 14px;
  margin: 0;
  padding: 14px 16px;
  background-color: #f6f8fc;
  border-radius: 10px;
  font-size: 14px;
}

.detalle-lista dt {
  color: #4a5670;
  font-weight: 600;
}

.detalle-lista dd {
  margin: 0;
  color: #1c1c1c;
}

.detalle-cuerpo h3 {
  margin: 0 0 8px;
  font-size: 14px;
  color: #0b1f46;
}

.texto-pre {
  margin: 0;
  padding: 14px;
  background-color: #f1f4f6;
  border-radius: 8px;
  white-space: pre-wrap;
  font-size: 14px;
  line-height: 1.5;
  color: #1c1c1c;
}
</style>
