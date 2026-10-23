<template>
  <Teleport to="body">
    <div class="panel-fondo" @mousedown.self="emit('cerrar')" @keydown.esc="emit('cerrar')">
      <aside ref="caja" class="panel" role="dialog" aria-modal="true" :aria-labelledby="idTitulo" tabindex="-1">
        <header class="panel-cabecera">
          <div>
            <p class="tipo">{{ textoTipoTarea(tarea.tipoTarea) }}</p>
            <h2 :id="idTitulo">{{ tarea.titulo }}</h2>
          </div>
          <button type="button" class="cerrar" aria-label="Cerrar" @click="emit('cerrar')">&times;</button>
        </header>

        <div class="panel-cuerpo">
          <div class="etiquetas">
            <span :class="['badge', claseEstadoTarea(tarea.estado)]">{{ textoEstadoTarea(tarea.estado) }}</span>
            <span :class="['badge', clasePrioridad(tarea.prioridad)]">Prioridad {{ textoPrioridad(tarea.prioridad).toLowerCase() }}</span>
          </div>

          <dl class="datos">
            <dt>Vence</dt><dd>{{ formatearFechaHora(tarea.fechaVencimiento) }}</dd>
            <dt>Propietario</dt><dd>{{ tarea.nombrePropietario ?? 'Sin asignar' }}</dd>
            <dt>Contacto</dt>
            <dd>
              <router-link v-if="tarea.contactoId" :to="`/contact/${tarea.contactoId}`">{{ tarea.nombreContacto }}</router-link>
              <template v-else>—</template>
            </dd>
            <dt>Negocio</dt>
            <dd>
              <router-link v-if="tarea.negocioId" :to="`/negocios/${tarea.negocioId}`">{{ tarea.nombreNegocio }}</router-link>
              <template v-else>—</template>
            </dd>
            <dt>Inmueble</dt>
            <dd>
              <router-link v-if="tarea.inmuebleId" :to="`/inmuebles/${tarea.inmuebleId}`">{{ tarea.codigoInmueble }} · {{ tarea.tituloInmueble }}</router-link>
              <template v-else>—</template>
            </dd>
            <dt>Creada</dt><dd>{{ formatearFechaHora(tarea.fechaCreacion) }}</dd>
          </dl>

          <section>
            <h3>Descripción</h3>
            <p class="descripcion">{{ tarea.descripcion || 'Sin descripción.' }}</p>
          </section>

          <template v-if="tarea.puedeEditar">
            <section v-if="tarea.estado !== 'completada'" class="completar">
              <h3>Completar tarea</h3>
              <label class="campo">
                <span>Resultado de la gestión (se guarda como nota{{ tarea.contactoId ? ' en el registro del contacto' : '' }})</span>
                <textarea v-model="resultado" rows="4" maxlength="5000" placeholder="Ej: El cliente confirmó la visita para el sábado" />
              </label>
              <button type="button" class="btn-primario" :disabled="ocupado" @click="completar">Marcar como completada</button>
            </section>

            <section class="estado">
              <h3>Cambiar estado</h3>
              <div class="botones">
                <button v-for="e in ESTADOS_EDITABLES" :key="e.valor" type="button" class="btn-secundario"
                        :disabled="ocupado || tarea.estadoGuardado === e.valor" @click="cambiar(e.valor)">
                  {{ e.texto }}
                </button>
              </div>
            </section>
          </template>
          <p v-else class="ayuda">Solo el propietario de la tarea, el propietario del contacto o un administrador pueden modificarla.</p>
          <p v-if="error" class="error" role="alert">{{ error }}</p>
        </div>

        <footer v-if="tarea.puedeEditar" class="panel-pie">
          <button type="button" class="btn-secundario peligro" :disabled="ocupado" @click="eliminar">Eliminar</button>
          <button type="button" class="btn-primario" @click="emit('editar', tarea)">Editar</button>
        </footer>
      </aside>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { onMounted, ref, useId } from 'vue'

import { mensajeDeError } from '@/services/api'
import {
  cambiarEstadoTarea, claseEstadoTarea, clasePrioridad, completarTarea, eliminarTarea, textoEstadoTarea, textoPrioridad,
  textoTipoTarea, type EstadoGuardado, type Tarea,
} from '@/services/tareas'
import { formatearFechaHora } from '@/utils/fechas'

// Detalle de una tarea: descripcion completa, vinculos, cambio de estado y "completar" con una nota del resultado
const props = defineProps<{ tarea: Tarea }>()
const emit = defineEmits<{ cerrar: []; cambio: [tarea: Tarea | null]; editar: [tarea: Tarea] }>()

const ESTADOS_EDITABLES: { valor: EstadoGuardado; texto: string }[] = [
  { valor: 'pendiente', texto: 'Pendiente' },
  { valor: 'en_proceso', texto: 'En proceso' },
  { valor: 'completada', texto: 'Completada' },
]

const idTitulo = useId()
const caja = ref<HTMLElement | null>(null)
onMounted(() => caja.value?.focus())

const resultado = ref('')
const ocupado = ref(false)
const error = ref('')

async function ejecutar(accion: () => Promise<Tarea | null>) {
  ocupado.value = true
  error.value = ''
  try {
    emit('cambio', await accion())
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    ocupado.value = false
  }
}

const completar = () => ejecutar(() => completarTarea(props.tarea.id, resultado.value.trim()))
const cambiar = (estado: EstadoGuardado) => ejecutar(() => cambiarEstadoTarea(props.tarea.id, estado))

function eliminar() {
  if (!window.confirm(`¿Eliminar la tarea "${props.tarea.titulo}"?`)) return
  ejecutar(async () => {
    await eliminarTarea(props.tarea.id)
    return null
  })
}
</script>

<style scoped src="../contactos/estatus.css"></style>
<style scoped src="../contactos/formulario.css"></style>
<style scoped>
.panel-fondo {
  position: fixed;
  inset: 0;
  z-index: 100;
  background-color: rgba(11, 31, 70, 0.35);
  display: flex;
  justify-content: flex-end;
}

.panel {
  width: min(440px, 100%);
  height: 100%;
  background-color: #ffffff;
  display: flex;
  flex-direction: column;
  box-shadow: -12px 0 32px rgba(11, 31, 70, 0.2);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  outline: none;
}

.panel-cabecera {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding: 24px 24px 8px;
}

.panel-cabecera h2 {
  margin: 0;
  font-family: 'Playfair Display', 'Merriweather', serif;
  font-size: 22px;
  color: #0b1f46;
  overflow-wrap: anywhere;
}

.tipo {
  margin: 0 0 4px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #636363;
}

.cerrar {
  background: none;
  border: none;
  font-size: 28px;
  color: #636363;
  cursor: pointer;
}

.panel-cuerpo {
  flex: 1;
  overflow-y: auto;
  padding: 8px 24px 16px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.etiquetas {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.datos {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 8px 16px;
  margin: 0;
  font-size: 14px;
}

.datos dt {
  color: #636363;
}

.datos dd {
  margin: 0;
  color: #1c1c1c;
  overflow-wrap: anywhere;
}

.datos a {
  color: #004cb5;
  text-decoration: none;
}

h3 {
  margin: 0 0 8px;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #636363;
}

.descripcion {
  margin: 0;
  white-space: pre-wrap;
  font-size: 14px;
  color: #1c1c1c;
}

.completar {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.botones {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.ayuda {
  margin: 0;
  color: #636363;
  font-size: 13px;
}

.error {
  margin: 0;
  color: #991b1b;
  font-size: 13px;
}

.panel-pie {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 16px 24px 24px;
  border-top: 1px solid #e3e8f4;
}

.btn-primario {
  align-self: flex-start;
  background-color: #0b1f46;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
}

.btn-secundario {
  background: transparent;
  color: #0b1f46;
  border: 1px solid #0b1f46;
  border-radius: 8px;
  padding: 8px 14px;
  font-size: 13px;
  cursor: pointer;
}

.btn-secundario.peligro {
  color: #991b1b;
  border-color: #991b1b;
}

.btn-primario:disabled,
.btn-secundario:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
