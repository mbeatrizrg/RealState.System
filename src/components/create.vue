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
        <router-link to="/menu" class="menu-btn">Menu</router-link>
        <router-link to="/signin" class="inc-btn">Iniciar Sesión</router-link>

      </nav>
    </header>

    <!-- Contenido principal -->
    <main class="main-content">
      <div class="image-section">
        <img class="hero-image" src="/PhotoPrincipal.png" alt="foto de portada" />
      </div>

      <div class="form-section">
        <h1 class="form-title">Crea una cuenta</h1>

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
            <label for="email">Correo</label>
            <input
              type="email"
              id="email"
              placeholder="correo@ejemplo.com"
              v-model.trim="usuario.correo"
              required
            />
          </div>

          <div class="form-group">
            <label for="phone">Teléfono</label>
            <input
              type="tel"
              id="phone"
              placeholder="+58 414-555 5555"
              v-model.trim="usuario.telefono"
            />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input
              type="password"
              id="password"
              placeholder="8 caracteres"
              minlength="8"
              v-model="usuario.contrasena"
              required
            />
          </div>

          <p v-if="mensaje" class="form-mensaje" :class="{ 'form-mensaje-error': esError }">{{ mensaje }}</p>

          <button type="submit" class="btn btn-submit" :disabled="enviando">
            {{ enviando ? 'Creando cuenta...' : 'Crear cuenta' }}
          </button>
        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { mensajeDeError } from '@/services/api'
import { registrarUsuario } from '@/services/usuarios'

const router = useRouter()

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
    setTimeout(() => router.push('/signin'), 1500)
  } catch (e) {
    mensaje.value = mensajeDeError(e)
    esError.value = true
  } finally {
    enviando.value = false
  }
}
</script>

<style scoped src= "./create-style.css"></style>
