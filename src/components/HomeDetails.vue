<template>
<div class="details-container">
    <!-- Enlace de regreso -->
    <a href="#" class="back-link" @click.prevent="goBack">Atras</a>

    <!-- Galería de imágenes -->
    <section class="gallery-grid">
      <div class="gallery-item">
        <img src="/foto1.jpg" alt="Cocina" />
      </div>
      <div class="gallery-item">
        <img src="/foto2.jpg" alt="Comedor" />
      </div>
      <div class="gallery-item">
        <img src="/foto3.jpg" alt="Fachada del edificio" />
      </div>
      <div class="gallery-item main-thumb">
        <img src="/foto4.jpg" alt="Piscina y fachada" />
        <button class="gallery-next-btn">&gt;</button>
      </div>
    </section>

    <!-- Información principal del inmueble -->
    <section class="property-info-section">
      <div class="info-left">
        <h1 class="property-title">Residencias Altos Del Mirador</h1>
        <p class="property-location">
          <span class="location-icon">📍</span> Valencia, Via Guataparo
        </p>
        <p class="property-description">
          Rifat Richani is an artful mosaic of luxury residences that blend minimalist details with modern living. A serene community where thoughtful design and exceptional amenities invites you to embrace an elevated lifestyle in Long Island City.
        </p>
        <button class="contact-btn">Contactanos &gt;</button>
      </div>

      <div class="info-right">
        <h2 class="details-title">Detalles</h2>
        <ul class="details-list">
          <li><strong>2 Habitaciones</strong> | <strong>2 baños</strong></li>
          <li>1 Puesto de Estacionamiento</li>
          <li>Aire acondicionado en las habitaciones</li>
          <li>Cocina empotrada</li>
          <li>Piscina</li>
          <li>Edificio | Piso 4</li>
        </ul>
      </div>
    </section>

    <!-- Mapa -->
    <section class="map-section">
      <img src="/mapa-valencia.jpg" alt="Ubicación en el mapa" class="map-image" />
    </section>

    <!-- Pie de página / Footer -->
    <footer class="property-footer">
      <div class="brand-logo">
        <img src="/brand.png" alt="Rifat Richani Bienes Raíces" />
      </div>
      
      <address class="footer-address">
        C.C.P. Otama, urb. El Viñedo, PB<br />
        Local 10, Valencia, Carabobo
      </address>

      <p class="footer-contact-label">CONTACTANOS</p>

      <div class="social-links">
        <a href="#" aria-label="X (Twitter)"><i class="icon-x"></i></a>
        <a href="#" aria-label="Instagram"><i class="icon-instagram"></i></a>
        <a href="#" aria-label="Facebook"><i class="icon-facebook"></i></a>
        <a href="#" aria-label="WhatsApp"><i class="icon-whatsapp"></i></a>
      </div>
    </footer>
  </div>





</template>



<script setup>
import {useRouter} from 'vue-router'

const router = useRouter()
const Search = ()=> {
  router.push('/details')
}
// Lógica de JavaScript / Vue (si es necesaria)
</script>

<style scoped src= "./detailstyle.css"></style>