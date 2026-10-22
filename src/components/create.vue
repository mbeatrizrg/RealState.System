<template>
  <PlantillaCuenta
    titulo="Crea tu cuenta"
    subtitulo="Regístrate para guardar favoritos y recibir asesoría personalizada."
    ancha
    :accion="{ texto: 'Iniciar Sesión', ruta: '/signin' }"
  >
    <form class="register-form" novalidate @submit.prevent="Registrar">
      <div class="form-row">
        <div class="form-group">
          <label for="firstName">Nombre</label>
          <input type="text" id="firstName" placeholder="Rifat" autocomplete="given-name" maxlength="100" v-model.trim="usuario.nombre" required />
        </div>

        <div class="form-group">
          <label for="lastName">Apellido</label>
          <input type="text" id="lastName" placeholder="Richani" autocomplete="family-name" maxlength="100" v-model.trim="usuario.apellido" required />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="cedula">Cédula de identidad</label>
          <input
            type="text"
            id="cedula"
            inputmode="numeric"
            placeholder="12345678"
            maxlength="12"
            v-model="usuario.cedula"
            @input="limpiarCedula"
            required
          />
          <span class="campo-ayuda">Solo números, sin V- ni puntos</span>
        </div>

        <!-- El telefono es obligatorio en la tabla Usuario de Supabase -->
        <div class="form-group">
          <label for="phone">Teléfono</label>
          <input type="tel" id="phone" placeholder="0414-555 5555" autocomplete="tel" maxlength="20" v-model.trim="usuario.telefono" required />
        </div>
      </div>

      <div class="form-group">
        <label for="gmail">Correo electrónico</label>
        <input type="email" id="gmail" placeholder="ejemplo@gmail.com" autocomplete="email" maxlength="255" v-model.trim="usuario.correo" required />
        <span class="campo-ayuda">Te enviaremos un código para confirmar tu cuenta</span>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="password">Contraseña</label>
          <div class="password-container">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="usuario.contrasena"
              placeholder="Mínimo 8 caracteres"
              autocomplete="new-password"
              class="password-field"
              minlength="8"
              maxlength="72"
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
          <div class="fuerza" :data-nivel="fuerza.nivel" aria-hidden="true"><span></span><span></span><span></span><span></span></div>
          <span class="campo-ayuda">{{ usuario.contrasena ? `Seguridad: ${fuerza.texto}` : 'Usa letras, números y símbolos' }}</span>
        </div>

        <div class="form-group">
          <label for="password2">Confirmar contraseña</label>
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password2"
            v-model="confirmacion"
            placeholder="Repite la contraseña"
            autocomplete="new-password"
            required
          />
          <span v-if="confirmacion && confirmacion !== usuario.contrasena" class="campo-ayuda error">Las contraseñas no coinciden</span>
        </div>
      </div>

      <p v-if="mensaje" class="form-mensaje" :class="{ 'form-mensaje-error': esError }" role="alert">{{ mensaje }}</p>

      <button type="submit" class="btn btn-submit" :disabled="enviando">
        {{ enviando ? 'Creando cuenta...' : 'Crear cuenta' }}
      </button>

      <p class="pie-form">
        ¿Ya tienes una cuenta?
        <router-link to="/signin" class="enlace-destacado">Inicia sesión</router-link>
      </p>
    </form>
  </PlantillaCuenta>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import PlantillaCuenta from '@/components/cuenta/PlantillaCuenta.vue'
import { mensajeDeError } from '@/services/api'
import { registrarUsuario } from '@/services/usuarios'

const router = useRouter()

const showPassword = ref(false)

const togglePassword = ()=> {
  showPassword.value = !showPassword.value
}

// Coincide con UsuarioRegistroDTO del backend (la cedula se envia como numero)
const usuario = reactive({ nombre: '', apellido: '', cedula: '', telefono: '', correo: '', contrasena: '' })
const confirmacion = ref('')
const enviando = ref(false)
const mensaje = ref('')
const esError = ref(false)

// Acepta "V-12.345.678" pegado y deja solo los digitos
const limpiarCedula = () => {
  usuario.cedula = usuario.cedula.replace(/\D/g, '').slice(0, 9)
}

// Nivel orientativo para el usuario (el backend solo exige 8 a 72 caracteres)
const fuerza = computed(() => {
  const clave = usuario.contrasena
  if (!clave) return { nivel: 0, texto: '' }
  let puntos = 0
  if (clave.length >= 8) puntos++
  if (clave.length >= 12) puntos++
  if (/[a-z]/.test(clave) && /[A-Z]/.test(clave)) puntos++
  if (/\d/.test(clave) && /[^A-Za-z0-9]/.test(clave)) puntos++
  const nivel = clave.length < 8 ? 1 : Math.max(1, puntos)
  return { nivel, texto: ['', 'débil', 'aceptable', 'buena', 'excelente'][nivel] }
})

// Revisa en el navegador lo mismo que valida el backend, para avisar sin esperar la respuesta
function validar() {
  if (!usuario.nombre || !usuario.apellido) return 'Ingresa tu nombre y apellido.'
  if (!/^\d{5,9}$/.test(usuario.cedula)) return 'La cédula debe tener entre 5 y 9 números.'
  if (usuario.telefono.length < 10) return 'El teléfono debe tener al menos 10 caracteres.'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(usuario.correo)) return 'Ingresa un correo electrónico válido.'
  if (usuario.contrasena.length < 8) return 'La contraseña debe tener al menos 8 caracteres.'
  if (usuario.contrasena !== confirmacion.value) return 'Las contraseñas no coinciden.'
  return ''
}

const Registrar = async ()=> {
  const error = validar()
  if (error) {
    mensaje.value = error
    esError.value = true
    return
  }
  enviando.value = true
  mensaje.value = ''
  try {
    mensaje.value = await registrarUsuario({ ...usuario, cedula: Number(usuario.cedula) })
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
<style scoped src= "./sign-in-style.css"></style>
<style scoped src= "./cuenta/cuenta-style.css"></style>
