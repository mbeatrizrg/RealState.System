<template>
    <footer class="page-6">
    <!-- boton superior derecho-->
        <div class="login-6">
            <button @click="toggleMenu" class="menu-btn">
                <span>{{ isMenuOpen ? 'Cerrar' : 'Menu' }}</span>
            </button> 
            <button @click="signIn" class="inc-btn">
                <span>Iniciar Sesión</span>
            </button> 
        </div>
        <!--contenido central-->
    <div class="footer-content">
      <div class="logo">
        <img src="/icon.png" alt="Rifat Richani Bienes Raices" class="footer-brand-img" />
      </div>
      <!-- Direccion-->
      <p class="footer-address">
        C.C.P. Otama, urb. El Viñedo, PB<br/>
        Local 10, Valencia, Carabobo
      </p>
      <!--Titulo de contacto-->
      <h3 class="contact-title">CONTACTANOS</h3>
      <!-- aqui estuvo Batman-->
      <!--Iconos de redes sociales-->
      <div class="social-icons">
        <a href="https://x.com/rifatrichanibr"><img src="/x.png" alt="X (Twitter)" /></a>
        <a href="https://www.instagram.com/rifatrichanibienesraices?stkn=c2YydWl4c3Q4anNr"><img src="/inst.png" alt="Instagram" /></a>
        <a href="https://www.facebook.com/share/19A5MuKs4X/?mibextid=wwXIfr"><img src="/facebook.png" alt="Facebook" /></a>
        <a href="https://wa.me/584145936070"><img src="/whatsapp.png" alt="WhatsApp"></a>
      </div>
    </div>
    </footer>

  <div class="menu-container" :class="{ 'open': isMenuOpen }">
    <!-- Navegación superior -->
    <header class="navbar">
      <button @click="toggleMenu" class="nav-link">
        Salir
      </button>
      <img src="/brand2.png" alt="Rifat Richani" class="brand" />
      <button @click="signIn" class="inc-btn">
        <span>Iniciar Sesión</span>
      </button>
    </header>

    <!-- Menú principal centrado -->
    <main class="menu-content">
      <nav class="menu-list">
        <button @click="Home" class="menu-item active"><span>Principal</span></button> 
        <button @click="Search" class="menu-item"><span>Disponibles</span></button> 
        <a href="#" class="menu-item">Favoritos</a>
        <button @click="Contact" class="menu-item"><span>Contactanos</span></button> 
      </nav>
    </main>
  </div>


</template>

<script setup>
import {useRouter} from 'vue-router'
import {ref} from 'vue'
const router = useRouter()

const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const signIn = ()=> {
  router.push('/signin')
}
</script>

<style scoped src= "./contacts-style.css"></style>