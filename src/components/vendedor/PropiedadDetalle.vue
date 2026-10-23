<template>
  <div class="crm-contactos">
    <MenuCrm activo="Inmuebles" />

    <main class="main-content registro">
      <router-link :to="volverA" class="volver">&larr; Volver a inmuebles</router-link>

      <p v-if="cargando && !detalle" class="estado-msg">Cargando inmueble...</p>
      <p v-else-if="error" class="estado-msg error" role="alert">{{ error }}</p>

      <template v-else-if="detalle">
        <header class="ficha-cabecera cabecera-inmueble">
          <div class="ficha-titulo">
            <p class="codigo-inmueble">{{ p.codigo }} · {{ p.tipoInmueble }} · {{ p.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Venta' }}</p>
            <h1 class="greeting">{{ p.titulo }}</h1>
            <p class="ubicacion-inmueble">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 21s-7-6.2-7-11.5A7 7 0 0119 9.5C19 14.8 12 21 12 21z" />
                <circle cx="12" cy="9.5" r="2.5" stroke-width="2" />
              </svg>
              {{ p.ubicacion }}<template v-if="p.ciudad">, {{ p.ciudad }}</template>
            </p>
            <div class="ficha-meta">
              <strong class="valor">{{ formatearPrecio(p.precio) }}<small v-if="p.tipoOperacion === 'alquiler'"> / mes</small></strong>
              <span :class="['badge', claseEstado(p.estado)]">{{ textoEstado(p.estado) }}</span>
            </div>
          </div>
          <div class="ficha-acciones">
            <router-link :to="`/details/${p.id}`" class="btn-secundario">Ver en el portal</router-link>
            <template v-if="detalle.puedeEditar">
              <router-link :to="`/control?inmueble=${p.id}`" class="btn-secundario">Editar datos y fotos</router-link>
              <button type="button" class="btn-secundario" @click="mostrarFicha = true">Editar ficha</button>
            </template>
            <button type="button" class="btn-secundario" @click="mostrarTarea = true">Crear tarea</button>
            <button type="button" class="btn-filtros" @click="mostrarNegocio = true">Crear negocio</button>
          </div>
        </header>

        <p v-if="aviso" class="aviso" role="status">{{ aviso }}</p>

        <GaleriaMosaico v-if="fotos.length" :fotos="fotos" :titulo="p.titulo" />
        <p v-else class="sin-fotos-aviso">
          Este inmueble aún no tiene fotos.
          <router-link v-if="detalle.puedeEditar" :to="`/control?inmueble=${p.id}`">Agregar fotos</router-link>
        </p>

        <div class="ficha-grid grid-inmueble">
          <div class="columna">
            <section class="tarjeta">
              <h2>Datos del inmueble</h2>
              <dl class="datos">
                <dt>Código RRBR</dt><dd>{{ p.codigo }}</dd>
                <dt>Tipo</dt><dd>{{ p.tipoInmueble }}</dd>
                <dt>Operación</dt><dd>{{ p.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Venta' }}</dd>
                <dt>Ubicación</dt><dd>{{ p.ubicacion }}<template v-if="p.ciudad">, {{ p.ciudad }}</template></dd>
                <dt>Precio</dt><dd>{{ formatearPrecio(p.precio) }}</dd>
                <dt>Estado</dt><dd>{{ textoEstado(p.estado) }}</dd>
                <dt>Asesor captador</dt><dd>{{ p.nombreAsesor ?? '—' }}</dd>
                <dt>Captado el</dt><dd>{{ formatearFecha(p.fechaCaptacion) }}</dd>
              </dl>
            </section>

            <section class="tarjeta">
              <h2>Propietario (dueño)</h2>
              <dl v-if="p.duenoNombre || p.duenoTelefono || p.duenoCorreo" class="datos">
                <dt>Nombre</dt><dd>{{ p.duenoNombre ?? '—' }}</dd>
                <dt>Teléfono</dt><dd><a v-if="p.duenoTelefono" :href="`tel:${p.duenoTelefono}`">{{ p.duenoTelefono }}</a><template v-else>—</template></dd>
                <dt>Correo</dt><dd><a v-if="p.duenoCorreo" :href="`mailto:${p.duenoCorreo}`">{{ p.duenoCorreo }}</a><template v-else>—</template></dd>
              </dl>
              <p v-else class="texto">No se han registrado los datos del dueño{{ detalle.puedeEditar ? ' (use "Editar ficha").' : '.' }}</p>
            </section>

            <section class="tarjeta">
              <h2>Clientes interesados <span class="contador">{{ detalle.interesados.length }}</span></h2>
              <ul v-if="detalle.interesados.length" class="lista-asociados">
                <li v-for="i in detalle.interesados" :key="i.leadId">
                  <router-link :to="`/contact/${i.contactoId}`">{{ i.nombre }}</router-link>
                  <small>{{ i.correo }} · {{ i.telefono }}</small>
                  <span :class="['badge', claseEstatus(i.estatusId)]">{{ nombreEstatus(i.estatusId) }}</span>
                </li>
              </ul>
              <p v-else class="texto">Nadie ha pedido información de este inmueble todavía.</p>
            </section>
          </div>

          <div class="columna">
            <section class="tarjeta">
              <h2>Características físicas</h2>
              <div class="caracteristicas">
                <div class="caracteristica">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M3 18v-6a2 2 0 012-2h14a2 2 0 012 2v6M3 18h18M3 18v2m18-2v2M6 10V7a2 2 0 012-2h8a2 2 0 012 2v3" />
                  </svg>
                  <strong>{{ p.habitaciones ?? '—' }}</strong><span>Habitaciones</span>
                </div>
                <div class="caracteristica">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M4 12h16v2a5 5 0 01-5 5H9a5 5 0 01-5-5v-2zM6 12V6a2 2 0 014 0M7 19l-1 2m11-2l1 2" />
                  </svg>
                  <strong>{{ p.banos ?? '—' }}</strong><span>Baños</span>
                </div>
                <div class="caracteristica">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M4 4h16v16H4zM4 9h3M4 14h3M9 4v3M14 4v3" />
                  </svg>
                  <strong>{{ p.metrosCuadrados ?? '—' }}<small v-if="p.metrosCuadrados != null"> m²</small></strong><span>Superficie</span>
                </div>
                <div class="caracteristica">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M5 17h14M6 17l1.5-5h9l1.5 5M7 17v2m10-2v2M8.5 12l1-3h5l1 3" />
                  </svg>
                  <strong>{{ p.estacionamientos ?? '—' }}</strong><span>Estacionamiento</span>
                </div>
                <div class="caracteristica">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M5 21V4h14v17M3 21h18M9 8h2m2 0h2M9 12h2m2 0h2M9 16h2m2 0h2" />
                  </svg>
                  <strong>{{ p.piso == null ? '—' : p.piso === 0 ? 'PB' : p.piso }}</strong><span>Piso</span>
                </div>
              </div>
              <ul v-if="detalle.extras.length" class="etiquetas">
                <li v-for="extra in detalle.extras" :key="extra">{{ extra }}</li>
              </ul>
              <p v-if="detalle.descripcion" class="descripcion-inmueble">{{ detalle.descripcion }}</p>
            </section>

            <section class="tarjeta">
              <h2>Documentos legales <span class="contador">{{ detalle.documentos.length }}</span></h2>
              <ul v-if="detalle.documentos.length" class="lista-asociados">
                <li v-for="doc in detalle.documentos" :key="doc.id" class="documento">
                  <span>
                    <button type="button" class="enlace" @click="descargar(doc)">📄 {{ doc.nombre }}</button>
                    <small> {{ formatearTamano(doc.tamano) }} · {{ formatearFecha(doc.fechaSubida) }}</small>
                  </span>
                  <button v-if="detalle.puedeEditar" type="button" class="btn-quitar" :disabled="guardando" @click="quitarDocumento(doc)">
                    Eliminar
                  </button>
                </li>
              </ul>
              <p v-else class="texto">Sin documentos (título de propiedad, cédula catastral, contratos...).</p>
              <template v-if="detalle.puedeEditar">
                <input ref="selector" type="file" multiple hidden accept=".pdf,.jpg,.jpeg,.png,.webp,.doc,.docx,.xls,.xlsx" @change="subir" />
                <button type="button" class="btn-secundario" :disabled="guardando" @click="selector?.click()">
                  {{ guardando ? 'Subiendo...' : 'Subir documentos' }}
                </button>
              </template>
            </section>

            <section class="tarjeta">
              <h2>Negocios <span class="contador">{{ detalle.negocios.length }}</span></h2>
              <div v-if="detalle.negocios.length" class="tabla-scroll">
                <table class="tabla-simple">
                  <thead><tr><th>Negocio</th><th>Cliente</th><th>Etapa</th><th>Valor</th><th>Propietario</th></tr></thead>
                  <tbody>
                    <tr v-for="neg in detalle.negocios" :key="neg.id">
                      <td><router-link :to="`/negocios/${neg.id}`">{{ neg.nombre }}</router-link></td>
                      <td><router-link :to="`/contact/${neg.contactoId}`">{{ neg.nombreContacto }}</router-link></td>
                      <td><span :class="['badge', claseEtapa(neg.etapaId)]">{{ neg.etapa }}</span></td>
                      <td>{{ formatearPrecio(neg.valor) }}</td>
                      <td>{{ neg.nombrePropietario ?? 'Sin asignar' }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <p v-else class="texto">Este inmueble no tiene negocios. Crea uno para darle seguimiento a un cliente interesado.</p>
            </section>

            <section class="tarjeta tarjeta-mapa">
              <h2>Ubicación</h2>
              <p class="texto">{{ p.ubicacion }}<template v-if="p.ciudad">, {{ p.ciudad }}</template></p>
              <iframe
                :src="`https://www.google.com/maps?q=${encodeURIComponent([p.ubicacion, p.ciudad].filter(Boolean).join(', '))}&output=embed`"
                class="mapa-inmueble"
                loading="lazy"
                referrerpolicy="no-referrer-when-downgrade"
                :title="`Ubicación de ${p.titulo} en el mapa`"
              />
            </section>
          </div>
        </div>
      </template>
    </main>

    <ModalCrm v-if="mostrarFicha && detalle" titulo="Editar ficha del inmueble" @cerrar="mostrarFicha = false">
      <form id="form-ficha" class="form-grid" @submit.prevent="guardarFicha">
        <label class="campo ancho">
          <span>Estado</span>
          <select v-model="ficha.estado">
            <option v-for="e in ESTADOS_INMUEBLE" :key="e.valor" :value="e.valor">{{ e.texto }}</option>
          </select>
          <small class="ayuda-campo">Vendido y alquilado ocultan el inmueble del portal.</small>
        </label>
        <label class="campo ancho"><span>Nombre del dueño</span><input v-model="ficha.duenoNombre" type="text" maxlength="150" /></label>
        <label class="campo"><span>Teléfono del dueño</span><input v-model="ficha.duenoTelefono" type="tel" maxlength="30" /></label>
        <label class="campo"><span>Correo del dueño</span><input v-model="ficha.duenoCorreo" type="email" maxlength="255" /></label>
        <label class="campo"><span>Habitaciones</span><input v-model.number="ficha.habitaciones" type="number" min="0" max="99" /></label>
        <label class="campo"><span>Baños</span><input v-model.number="ficha.banos" type="number" min="0" max="99" /></label>
        <label class="campo"><span>Metros cuadrados</span><input v-model.number="ficha.metrosCuadrados" type="number" min="0" step="0.01" /></label>
        <label class="campo"><span>Puestos de estacionamiento</span><input v-model.number="ficha.estacionamientos" type="number" min="0" max="99" /></label>
        <label class="campo ancho"><span>Piso (0 = planta baja)</span><input v-model.number="ficha.piso" type="number" min="-10" max="200" /></label>
      </form>
      <p v-if="errorFicha" class="error-form" role="alert">{{ errorFicha }}</p>
      <template #pie>
        <button type="button" class="btn-secundario" @click="mostrarFicha = false">Cancelar</button>
        <button type="submit" form="form-ficha" class="btn-primario" :disabled="guardando">Guardar</button>
      </template>
    </ModalCrm>

    <ModalTarea
      v-if="mostrarTarea && detalle"
      :propietarios="propietarios"
      :inmueble-inicial="{ id: p.id, codigo: p.codigo, titulo: p.titulo }"
      @cerrar="mostrarTarea = false"
      @guardada="alGuardarTarea"
    />
    <ModalNegocio
      v-if="mostrarNegocio && detalle"
      :etapas="etapas"
      :propietarios="propietarios"
      :inmueble-inicial="p"
      @cerrar="mostrarNegocio = false"
      @guardado="alCrearNegocio"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import GaleriaMosaico from '@/components/GaleriaMosaico.vue'
import MenuCrm from './MenuCrm.vue'
import ModalCrm from './contactos/ModalCrm.vue'
import ModalNegocio from './negocios/ModalNegocio.vue'
import ModalTarea from './tareas/ModalTarea.vue'
import { descargarArchivo, formatearTamano, PESO_MAXIMO_ARCHIVO, type Archivo } from '@/services/actividades'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import { obtenerEstatusLead, obtenerPropietarios } from '@/services/contactos'
import { claseEtapa, obtenerEtapas, type EtapaNegocio } from '@/services/negocios'
import {
  actualizarFicha, claseEstado, eliminarDocumento, ESTADOS_INMUEBLE, obtenerPropiedad, subirDocumentos, textoEstado,
  type FichaPropiedad, type PropiedadDetalle,
} from '@/services/propiedades'
import { formatearFecha } from '@/utils/fechas'
import { formatearPrecio, urlImagen } from '@/utils/formato'
import { claseEstatus } from './contactos/estatus'

const route = useRoute()
const router = useRouter()

const detalle = ref<PropiedadDetalle | null>(null)
const p = computed(() => detalle.value!.propiedad)
const etapas = ref<EtapaNegocio[]>([])
const propietarios = ref<Catalogo[]>([])
const estatusLead = ref<Catalogo[]>([])
const cargando = ref(false)
const guardando = ref(false)
const error = ref('')
const aviso = ref('')
const selector = ref<HTMLInputElement | null>(null)
const mostrarNegocio = ref(false)
const mostrarTarea = ref(false)
function alGuardarTarea() {
  mostrarTarea.value = false
  aviso.value = 'Tarea creada. Puedes verla en el módulo de Tareas.'
}

const volverA = computed(() => {
  const anterior = typeof window !== 'undefined' ? (window.history.state?.back as string | undefined) : undefined
  return anterior?.startsWith('/inmuebles') && !/^\/inmuebles\/\d/.test(anterior) ? anterior : '/inmuebles'
})
// Fotos con la URL completa (las subidas se sirven desde el backend)
const fotos = computed(() => detalle.value?.imagenes.map((f) => urlImagen(f.url)) ?? [])
const nombreEstatus = (id: number) => estatusLead.value.find((e) => e.id === id)?.nombre ?? `Estatus ${id}`

async function cargar() {
  cargando.value = true
  error.value = ''
  try {
    detalle.value = await obtenerPropiedad(String(route.params.id))
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
}

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

// ----- Ficha: estado, dueño y caracteristicas -----
const mostrarFicha = ref(false)
const errorFicha = ref('')
const ficha = reactive<FichaPropiedad>({
  estado: 'disponible', duenoNombre: '', duenoTelefono: '', duenoCorreo: '', habitaciones: null, banos: null, metrosCuadrados: null,
  estacionamientos: null, piso: null,
})
watch(mostrarFicha, (abierto) => {
  if (!abierto || !detalle.value) return
  const d = detalle.value.propiedad
  errorFicha.value = ''
  Object.assign(ficha, {
    estado: d.estado, duenoNombre: d.duenoNombre ?? '', duenoTelefono: d.duenoTelefono ?? '', duenoCorreo: d.duenoCorreo ?? '',
    habitaciones: d.habitaciones, banos: d.banos, metrosCuadrados: d.metrosCuadrados,
    estacionamientos: d.estacionamientos, piso: d.piso,
  })
})

// Los campos numericos vacios llegan como '' desde v-model.number: se envian como null
const numeroONulo = (valor: unknown) => (valor === '' || valor === null || valor === undefined ? null : Number(valor))

async function guardarFicha() {
  errorFicha.value = ''
  const cuerpo: FichaPropiedad = {
    ...ficha,
    habitaciones: numeroONulo(ficha.habitaciones),
    banos: numeroONulo(ficha.banos),
    metrosCuadrados: numeroONulo(ficha.metrosCuadrados),
    estacionamientos: numeroONulo(ficha.estacionamientos),
    piso: numeroONulo(ficha.piso),
  }
  guardando.value = true
  try {
    await actualizarFicha(p.value.id, cuerpo)
    mostrarFicha.value = false
    aviso.value = 'Ficha del inmueble actualizada.'
    await cargar()
  } catch (e) {
    errorFicha.value = mensajeDeError(e)
  } finally {
    guardando.value = false
  }
}

// ----- Documentos legales -----
async function subir(evento: Event) {
  const input = evento.target as HTMLInputElement
  const archivos = Array.from(input.files ?? [])
  input.value = ''
  if (!archivos.length) return
  const grandes = archivos.filter((a) => a.size > PESO_MAXIMO_ARCHIVO)
  if (grandes.length) {
    aviso.value = `${grandes.map((a) => a.name).join(', ')} pesa más de 10 MB.`
    return
  }
  await ejecutar(() => subirDocumentos(p.value.id, archivos), archivos.length === 1 ? 'Documento subido.' : 'Documentos subidos.')
}

function quitarDocumento(doc: Archivo) {
  if (!window.confirm(`¿Eliminar "${doc.nombre}"?`)) return
  ejecutar(() => eliminarDocumento(doc.id), 'Documento eliminado.')
}

async function descargar(doc: Archivo) {
  try {
    await descargarArchivo(doc)
  } catch (e) {
    aviso.value = mensajeDeError(e)
  }
}

function alCrearNegocio(id: number) {
  mostrarNegocio.value = false
  router.push(`/negocios/${id}`)
}

watch(() => route.params.id, () => {
  aviso.value = ''
  cargar()
})

onMounted(() => {
  cargar()
  obtenerEtapas().then((lista) => { etapas.value = lista }).catch(() => {})
  obtenerPropietarios().then((lista) => { propietarios.value = lista }).catch(() => {})
  obtenerEstatusLead().then((lista) => { estatusLead.value = lista }).catch(() => {})
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/estatus.css"></style>
<style scoped src="./contactos/detalle.css"></style>
<style scoped src="./contactos/registro.css"></style>
<style scoped src="./contactos/ficha.css"></style>
<style scoped src="./contactos/formulario.css"></style>
<style scoped src="./contactos/ficha-inmueble.css"></style>
