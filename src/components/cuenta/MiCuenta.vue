<template>
  <PlantillaCuenta :titulo="perfil ? `Hola, ${perfil.nombre}` : 'Mi cuenta'" scroll>
    <p v-if="cargando" class="form-mensaje">Cargando tu cuenta...</p>
    <p v-else-if="errorCarga" class="form-mensaje form-mensaje-error">{{ errorCarga }}</p>

    <template v-else-if="perfil">
      <!-- Datos personales -->
      <section class="bloque-cuenta">
        <h2 class="subtitulo">Mis datos</h2>
        <form class="login-form" @submit.prevent="GuardarDatos">
          <div class="form-group">
            <label for="nombre">Nombre</label>
            <input type="text" id="nombre" v-model.trim="datos.nombre" maxlength="100" required />
          </div>
          <div class="form-group">
            <label for="apellido">Apellido</label>
            <input type="text" id="apellido" v-model.trim="datos.apellido" maxlength="100" required />
          </div>
          <div class="form-group">
            <label for="phone">Teléfono</label>
            <input type="tel" id="phone" v-model.trim="datos.telefono" minlength="10" maxlength="20" required />
          </div>
          <div class="form-group">
            <label for="negocio">¿Qué estás buscando?</label>
            <select id="negocio" v-model="datos.tipoNegocio">
              <option value="Sin definir">Sin definir</option>
              <option value="Compra">Comprar</option>
              <option value="Alquiler">Alquilar</option>
              <option value="Venta">Vender</option>
            </select>
          </div>

          <p v-if="avisos.datos.texto" class="form-mensaje" :class="{ 'form-mensaje-error': avisos.datos.error }">{{ avisos.datos.texto }}</p>
          <button type="submit" class="btn btn-submit" :disabled="ocupado">Guardar cambios</button>
        </form>
      </section>

      <!-- Cambio de correo (requiere confirmar un codigo enviado al correo nuevo) -->
      <section class="bloque-cuenta">
        <h2 class="subtitulo">Correo</h2>
        <p class="texto-ayuda">Correo actual: <strong>{{ perfil.correo }}</strong></p>

        <form v-if="!esperandoCodigoCorreo" class="login-form" @submit.prevent="SolicitarCambioCorreo">
          <div class="form-group">
            <label for="correoNuevo">Correo nuevo</label>
            <input type="email" id="correoNuevo" v-model.trim="cambioCorreo.correoNuevo" required />
          </div>
          <div class="form-group">
            <label for="claveCorreo">Contraseña actual</label>
            <input type="password" id="claveCorreo" v-model="cambioCorreo.contrasena" autocomplete="current-password" required />
          </div>
          <p v-if="avisos.correo.texto" class="form-mensaje" :class="{ 'form-mensaje-error': avisos.correo.error }">{{ avisos.correo.texto }}</p>
          <button type="submit" class="btn btn-submit" :disabled="ocupado">Cambiar correo</button>
        </form>

        <form v-else class="login-form" @submit.prevent="ConfirmarCambioCorreo">
          <p class="texto-ayuda">Enviamos un código a <strong>{{ perfil.correoPendiente || cambioCorreo.correoNuevo }}</strong>.</p>
          <div class="form-group">
            <label for="codigoCorreo">Código</label>
            <input
              type="text"
              id="codigoCorreo"
              inputmode="numeric"
              autocomplete="one-time-code"
              maxlength="6"
              pattern="\d{6}"
              v-model.trim="codigoCorreo"
              required
            />
          </div>
          <p v-if="avisos.correo.texto" class="form-mensaje" :class="{ 'form-mensaje-error': avisos.correo.error }">{{ avisos.correo.texto }}</p>
          <button type="submit" class="btn btn-submit" :disabled="ocupado">Confirmar correo</button>
          <div class="enlaces-cuenta">
            <button type="button" class="enlace" @click="esperandoCodigoCorreo = false">Usar otro correo</button>
          </div>
        </form>
      </section>

      <!-- Cambio de contraseña (cierra la sesion en todos los dispositivos) -->
      <section class="bloque-cuenta">
        <h2 class="subtitulo">Contraseña</h2>
        <form class="login-form" @submit.prevent="CambiarContrasena">
          <div class="form-group">
            <label for="claveActual">Contraseña actual</label>
            <input type="password" id="claveActual" v-model="claves.actual" autocomplete="current-password" required />
          </div>
          <div class="form-group">
            <label for="claveNueva">Contraseña nueva</label>
            <input type="password" id="claveNueva" v-model="claves.nueva" minlength="8" maxlength="72" autocomplete="new-password" required />
          </div>
          <div class="form-group">
            <label for="claveRepetida">Repite la contraseña nueva</label>
            <input type="password" id="claveRepetida" v-model="claves.repetida" autocomplete="new-password" required />
          </div>
          <p v-if="avisos.clave.texto" class="form-mensaje" :class="{ 'form-mensaje-error': avisos.clave.error }">{{ avisos.clave.texto }}</p>
          <button type="submit" class="btn btn-submit" :disabled="ocupado">Cambiar contraseña</button>
        </form>
      </section>

      <section class="bloque-cuenta">
        <button type="button" class="btn btn-submit btn-secundario" @click="CerrarSesion">Cerrar sesión</button>
      </section>
    </template>
  </PlantillaCuenta>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import PlantillaCuenta from './PlantillaCuenta.vue'
import { mensajeDeError } from '@/services/api'
import {
  actualizarPerfil,
  cambiarContrasena,
  cerrarSesion,
  confirmarCambioCorreo,
  obtenerPerfilSesion,
  solicitarCambioCorreo,
} from '@/services/auth'

const router = useRouter()

const perfil = ref(null)
const cargando = ref(true)
const errorCarga = ref('')
const ocupado = ref(false)

const datos = reactive({ nombre: '', apellido: '', telefono: '', tipoNegocio: 'Sin definir' })
const cambioCorreo = reactive({ correoNuevo: '', contrasena: '' })
const codigoCorreo = ref('')
const esperandoCodigoCorreo = ref(false)
const claves = reactive({ actual: '', nueva: '', repetida: '' })
const avisos = reactive({
  datos: { texto: '', error: false },
  correo: { texto: '', error: false },
  clave: { texto: '', error: false },
})

const cargarPerfil = async () => {
  perfil.value = await obtenerPerfilSesion()
  Object.assign(datos, {
    nombre: perfil.value.nombre,
    apellido: perfil.value.apellido ?? '',
    telefono: perfil.value.telefono ?? '',
    tipoNegocio: perfil.value.tipoNegocio ?? 'Sin definir',
  })
  esperandoCodigoCorreo.value = !!perfil.value.correoPendiente
}

onMounted(async () => {
  try {
    await cargarPerfil()
  } catch (e) {
    errorCarga.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
})

// Ejecuta una accion mostrando su resultado en el aviso indicado
const ejecutar = async (aviso, accion) => {
  ocupado.value = true
  aviso.texto = ''
  try {
    aviso.texto = await accion()
    aviso.error = false
  } catch (e) {
    aviso.texto = mensajeDeError(e)
    aviso.error = true
  } finally {
    ocupado.value = false
  }
}

const GuardarDatos = () =>
  ejecutar(avisos.datos, async () => {
    await actualizarPerfil({ ...datos })
    await cargarPerfil()
    return 'Datos actualizados.'
  })

const SolicitarCambioCorreo = () =>
  ejecutar(avisos.correo, async () => {
    const { mensaje } = await solicitarCambioCorreo(cambioCorreo.correoNuevo, cambioCorreo.contrasena)
    cambioCorreo.contrasena = ''
    esperandoCodigoCorreo.value = true
    return mensaje
  })

const ConfirmarCambioCorreo = () =>
  ejecutar(avisos.correo, async () => {
    const { mensaje } = await confirmarCambioCorreo(codigoCorreo.value)
    codigoCorreo.value = ''
    cambioCorreo.correoNuevo = ''
    await cargarPerfil()
    return mensaje
  })

const CambiarContrasena = async () => {
  if (claves.nueva !== claves.repetida) {
    avisos.clave.texto = 'Las contraseñas nuevas no coinciden.'
    avisos.clave.error = true
    return
  }
  await ejecutar(avisos.clave, async () => {
    const { mensaje } = await cambiarContrasena('USUARIO', claves.actual, claves.nueva)
    // El backend cierra todas las sesiones: se vuelve a iniciar sesion
    setTimeout(() => router.push('/signin'), 1500)
    return mensaje
  })
  claves.actual = ''
  claves.nueva = ''
  claves.repetida = ''
}

const CerrarSesion = async () => {
  await cerrarSesion()
  router.push('/')
}
</script>

<style scoped src="../sign-in-style.css"></style>
<style scoped src="./cuenta-style.css"></style>
