<template>
<div class="details-container">
    <!-- Enlace de regreso -->
    <button @click="Search" class="back-btn">
      <span>Atras</span>
    </button> 

    <!-- Galería de imágenes -->
    <section class="carousel">
      <div class="carousel-item">
        <img src="/innerbuilding.png" alt="Cocina" />
      </div>
      <div class="carousel-item">
        <img src="/innerbuilding2.png" alt="Comedor" />
      </div>
      <div class="carousel-item">
        <img src="/building.png" alt="Fachada del edificio" />
      </div>
      <div class="carousel-item">
        <img src="/innerbuilding4.png" alt="Piscina y fachada" />
        <button class="next-btn">&gt;</button>
      </div>
    </section>

    <!-- Información principal del inmueble -->
    <section class="info-section">
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
      <!-- Mapa /* <img src="/mapa-valencia.jpg" alt="Ubicación en el mapa" class="map-image" /> */-->
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
      <!-- aqui estuvo Batman-->
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
import {useRouter} from 'vue-router'

const router = useRouter()

const Search = ()=> {
  router.push('/search')
}
// Lógica de JavaScript / Vue (si es necesaria)
</script>

<style scoped src= "./detailstyle.css"></style>