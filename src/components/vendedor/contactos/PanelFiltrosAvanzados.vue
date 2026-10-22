<template>
  <Teleport to="body">
    <div class="panel-fondo" @mousedown.self="emit('cerrar')" @keydown.esc="emit('cerrar')">
      <aside class="panel" role="dialog" aria-modal="true" aria-labelledby="titulo-avanzados">
        <header class="panel-cabecera">
          <h2 id="titulo-avanzados">Filtros avanzados</h2>
          <button type="button" class="cerrar" aria-label="Cerrar" @click="emit('cerrar')">&times;</button>
        </header>

        <form class="panel-cuerpo" @submit.prevent="aplicar">
          <p class="ayuda">Se combinan con los filtros de la barra: un contacto debe cumplir todos.</p>

          <div class="campo">
            <label for="av-nombre">Nombre contiene</label>
            <input id="av-nombre" ref="primero" v-model="valores.nombre" type="text" maxlength="100" placeholder="Ej: María López" />
          </div>
          <div class="campo">
            <label for="av-correo">Correo contiene</label>
            <input id="av-correo" v-model="valores.correo" type="text" maxlength="255" placeholder="Ej: @gmail.com" />
          </div>
          <div class="campo">
            <label for="av-telefono">Teléfono contiene</label>
            <input id="av-telefono" v-model="valores.telefono" type="text" maxlength="30" placeholder="Ej: 0414" />
          </div>
          <div class="campo">
            <label for="av-cedula">Cédula</label>
            <input id="av-cedula" v-model="valores.cedula" type="text" inputmode="numeric" maxlength="10" placeholder="Número sin V/E" />
          </div>
          <div class="campo">
            <label for="av-negocio">Tipo de negocio</label>
            <input id="av-negocio" v-model="valores.tipoNegocio" type="text" list="av-negocios" maxlength="50" placeholder="Compra, alquiler, venta" />
            <datalist id="av-negocios">
              <option value="compra" />
              <option value="alquiler" />
              <option value="venta" />
            </datalist>
          </div>
          <div class="campo">
            <label for="av-verificado">Cuenta del portal</label>
            <select id="av-verificado" v-model="valores.verificado">
              <option value="">Todas</option>
              <option value="true">Correo verificado</option>
              <option value="false">Sin verificar</option>
            </select>
          </div>
          <p v-if="errorCedula" class="error">La cédula debe ser un número.</p>
        </form>

        <footer class="panel-pie">
          <button type="button" class="btn-secundario" @click="limpiar">Limpiar</button>
          <button type="button" class="btn-primario" :disabled="errorCedula" @click="aplicar">Aplicar filtros</button>
        </footer>
      </aside>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'

import { filtrosAvanzadosVacios, type FiltrosAvanzados } from '@/services/contactos'

// Se edita una copia: los cambios solo se aplican al pulsar "Aplicar filtros"
const props = defineProps<{ modelValue: FiltrosAvanzados }>()
const emit = defineEmits<{ cerrar: []; 'update:modelValue': [valor: FiltrosAvanzados] }>()

const valores = reactive<FiltrosAvanzados>({ ...props.modelValue })
const errorCedula = computed(() => valores.cedula.trim() !== '' && !/^\d+$/.test(valores.cedula.trim()))
const primero = ref<HTMLInputElement | null>(null)
onMounted(() => primero.value?.focus())

function aplicar() {
  if (errorCedula.value) return
  emit('update:modelValue', { ...valores })
  emit('cerrar')
}

function limpiar() {
  Object.assign(valores, filtrosAvanzadosVacios())
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
