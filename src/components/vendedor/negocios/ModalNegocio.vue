<template>
  <ModalCrm :titulo="negocio ? 'Editar negocio' : 'Crear negocio'" @cerrar="emit('cerrar')">
    <form :id="idFormulario" class="form-grid" novalidate @submit.prevent="guardar">
      <div class="campo ancho">
        <span>Inmueble</span>
        <SelectorBusqueda
          id="negocio-inmueble"
          v-model="datos.inmuebleId"
          :buscar="buscarInmuebles"
          :texto-inicial="textoInmueble"
          placeholder="Código RRBR, título o ubicación"
          requerido
          @elegido="alElegirInmueble"
        />
      </div>
      <div class="campo ancho">
        <span>Contacto (comprador o inquilino)</span>
        <SelectorBusqueda
          id="negocio-contacto"
          v-model="datos.contactoId"
          :buscar="buscarClientes"
          :texto-inicial="textoContacto"
          placeholder="Nombre, correo o teléfono"
          requerido
        />
      </div>
      <label class="campo ancho">
        <span>Nombre del negocio</span>
        <input v-model="datos.nombre" type="text" maxlength="150" required placeholder="Ej: Venta Apto. Urb. El Bosque" />
      </label>
      <label class="campo">
        <span>Tipo de operación</span>
        <select v-model="datos.tipoOperacion">
          <option value="venta">Venta</option>
          <option value="alquiler">Alquiler</option>
        </select>
      </label>
      <label class="campo">
        <span>Valor (USD)</span>
        <input v-model.number="datos.valor" type="number" min="0" step="0.01" required />
      </label>
      <label class="campo">
        <span>Etapa</span>
        <select v-model.number="datos.etapaId" required>
          <option v-for="e in etapas" :key="e.id" :value="e.id">{{ e.nombre }}</option>
        </select>
      </label>
      <label class="campo">
        <span>Fecha estimada de cierre</span>
        <input v-model="fechaCierre" type="date" />
      </label>
      <label class="campo ancho">
        <span>Propietario del negocio (vendedor)</span>
        <select v-model="datos.propietarioId">
          <option :value="null">Sin asignar</option>
          <option v-for="p in opcionesPropietario" :key="p.id" :value="p.id">{{ p.nombre }}</option>
        </select>
        <small v-if="!esAdmin" class="ayuda-campo">Un vendedor solo puede asignarse el negocio a sí mismo.</small>
      </label>
    </form>
    <p v-if="error" class="error-form" role="alert">{{ error }}</p>
    <template #pie>
      <button type="button" class="btn-secundario" @click="emit('cerrar')">Cancelar</button>
      <button type="submit" :form="idFormulario" class="btn-primario" :disabled="guardando">
        {{ guardando ? 'Guardando...' : negocio ? 'Guardar cambios' : 'Crear negocio' }}
      </button>
    </template>
  </ModalCrm>
</template>

<script setup lang="ts">
import { computed, reactive, ref, useId } from 'vue'

import ModalCrm from '../contactos/ModalCrm.vue'
import SelectorBusqueda, { type OpcionSelector } from '../contactos/SelectorBusqueda.vue'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import { buscarContactos } from '@/services/contactos'
import {
  actualizarNegocio, crearNegocio, type EtapaNegocio, type Negocio, type NegocioFormulario,
} from '@/services/negocios'
import { buscarPropiedades, type Propiedad } from '@/services/propiedades'
import { esAdmin, sesion } from '@/services/sesion'

/* Crear o editar un negocio. Desde el registro de un contacto o la ficha de un inmueble se abre con ese dato ya elegido
   (contactoInicial / inmuebleInicial) */
const props = defineProps<{
  etapas: EtapaNegocio[]
  propietarios: Catalogo[]
  negocio?: Negocio | null
  contactoInicial?: { id: number; nombre: string } | null
  inmuebleInicial?: Propiedad | null
}>()
const emit = defineEmits<{ cerrar: []; guardado: [id: number] }>()

const idFormulario = useId()
const n = props.negocio
const inmueble = props.inmuebleInicial

const datos = reactive<NegocioFormulario>({
  nombre: n?.nombre ?? (inmueble ? nombreSugerido(inmueble) : ''),
  inmuebleId: n?.inmuebleId ?? inmueble?.id ?? null,
  contactoId: n?.contactoId ?? props.contactoInicial?.id ?? null,
  tipoOperacion: n?.tipoOperacion ?? inmueble?.tipoOperacion ?? 'venta',
  valor: n?.valor ?? inmueble?.precio ?? null,
  etapaId: n?.etapaId ?? props.etapas[0]?.id ?? null,
  fechaCierreEstimada: n?.fechaCierreEstimada ?? null,
  // Por defecto el negocio queda a cargo de quien lo crea
  propietarioId: n ? n.propietarioId : sesion.id,
})
const fechaCierre = computed({
  get: () => datos.fechaCierreEstimada ?? '',
  set: (valor: string) => { datos.fechaCierreEstimada = valor || null },
})

const textoInmueble = n ? `${n.codigoInmueble} · ${n.tituloInmueble}` : inmueble ? `${inmueble.codigo} · ${inmueble.titulo}` : ''
const textoContacto = n ? n.nombreContacto : props.contactoInicial?.nombre ?? ''

// Administrador: cualquier empleado activo. Vendedor: solo el mismo (o el propietario actual si edita)
const opcionesPropietario = computed(() => {
  const activos = props.propietarios.filter((p) => !p.nombre.endsWith('(inactivo)'))
  if (esAdmin.value) return activos
  return activos.filter((p) => p.id === sesion.id || p.id === n?.propietarioId)
})

function nombreSugerido(p: Propiedad) {
  return `${p.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Venta'} ${p.titulo}`.slice(0, 150)
}

const buscarInmuebles = async (q: string): Promise<OpcionSelector[]> =>
  (await buscarPropiedades({ q, tamano: 10, orden: 'titulo', direccion: 'asc' })).contenido.map((p) => ({
    id: p.id,
    texto: `${p.codigo} · ${p.titulo}`,
    detalle: `${p.tipoInmueble} · ${p.tipoOperacion === 'alquiler' ? 'Alquiler' : 'Venta'} · ${p.ubicacion}`,
    dato: p,
  }))

const buscarClientes = async (q: string): Promise<OpcionSelector[]> =>
  (await buscarContactos({ q, tamano: 10, orden: 'nombre', direccion: 'asc' })).contenido.map((c) => ({
    id: c.id,
    texto: `${c.nombre} ${c.apellido}`,
    detalle: `${c.correo} · ${c.telefono}`,
  }))

// Al elegir el inmueble se proponen la operacion, el valor y el nombre (si aun no se escribieron)
function alElegirInmueble(opcion: OpcionSelector) {
  const p = opcion.dato as Propiedad
  datos.tipoOperacion = p.tipoOperacion
  if (datos.valor === null || !n) datos.valor = p.precio
  if (!datos.nombre.trim() || !n) datos.nombre = nombreSugerido(p)
}

const guardando = ref(false)
const error = ref('')

function validar() {
  if (!datos.inmuebleId) return 'Seleccione el inmueble de la lista.'
  if (!datos.contactoId) return 'Seleccione el contacto de la lista.'
  if (!datos.nombre.trim()) return 'Escriba el nombre del negocio.'
  if (datos.valor === null || Number.isNaN(datos.valor) || datos.valor < 0) return 'Indique un valor válido.'
  if (!datos.etapaId) return 'Seleccione la etapa.'
  return ''
}

async function guardar() {
  error.value = validar()
  if (error.value) return
  guardando.value = true
  try {
    const cuerpo = { ...datos, nombre: datos.nombre.trim() }
    if (n) {
      await actualizarNegocio(n.id, cuerpo)
      emit('guardado', n.id)
    } else {
      emit('guardado', (await crearNegocio(cuerpo)).id)
    }
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    guardando.value = false
  }
}
</script>

<style scoped src="../contactos/formulario.css"></style>
