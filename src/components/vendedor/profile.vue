<template>
  <div class="leads-page">
    <!-- Barra lateral (Sidebar) -->
    <aside class="sidebar">
      <div class="logo2-container">
        <img src="/Logo2.png" alt="Icono2" class="logo" />
      </div>

      <nav class="menu">
        <ul>
          <!-- Elemento activo resaltado -->
          <li class="menu-item">
            <button class="btn-menu" @click="vendedor">Inventario</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="contactos">Contactos</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Leads</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Comunicaciones</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="control">Control</button>
          </li>
          <li class="menu-item">
            <button class="btn-menu" @click="Leads">Calendario</button>
          </li>
          <li class="menu-item active">
            <button class="btn-menu" @click="profile">Perfil</button>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- Contenido Principal -->
    <main class="main-content">
      <!-- Cabecera con saludo y botón de filtros -->
      <div class="header-top">
        <!-- Usamos interpolación de Vue para el nombre dinámico -->
        <!-- Círculo de la foto -->
        <div class="profile-avatar" @click="seleccionarFoto">
          <img v-if="fotoUrl" :src="fotoUrl" alt="Foto de perfil" class="avatar-img" />
          <div v-else class="avatar-placeholder">
            <span>Subir foto</span>
          </div>
        </div>

          <!-- Botón para quitar la foto (solo se muestra si hay foto) -->
        <button 
          v-if="fotoUrl" 
          type="button" 
          class="btn-remove-photo" 
          @click="eliminarFoto"
          >
          Quitar foto
        </button>

        <!-- Input oculto para cargar archivo de imagen -->
        <input 
          ref="fileInput" 
          type="file" 
          accept="image/*" 
          style="display: none;" 
          @change="alCambiarFoto" 
          />
        <button class="btn-filtros">
          Filtros
          <svg class="icon-chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
    </div>

      <h1 class="greeting"> Hola, {{ datosPerfil.saludo }}</h1>
      <p v-if="errorCarga" class="mensaje-perfil mensaje-error">{{ errorCarga }}</p>
      <section class="profile-form">
        <!--Fila 1-->
        <div class="form-row">
          <div class="form-group full-width">
            <label>Nombre</label>
            <input 
              type="text" 
              v-model="datosPerfil.nombre" 
              class="input-dark" 
              readonly
            />
          </div>
        </div>

        <!--FIla 2-->
        <div class="form-row">
          <div class="form-group short-width">
            <label>ID</label>
            <input
              type="text"  
              :value="datosPerfil.id"
              class="input-dark"
              readonly
            />
          </div>
          <div class="form-group flex-grow">
              <label for="gmail">Gmail</label>
            <input
              type="email"  
              :value="datosPerfil.gmail"
              id="gmail" 
              class="input-dark"
              readonly
            />
        </div>
      </div>

      <!--Fila 3: en Supabase el empleado no tiene telefono, se muestra la cedula y el rol-->
      <div class="form-row">
          <div class="form-group short-width">
            <label>Cédula</label>
            <input 
              type="text" 
              :value="datosPerfil.cedula"
              class="input-dark" 
              readonly
            />
          </div>

          <div class="form-group short-width">
            <label>Rol</label>
            <input
              class="input-dark" 
              type="text" 
              :value="datosPerfil.rol"
              readonly
            />
          </div>
        </div>
      </section>

      <!-- Cambio de contraseña: al guardar se cierran todas las sesiones y se vuelve al login -->
      <section class="profile-form">
        <h2 class="subtitulo-perfil">Cambiar contraseña</h2>
        <form @submit.prevent="CambiarContrasena">
          <div class="form-row">
            <div class="form-group short-width">
              <label for="claveActual">Contraseña actual</label>
              <input id="claveActual" type="password" v-model="claves.actual" class="input-dark" autocomplete="current-password" required />
            </div>
            <div class="form-group short-width">
              <label for="claveNueva">Contraseña nueva</label>
              <input id="claveNueva" type="password" v-model="claves.nueva" class="input-dark" minlength="8" maxlength="72" autocomplete="new-password" required />
            </div>
            <div class="form-group short-width">
              <label for="claveRepetida">Repetir contraseña</label>
              <input id="claveRepetida" type="password" v-model="claves.repetida" class="input-dark" autocomplete="new-password" required />
            </div>
          </div>
          <p v-if="mensajeClave" class="mensaje-perfil" :class="{ 'mensaje-error': errorClave }">{{ mensajeClave }}</p>
          <div class="acciones-perfil">
            <button type="submit" class="btn-perfil" :disabled="guardando">Guardar contraseña</button>
            <button type="button" class="btn-perfil btn-perfil-secundario" @click="CerrarSesion">Cerrar sesión</button>
          </div>
        </form>
      </section>
    </main>
  </div>
</template>


<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { mensajeDeError } from '@/services/api'
import { cambiarContrasena, cerrarSesion, obtenerPerfilSesion } from '@/services/auth'

const router = useRouter()

// Datos del empleado con sesion iniciada (GET /api/auth/me)
const datosPerfil = ref({ saludo: '', nombre: '', id: '', gmail: '', cedula: '', rol: '' })
const errorCarga = ref('')

onMounted(async () => {
  try {
    const perfil = await obtenerPerfilSesion()
    datosPerfil.value = {
      saludo: perfil.nombre,
      nombre: [perfil.nombre, perfil.apellido].filter(Boolean).join(' '),
      id: perfil.id,
      gmail: perfil.correo,
      cedula: perfil.cedula ?? '',
      rol: perfil.esAdmin ? 'Administrador' : 'Vendedor',
    }
  } catch (e) {
    errorCarga.value = mensajeDeError(e)
  }
})

const claves = reactive({ actual: '', nueva: '', repetida: '' })
const mensajeClave = ref('')
const errorClave = ref(false)
const guardando = ref(false)

const CambiarContrasena = async () => {
  if (claves.nueva !== claves.repetida) {
    mensajeClave.value = 'Las contraseñas nuevas no coinciden.'
    errorClave.value = true
    return
  }
  guardando.value = true
  try {
    mensajeClave.value = (await cambiarContrasena('EMPLEADO', claves.actual, claves.nueva)).mensaje
    errorClave.value = false
    // El backend cierra todas las sesiones del empleado: se vuelve al login del CRM
    setTimeout(() => router.push('/crm/login'), 1500)
  } catch (e) {
    mensajeClave.value = mensajeDeError(e)
    errorClave.value = true
  } finally {
    claves.actual = ''
    claves.nueva = ''
    claves.repetida = ''
    guardando.value = false
  }
}

const CerrarSesion = async () => {
  await cerrarSesion()
  router.push('/crm/login')
}

const Leads = () => {
  router.push('/leads')
}

const vendedor = () => {
  router.push('/vendedor')
}

const contactos = () => {
  router.push('/contact')
}

const control = ()=> {
  router.push('/control')
}

const profile = () => {
  router.push('/profile')
}

const fotoUrl = ref(null)
const fileInput = ref(null)

// Abrir el selector de archivos al hacer clic en el círculo
const seleccionarFoto = () => {
  fileInput.value.click()
}

// Cargar la foto seleccionada
const alCambiarFoto = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      fotoUrl.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// Quitar / borrar la foto
const eliminarFoto = () => {
  fotoUrl.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

</script>

<style scoped src="./profile.css"></style>
