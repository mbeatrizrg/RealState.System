<template>
  <div class="crm-contactos">
    <MenuCrm activo="Contactos" />

    <main class="main-content registro">
      <router-link :to="volverA" class="volver">&larr; Volver a contactos</router-link>

      <p v-if="cargando && !detalle" class="estado-msg">Cargando contacto...</p>
      <p v-else-if="error" class="estado-msg error" role="alert">{{ error }}</p>

      <template v-else-if="detalle">
        <p v-if="aviso" class="aviso" role="status">{{ aviso }}</p>

        <div class="registro-grid">
          <!-- ===== Barra lateral izquierda: datos principales y propiedades del contacto ===== -->
          <aside class="lateral">
            <section class="tarjeta perfil">
              <div class="perfil-cabecera">
                <span class="avatar grande" aria-hidden="true">{{ iniciales }}</span>
                <div class="perfil-nombre">
                  <h1>{{ c.nombre }} {{ c.apellido }}</h1>
                  <span :class="['badge', claseEstatus(c.estatusId)]">{{ c.estatus ?? 'Sin lead' }}</span>
                </div>
                <div ref="menuAcciones" class="menu-acciones">
                  <button type="button" class="btn-acciones" :aria-expanded="accionesAbiertas" aria-haspopup="menu"
                          @click="accionesAbiertas = !accionesAbiertas">
                    Acciones ▾
                  </button>
                  <ul v-if="accionesAbiertas" class="menu-lista" role="menu">
                    <li role="none"><button role="menuitem" type="button" @click="verTodas">
                      {{ todasLasPropiedades ? 'Ver solo las principales' : 'Ver todas las propiedades' }}
                    </button></li>
                    <li role="none"><button role="menuitem" type="button" @click="abrirNegocio">Crear negocio</button></li>
                    <li v-if="!esAdmin && c.propietarioId === null" role="none">
                      <button role="menuitem" type="button" :disabled="guardando" @click="asignar(sesion.id)">Asignarme este contacto</button>
                    </li>
                    <li v-if="!esAdmin && c.propietarioId === sesion.id" role="none">
                      <button role="menuitem" type="button" :disabled="guardando" @click="asignar(null)">Liberar contacto</button>
                    </li>
                    <li v-if="whatsapp" role="none"><a role="menuitem" :href="whatsapp" target="_blank" rel="noopener">Abrir WhatsApp</a></li>
                  </ul>
                </div>
              </div>

              <!-- Accesos rapidos -->
              <nav class="accesos" aria-label="Accesos rápidos">
                <button v-for="acceso in ACCESOS" :key="acceso.tipo" type="button" class="acceso" @click="abrirAcceso(acceso.tipo)">
                  <span class="acceso-icono" aria-hidden="true">{{ acceso.icono }}</span>
                  {{ acceso.texto }}
                </button>
              </nav>
            </section>

            <section class="tarjeta">
              <h2>Información del contacto</h2>
              <dl class="datos">
                <dt>Correo</dt><dd><a :href="`mailto:${c.correo}`">{{ c.correo }}</a></dd>
                <dt>Teléfono</dt><dd><a :href="`tel:${c.telefono}`">{{ c.telefono }}</a></dd>
                <dt>Propietario</dt><dd>{{ c.nombrePropietario ?? 'Sin asignar' }}</dd>
                <dt>Estatus del lead</dt><dd>{{ c.estatus ?? 'Sin lead' }}</dd>
                <dt>Última actividad</dt><dd>{{ c.ultimaActividad ? formatearFechaHora(c.ultimaActividad) : 'Sin actividad' }}</dd>
                <template v-if="todasLasPropiedades">
                  <dt>Cédula</dt><dd>{{ c.cedula ?? '—' }}</dd>
                  <dt>Tipo de negocio</dt><dd>{{ c.tipoNegocio || '—' }}</dd>
                  <dt>Cuenta del portal</dt><dd>{{ c.verificado ? 'Correo verificado' : 'Sin verificar' }}</dd>
                  <dt>Fecha de creación</dt><dd>{{ formatearFechaHora(c.fechaCreacion) }}</dd>
                  <dt>Leads</dt><dd>{{ c.cantidadLeads }}</dd>
                  <dt>Negocios</dt><dd>{{ negocios.length }}</dd>
                  <dt>Favoritos</dt><dd>{{ detalle.favoritos.length }}</dd>
                </template>
              </dl>
              <button type="button" class="enlace" @click="todasLasPropiedades = !todasLasPropiedades">
                {{ todasLasPropiedades ? 'Ver menos' : 'Ver todas las propiedades' }}
              </button>
            </section>

            <section v-if="esAdmin" class="tarjeta">
              <h2>Propietario del contacto</h2>
              <label class="campo">
                <span>Vendedor asignado</span>
                <select v-model="propietarioElegido">
                  <option :value="null">Sin asignar</option>
                  <option v-for="p in propietarios" :key="p.id" :value="p.id" :disabled="p.nombre.endsWith('(inactivo)')">{{ p.nombre }}</option>
                </select>
              </label>
              <button type="button" class="btn-filtros" :disabled="guardando || propietarioElegido === c.propietarioId"
                      @click="asignar(propietarioElegido)">
                Guardar propietario
              </button>
            </section>
          </aside>

          <!-- ===== Centro: historial continuo de actividad ===== -->
          <section class="centro">
            <div class="tarjeta">
              <h2>Actividad</h2>
              <p v-if="errorHistorial" class="texto error" role="alert">{{ errorHistorial }}</p>
              <HistorialActividad v-else :actividades="historial" @cambio="recargarRelacionados" />
            </div>
          </section>

          <!-- ===== Barra lateral derecha: informacion asociada ===== -->
          <aside class="lateral">
            <section class="tarjeta">
              <h2>
                Negocios en marcha <span class="contador">{{ negociosAbiertos.length }}</span>
                <button type="button" class="enlace derecha" @click="abrirNegocio">+ Crear</button>
              </h2>
              <ul v-if="negociosAbiertos.length" class="lista-asociados">
                <li v-for="n in negociosAbiertos" :key="n.id">
                  <router-link :to="`/negocios/${n.id}`">{{ n.nombre }}</router-link>
                  <span :class="['badge', claseEtapa(n.etapaId)]">{{ n.etapa }}</span>
                  <small>{{ formatearPrecio(n.valor) }}<template v-if="n.fechaCierreEstimada"> · cierre {{ formatearFecha(`${n.fechaCierreEstimada}T00:00:00`) }}</template></small>
                </li>
              </ul>
              <p v-else class="texto">Sin negocios abiertos.</p>
            </section>

            <section class="tarjeta">
              <h2>Pedidos realizados <span class="contador">{{ pedidos.length }}</span></h2>
              <ul v-if="pedidos.length" class="lista-asociados">
                <li v-for="n in pedidos" :key="n.id">
                  <router-link :to="`/negocios/${n.id}`">{{ n.nombre }}</router-link>
                  <small>{{ n.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Compra' }} · {{ formatearPrecio(n.valor) }} · {{ formatearFecha(n.fechaCierre) }}</small>
                </li>
              </ul>
              <p v-else class="texto">Todavía no ha cerrado ninguna compra o alquiler.</p>
            </section>

            <section class="tarjeta">
              <h2>Tickets de servicio</h2>
              <p class="texto">No hay tickets registrados. El seguimiento de solicitudes del cliente se lleva por ahora con tareas y notas.</p>
            </section>

            <section class="tarjeta">
              <h2>Adjuntos <span class="contador">{{ adjuntos.length }}</span></h2>
              <ul v-if="adjuntos.length" class="lista-asociados">
                <li v-for="archivo in adjuntos" :key="archivo.id">
                  <button type="button" class="enlace" @click="descargar(archivo)">📎 {{ archivo.nombre }}</button>
                  <small>{{ formatearTamano(archivo.tamano) }} · {{ formatearFecha(archivo.fechaSubida) }}</small>
                </li>
              </ul>
              <p v-else class="texto">Los archivos enviados por correo o adjuntos a notas y llamadas aparecen aquí.</p>
            </section>

            <section class="tarjeta">
              <h2>Leads <span class="contador">{{ detalle.leads.length }}</span></h2>
              <ul v-if="detalle.leads.length" class="lista-asociados">
                <li v-for="lead in detalle.leads" :key="lead.id">
                  <router-link :to="`/inmuebles/${lead.inmuebleId}`">{{ lead.tituloInmueble }}</router-link>
                  <small>{{ lead.rrbrInmueble }} · {{ lead.nombreVendedor }}</small>
                  <select v-if="puedeEditarLead(lead)" :value="lead.estatus" class="select-estatus"
                          :aria-label="`Estatus del lead de ${lead.tituloInmueble}`" :disabled="guardando"
                          @change="cambiarEstatus(lead, Number(($event.target as HTMLSelectElement).value))">
                    <option v-for="e in estatusLead" :key="e.id" :value="e.id">{{ e.nombre }}</option>
                  </select>
                  <span v-else :class="['badge', claseEstatus(lead.estatus)]">{{ nombreEstatus(lead.estatus) }}</span>
                </li>
              </ul>
              <p v-else class="texto">Aún no tiene leads. Se crean desde el portal ("Contáctanos") o desde Control.</p>

              <details v-if="leadsEditables.length" class="comunicacion">
                <summary>Registrar comunicación de un lead</summary>
                <form class="form-comunicacion" @submit.prevent="guardarComunicacion">
                  <label class="campo">
                    <span>Lead</span>
                    <select v-model="nueva.leadId" required>
                      <option v-for="lead in leadsEditables" :key="lead.id" :value="lead.id">{{ lead.tituloInmueble }}</option>
                    </select>
                  </label>
                  <label class="campo">
                    <span>Canal</span>
                    <select v-model="nueva.canalId" required>
                      <option v-for="canal in canales" :key="canal.id" :value="canal.id">{{ canal.nombre }}</option>
                    </select>
                  </label>
                  <label class="campo">
                    <span>Resumen</span>
                    <textarea v-model="nueva.descripcion" rows="3" maxlength="2000" required placeholder="¿Qué se habló con el contacto?" />
                  </label>
                  <button type="submit" class="btn-filtros" :disabled="guardando || !nueva.descripcion.trim() || !nueva.canalId">Registrar</button>
                </form>
              </details>
            </section>

            <section class="tarjeta">
              <h2>Actividad en el portal</h2>
              <h3 class="subtitulo-tarjeta">Favoritos</h3>
              <ul v-if="detalle.favoritos.length" class="lista-inmuebles">
                <li v-for="f in detalle.favoritos" :key="f.inmuebleId">
                  <router-link :to="`/inmuebles/${f.inmuebleId}`">{{ f.titulo }}</router-link>
                  <span>{{ formatearPrecio(f.precio) }}<template v-if="f.vendido"> · Vendido</template></span>
                </li>
              </ul>
              <p v-else class="texto">No ha guardado inmuebles en favoritos.</p>
              <h3 class="subtitulo-tarjeta">Vistos recientemente</h3>
              <ul v-if="detalle.vistasRecientes.length" class="lista-inmuebles">
                <li v-for="(v, i) in detalle.vistasRecientes" :key="`${v.inmuebleId}-${i}`">
                  <router-link :to="`/inmuebles/${v.inmuebleId}`">{{ v.titulo }}</router-link>
                  <span>{{ formatearFechaHora(v.fecha) }}</span>
                </li>
              </ul>
              <p v-else class="texto">No hay vistas registradas en el portal.</p>
            </section>
          </aside>
        </div>
      </template>
    </main>

    <ModalActividad
      v-if="nuevaActividad && detalle"
      :tipo="nuevaActividad"
      :contacto-id="c.id"
      :nombre-contacto="`${c.nombre} ${c.apellido}`"
      :correo-contacto="c.correo"
      :negocios="negociosAbiertos"
      @cerrar="nuevaActividad = null"
      @guardada="alGuardarActividad"
    />
    <ModalTarea
      v-if="mostrarTarea && detalle"
      :propietarios="propietarios"
      :contacto-inicial="{ id: c.id, nombre: `${c.nombre} ${c.apellido}` }"
      @cerrar="mostrarTarea = false"
      @guardada="alGuardarTarea"
    />
    <ModalNegocio
      v-if="mostrarNegocio && detalle"
      :etapas="etapas"
      :propietarios="propietarios"
      :contacto-inicial="{ id: c.id, nombre: `${c.nombre} ${c.apellido}` }"
      @cerrar="mostrarNegocio = false"
      @guardado="alCrearNegocio"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import MenuCrm from './MenuCrm.vue'
import HistorialActividad from './contactos/HistorialActividad.vue'
import ModalActividad from './contactos/ModalActividad.vue'
import ModalNegocio from './negocios/ModalNegocio.vue'
import ModalTarea from './tareas/ModalTarea.vue'
import {
  descargarArchivo, formatearTamano, obtenerAdjuntosContacto, obtenerHistorialContacto,
  type Actividad, type Archivo, type TipoActividad,
} from '@/services/actividades'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import {
  cambiarEstatusLead, cambiarPropietario, obtenerCanales, obtenerContacto, obtenerEstatusLead, obtenerPropietarios,
  registrarComunicacion, type ContactoDetalle, type LeadContacto,
} from '@/services/contactos'
import { buscarNegocios, claseEtapa, obtenerEtapas, type EtapaNegocio, type Negocio } from '@/services/negocios'
import { esAdmin, sesion } from '@/services/sesion'
import { formatearFecha, formatearFechaHora } from '@/utils/fechas'
import { formatearPrecio } from '@/utils/formato'
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

const detalle = ref<ContactoDetalle | null>(null)
const c = computed(() => detalle.value!.contacto)
const historial = ref<Actividad[]>([])
const adjuntos = ref<Archivo[]>([])
const negocios = ref<Negocio[]>([])
const cargando = ref(false)
const guardando = ref(false)
const error = ref('')
const errorHistorial = ref('')
const aviso = ref('')

const propietarios = ref<Catalogo[]>([])
const estatusLead = ref<Catalogo[]>([])
const canales = ref<Catalogo[]>([])
const etapas = ref<EtapaNegocio[]>([])
const propietarioElegido = ref<number | null>(null)
const todasLasPropiedades = ref(false)
const nuevaActividad = ref<TipoActividad | null>(null)
const mostrarNegocio = ref(false)
const mostrarTarea = ref(false)

// La tarea usa el formulario del modulo de Tareas (tipo, prioridad, inmueble); el resto, el de actividades
function abrirAcceso(tipo: TipoActividad) {
  if (tipo === 'TAREA') mostrarTarea.value = true
  else nuevaActividad.value = tipo
}

function alGuardarTarea() {
  mostrarTarea.value = false
  aviso.value = 'Tarea creada.'
  recargarRelacionados()
}

// Vuelve a la pestaña desde la que se abrio la ficha (el navegador guarda la URL anterior en history.state.back)
const volverA = computed(() => {
  const anterior = typeof window !== 'undefined' ? (window.history.state?.back as string | undefined) : undefined
  return anterior?.startsWith('/contact?') || anterior === '/contact' ? anterior : '/contact'
})

const iniciales = computed(() => `${c.value.nombre.charAt(0)}${c.value.apellido.charAt(0)}`.toUpperCase())

// Telefonos venezolanos "0414-1234567" -> wa.me/584141234567
const whatsapp = computed(() => {
  const digitos = c.value.telefono.replace(/\D/g, '')
  if (digitos.length < 10) return ''
  return `https://wa.me/${digitos.startsWith('0') ? `58${digitos.slice(1)}` : digitos}`
})

const negociosAbiertos = computed(() => negocios.value.filter((n) => n.etapaAbierta))
// Pedidos realizados: compras o alquileres cerrados con exito (etapa "Cierre ganado")
const pedidos = computed(() => negocios.value.filter((n) => !n.etapaAbierta && n.etapaId === 6))

const nombreEstatus = (id: number) => estatusLead.value.find((e) => e.id === id)?.nombre ?? `Estatus ${id}`
// El backend vuelve a validar: solo el vendedor del inmueble o un administrador puede modificar el lead
const puedeEditarLead = (lead: LeadContacto) => esAdmin.value || lead.empleadoId === sesion.id
const leadsEditables = computed(() => detalle.value?.leads.filter(puedeEditarLead) ?? [])
const nueva = reactive({ leadId: null as number | null, canalId: null as number | null, descripcion: '' })

// ----- Menu "Acciones" -----
const accionesAbiertas = ref(false)
const menuAcciones = ref<HTMLElement | null>(null)
function clicFuera(evento: MouseEvent) {
  if (menuAcciones.value && !menuAcciones.value.contains(evento.target as Node)) accionesAbiertas.value = false
}
onMounted(() => document.addEventListener('mousedown', clicFuera))
onBeforeUnmount(() => document.removeEventListener('mousedown', clicFuera))

function verTodas() {
  todasLasPropiedades.value = !todasLasPropiedades.value
  accionesAbiertas.value = false
}
function abrirNegocio() {
  accionesAbiertas.value = false
  mostrarNegocio.value = true
}

// ----- Carga -----
async function cargar() {
  cargando.value = true
  error.value = ''
  try {
    detalle.value = await obtenerContacto(String(route.params.id))
    propietarioElegido.value = detalle.value.contacto.propietarioId
    if (!leadsEditables.value.some((l) => l.id === nueva.leadId)) nueva.leadId = leadsEditables.value[0]?.id ?? null
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
}

// Historial, adjuntos y negocios (se recargan despues de cada actividad sin volver a pedir la ficha)
async function recargarRelacionados() {
  const id = String(route.params.id)
  errorHistorial.value = ''
  const [h, a, n] = await Promise.allSettled([
    obtenerHistorialContacto(id),
    obtenerAdjuntosContacto(id),
    buscarNegocios({ contactoId: Number(id), tamano: 100, orden: 'fechaCreacion', direccion: 'desc' }),
  ])
  if (h.status === 'fulfilled') historial.value = h.value
  else errorHistorial.value = mensajeDeError(h.reason)
  if (a.status === 'fulfilled') adjuntos.value = a.value
  if (n.status === 'fulfilled') negocios.value = n.value.contenido
}

async function ejecutar(accion: () => Promise<unknown>, mensaje: string) {
  guardando.value = true
  aviso.value = ''
  accionesAbiertas.value = false
  try {
    await accion()
    aviso.value = mensaje
    await Promise.all([cargar(), recargarRelacionados()])
    return true
  } catch (e) {
    aviso.value = mensajeDeError(e)
    return false
  } finally {
    guardando.value = false
  }
}

const asignar = (empleadoId: number | null) =>
  ejecutar(() => cambiarPropietario(c.value.id, empleadoId),
    empleadoId === null ? 'El contacto quedó sin asignar.' : 'Propietario actualizado.')

const cambiarEstatus = (lead: LeadContacto, estatus: number) =>
  ejecutar(() => cambiarEstatusLead(lead.id, estatus), `Estatus del lead "${lead.tituloInmueble}" actualizado.`)

async function guardarComunicacion() {
  if (!nueva.leadId || !nueva.canalId || !nueva.descripcion.trim()) return
  const { leadId, canalId } = nueva
  const descripcion = nueva.descripcion.trim()
  if (await ejecutar(() => registrarComunicacion(leadId, canalId, descripcion), 'Comunicación registrada.')) {
    nueva.descripcion = ''
  }
}

const MENSAJES: Record<TipoActividad, string> = {
  NOTA: 'Nota guardada.', CORREO: 'Correo guardado en el historial.', LLAMADA: 'Llamada registrada.', TAREA: 'Tarea creada.',
  REUNION: 'Reunión programada.',
}
function alGuardarActividad(actividad: Actividad) {
  nuevaActividad.value = null
  aviso.value = MENSAJES[actividad.tipo as TipoActividad] ?? ''
  recargarRelacionados()
  cargar()
}

function alCrearNegocio(id: number) {
  mostrarNegocio.value = false
  router.push(`/negocios/${id}`)
}

async function descargar(archivo: Archivo) {
  try {
    await descargarArchivo(archivo)
  } catch (e) {
    aviso.value = mensajeDeError(e)
  }
}

// Al pasar de un contacto a otro sin salir de la pagina
watch(() => route.params.id, () => {
  aviso.value = ''
  todasLasPropiedades.value = false
  cargar()
  recargarRelacionados()
})

onMounted(() => {
  cargar()
  recargarRelacionados()
  obtenerEstatusLead().then((lista) => { estatusLead.value = lista }).catch(() => {})
  obtenerEtapas().then((lista) => { etapas.value = lista }).catch(() => {})
  obtenerPropietarios().then((lista) => { propietarios.value = lista }).catch(() => {})
  obtenerCanales()
    .then((lista) => {
      canales.value = lista
      nueva.canalId = lista[0]?.id ?? null
    })
    .catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
<style scoped src="./contactos/detalle.css"></style>
<style scoped src="./contactos/registro.css"></style>
