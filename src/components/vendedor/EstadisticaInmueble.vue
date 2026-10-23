<template>
  <div class="crm-contactos">
    <MenuCrm activo="Estadísticas" />

    <main class="main-content registro">
      <router-link to="/estadisticas" class="volver">&larr; Volver a estadísticas</router-link>

      <p v-if="cargando && !datos" class="estado-msg">Cargando interacciones...</p>
      <p v-else-if="error" class="estado-msg error" role="alert">{{ error }}</p>

      <template v-else-if="datos">
        <header class="ficha-cabecera">
          <div class="ficha-titulo">
            <h1 class="greeting">{{ datos.codigo }} · {{ datos.titulo }}</h1>
            <div class="ficha-meta">
              <span>{{ datos.tipo }}</span>
              <span>{{ datos.zona }}</span>
              <span>Vendedor: <strong>{{ datos.vendedor ?? '—' }}</strong></span>
            </div>
          </div>
          <div class="ficha-acciones">
            <router-link :to="`/inmuebles/${datos.id}`" class="btn-secundario">Ver ficha del inmueble</router-link>
          </div>
        </header>

        <div class="filtros-nivel" role="group" aria-label="Filtrar por nivel de interacción">
          <button type="button" :class="['chip', { activo: !nivel }]" @click="cambiarNivel(null)">Todos <span>{{ totalContactos }}</span></button>
          <button v-for="n in NIVELES" :key="n.valor" type="button" :class="['chip', { activo: nivel === n.valor }]" @click="cambiarNivel(n.valor)">
            <span class="punto" :style="{ backgroundColor: COLOR_NIVEL[n.valor] }" aria-hidden="true" />
            {{ n.texto }}
          </button>
        </div>

        <section class="tarjeta">
          <h2>Contactos que interactuaron <span class="contador">{{ datos.contactos.length }}</span></h2>
          <div v-if="datos.contactos.length" class="tabla-scroll">
            <table class="tabla-simple">
              <thead>
                <tr><th>Contacto</th><th>Nivel alcanzado</th><th>Consultas</th><th>Visitas</th><th>Negocio</th><th>Última interacción</th><th></th></tr>
              </thead>
              <tbody>
                <template v-for="c in datos.contactos" :key="c.contactoId">
                  <tr>
                    <td>
                      <router-link :to="`/contact/${c.contactoId}`">{{ c.nombre }}</router-link>
                      <small class="subtexto">{{ c.correo }} · {{ c.telefono }}</small>
                    </td>
                    <td>
                      <span class="nivel">
                        <span class="punto" :style="{ backgroundColor: COLOR_NIVEL[c.nivel] }" aria-hidden="true" />
                        {{ c.etiquetaNivel }}
                      </span>
                    </td>
                    <td>{{ c.consultas }}</td>
                    <td>{{ c.visitas }}</td>
                    <td>
                      <router-link v-if="c.negocioId" :to="`/negocios/${c.negocioId}`">{{ c.negocio }}</router-link>
                      <small v-if="c.etapa" class="subtexto">{{ c.etapa }}</small>
                      <template v-if="!c.negocioId">—</template>
                    </td>
                    <td>{{ formatearFechaHora(c.ultimaInteraccion) }}</td>
                    <td>
                      <button type="button" class="enlace" :aria-expanded="abiertos.has(c.contactoId)" @click="alternar(c.contactoId)">
                        {{ abiertos.has(c.contactoId) ? 'Ocultar acciones' : `Ver acciones (${c.acciones.length})` }}
                      </button>
                    </td>
                  </tr>
                  <tr v-if="abiertos.has(c.contactoId)" class="fila-acciones">
                    <td colspan="7">
                      <ol class="acciones-lista">
                        <li v-for="(a, i) in c.acciones" :key="i">
                          <span class="accion-fecha">{{ formatearFechaHora(a.fecha) }}</span>
                          <span class="accion-tipo">{{ a.tipo }}</span>
                          <span>{{ a.descripcion }}</span>
                        </li>
                      </ol>
                    </td>
                  </tr>
                </template>
              </tbody>
            </table>
          </div>
          <p v-else class="texto">{{ nivel ? 'Ningún contacto está en este nivel.' : 'Nadie ha interactuado con este inmueble todavía.' }}</p>
        </section>
      </template>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import MenuCrm from './MenuCrm.vue'
import { COLOR_NIVEL } from './estadisticas/colores'
import { mensajeDeError } from '@/services/api'
import { obtenerInteraccionInmueble, type InteraccionInmueble, type NivelInteraccion } from '@/services/estadisticas'
import { formatearFechaHora } from '@/utils/fechas'

/* Vista detallada desde Estadisticas: los contactos que interactuaron con un inmueble, el nivel al que llegaron y
   cada accion (consultas, mensajes, visitas, negocio y cambios de etapa). ?nivel= viene de la porcion del pastel */
const route = useRoute()
const router = useRouter()

const NIVELES: { valor: NivelInteraccion; texto: string }[] = [
  { valor: 'INFORMACION', texto: 'Solo pidió información' },
  { valor: 'AGENDO', texto: 'Agendó visita' },
  { valor: 'VISITO', texto: 'Realizó visita física' },
  { valor: 'OFERTA', texto: 'Presentó oferta' },
  { valor: 'CERRO', texto: 'Compró / alquiló' },
  { valor: 'DESCARTO', texto: 'Descartó' },
]

const datos = ref<InteraccionInmueble | null>(null)
const totalContactos = ref(0)
const cargando = ref(false)
const error = ref('')
const abiertos = ref(new Set<number>())

const nivel = computed(() => {
  const valor = String(route.query.nivel ?? '')
  return NIVELES.some((n) => n.valor === valor) ? (valor as NivelInteraccion) : null
})
const cambiarNivel = (n: NivelInteraccion | null) => router.replace({ query: { ...route.query, nivel: n ?? undefined } })

function alternar(id: number) {
  const nuevos = new Set(abiertos.value)
  if (nuevos.has(id)) nuevos.delete(id)
  else nuevos.add(id)
  abiertos.value = nuevos
}

async function cargar() {
  cargando.value = true
  error.value = ''
  try {
    datos.value = await obtenerInteraccionInmueble(String(route.params.id), nivel.value ?? undefined)
    if (!nivel.value) totalContactos.value = datos.value.contactos.length
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
}

watch(() => [route.params.id, nivel.value], cargar)
onMounted(async () => {
  await cargar()
  // El total de "Todos" se pide una vez aunque se entre ya filtrado por un nivel
  if (nivel.value) {
    obtenerInteraccionInmueble(String(route.params.id)).then((d) => { totalContactos.value = d.contactos.length }).catch(() => {})
  }
})
</script>

<style scoped src="./contact.css"></style>
<style scoped src="./contactos/detalle.css"></style>
<style scoped src="./contactos/registro.css"></style>
<style scoped src="./contactos/ficha.css"></style>
<style scoped>
.filtros-nivel {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background-color: #ffffff;
  border: 1px solid #d5dbe6;
  color: #0b1f46;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
}

.chip.activo {
  background-color: #0b1f46;
  border-color: #0b1f46;
  color: #ffffff;
}

.punto {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.nivel {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.subtexto {
  display: block;
  color: #636363;
  font-size: 12px;
}

.fila-acciones td {
  background-color: #f8fafc;
}

.acciones-lista {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}

.acciones-lista li {
  display: grid;
  grid-template-columns: 150px 80px 1fr;
  gap: 10px;
}

.accion-fecha {
  color: #636363;
}

.accion-tipo {
  font-weight: 600;
  color: #0b1f46;
}

@media (max-width: 700px) {
  .acciones-lista li {
    grid-template-columns: 1fr;
    gap: 2px;
  }
}
</style>
