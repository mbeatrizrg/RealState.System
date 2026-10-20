<template>
  <div class="layout-container">
    <!-- Encabezado -->
    <header class="CommonHeader">
      <div class="logo">
        <img src="/icon.png" alt="Icono" class="icon" />
        <img src="/brand.png" alt="Rifat Richani" class="brand" />
      </div>
      
      <nav class="primary-nav">
        <!-- cambiar para que se vea como el search btn -->
        <button @click="toggleMenu" class="menu-btn">
          <span>{{ isMenuOpen ? 'Cerrar' : 'Menu' }}</span>
        </button>  
        <button @click="Create" class="inc-btn">
          <span>Crear cuenta</span>
        </button>
      </nav>
    </header>

    <!-- Contenido principal-->
    <main class="main-content">
      <div class="vision-container">
          <article class="image-article">
            <div class="image-4">
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>
          </article>
        </div>

      <div class="form-section">
        <h1 class="form-title">Iniciar sesion</h1>

        <form class="login-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="gmail">Gmail</label>
            <input 
              type="email" 
              id="gmail" 
              v-model="email"
              placeholder="example@gmail.com" 
            />
          </div>

          <div class="form-group">
            <label for="password">Cotraseña</label>
            <input 
              type="password" 
              id="password" 
              v-model="password"
              placeholder="8 caracteres" 
            />
          </div>

          <button type="submit" class="btn btn-submit">Iniciar sesion</button>
        </form>
      </div>
    </main>

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
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import api from '@/api' // Asegúrate de haber creado el archivo src/api.ts

const router = useRouter()

const isMenuOpen = ref(false)
const email = ref('')
const password = ref('')

const handleLogin = async () => {
  try {
    // Apuntamos al endpoint del Vendedor en el CRM
    const response = await api.post('/auth/login/empleado', {
      correo: email.value,       // Mapeado a DTO de Spring Boot
      contrasena: password.value // Mapeado a DTO de Spring Boot
    })
    
    console.log("¡Respuesta del backend recibida!", response.data)
    
    // Si el backend nos envía un token, lo guardamos para mantener la sesión
    if (response.data.token) {
      localStorage.setItem('token_crm', response.data.token)
    }
    
    // Redirigimos al vendedor directamente a su panel
    router.push('/leads')
    
  } catch (error) {
    console.error("Error de conexión:", error)
    if (error.response && error.response.status === 401) {
      alert("Credenciales incorrectas. Verifica tu correo y contraseña.")
    } else {
      alert("Error: No se pudo conectar con el servidor Backend")
    }
  }
}

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}
const signIn = ()=> {
  router.push('/signin')
}
const Create = ()=> {
  router.push('/create')
}
const Home = ()=> {
  router.push('/')
}
const Contact = ()=> {
  router.push('/contacts')
}
const Search = ()=> {
  router.push('/search')
}
</script>

<style scoped src= "./sign-in-style.css"></style>