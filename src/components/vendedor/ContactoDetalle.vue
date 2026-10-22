<template>
  <div class="crm-contactos">
    <MenuCrm activo="Contactos" />

    <main class="main-content">
      <router-link :to="volverA" class="volver">&larr; Volver a contactos</router-link>

      <p v-if="cargando && !detalle" class="estado-msg">Cargando contacto...</p>
      <p v-else-if="error" class="estado-msg error" role="alert">{{ error }}</p>

      <template v-else-if="detalle">
        <!-- Cabecera de la ficha -->
        <header class="ficha-cabecera">
          <span class="avatar grande" aria-hidden="true">{{ iniciales }}</span>
          <div class="ficha-titulo">
            <h1 class="greeting">{{ c.nombre }} {{ c.apellido }}</h1>
            <div class="ficha-meta">
              <span :class="['badge', claseEstatus(c.estatusId)]">{{ c.estatus ?? 'Sin lead' }}</span>
              <span>Propietario: <strong>{{ c.nombrePropietario ?? 'Sin asignar' }}</strong></span>
            </div>
          </div>
          <div class="ficha-acciones">
            <a :href="`mailto:${c.correo}`" class="btn-secundario">Enviar correo</a>
            <a :href="`tel:${c.telefono}`" class="btn-secundario">Llamar</a>
            <a v-if="whatsapp" :href="whatsapp" target="_blank" rel="noopener" class="btn-filtros">WhatsApp</a>
          </div>
        </header>

        <p v-if="aviso" class="aviso" role="status">{{ aviso }}</p>

        <div class="ficha-grid">
          <!-- Columna izquierda: datos y propietario -->
          <div class="columna">
            <section class="tarjeta">
              <h2>Información del contacto</h2>
              <dl class="datos">
                <dt>Correo</dt><dd>{{ c.correo }}</dd>
                <dt>Teléfono</dt><dd>{{ c.telefono }}</dd>
                <dt>Cédula</dt><dd>{{ c.cedula ?? '—' }}</dd>
                <dt>Tipo de negocio</dt><dd>{{ c.tipoNegocio || '—' }}</dd>
                <dt>Cuenta del portal</dt><dd>{{ c.verificado ? 'Correo verificado' : 'Sin verificar' }}</dd>
                <dt>Fecha de creación</dt><dd>{{ formatearFechaHora(c.fechaCreacion) }}</dd>
                <dt>Última actividad</dt><dd>{{ c.ultimaActividad ? formatearFechaHora(c.ultimaActividad) : 'Sin actividad' }}</dd>
                <dt>Leads</dt><dd>{{ c.cantidadLeads }}</dd>
              </dl>
            </section>

            <section class="tarjeta">
              <h2>Propietario del contacto</h2>
              <!-- Admin: asigna a cualquier empleado. Vendedor: toma contactos sin asignar o libera los suyos -->
              <template v-if="esAdmin">
                <label class="campo">
                  <span>Vendedor asignado</span>
                  <select v-model="propietarioElegido">
                    <option :value="null">Sin asignar</option>
                    <option v-for="p in propietarios" :key="p.id" :value="p.id" :disabled="p.nombre.endsWith('(inactivo)')">
                      {{ p.nombre }}
                    </option>
                  </select>
                </label>
                <button
                  type="button"
                  class="btn-filtros"
                  :disabled="guardando || propietarioElegido === c.propietarioId"
                  @click="asignar(propietarioElegido)"
                >
                  Guardar propietario
                </button>
              </template>
              <template v-else>
                <p class="texto">{{ c.nombrePropietario ? `Este contacto está a cargo de ${c.nombrePropietario}.` : 'Este contacto no tiene propietario.' }}</p>
                <button v-if="c.propietarioId === null" type="button" class="btn-filtros" :disabled="guardando" @click="asignar(sesion.id)">
                  Asignarme este contacto
                </button>
                <button v-else-if="c.propietarioId === sesion.id" type="button" class="btn-secundario" :disabled="guardando" @click="asignar(null)">
                  Liberar contacto
                </button>
              </template>
            </section>

            <section class="tarjeta">
              <h2>Favoritos en el portal</h2>
              <ul v-if="detalle.favoritos.length" class="lista-inmuebles">
                <li v-for="f in detalle.favoritos" :key="f.inmuebleId">
                  <router-link :to="`/details/${f.inmuebleId}`">{{ f.titulo }}</router-link>
                  <span>{{ formatearPrecio(f.precio) }}<template v-if="f.vendido"> · Vendido</template></span>
                </li>
              </ul>
              <p v-else class="texto">No ha guardado inmuebles en favoritos.</p>
            </section>
          </div>

          <!-- Columna derecha: leads, comunicaciones y vistas -->
          <div class="columna">
            <section class="tarjeta">
              <h2>Leads <span class="contador">{{ detalle.leads.length }}</span></h2>
              <div v-if="detalle.leads.length" class="tabla-scroll">
                <table class="tabla-simple">
                  <thead>
                    <tr><th>Inmueble</th><th>Código RRBR</th><th>Vendedor</th><th>Estatus</th></tr>
                  </thead>
                  <tbody>
                    <tr v-for="lead in detalle.leads" :key="lead.id">
                      <td><router-link :to="`/details/${lead.inmuebleId}`">{{ lead.tituloInmueble }}</router-link></td>
                      <td>{{ lead.rrbrInmueble }}</td>
                      <td>{{ lead.nombreVendedor }}</td>
                      <td>
                        <select
                          v-if="puedeEditar(lead)"
                          :value="lead.estatus"
                          class="select-estatus"
                          :aria-label="`Estatus del lead de ${lead.tituloInmueble}`"
                          :disabled="guardando"
                          @change="cambiarEstatus(lead, Number(($event.target as HTMLSelectElement).value))"
                        >
                          <option v-for="e in estatusLead" :key="e.id" :value="e.id">{{ e.nombre }}</option>
                        </select>
                        <span v-else :class="['badge', claseEstatus(lead.estatus)]">{{ nombreEstatus(lead.estatus) }}</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <p v-else class="texto">Este contacto aún no tiene leads. Se crean desde el detalle de un inmueble ("Contáctanos") o desde Control.</p>
            </section>

            <section class="tarjeta">
              <h2>Comunicaciones</h2>
              <form v-if="leadsEditables.length" class="form-comunicacion" @submit.prevent="guardarComunicacion">
                <div class="fila-campos">
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
                </div>
                <label class="campo">
                  <span>Resumen</span>
                  <textarea v-model="nueva.descripcion" rows="3" maxlength="2000" required placeholder="¿Qué se habló con el contacto?" />
                </label>
                <button type="submit" class="btn-filtros" :disabled="guardando || !nueva.descripcion.trim() || !nueva.canalId">
                  Registrar comunicación
                </button>
              </form>

              <ol v-if="detalle.comunicaciones.length" class="linea-tiempo">
                <li v-for="com in detalle.comunicaciones" :key="com.id">
                  <div class="evento-cabecera">
                    <strong>{{ com.canal }}</strong>
                    <span>{{ formatearFechaHora(com.fechaCreacion) }} · {{ com.nombreVendedor }}</span>
                  </div>
                  <p>{{ com.descripcion }}</p>
                </li>
              </ol>
              <p v-else class="texto">
                {{ esAdmin ? 'No hay comunicaciones registradas.' : 'No hay comunicaciones registradas en tus leads con este contacto.' }}
              </p>
            </section>

            <section class="tarjeta">
              <h2>Inmuebles vistos recientemente</h2>
              <ul v-if="detalle.vistasRecientes.length" class="lista-inmuebles">
                <li v-for="(v, i) in detalle.vistasRecientes" :key="`${v.inmuebleId}-${i}`">
                  <router-link :to="`/details/${v.inmuebleId}`">{{ v.titulo }}</router-link>
                  <span>{{ formatearFechaHora(v.fecha) }}</span>
                </li>
              </ul>
              <p v-else class="texto">No hay vistas registradas en el portal.</p>
            </section>
          </div>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

import MenuCrm from './MenuCrm.vue'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import {
  cambiarEstatusLead, cambiarPropietario, obtenerCanales, obtenerContacto, obtenerEstatusLead, obtenerPropietarios,
  registrarComunicacion, type ContactoDetalle, type LeadContacto,
} from '@/services/contactos'
import { esAdmin, sesion } from '@/services/sesion'
import { formatearFechaHora } from '@/utils/fechas'
import { formatearPrecio } from '@/utils/formato'
import { claseEstatus } from './contactos/estatus'

const route = useRoute()

const detalle = ref<ContactoDetalle | null>(null)
const c = computed(() => detalle.value!.contacto)
const cargando = ref(false)
const guardando = ref(false)
const error = ref('')
const aviso = ref('')

const propietarios = ref<Catalogo[]>([])
const estatusLead = ref<Catalogo[]>([])
const canales = ref<Catalogo[]>([])
const propietarioElegido = ref<number | null>(null)

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

const nombreEstatus = (id: number) => estatusLead.value.find((e) => e.id === id)?.nombre ?? `Estatus ${id}`

// El backend vuelve a validar: solo el vendedor del inmueble o un administrador puede modificar el lead
const puedeEditar = (lead: LeadContacto) => esAdmin.value || lead.empleadoId === sesion.id
const leadsEditables = computed(() => detalle.value?.leads.filter(puedeEditar) ?? [])

const nueva = reactive({ leadId: null as number | null, canalId: null as number | null, descripcion: '' })

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

// Ejecuta una accion, muestra su mensaje y recarga la ficha. Devuelve si la accion se completo
async function ejecutar(accion: () => Promise<unknown>, mensaje: string) {
  guardando.value = true
  aviso.value = ''
  try {
    await accion()
    aviso.value = mensaje
    await cargar()
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

// Al pasar de un contacto a otro sin salir de la pagina
watch(() => route.params.id, () => {
  aviso.value = ''
  cargar()
})

onMounted(() => {
  cargar()
  obtenerEstatusLead().then((lista) => { estatusLead.value = lista }).catch(() => {})
  obtenerCanales()
    .then((lista) => {
      canales.value = lista
      nueva.canalId = lista[0]?.id ?? null
    })
    .catch(() => {})
  if (esAdmin.value) obtenerPropietarios().then((lista) => { propietarios.value = lista }).catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
<style scoped src="./contactos/detalle.css"></style>
