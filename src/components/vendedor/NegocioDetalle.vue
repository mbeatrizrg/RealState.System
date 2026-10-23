<template>
  <div class="crm-contactos">
    <MenuCrm activo="Negocios" />

    <main class="main-content registro">
      <router-link :to="volverA" class="volver">&larr; Volver a negocios</router-link>

      <p v-if="cargando && !detalle" class="estado-msg">Cargando negocio...</p>
      <p v-else-if="error" class="estado-msg error" role="alert">{{ error }}</p>

      <template v-else-if="detalle">
        <header class="ficha-cabecera">
          <div class="ficha-titulo">
            <h1 class="greeting">{{ n.nombre }}</h1>
            <div class="ficha-meta">
              <span :class="['badge', claseEtapa(n.etapaId)]">{{ n.etapa }}</span>
              <strong class="valor">{{ formatearPrecio(n.valor) }}</strong>
              <span>{{ n.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Venta' }}</span>
              <span>Propietario: <strong>{{ n.nombrePropietario ?? 'Sin asignar' }}</strong></span>
            </div>
          </div>
          <div v-if="detalle.puedeEditar" class="ficha-acciones">
            <button type="button" class="btn-secundario" @click="mostrarEdicion = true">Editar</button>
            <button type="button" class="btn-secundario peligro" :disabled="guardando" @click="eliminar">Eliminar</button>
          </div>
        </header>

        <!-- Etapas del pipeline: clic para mover el negocio -->
        <ol class="etapas" aria-label="Etapas del negocio">
          <li v-for="(e, i) in etapas" :key="e.id">
            <button
              type="button"
              :class="['etapa', { actual: e.id === n.etapaId, pasada: indiceActual > i && n.etapaAbierta && e.abierta, cierre: !e.abierta }]"
              :aria-current="e.id === n.etapaId ? 'step' : undefined"
              :disabled="!detalle.puedeEditar || guardando || e.id === n.etapaId"
              @click="moverA(e)"
            >
              {{ e.nombre }}
            </button>
          </li>
        </ol>

        <p v-if="aviso" class="aviso" role="status">{{ aviso }}</p>

        <div class="registro-grid">
          <aside class="lateral">
            <section class="tarjeta">
              <h2>Datos del negocio</h2>
              <dl class="datos">
                <dt>Operación</dt><dd>{{ n.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Venta' }}</dd>
                <dt>Valor</dt><dd>{{ formatearPrecio(n.valor) }}</dd>
                <dt>Etapa</dt><dd>{{ n.etapa }}</dd>
                <dt>Cierre estimado</dt><dd>{{ n.fechaCierreEstimada ? formatearFecha(`${n.fechaCierreEstimada}T00:00:00`) : '—' }}</dd>
                <dt v-if="n.fechaCierre">Cerrado el</dt><dd v-if="n.fechaCierre">{{ formatearFechaHora(n.fechaCierre) }}</dd>
                <dt>Propietario</dt><dd>{{ n.nombrePropietario ?? 'Sin asignar' }}</dd>
                <dt>Creado</dt><dd>{{ formatearFechaHora(n.fechaCreacion) }}</dd>
                <dt>Actualizado</dt><dd>{{ formatearFechaHora(n.fechaActualizacion) }}</dd>
              </dl>
            </section>

            <section class="tarjeta">
              <h2>Cliente {{ n.tipoOperacion === 'alquiler' ? 'inquilino' : 'comprador' }}</h2>
              <div class="cliente">
                <span class="avatar" aria-hidden="true">{{ inicialesCliente }}</span>
                <router-link :to="`/contact/${cliente.id}`" class="nombre">{{ cliente.nombre }} {{ cliente.apellido }}</router-link>
              </div>
              <dl class="datos">
                <dt>Correo</dt><dd><a :href="`mailto:${cliente.correo}`">{{ cliente.correo }}</a></dd>
                <dt>Teléfono</dt><dd><a :href="`tel:${cliente.telefono}`">{{ cliente.telefono }}</a></dd>
                <dt>Estatus</dt><dd><span :class="['badge', claseEstatus(cliente.estatusId)]">{{ cliente.estatus ?? 'Sin lead' }}</span></dd>
                <dt>Propietario</dt><dd>{{ cliente.nombrePropietario ?? 'Sin asignar' }}</dd>
              </dl>
            </section>
          </aside>

          <section class="centro">
            <div class="tarjeta">
              <h2>Historial de seguimiento</h2>
              <nav class="accesos accesos-fila" aria-label="Registrar seguimiento">
                <button v-for="acceso in ACCESOS" :key="acceso.tipo" type="button" class="acceso" @click="abrirAcceso(acceso.tipo)">
                  <span class="acceso-icono" aria-hidden="true">{{ acceso.icono }}</span>
                  {{ acceso.texto }}
                </button>
              </nav>
              <HistorialActividad :actividades="detalle.historial" vacio="Aún no hay seguimiento registrado en este negocio." @cambio="cargar" />
            </div>
          </section>

          <aside class="lateral">
            <section class="tarjeta">
              <h2>Inmueble vinculado</h2>
              <img :src="urlImagen(fotoActual)" :alt="detalle.inmueble.titulo" class="foto-principal" />
              <div v-if="detalle.fotos.length > 1" class="miniaturas">
                <button v-for="foto in detalle.fotos" :key="foto.id" type="button" :class="{ activa: foto.url === fotoActual }"
                        :aria-label="`Ver foto ${foto.id}`" @click="fotoActual = foto.url">
                  <img :src="urlImagen(foto.url)" alt="" loading="lazy" />
                </button>
              </div>
              <router-link :to="`/inmuebles/${detalle.inmueble.id}`" class="nombre">{{ detalle.inmueble.titulo }}</router-link>
              <dl class="datos">
                <dt>Código</dt><dd>{{ detalle.inmueble.codigo }}</dd>
                <dt>Tipo</dt><dd>{{ detalle.inmueble.tipoInmueble }}</dd>
                <dt>Ubicación</dt><dd>{{ detalle.inmueble.ubicacion }}<template v-if="detalle.inmueble.ciudad">, {{ detalle.inmueble.ciudad }}</template></dd>
                <dt>Precio</dt><dd>{{ formatearPrecio(detalle.inmueble.precio) }}</dd>
                <dt>Estado</dt><dd><span :class="['badge', claseEstado(detalle.inmueble.estado)]">{{ textoEstado(detalle.inmueble.estado) }}</span></dd>
                <template v-if="detalle.inmueble.habitaciones != null"><dt>Habitaciones</dt><dd>{{ detalle.inmueble.habitaciones }}</dd></template>
                <template v-if="detalle.inmueble.banos != null"><dt>Baños</dt><dd>{{ detalle.inmueble.banos }}</dd></template>
                <template v-if="detalle.inmueble.metrosCuadrados != null"><dt>Superficie</dt><dd>{{ detalle.inmueble.metrosCuadrados }} m²</dd></template>
                <dt>Asesor captador</dt><dd>{{ detalle.inmueble.nombreAsesor ?? '—' }}</dd>
              </dl>
            </section>
          </aside>
        </div>
      </template>
    </main>

    <ModalNegocio
      v-if="mostrarEdicion && detalle"
      :etapas="etapas"
      :propietarios="propietarios"
      :negocio="n"
      @cerrar="mostrarEdicion = false"
      @guardado="alEditar"
    />
    <ModalTarea
      v-if="mostrarTarea && detalle"
      :propietarios="propietarios"
      :negocio-inicial="n"
      @cerrar="mostrarTarea = false"
      @guardada="alGuardarActividad"
    />
    <ModalActividad
      v-if="nuevaActividad && detalle"
      :tipo="nuevaActividad"
      :contacto-id="cliente.id"
      :nombre-contacto="`${cliente.nombre} ${cliente.apellido}`"
      :correo-contacto="cliente.correo"
      :negocio-id="n.id"
      @cerrar="nuevaActividad = null"
      @guardada="alGuardarActividad"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import MenuCrm from './MenuCrm.vue'
import HistorialActividad from './contactos/HistorialActividad.vue'
import ModalActividad from './contactos/ModalActividad.vue'
import ModalNegocio from './negocios/ModalNegocio.vue'
import ModalTarea from './tareas/ModalTarea.vue'
import type { TipoActividad } from '@/services/actividades'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import { obtenerPropietarios } from '@/services/contactos'
import {
  cambiarEtapaNegocio, claseEtapa, eliminarNegocio, obtenerEtapas, obtenerNegocio, type EtapaNegocio, type NegocioDetalle,
} from '@/services/negocios'
import { claseEstado, textoEstado } from '@/services/propiedades'
import { formatearFecha, formatearFechaHora } from '@/utils/fechas'
import { formatearPrecio, urlImagen } from '@/utils/formato'
import { claseEstatus } from './contactos/estatus'

const route = useRoute()
const router = useRouter()

const ACCESOS: { tipo: TipoActividad; texto: string; icono: string }[] = [
  { tipo: 'NOTA', texto: 'Nota', icono: '✎' },
  { tipo: 'CORREO', texto: 'Correo', icono: '✉' },
  { tipo: 'LLAMADA', texto: 'Llamada', icono: '☎' },
  { tipo: 'TAREA', texto: 'Tarea', icono: '✓' },
  { tipo: 'REUNION', texto: 'Reunión', icono: '◷' },
]

const detalle = ref<NegocioDetalle | null>(null)
const n = computed(() => detalle.value!.negocio)
const cliente = computed(() => detalle.value!.contacto)
const etapas = ref<EtapaNegocio[]>([])
const propietarios = ref<Catalogo[]>([])
const cargando = ref(false)
const guardando = ref(false)
const error = ref('')
const aviso = ref('')
const fotoActual = ref<string | null>(null)
const mostrarEdicion = ref(false)
const nuevaActividad = ref<TipoActividad | null>(null)
const mostrarTarea = ref(false)
function abrirAcceso(tipo: TipoActividad) {
  if (tipo === 'TAREA') mostrarTarea.value = true
  else nuevaActividad.value = tipo
}

const volverA = computed(() => {
  const anterior = typeof window !== 'undefined' ? (window.history.state?.back as string | undefined) : undefined
  return anterior?.startsWith('/negocios') && !/^\/negocios\/\d/.test(anterior) ? anterior : '/negocios'
})
const indiceActual = computed(() => etapas.value.findIndex((e) => e.id === n.value.etapaId))
const inicialesCliente = computed(() => `${cliente.value.nombre.charAt(0)}${cliente.value.apellido.charAt(0)}`.toUpperCase())

async function cargar() {
  cargando.value = true
  error.value = ''
  try {
    detalle.value = await obtenerNegocio(String(route.params.id))
    const fotos = detalle.value.fotos
    if (!fotos.some((f) => f.url === fotoActual.value)) fotoActual.value = fotos[0]?.url ?? null
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
}

async function moverA(etapa: EtapaNegocio) {
  guardando.value = true
  aviso.value = ''
  try {
    await cambiarEtapaNegocio(n.value.id, etapa.id)
    aviso.value = `El negocio pasó a "${etapa.nombre}".`
    await cargar()
  } catch (e) {
    aviso.value = mensajeDeError(e)
  } finally {
    guardando.value = false
  }
}

async function eliminar() {
  if (!window.confirm(`¿Eliminar el negocio "${n.value.nombre}"? Su historial se conserva en el registro del contacto.`)) return
  guardando.value = true
  try {
    await eliminarNegocio(n.value.id)
    router.push('/negocios')
  } catch (e) {
    aviso.value = mensajeDeError(e)
    guardando.value = false
  }
}

function alEditar() {
  mostrarEdicion.value = false
  aviso.value = 'Negocio actualizado.'
  cargar()
}

function alGuardarActividad() {
  nuevaActividad.value = null
  mostrarTarea.value = false
  aviso.value = 'Seguimiento registrado.'
  cargar()
}

watch(() => route.params.id, () => {
  aviso.value = ''
  cargar()
})

onMounted(() => {
  cargar()
  obtenerEtapas().then((lista) => { etapas.value = lista }).catch(() => {})
  obtenerPropietarios().then((lista) => { propietarios.value = lista }).catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
<style scoped src="./contactos/detalle.css"></style>
<style scoped src="./contactos/registro.css"></style>
<style scoped src="./contactos/ficha.css"></style>
