<template>
  <PlantillaCuenta :titulo="esEmpleado ? 'Recuperar acceso al CRM' : 'Recuperar contraseña'">
    <!-- Paso 1: pedir el codigo -->
    <form v-if="paso === 1" class="login-form" @submit.prevent="SolicitarCodigo">
      <p class="texto-ayuda">Escribe el correo de tu cuenta y te enviaremos un código para crear una contraseña nueva.</p>

      <div class="form-group">
        <label for="gmail">Gmail</label>
        <input type="email" id="gmail" placeholder="ejemplo@gmail.com" v-model.trim="correo" required />
      </div>

      <p v-if="mensaje" class="form-mensaje" :class="{ 'form-mensaje-error': esError }">{{ mensaje }}</p>

      <button type="submit" class="btn btn-submit" :disabled="enviando">
        {{ enviando ? 'Enviando...' : 'Enviar código' }}
      </button>

      <div class="enlaces-cuenta">
        <button type="button" class="enlace" @click="paso = 2">Ya tengo un código</button>
        <router-link :to="rutaLogin" class="enlace">Volver a iniciar sesión</router-link>
      </div>
    </form>

    <!-- Paso 2: codigo + contraseña nueva -->
    <form v-else class="login-form" @submit.prevent="Restablecer">
      <p class="texto-ayuda">Si el correo está registrado recibirás un código de 6 dígitos (vence en 15 minutos).</p>

      <div class="form-group">
        <label for="gmail2">Gmail</label>
        <input type="email" id="gmail2" placeholder="ejemplo@gmail.com" v-model.trim="correo" required />
      </div>

      <div class="form-group">
        <label for="codigo">Código</label>
        <input
          type="text"
          id="codigo"
          inputmode="numeric"
          autocomplete="one-time-code"
          maxlength="6"
          pattern="\d{6}"
          placeholder="123456"
          v-model.trim="codigo"
          required
        />
      </div>

      <div class="form-group">
        <label for="password">Contraseña nueva</label>
        <div class="password-container">
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password"
            v-model="contrasenaNueva"
            placeholder="8 caracteres"
            class="password-field"
            minlength="8"
            maxlength="72"
            autocomplete="new-password"
            required
          />
          <span class="box-password" :class="{ active: showPassword }" @click="showPassword = !showPassword"></span>
        </div>
      </div>

      <div class="form-group">
        <label for="password2">Repite la contraseña</label>
        <input
          :type="showPassword ? 'text' : 'password'"
          id="password2"
          v-model="confirmacion"
          placeholder="8 caracteres"
          autocomplete="new-password"
          required
        />
      </div>

      <p v-if="mensaje" class="form-mensaje" :class="{ 'form-mensaje-error': esError }">{{ mensaje }}</p>

      <button type="submit" class="btn btn-submit" :disabled="enviando">
        {{ enviando ? 'Guardando...' : 'Cambiar contraseña' }}
      </button>

      <div class="enlaces-cuenta">
        <button type="button" class="enlace" @click="paso = 1">Pedir otro código</button>
        <router-link :to="rutaLogin" class="enlace">Volver a iniciar sesión</router-link>
      </div>
    </form>
  </PlantillaCuenta>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import PlantillaCuenta from './PlantillaCuenta.vue'
import { mensajeDeError } from '@/services/api'
import { restablecerContrasena, solicitarRecuperacion } from '@/services/auth'

const route = useRoute()
const router = useRouter()

// /recuperar?tipo=empleado se usa desde el login del CRM
const esEmpleado = computed(() => route.query.tipo === 'empleado')
const tipo = computed(() => (esEmpleado.value ? 'EMPLEADO' : 'USUARIO'))
const rutaLogin = computed(() => (esEmpleado.value ? '/crm/login' : '/signin'))

const paso = ref(1)
const correo = ref(typeof route.query.correo === 'string' ? route.query.correo : '')
const codigo = ref('')
const contrasenaNueva = ref('')
const confirmacion = ref('')
const showPassword = ref(false)
const mensaje = ref('')
const esError = ref(false)
const enviando = ref(false)

const SolicitarCodigo = async () => {
  enviando.value = true
  mensaje.value = ''
  try {
    mensaje.value = (await solicitarRecuperacion(correo.value, tipo.value)).mensaje
    esError.value = false
    paso.value = 2
  } catch (e) {
    mensaje.value = mensajeDeError(e)
    esError.value = true
  } finally {
    enviando.value = false
  }
}

const Restablecer = async () => {
  if (contrasenaNueva.value !== confirmacion.value) {
    mensaje.value = 'Las contraseñas no coinciden.'
    esError.value = true
    return
  }
  enviando.value = true
  mensaje.value = ''
  try {
    const respuesta = await restablecerContrasena({
      correo: correo.value,
      tipo: tipo.value,
      codigo: codigo.value,
      contrasenaNueva: contrasenaNueva.value,
    })
    mensaje.value = respuesta.mensaje
    esError.value = false
    setTimeout(() => router.push({ path: rutaLogin.value, query: { correo: correo.value } }), 1500)
  } catch (e) {
    mensaje.value = mensajeDeError(e)
    esError.value = true
  } finally {
    enviando.value = false
  }
}
</script>

<style scoped src="../sign-in-style.css"></style>
<style scoped src="./cuenta-style.css"></style>
