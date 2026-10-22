<template>
  <PlantillaCuenta
    titulo="Bienvenido de nuevo"
    subtitulo="Inicia sesión para guardar tus propiedades favoritas y hablar con un asesor."
    :accion="{ texto: 'Crear cuenta', ruta: '/create' }"
  >
    <form class="login-form" novalidate @submit.prevent="IniciarSesion">
      <div class="form-group">
        <label for="gmail">Correo electrónico</label>
        <input
          type="email"
          id="gmail"
          placeholder="ejemplo@gmail.com"
          autocomplete="email"
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
            placeholder="Tu contraseña"
            autocomplete="current-password"
            class="password-field"
            required
          />
          <button
            type="button"
            class="box-password"
            :class="{ active: showPassword }"
            :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
            @click="togglePassword"
          ></button>
        </div>
      </div>

      <div class="enlaces-cuenta">
        <router-link to="/crm/login" class="enlace">Soy asesor</router-link>
        <router-link :to="{ path: '/recuperar', query: { correo: credenciales.correo || undefined } }" class="enlace">¿Olvidaste tu contraseña?</router-link>
      </div>

      <p v-if="mensaje" class="form-mensaje" :class="{ 'form-mensaje-error': esError }" role="alert">{{ mensaje }}</p>

      <button type="submit" class="btn btn-submit" :disabled="enviando">
        {{ enviando ? 'Ingresando...' : 'Iniciar sesión' }}
      </button>

      <p class="separador">¿Primera vez aquí?</p>
      <p class="pie-form">
        <router-link to="/create" class="enlace-destacado">Crea una cuenta gratis</router-link>
      </p>
    </form>
  </PlantillaCuenta>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import PlantillaCuenta from '@/components/cuenta/PlantillaCuenta.vue'
import { mensajeDeError } from '@/services/api'
import { loginUsuario } from '@/services/auth'

const route = useRoute()
const router = useRouter()

const showPassword = ref(false)

const togglePassword = ()=> {
  showPassword.value = !showPassword.value
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
  if (!credenciales.correo || !credenciales.contrasena) {
    mensaje.value = 'Ingresa tu correo y tu contraseña.'
    esError.value = true
    return
  }
  enviando.value = true
  mensaje.value = ''
  try {
    await loginUsuario(credenciales)
    router.replace(destino())
  } catch (e) {
    // 403 "Debe confirmar su correo...": la contraseña es correcta pero la cuenta aun no confirmo su correo.
    // Otros 403 (ej. el servidor rechaza el origen por CORS) se muestran como error
    if (e?.status === 403 && /confirmar/i.test(e.message)) {
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
