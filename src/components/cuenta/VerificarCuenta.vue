<template>
  <PlantillaCuenta titulo="Confirma tu correo">
    <p class="texto-ayuda">
      Enviamos un código de 6 dígitos a tu correo. Ingrésalo para activar tu cuenta; vence en 15 minutos.
    </p>

    <form class="login-form" @submit.prevent="Confirmar">
      <div class="form-group">
        <label for="gmail">Gmail</label>
        <input type="email" id="gmail" placeholder="ejemplo@gmail.com" v-model.trim="correo" required />
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

      <p v-if="mensaje" class="form-mensaje" :class="{ 'form-mensaje-error': esError }">{{ mensaje }}</p>

      <button type="submit" class="btn btn-submit" :disabled="enviando">
        {{ enviando ? 'Confirmando...' : 'Confirmar cuenta' }}
      </button>

      <div class="enlaces-cuenta">
        <button type="button" class="enlace" :disabled="!correo || reenviando" @click="Reenviar">Reenviar código</button>
        <router-link to="/signin" class="enlace">Ya confirmé mi cuenta</router-link>
      </div>
    </form>
  </PlantillaCuenta>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import PlantillaCuenta from './PlantillaCuenta.vue'
import { mensajeDeError } from '@/services/api'
import { confirmarCuenta, reenviarCodigo } from '@/services/auth'

const route = useRoute()
const router = useRouter()

// El correo llega desde el registro o el login (?correo=...)
const correo = ref(typeof route.query.correo === 'string' ? route.query.correo : '')
const codigo = ref('')
const mensaje = ref('')
const esError = ref(false)
const enviando = ref(false)
const reenviando = ref(false)

const Confirmar = async () => {
  enviando.value = true
  mensaje.value = ''
  try {
    mensaje.value = (await confirmarCuenta(correo.value, codigo.value)).mensaje
    esError.value = false
    setTimeout(() => router.push({ path: '/signin', query: { correo: correo.value } }), 1500)
  } catch (e) {
    mensaje.value = mensajeDeError(e)
    esError.value = true
  } finally {
    enviando.value = false
  }
}

const Reenviar = async () => {
  reenviando.value = true
  try {
    mensaje.value = (await reenviarCodigo(correo.value)).mensaje
    esError.value = false
  } catch (e) {
    mensaje.value = mensajeDeError(e)
    esError.value = true
  } finally {
    reenviando.value = false
  }
}
</script>

<style scoped src="../sign-in-style.css"></style>
<style scoped src="./cuenta-style.css"></style>
