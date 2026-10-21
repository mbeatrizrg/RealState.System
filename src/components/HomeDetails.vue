<template>
<div class="details-container">
    <!-- Enlace de regreso -->
    <button @click="Atras" class="back-btn">
      <span>Atras</span>
    </button>

    <p v-if="cargando" class="estado-msg">Cargando propiedad...</p>
    <p v-else-if="error" class="estado-msg">{{ error }}</p>

    <template v-else-if="inmueble">
      <!-- Galería de imágenes -->
      <section class="carousel">
        <div v-for="(url, indice) in imagenes" :key="url" class="carousel-item">
          <img :src="url" :alt="`${inmueble.titulo} - foto ${indice + 1}`" />
        </div>
      </section>

      <!-- Información principal del inmueble -->
      <section class="info-section">
        <div class="info-left">
          <h1 class="property-title">{{ inmueble.titulo }}</h1>
          <p class="property-location">
            <img src="/location.png" alt="Ubicación" class="location-icon" />
            <span>{{ inmueble.nombreCiudad || 'Carabobo' }}</span>
          </p>
          <p class="property-price">{{ formatearPrecio(inmueble.precio) }}</p>
          <p class="property-description">
            {{ inmueble.descripcion || 'Pronto agregaremos la descripción de esta propiedad.' }}
          </p>
            <!-- PENDIENTE: este boton debe crear un Lead en el CRM -->
            <a href="#" class="contact-btn">Contactanos &gt;</a>
        </div>

        <div class="info-right">
          <h2 class="details-title">Detalles</h2>
          <ul class="details-list">
            <li><strong>{{ inmueble.nombreTipoInmueble }}</strong> | {{ inmueble.nombreEstatus }}</li>
            <li v-for="extra in inmueble.extras" :key="extra">{{ extra }}</li>
            <li>Código: {{ inmueble.rrbr }}</li>
          </ul>
        </div>
      </section>
    </template>

    <!-- Mapa -->
    <section class="map-section">
      <img src="/reference-img.png" alt="Ubicación en el mapa" class="map-image" />
    </section>

    <!-- Pie de página / Footer -->
    <footer class="page-6">
    <!--contenido central-->
      <div class="footer-content">
        <div class="logo">
          <img src="/icon.png" alt="Rifat Richani Bienes Raices" class="footer-brand-img" />
        </div>
      <!-- Direccion-->
      <p class="footer-address">
        C.C.P. Otama, urb. El Viñedo, PB<br />
        Local 10, Valencia, Carabobo
      </p>
      <!--Titulo de contacto-->
      <h3 class="contact-title">CONTACTANOS</h3>
      <!--Iconos de redes sociales-->
      <div class="social-icons">
        <a href="#"><img src="/x.png" alt="X (Twitter)" /></a>
        <a href="#"><img src="/inst.png" alt="Instagram" /></a>
        <a href="#"><img src="/facebook.png" alt="Facebook" /></a>
        <a href="#"><img src="/whatsapp.png" alt="WhatsApp"></a>
      </div>



      </div>
    </footer>
</div>






</template>



<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { mensajeDeError } from '@/services/api'
import { obtenerInmueble, registrarVista } from '@/services/inmuebles'
import { formatearPrecio, IMAGEN_POR_DEFECTO } from '@/utils/formato'

const route = useRoute()
const router = useRouter()

const inmueble = ref(null)
const cargando = ref(true)
const error = ref('')

const imagenes = computed(() =>
  inmueble.value?.urlsGaleria?.length ? inmueble.value.urlsGaleria : [IMAGEN_POR_DEFECTO]
)

watch(() => route.params.id, async (id) => {
  if (!id) return
  cargando.value = true
  error.value = ''
  try {
    inmueble.value = await obtenerInmueble(id)
    // La vista es telemetria para el CRM, si falla no debe afectar al usuario
    registrarVista(inmueble.value.id).catch(() => {})
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
}, { immediate: true })

// Vuelve a los resultados de busqueda (con sus filtros) o a /search si se entro directo al detalle
const Atras = ()=> {
  if (window.history.state?.back) router.back()
  else router.push('/search')
}
</script>

<style scoped src= "./detailstyle.css"></style>
