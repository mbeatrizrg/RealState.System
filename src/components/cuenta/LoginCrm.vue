<template>
  <PlantillaCuenta titulo="Acceso de asesores (CRM)">
    <form class="login-form" @submit.prevent="IniciarSesion">
      <div class="form-group">
        <label for="gmail">Correo corporativo</label>
        <input
          type="email"
          id="gmail"
          placeholder="asesor@rifatrichani.com"
          autocomplete="username"
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
            placeholder="Contraseña"
            class="password-field"
            autocomplete="current-password"
            required
          />
          <span class="box-password" :class="{ active: showPassword }" @click="showPassword = !showPassword"></span>
        </div>
      </div>

      <p v-if="mensaje" class="form-mensaje form-mensaje-error">{{ mensaje }}</p>

      <button type="submit" class="btn btn-submit" :disabled="enviando">
        {{ enviando ? 'Ingresando...' : 'Ingresar al CRM' }}
      </button>

      <div class="enlaces-cuenta">
        <router-link :to="{ path: '/recuperar', query: { tipo: 'empleado' } }" class="enlace">¿Olvidaste tu contraseña?</router-link>
        <router-link to="/signin" class="enlace">Soy cliente</router-link>
      </div>
    </form>
  </PlantillaCuenta>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import PlantillaCuenta from './PlantillaCuenta.vue'
import { mensajeDeError } from '@/services/api'
import { loginEmpleado } from '@/services/auth'

const route = useRoute()
const router = useRouter()

// Coincide con UsuarioLogeoDTO del backend
const credenciales = reactive({ correo: typeof route.query.correo === 'string' ? route.query.correo : '', contrasena: '' })
const showPassword = ref(false)
const mensaje = ref(route.query.expirada ? 'Tu sesión expiró. Inicia sesión de nuevo.' : '')
const enviando = ref(false)

// Solo se vuelve a rutas internas del sitio (evita redirecciones a otros dominios)
const destino = () => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') && !redirect.startsWith('//') && !redirect.includes('\\') ? redirect : '/vendedor'
}

const IniciarSesion = async () => {
  enviando.value = true
  mensaje.value = ''
  try {
    await loginEmpleado(credenciales)
    router.replace(destino())
  } catch (e) {
    mensaje.value = mensajeDeError(e)
  } finally {
    credenciales.contrasena = ''
    enviando.value = false
  }
}
</script>

<style scoped src="../sign-in-style.css"></style>
<style scoped src="./cuenta-style.css"></style>
