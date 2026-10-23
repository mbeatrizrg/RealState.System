<template>
  <Teleport to="body">
    <div class="panel-fondo" @mousedown.self="emit('cerrar')" @keydown.esc="emit('cerrar')">
      <aside class="panel" role="dialog" aria-modal="true" :aria-labelledby="idTitulo">
        <header class="panel-cabecera">
          <h2 :id="idTitulo">Filtros avanzados</h2>
          <button type="button" class="cerrar" aria-label="Cerrar" @click="emit('cerrar')">&times;</button>
        </header>

        <form class="panel-cuerpo" @submit.prevent="aplicar">
          <p class="ayuda">Se combinan con los filtros de la barra: {{ ayuda }}</p>
          <div v-for="campo in campos" :key="campo.clave" class="campo">
            <label :for="`${idTitulo}-${campo.clave}`">{{ campo.etiqueta }}</label>
            <select v-if="campo.opciones" :id="`${idTitulo}-${campo.clave}`" v-model="valores[campo.clave]">
              <option value="">Todos</option>
              <option v-for="o in campo.opciones" :key="o.valor" :value="o.valor">{{ o.texto }}</option>
            </select>
            <input
              v-else
              :id="`${idTitulo}-${campo.clave}`"
              ref="entradas"
              v-model="valores[campo.clave]"
              :type="campo.numerico ? 'number' : 'text'"
              :min="campo.numerico ? 0 : undefined"
              :inputmode="campo.numerico ? 'decimal' : undefined"
              maxlength="100"
              :placeholder="campo.placeholder"
            />
          </div>
          <p v-if="error" class="error">{{ error }}</p>
        </form>

        <footer class="panel-pie">
          <button type="button" class="btn-secundario" @click="limpiar">Limpiar</button>
          <button type="button" class="btn-primario" :disabled="!!error" @click="aplicar">Aplicar filtros</button>
        </footer>
      </aside>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, useId } from 'vue'

export interface CampoAvanzado {
  clave: string
  etiqueta: string
  placeholder?: string
  // Solo numeros >= 0 (rangos de precio, metros...)
  numerico?: boolean
  // Con opciones se muestra un select (sin elegir = todos)
  opciones?: { valor: string; texto: string }[]
}

/* Panel lateral de filtros avanzados de Negocios e Inmuebles (Contactos usa PanelFiltrosAvanzados).
   v-model: { clave: texto }. Se edita una copia y solo se aplica al pulsar "Aplicar filtros".
   rangos: pares [minimo, maximo] que se validan (el minimo no puede ser mayor) */
const props = withDefaults(defineProps<{
  modelValue: Record<string, string>
  campos: CampoAvanzado[]
  ayuda?: string
  rangos?: [string, string][]
}>(), { ayuda: 'un registro debe cumplir todos.', rangos: () => [] })
const emit = defineEmits<{ cerrar: []; 'update:modelValue': [valor: Record<string, string>] }>()

const idTitulo = useId()
const valores = reactive<Record<string, string>>({ ...props.modelValue })
// Se enfoca el primer campo de texto al abrir
const entradas = ref<HTMLInputElement[]>([])
onMounted(() => entradas.value[0]?.focus())

const error = computed(() => {
  for (const [min, max] of props.rangos) {
    const a = String(valores[min] ?? '').trim()
    const b = String(valores[max] ?? '').trim()
    if (a !== '' && b !== '' && Number(a) > Number(b)) {
      const etiqueta = props.campos.find((c) => c.clave === min)?.etiqueta ?? min
      return `"${etiqueta}" no puede ser mayor que el máximo.`
    }
  }
  return ''
})

function aplicar() {
  if (error.value) return
  const limpio: Record<string, string> = {}
  props.campos.forEach((c) => { limpio[c.clave] = String(valores[c.clave] ?? '').trim() })
  emit('update:modelValue', limpio)
  emit('cerrar')
}

function limpiar() {
  props.campos.forEach((c) => { valores[c.clave] = '' })
}
</script>

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
  width: min(380px, 100%);
  height: 100%;
  background-color: #ffffff;
  display: flex;
  flex-direction: column;
  box-shadow: -12px 0 32px rgba(11, 31, 70, 0.2);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.panel-cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 8px;
}

.panel-cabecera h2 {
  margin: 0;
  font-family: 'Playfair Display', 'Merriweather', serif;
  font-size: 22px;
  color: #0b1f46;
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
  gap: 14px;
}

.ayuda {
  margin: 0;
  font-size: 13px;
  color: #636363;
}

.campo {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.campo label {
  font-size: 13px;
  font-weight: 600;
  color: #0b1f46;
}

.campo input,
.campo select {
  background-color: #0c1452;
  color: #ffffff;
  border: none;
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
}

.campo input::placeholder {
  color: #92abde;
}

.error {
  margin: 0;
  color: #991b1b;
  font-size: 13px;
}

.panel-pie {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 24px 24px;
  border-top: 1px solid #e3e8f4;
}

.btn-primario {
  background-color: #0b1f46;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
}

.btn-primario:disabled {
  opacity: 0.5;
}

.btn-secundario {
  background: transparent;
  color: #0b1f46;
  border: 1px solid #0b1f46;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
}
</style>
