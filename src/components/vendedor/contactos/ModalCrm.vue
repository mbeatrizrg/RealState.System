<template>
  <Teleport to="body">
    <div class="modal-fondo" @mousedown.self="emit('cerrar')" @keydown.esc="emit('cerrar')">
      <section ref="caja" class="modal-caja" role="dialog" aria-modal="true" :aria-labelledby="idTitulo" tabindex="-1">
        <header class="modal-cabecera">
          <h2 :id="idTitulo">{{ titulo }}</h2>
          <button type="button" class="modal-cerrar" aria-label="Cerrar" @click="emit('cerrar')">&times;</button>
        </header>
        <div class="modal-cuerpo">
          <slot />
        </div>
        <footer class="modal-pie">
          <slot name="pie" />
        </footer>
      </section>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { onMounted, ref, useId } from 'vue'

// Ventana modal del CRM: se cierra con Escape, con la X o haciendo clic fuera de la caja
defineProps<{ titulo: string }>()
const emit = defineEmits<{ cerrar: [] }>()

const idTitulo = useId()
const caja = ref<HTMLElement | null>(null)
onMounted(() => caja.value?.focus())
</script>

<style scoped>
.modal-fondo {
  position: fixed;
  inset: 0;
  z-index: 100;
  background-color: rgba(11, 31, 70, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.modal-caja {
  background-color: #ffffff;
  border-radius: 14px;
  width: min(520px, 100%);
  max-height: calc(100vh - 32px);
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 48px rgba(11, 31, 70, 0.3);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  outline: none;
}

.modal-cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px 12px;
}

.modal-cabecera h2 {
  margin: 0;
  font-family: 'Playfair Display', 'Merriweather', serif;
  font-size: 22px;
  color: #0b1f46;
}

.modal-cerrar {
  background: none;
  border: none;
  font-size: 28px;
  line-height: 1;
  color: #636363;
  cursor: pointer;
}

.modal-cuerpo {
  padding: 4px 24px 16px;
  overflow-y: auto;
  color: #1c1c1c;
}

.modal-pie {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 14px 24px 20px;
  border-top: 1px solid #e3e8f4;
}

.modal-pie :deep(.btn-primario) {
  background-color: #0b1f46;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
}

.modal-pie :deep(.btn-primario:disabled) {
  opacity: 0.5;
  cursor: default;
}

.modal-pie :deep(.btn-secundario) {
  background: transparent;
  color: #0b1f46;
  border: 1px solid #0b1f46;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
}
</style>
