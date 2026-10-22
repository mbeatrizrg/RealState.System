<template>
  <div class="leads-page">
    <!-- Barra lateral (Sidebar) -->
    <aside class="sidebar">
      <div class="logo2-container">
        <img src="/Logo2.png" alt="Icono2" class="logo" />
      </div>

      <nav class="menu">
        <ul>
          <li class="menu-item">
            <button class="btn-menu" @click="vendedor">Inventario</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="contactos">Contactos</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Leads</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Comunicaciones</button>
          </li>
          <li class="menu-item active">
            <button class="btn-menu" @click="control">Control</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Calendario</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="profile">Perfil</button>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- Contenido Principal -->
    <main class="main-content">
      <header class="header">
        <h1 class="greeting">Hola, {{ sesion.nombre }}</h1>
        <button class="btn-filtros">
          Filtros
          <svg class="icon-chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </header>

      <!-- Barra de búsqueda -->
      <section class="search-section">
        <div class="search-input-wrapper">
          <svg class="icon-search" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            type="text"
            placeholder="Buscar usuarios"
            class="search-input">
        </div>
      </section>

    <section>
      <div class="form-container">
        <!-- Modo "gestionar fotos" de un inmueble existente (/control?inmueble=ID) -->
        <template v-if="inmuebleEditado">
          <div class="editar-fotos-cabecera">
            <div>
              <p class="editar-fotos-subtitulo">Gestionar fotos</p>
              <h2 class="editar-fotos-titulo">{{ inmuebleEditado.titulo }}</h2>
              <p class="editar-fotos-subtitulo">Código {{ inmuebleEditado.rrbr }}</p>
            </div>
            <button type="button" class="btn-secundario" @click="salirDeFotos">Volver al formulario</button>
          </div>
          <div class="form-section editar-fotos">
            <GaleriaFotos
              v-model="fotosPendientes"
              :existentes="inmuebleEditado.imagenes"
              :deshabilitado="guardando"
              @eliminar="quitarFotoGuardada"
            />
          </div>
        </template>

        <template v-else>
      <!-- Switch de opciones -->
        <div class="switch">
          <button
            :class="['btn-action', { active: activeSection === 'usuario' }]"
            @click="activeSection = 'usuario'">
            Añadir Cliente
          </button>
          <button
            :class="['btn-action', { active: activeSection === 'inmueble' }]"
            @click="activeSection = 'inmueble'">
            Añadir Inmueble
          </button>
        </div>

          <!-- Formulario Usuario -->
          <div v-if="activeSection === 'usuario'" class="form-section">
            <div class="group">
              <label>Nombre</label>
              <input type="text" placeholder="Rifat"
              />
            </div>

            <div class="group">
              <label>Apellido</label>
              <input type="text" placeholder="Richani"
              />
            </div>

            <div class="group">
              <label>Nro de Teléfono</label>
              <input type="text" placeholder="ej: 414-496577"
              />
            </div>

            <div class="group">
              <label>Inmueble deseado</label>
              <input type="text" placeholder="ID: ET5652"
              />
            </div>

            <div class="group full-width">
              <label>Gmail</label>
              <input type="text" placeholder="ej: ejemplo@gmail.com"
              />
            </div>
          </div>

        <!-- Formulario Inmueble: columna izquierda (fotos y usuario vinculado) + grilla de campos en 2 columnas -->
        <form v-if="activeSection === 'inmueble'" id="form-inmueble" class="form-section inmueble-container" @submit.prevent="guardarInmueble">
          <div class="inmueble-col-left">
            <GaleriaFotos v-model="fotosPendientes" :deshabilitado="guardando" />

            <div class="group">
              <label for="usuario-vinculado">Usuario vinculado</label>
              <div class="select-wrapper">
                <select id="usuario-vinculado" v-model="form.usuarioId">
                  <option value="">Ninguno</option>
                  <option v-for="cliente in clientes" :key="cliente.id" :value="String(cliente.id)">
                    {{ cliente.nombre }} {{ cliente.apellido }}
                  </option>
                </select>
                <span class="chevron">&#x203A;</span>
              </div>
            </div>
          </div>

          <div class="inmueble-fields-grid">
            <div class="group full-width">
              <label for="titulo">Título</label>
              <input id="titulo" v-model="form.titulo" type="text" maxlength="500" required placeholder="ej: Residencias Altos Del Mirador" />
            </div>

            <div class="group">
              <label for="modalidad">Tipo</label>
              <div class="select-wrapper">
                <select id="modalidad" v-model="form.modalidad" required>
                  <option value="venta">Venta</option>
                  <option value="alquiler">Alquiler</option>
                </select>
                <span class="chevron">&#x203A;</span>
              </div>
            </div>

            <div class="group">
              <label for="tipo-inmueble">Inmueble</label>
              <div class="select-wrapper">
                <select id="tipo-inmueble" v-model="form.tipoInmuebleId" required>
                  <option value="" disabled>Seleccione</option>
                  <option v-for="tipo in tiposInmueble" :key="tipo.id" :value="String(tipo.id)">{{ tipo.nombre }}</option>
                </select>
                <span class="chevron">&#x203A;</span>
              </div>
            </div>

            <div class="group">
              <label for="rrbr">Codigo RRBR</label>
              <input id="rrbr" v-model="form.rrbr" type="text" maxlength="255" placeholder="Opcional: se genera solo" />
            </div>

            <div class="group">
              <label for="precio">Precio</label>
              <input id="precio" v-model="form.precio" type="number" min="1" step="1" required placeholder="ej: 50000" />
            </div>

            <div class="group">
              <label for="direccion">Direccion</label>
              <input id="direccion" v-model="form.ubicacion" type="text" maxlength="500" required placeholder="ej: Av. Bolívar Norte" />
            </div>

            <div class="group">
              <label for="extra">Extra</label>
              <div class="select-wrapper">
                <select id="extra" v-model="form.extraId" required>
                  <option value="" disabled>Seleccione</option>
                  <option v-for="extra in extras" :key="extra.id" :value="String(extra.id)">{{ extra.nombre }}</option>
                </select>
                <span class="chevron">&#x203A;</span>
              </div>
            </div>

            <div class="group">
              <label for="ciudad">Ciudad</label>
              <div class="select-wrapper">
                <select id="ciudad" v-model="form.ciudadId" required>
                  <option value="" disabled>Seleccione</option>
                  <option v-for="ciudad in ciudades" :key="ciudad.id" :value="String(ciudad.id)">{{ ciudad.nombre }}</option>
                </select>
                <span class="chevron">&#x203A;</span>
              </div>
            </div>

            <div class="group full-width">
              <label for="descripcion">Descripción</label>
              <textarea id="descripcion" v-model="form.descripcion" rows="4" required placeholder="Describe el inmueble"></textarea>
            </div>
          </div>
        </form>
        </template>

        <p v-if="errorCatalogos" class="aviso error">No se pudieron cargar las listas: {{ errorCatalogos }}</p>
        <p v-if="mensaje" :class="['aviso', mensaje.tipo]">{{ mensaje.texto }}</p>
      </div>
    </section>
        <button
          v-if="inmuebleEditado"
          type="button"
          class="btn-añadir"
          :disabled="guardando || fotosPendientes.length === 0"
          @click="subirFotosPendientes">
          {{ guardando ? 'Subiendo...' : 'Guardar fotos' }}
        </button>
        <button
          v-else-if="activeSection === 'inmueble'"
          type="submit"
          form="form-inmueble"
          class="btn-añadir"
          :disabled="guardando">
          {{ guardando ? 'Guardando...' : 'Añadir' }}
          <svg class="icon-chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>

    </main>
  </div>
</template>

<script setup>

import { useRoute, useRouter } from 'vue-router'
import { onMounted, reactive, ref, watch } from 'vue'

import GaleriaFotos from './GaleriaFotos.vue'
import { mensajeDeError } from '@/services/api'
import { obtenerCiudades, obtenerExtras, obtenerTiposInmueble } from '@/services/catalogos'
import { eliminarImagen, obtenerInmueble, registrarInmueble, subirImagenes } from '@/services/inmuebles'
import { sesion } from '@/services/sesion'
import { obtenerClientesCrm, vincularCliente } from '@/services/usuarios'

const router = useRouter()
const route = useRoute()

const Leads = ()=> {
  router.push('/leads')
}

const vendedor = ()=> {
  router.push('/vendedor')
}

const contactos = ()=> {
  router.push('/contact')
}

const control = ()=> {
  router.push('/control')
}

const profile = ()=> {
  router.push('/profile')
}

const activeSection = ref('usuario')

// ----- Catalogos de los selects -----
const ciudades = ref([])
const tiposInmueble = ref([])
const extras = ref([])
const clientes = ref([])
const errorCatalogos = ref('')

onMounted(async () => {
  try {
    const [listaCiudades, listaTipos, listaExtras] = await Promise.all([obtenerCiudades(), obtenerTiposInmueble(), obtenerExtras()])
    ciudades.value = listaCiudades
    tiposInmueble.value = listaTipos
    extras.value = listaExtras
  } catch (e) {
    errorCatalogos.value = mensajeDeError(e)
  }
  // Si falla la lista de clientes el inmueble se puede crear igual, sin usuario vinculado
  obtenerClientesCrm().then((lista) => { clientes.value = lista }).catch(() => {})
})

// ----- Formulario "Añadir Inmueble" -----
const formularioVacio = () => ({
  titulo: '',
  modalidad: 'venta',
  tipoInmuebleId: '',
  rrbr: '',
  precio: '',
  ubicacion: '',
  extraId: '',
  ciudadId: '',
  descripcion: '',
  usuarioId: '',
})
const form = reactive(formularioVacio())
// Fotos elegidas que se suben al guardar (maximo 6 por inmueble, ver GaleriaFotos)
const fotosPendientes = ref([])
const guardando = ref(false)
// { tipo: 'exito' | 'error', texto }
const mensaje = ref(null)

/* 1) crea el inmueble, 2) sube las fotos, 3) crea el lead del usuario vinculado.
   Si el inmueble se crea pero las fotos fallan, se abre el modo "gestionar fotos" para reintentar sin perderlas */
async function guardarInmueble() {
  guardando.value = true
  mensaje.value = null
  let id
  try {
    const respuesta = await registrarInmueble({
      titulo: form.titulo.trim(),
      precio: Number(form.precio),
      descripcion: form.descripcion.trim(),
      rrbr: form.rrbr.trim() || undefined,
      ubicacion: form.ubicacion.trim(),
      isAlquiler: form.modalidad === 'alquiler',
      ciudadId: Number(form.ciudadId),
      tipoInmuebleId: Number(form.tipoInmuebleId),
      extraId: Number(form.extraId),
    })
    id = respuesta.id
  } catch (e) {
    mensaje.value = { tipo: 'error', texto: mensajeDeError(e) }
    guardando.value = false
    return
  }

  const avisos = []
  if (form.usuarioId) {
    try {
      await vincularCliente(Number(form.usuarioId), id)
    } catch (e) {
      avisos.push(`no se pudo vincular el usuario (${mensajeDeError(e)})`)
    }
  }
  if (fotosPendientes.value.length) {
    try {
      await subirImagenes(id, fotosPendientes.value)
      fotosPendientes.value = []
    } catch (e) {
      Object.assign(form, formularioVacio())
      guardando.value = false
      await router.replace({ path: '/control', query: { inmueble: String(id) } })
      mensaje.value = { tipo: 'error', texto: `El inmueble se creó, pero las fotos no se subieron: ${mensajeDeError(e)}. Puede intentarlo de nuevo aquí.` }
      return
    }
  }

  Object.assign(form, formularioVacio())
  guardando.value = false
  mensaje.value = avisos.length
    ? { tipo: 'error', texto: `Inmueble registrado, pero ${avisos.join(' y ')}.` }
    : { tipo: 'exito', texto: 'Inmueble registrado exitosamente.' }
}

// ----- Modo "gestionar fotos" (/control?inmueble=ID) -----
const inmuebleEditado = ref(null)

watch(() => route.query.inmueble, async (id) => {
  inmuebleEditado.value = null
  if (!id) return
  activeSection.value = 'inmueble'
  try {
    inmuebleEditado.value = await obtenerInmueble(String(id))
  } catch (e) {
    mensaje.value = { tipo: 'error', texto: mensajeDeError(e) }
  }
}, { immediate: true })

async function recargarInmueble() {
  inmuebleEditado.value = await obtenerInmueble(inmuebleEditado.value.id)
}

async function subirFotosPendientes() {
  guardando.value = true
  mensaje.value = null
  try {
    await subirImagenes(inmuebleEditado.value.id, fotosPendientes.value)
    fotosPendientes.value = []
    await recargarInmueble()
    mensaje.value = { tipo: 'exito', texto: 'Fotos guardadas.' }
  } catch (e) {
    mensaje.value = { tipo: 'error', texto: mensajeDeError(e) }
  } finally {
    guardando.value = false
  }
}

async function quitarFotoGuardada(imagen) {
  guardando.value = true
  mensaje.value = null
  try {
    await eliminarImagen(imagen.id)
    await recargarInmueble()
  } catch (e) {
    mensaje.value = { tipo: 'error', texto: mensajeDeError(e) }
  } finally {
    guardando.value = false
  }
}

function salirDeFotos() {
  fotosPendientes.value = []
  mensaje.value = null
  router.push('/control')
}

</script>

<style scoped src= "./control.css"></style>
