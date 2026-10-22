<template>
  <nav v-if="totalPaginas > 1" class="paginacion" aria-label="Paginación">
    <button class="pag-btn" :disabled="pagina <= 1" @click="emit('cambiar', pagina - 1)">&lsaquo; Anterior</button>

    <template v-for="(item, indice) in paginas" :key="indice">
      <span v-if="item === null" class="pag-puntos">…</span>
      <button
        v-else
        :class="['pag-btn', 'pag-numero', { activa: item === pagina }]"
        :aria-current="item === pagina ? 'page' : undefined"
        @click="item !== pagina && emit('cambiar', item)"
      >
        {{ item }}
      </button>
    </template>

    <button class="pag-btn" :disabled="pagina >= totalPaginas" @click="emit('cambiar', pagina + 1)">Siguiente &rsaquo;</button>
    <span v-if="totalElementos !== undefined" class="pag-total">{{ totalElementos }} resultados</span>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'

// pagina empieza en 1 (como se muestra al usuario)
const props = defineProps<{ pagina: number; totalPaginas: number; totalElementos?: number }>()
const emit = defineEmits<{ cambiar: [pagina: number] }>()

// Primera, ultima y las dos vecinas de la actual; null marca un salto (…)
const paginas = computed(() => {
  const { pagina, totalPaginas } = props
  const visibles = new Set([1, totalPaginas, pagina - 1, pagina, pagina + 1].filter((n) => n >= 1 && n <= totalPaginas))
  const ordenadas = [...visibles].sort((a, b) => a - b)
  const resultado: (number | null)[] = []
  ordenadas.forEach((n, i) => {
    const anterior = ordenadas[i - 1]
    if (i > 0 && anterior !== undefined && n - anterior > 1) resultado.push(null)
    resultado.push(n)
  })
  return resultado
})
</script>

<style scoped>
.paginacion {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin: 30px auto;
  font-family: inherit;
}

.pag-btn {
  background-color: #ffffff;
  color: #0b1f46;
  border: 1px solid #0b1f46;
  border-radius: 8px;
  padding: 8px 14px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.pag-btn:hover:not(:disabled):not(.activa) {
  background-color: #e3e8f4;
}

.pag-btn:disabled {
  opacity: 0.4;
  cursor: default;
}

.pag-numero.activa {
  background-color: #0b1f46;
  color: #ffffff;
  cursor: default;
}

.pag-puntos {
  color: #636363;
  padding: 0 4px;
}

.pag-total {
  color: #636363;
  font-size: 13px;
  margin-left: 8px;
}
</style>
