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

        <form class="login-form" @submit.prevent="IniciarSesion">
          <div class="form-group">
            <label for="gmail">Gmail</label>
            <input
              type="email"
              id="gmail"
              placeholder="ejemplo@gmail.com"
              v-model.trim="credenciales.correo"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <div class="password-container">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="credenciales.contrasena"
              placeholder="8 caracteres"
              class="password-field"
              required
            />
            <span class="box-password"
              :class="{active: showPassword }"
              @click="togglePassword">
            </span>
            </div>
          </div>

          <p v-if="mensaje" class="form-mensaje" :class="{ 'form-mensaje-error': esError }">{{ mensaje }}</p>

          <button type="submit" class="btn btn-submit" :disabled="enviando">
            {{ enviando ? 'Ingresando...' : 'Iniciar sesion' }}
          </button>

          <div class="enlaces-cuenta">
            <router-link :to="{ path: '/recuperar', query: { correo: credenciales.correo || undefined } }" class="enlace">¿Olvidaste tu contraseña?</router-link>
            <router-link to="/crm/login" class="enlace">Soy asesor</router-link>
          </div>
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
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { mensajeDeError } from '@/services/api'
import { loginUsuario } from '@/services/auth'

const route = useRoute()
const router = useRouter()

const showPassword = ref(false)

const togglePassword = ()=> {
  showPassword.value = !showPassword.value
}

const isMenuOpen = ref(false)
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

// Coincide con UsuarioLogeoDTO del backend. El correo puede venir de la verificacion o la recuperacion (?correo=)
const credenciales = reactive({ correo: typeof route.query.correo === 'string' ? route.query.correo : '', contrasena: '' })
const mensaje = ref(route.query.expirada ? 'Tu sesión expiró. Inicia sesión de nuevo.' : '')
const esError = ref(!!route.query.expirada)
const enviando = ref(false)

// Solo se vuelve a rutas internas del sitio (evita redirecciones a otros dominios)
const destino = () => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') && !redirect.startsWith('//') && !redirect.includes('\\') ? redirect : '/'
}

const IniciarSesion = async ()=> {
  enviando.value = true
  mensaje.value = ''
  try {
    await loginUsuario(credenciales)
    router.replace(destino())
  } catch (e) {
    // 403: la contraseña es correcta pero la cuenta aun no confirmo su correo
    if (e?.status === 403) {
      router.push({ path: '/verificar', query: { correo: credenciales.correo } })
      return
    }
    mensaje.value = mensajeDeError(e)
    esError.value = true
  } finally {
    credenciales.contrasena = ''
    enviando.value = false
  }
}
</script>
<style scoped src= "./sign-in-style.css"></style>
<style scoped src= "./cuenta/cuenta-style.css"></style>
