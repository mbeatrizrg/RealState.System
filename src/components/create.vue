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
        <button @click="signIn" class="inc-btn">
          <span>Iniciar Sesión</span>
        </button>

      </nav>
    </header>

    <!-- Contenido principal -->
    <main class="main-content">
      <div class="vision-container">
          <article class="image-article">
            <div class="image-4">
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>
          </article>
        </div>

      <div class="form-section">

        <form class="register-form" @submit.prevent="Registrar">

          <div class="form-row">
            <div class="form-group half-width">
              <label for="firstName">Nombre</label>
              <input
                type="text"
                id="firstName"
                placeholder="Rifat"
                v-model.trim="usuario.nombre"
                required
              />
            </div>

            <div class="form-group half-width">
              <label for="lastName">Apellido</label>
              <input
                type="text"
                id="lastName"
                placeholder="Richani"
                v-model.trim="usuario.apellido"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="gmail">Gmail</label>
            <input
              type="email"
              id="gmail"
              placeholder="ejemplo@gmail.com"
              v-model.trim="usuario.correo"
              required
            />
          </div>

          <!-- El telefono es obligatorio en la tabla Usuario de Supabase -->
          <div class="form-group">
            <label for="phone">Teléfono</label>
            <input
              type="tel"
              id="phone"
              placeholder="+58 414-555 5555"
              v-model.trim="usuario.telefono"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <div class="password-container">
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="usuario.contrasena"
                placeholder="8 caracteres"
                class="password-field"
                minlength="8"
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
            {{ enviando ? 'Creando cuenta...' : 'Crear Cuenta' }}
          </button>
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
import { useRouter } from 'vue-router'

import { mensajeDeError } from '@/services/api'
import { registrarUsuario } from '@/services/usuarios'

const router = useRouter()

const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}
const signIn = ()=> {
  router.push('/signin')
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

const showPassword = ref(false)

const togglePassword = ()=> {
  showPassword.value = !showPassword.value
}

// Coincide con UsuarioRegistroDTO del backend
const usuario = reactive({ nombre: '', apellido: '', correo: '', telefono: '', contrasena: '' })
const enviando = ref(false)
const mensaje = ref('')
const esError = ref(false)

const Registrar = async ()=> {
  enviando.value = true
  mensaje.value = ''
  try {
    mensaje.value = await registrarUsuario(usuario)
    esError.value = false
    // Si el backend exige verificar el correo (lo indica en el mensaje) se pasa a ingresar el codigo
    const correo = usuario.correo
    const siguiente = mensaje.value.includes('código') ? '/verificar' : '/signin'
    setTimeout(() => router.push({ path: siguiente, query: { correo } }), 1500)
  } catch (e) {
    mensaje.value = mensajeDeError(e)
    esError.value = true
  } finally {
    enviando.value = false
  }
}
</script>
<style scoped src= "./create-style.css"></style>
<style scoped src= "./cuenta/cuenta-style.css"></style>
