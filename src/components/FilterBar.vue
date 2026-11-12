<template>
  <div class="filter-bar">
    <!-- Nivel Superior: Pestañas -->
    <div class="filter-tabs">
      <!-- Comprar (isAlquiler = false) -->
      <input 
        type="radio" 
        id="modalidad-compra" 
        :value="false" 
        class="hidden-input" 
        v-model="filtros.isAlquiler"
      >
      <label 
        for="modalidad-compra" 
        class="tab-label"
        :class="{ 'tab-activa': filtros.isAlquiler === false }"
      >
        Comprar
      </label>

      <!-- Alquilar (isAlquiler = true) -->
      <input 
        type="radio" 
        id="modalidad-alquiler" 
        :value="true" 
        class="hidden-input" 
        v-model="filtros.isAlquiler"
      >
      <label 
        for="modalidad-alquiler" 
        class="tab-label"
        :class="{ 'tab-activa': filtros.isAlquiler === true }"
      >
        Alquilar
      </label>
    </div>

    <!-- Nivel Inferior: Selectores y Botón -->
    <div class="filter-inputs">
      <!-- Locación / Ciudades -->
      <div class="filter-group">
        <label for="locacion">Locación</label>
        <select id="locacion" name="locacion" v-model="filtros.ciudadId">
          <option value="">Todas las ciudades</option>
          <option v-for="ciudad in ciudades" :key="ciudad.id" :value="ciudad.id">
            {{ ciudad.nombre }}
          </option>
        </select>
      </div>

      <div class="divider"></div>

      <!-- Tipo de Propiedad -->
      <div class="filter-group">
        <label for="tipo">Tipo de Propiedad</label>
        <select id="tipo" name="tipo" v-model="filtros.tipoInmuebleId">
          <option value="">Todos los tipos</option>
          <option v-for="tipo in tiposInmueble" :key="tipo.id" :value="tipo.id">
            {{ tipo.nombre }}
          </option>
        </select>
      </div>

      <div class="divider"></div>

      <button @click="ejecutarBusqueda" class="search-btn">
        <img src="/lupa.svg" alt="Buscar" />
        <span>Buscar Propiedad</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFiltros } from '@/composables/useFiltros'

const route = useRoute()
const router = useRouter()

// Usamos directamente el estado y el lector de URL de tu composable
const { ciudades, tiposInmueble, filtros, leerFiltrosDeUrl } = useFiltros()

// Cada vez que cargue el componente o cambie la URL (ej. al pasar de Home a /search),
// sincroniza las pestañas y los selects con lo que dice la URL
watch(() => route.query, () => {
  leerFiltrosDeUrl()
}, { immediate: true })

const ejecutarBusqueda = () => {
  const query = {
    // Como filtros.isAlquiler ya es booleano, lo pasamos directo a string para la URL
    isAlquiler: filtros.isAlquiler ? 'true' : 'false'
  }

  if (filtros.ciudadId) {
    query.ciudadId = filtros.ciudadId
  }

  if (filtros.tipoInmuebleId) {
    query.tipoInmuebleId = filtros.tipoInmuebleId
  }

  router.push({ path: '/search', query })
}
</script>
<style scoped>

.filter-bar {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 90%;
  max-width: 1300px;
  margin: 0 auto;
  background-color: #ffffff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  margin-top: -50px;
}

/* Pestañas (Comprar / Alquilar) */
.filter-tabs {
  display: flex;
  width: 100%;
  border-bottom: 1px solid #e0e0e0;
}

.hidden-input {
  display: none;
}

.tab-label {
  flex: 1; /* Ambas pestañas miden el 50% */
  text-align: center;
  padding: 16px 0;
  font-size: 20px;
  font-weight: 500;
  color: #1c1c1c;
  background-color: #ffffff;
  cursor: pointer;
  transition: background-color 0.2s;
}

.filter-tabs .hidden-input:checked + .tab-label {
  background-color: #dcdcdc;
}

.filter-inputs {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 10px 30px;
  flex: 1;
  gap: 20px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  flex: 1;
  gap: 5px;
}

.filter-group label {
  font-size: 18px;
  font-weight: 500;
  color: #000000; /* Títulos en negro */
}

.filter-inputs .divider {
  width: 1px;
  height: 40px;
  background-color: #ddd; /* O el color gris que estés usando */
}

/* Alineación interna de los inputs/selects para quitar bordes por defecto si es necesario */
.filter-group select {
  font-size: 14px;
  color: #888888; /* Valor seleccionado en gris */
  border: none;
  outline: none;
  background: transparent;
  cursor: pointer;
}

/* Líneas divisorias verticales entre filtros */
.divider {
  width: 1px;
  height: 45px;
  background-color: #e0e0e0;
}

/* Botón Buscar */
.search-btn {
  background-color: #0b1f46;
  color: #ffffff;
  border: none;
  border-radius: 12px;
  padding: 14px 24px;
  font-size: 15px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.search-btn img {
  width: 18px;
  height: 18px;
}

.hidden-input {
  display: none;
}

</style>