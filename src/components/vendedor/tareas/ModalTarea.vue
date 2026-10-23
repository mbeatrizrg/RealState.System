<template>
  <ModalCrm :titulo="tarea ? 'Editar tarea' : 'Crear tarea'" @cerrar="emit('cerrar')">
    <form :id="idFormulario" class="form-grid" novalidate @submit.prevent="guardar">
      <label class="campo ancho">
        <span>Título o asunto</span>
        <input ref="primero" v-model="datos.titulo" type="text" maxlength="200" required placeholder="Ej: Mostrar el apartamento de El Bosque" />
      </label>
      <label class="campo">
        <span>Tipo de tarea</span>
        <select v-model="datos.tipoTarea">
          <option v-for="t in TIPOS_TAREA" :key="t.valor" :value="t.valor">{{ t.texto }}</option>
        </select>
      </label>
      <label class="campo">
        <span>Prioridad</span>
        <select v-model="datos.prioridad">
          <option v-for="p in PRIORIDADES" :key="p.valor" :value="p.valor">{{ p.texto }}</option>
        </select>
      </label>
      <label class="campo">
        <span>Vence el</span>
        <input v-model="vence" type="datetime-local" required />
      </label>
      <label class="campo">
        <span>Estado</span>
        <select v-model="datos.estado">
          <option value="pendiente">Pendiente</option>
          <option value="en_proceso">En proceso</option>
          <option value="completada">Completada</option>
        </select>
      </label>

      <div class="campo ancho">
        <span>Negocio vinculado (opcional)</span>
        <SelectorBusqueda id="tarea-negocio" v-model="datos.negocioId" :buscar="buscarNegociosSelector" :texto-inicial="textoNegocio"
                          placeholder="Nombre del negocio, cliente o código" @elegido="alElegirNegocio" />
        <small class="ayuda-campo">Con un negocio, el contacto y el inmueble son los del negocio.</small>
      </div>
      <div class="campo">
        <span>Contacto asociado</span>
        <SelectorBusqueda id="tarea-contacto" v-model="datos.contactoId" :buscar="buscarClientes" :texto-inicial="textoContacto"
                          placeholder="Nombre, correo o teléfono" />
      </div>
      <div class="campo">
        <span>Inmueble</span>
        <SelectorBusqueda id="tarea-inmueble" v-model="datos.inmuebleId" :buscar="buscarInmuebles" :texto-inicial="textoInmueble"
                          placeholder="Código RRBR o título" />
      </div>

      <label class="campo ancho">
        <span>Propietario de la tarea (vendedor)</span>
        <select v-model="datos.propietarioId" :disabled="!esAdmin">
          <option v-for="p in opcionesPropietario" :key="p.id" :value="p.id">{{ p.nombre }}</option>
        </select>
        <small v-if="!esAdmin" class="ayuda-campo">Un vendedor solo puede crear tareas para sí mismo.</small>
      </label>
      <label class="campo ancho">
        <span>Descripción</span>
        <textarea v-model="datos.descripcion" rows="4" maxlength="5000" placeholder="Qué hay que hacer, qué llevar, qué documentos pedir..." />
      </label>
    </form>
    <p v-if="error" class="error-form" role="alert">{{ error }}</p>
    <template #pie>
      <button type="button" class="btn-secundario" @click="emit('cerrar')">Cancelar</button>
      <button type="submit" :form="idFormulario" class="btn-primario" :disabled="guardando">
        {{ guardando ? 'Guardando...' : tarea ? 'Guardar cambios' : 'Crear tarea' }}
      </button>
    </template>
  </ModalCrm>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, useId } from 'vue'

import ModalCrm from '../contactos/ModalCrm.vue'
import SelectorBusqueda, { type OpcionSelector } from '../contactos/SelectorBusqueda.vue'
import { mensajeDeError } from '@/services/api'
import type { Catalogo } from '@/services/catalogos'
import { buscarContactos } from '@/services/contactos'
import { buscarNegocios, type Negocio } from '@/services/negocios'
import { buscarPropiedades } from '@/services/propiedades'
import { esAdmin, sesion } from '@/services/sesion'
import { actualizarTarea, crearTarea, PRIORIDADES, TIPOS_TAREA, type Tarea, type TareaFormulario } from '@/services/tareas'

/* Crear o editar una tarea. Desde el registro del contacto, un negocio o un inmueble se abre con ese vinculo elegido */
const props = defineProps<{
  propietarios: Catalogo[]
  tarea?: Tarea | null
  contactoInicial?: { id: number; nombre: string } | null
  negocioInicial?: { id: number; nombre: string; contactoId: number; nombreContacto: string; inmuebleId: number; codigoInmueble: string; tituloInmueble: string } | null
  inmuebleInicial?: { id: number; codigo: string; titulo: string } | null
}>()
const emit = defineEmits<{ cerrar: []; guardada: [tarea: Tarea] }>()

const idFormulario = useId()
const t = props.tarea
const neg = props.negocioInicial

// "2026-10-22T15:30" en la hora local, para <input type="datetime-local">
function fechaLocal(fecha: Date) {
  fecha.setSeconds(0, 0)
  return new Date(fecha.getTime() - fecha.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
}
const mananaMismaHora = () => fechaLocal(new Date(Date.now() + 24 * 3600 * 1000))

const datos = reactive<TareaFormulario>({
  titulo: t?.titulo ?? '',
  descripcion: t?.descripcion ?? '',
  tipoTarea: t?.tipoTarea ?? 'LLAMADA',
  prioridad: t?.prioridad ?? 'media',
  estado: t?.estadoGuardado ?? 'pendiente',
  fechaVencimiento: '',
  contactoId: t?.contactoId ?? neg?.contactoId ?? props.contactoInicial?.id ?? null,
  negocioId: t?.negocioId ?? neg?.id ?? null,
  inmuebleId: t?.inmuebleId ?? neg?.inmuebleId ?? props.inmuebleInicial?.id ?? null,
  propietarioId: t?.propietarioId ?? sesion.id,
})
const vence = ref(t?.fechaVencimiento ? t.fechaVencimiento.slice(0, 16) : mananaMismaHora())

const textoNegocio = ref(t?.nombreNegocio ?? neg?.nombre ?? '')
const textoContacto = ref(t?.nombreContacto ?? neg?.nombreContacto ?? props.contactoInicial?.nombre ?? '')
const textoInmueble = ref(
  t?.codigoInmueble ? `${t.codigoInmueble} · ${t.tituloInmueble}`
    : neg ? `${neg.codigoInmueble} · ${neg.tituloInmueble}`
      : props.inmuebleInicial ? `${props.inmuebleInicial.codigo} · ${props.inmuebleInicial.titulo}` : '')

// Administrador: cualquier empleado activo. Vendedor: solo el mismo (o el propietario actual si edita)
const opcionesPropietario = computed(() => {
  const activos = props.propietarios.filter((p) => !p.nombre.endsWith('(inactivo)'))
  const lista = esAdmin.value ? activos : activos.filter((p) => p.id === sesion.id || p.id === t?.propietarioId)
  return lista.length ? lista : [{ id: sesion.id ?? 0, nombre: sesion.nombre ?? 'Yo' }]
})

const buscarNegociosSelector = async (q: string): Promise<OpcionSelector[]> =>
  (await buscarNegocios({ q, tamano: 10, abiertos: true, orden: 'nombre', direccion: 'asc' })).contenido.map((n) => ({
    id: n.id, texto: n.nombre, detalle: `${n.nombreContacto} · ${n.codigoInmueble} · ${n.etapa}`, dato: n,
  }))

const buscarClientes = async (q: string): Promise<OpcionSelector[]> =>
  (await buscarContactos({ q, tamano: 10, orden: 'nombre', direccion: 'asc' })).contenido.map((c) => ({
    id: c.id, texto: `${c.nombre} ${c.apellido}`, detalle: `${c.correo} · ${c.telefono}`,
  }))

const buscarInmuebles = async (q: string): Promise<OpcionSelector[]> =>
  (await buscarPropiedades({ q, tamano: 10, orden: 'titulo', direccion: 'asc' })).contenido.map((p) => ({
    id: p.id, texto: `${p.codigo} · ${p.titulo}`, detalle: `${p.tipoInmueble} · ${p.ubicacion}`,
  }))

// El backend toma el contacto y el inmueble del negocio: se muestran aqui para que no haya sorpresas
function alElegirNegocio(opcion: OpcionSelector) {
  const n = opcion.dato as Negocio
  datos.contactoId = n.contactoId
  datos.inmuebleId = n.inmuebleId
  textoContacto.value = n.nombreContacto
  textoInmueble.value = `${n.codigoInmueble} · ${n.tituloInmueble}`
}

const primero = ref<HTMLInputElement | null>(null)
onMounted(() => primero.value?.focus())

const guardando = ref(false)
const error = ref('')

function validar() {
  if (!datos.titulo.trim()) return 'Escriba el título de la tarea.'
  if (!vence.value) return 'Indique la fecha de vencimiento.'
  if (!datos.contactoId && !datos.inmuebleId && !datos.negocioId) return 'Vincule la tarea a un contacto, un inmueble o un negocio.'
  return ''
}

async function guardar() {
  error.value = validar()
  if (error.value) return
  guardando.value = true
  const cuerpo: TareaFormulario = { ...datos, titulo: datos.titulo.trim(), fechaVencimiento: `${vence.value}:00` }
  try {
    emit('guardada', t ? await actualizarTarea(t.id, cuerpo) : await crearTarea(cuerpo))
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    guardando.value = false
  }
}
</script>

<style scoped src="../contactos/formulario.css"></style>
