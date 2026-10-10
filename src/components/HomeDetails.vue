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
          <img src="/location.png" alt="Ubicación" class="location-icon" />
          <span>Valencia, Via Guataparo</span>
        </p>
        <p class="property-description">
          Rifat Richani is an artful mosaic of luxury residences that blend minimalist details with modern living. A serene community where thoughtful design and exceptional amenities invites you to embrace an elevated lifestyle in Long Island City.
        </p>
          <a href="#" class="contact-btn">Contactanos &gt;</a>
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
      <iframe 
        src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3929.091187882168!2d-68.0681556847226!3d10.1740420928704!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x8c2a5e7b8e5b8e5b%3A0x8c2a5e7b8e5b8e5b!2sResidencias%20Altos%20Del%20Mirador!5e0!3m2!1ses-419!2sve!4v1697040000000">
        style="border:0;"
        allowfullscreen=""
        loading="lazy"
        referrerpolicy="no-referrer-when-downgrade">
      </iframe>
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
        <a href="https://x.com/rifatrichanibr"><img src="/x.png" alt="X (Twitter)" /></a>
        <a href="https://www.instagram.com/rifatrichanibienesraices?stkn=c2YydWl4c3Q4anNr"><img src="/inst.png" alt="Instagram" /></a>
        <a href="https://www.facebook.com/share/19A5MuKs4X/?mibextid=wwXIfr"><img src="/facebook.png" alt="Facebook" /></a>
        <a href="https://wa.me/584145936070"><img src="/whatsapp.png" alt="WhatsApp"></a>
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