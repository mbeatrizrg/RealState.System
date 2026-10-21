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
        <router-link to="/create" class="inc-btn">Crear cuenta</router-link>

      </nav>
    </header>

    <!-- Contenido principal-->
    <main class="main-content">
      <div class="image-section">
        <img class="hero-image" src="/PhotoPrincipal.png" alt="foto de portada" />
      </div>

      <div class="form-section">
        <h1 class="form-title">Iniciar sesion</h1>

        <form class="login-form" @submit.prevent="IniciarSesion">
          <div class="form-group">
            <label for="email">Correo</label>
            <input
              type="email"
              id="email"
              placeholder="correo@ejemplo.com"
              v-model.trim="credenciales.correo"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input
              type="password"
              id="password"
              placeholder="8 caracteres"
              v-model="credenciales.contrasena"
              required
            />
          </div>

          <p v-if="mensaje" class="form-mensaje">{{ mensaje }}</p>

          <button type="submit" class="btn btn-submit">Iniciar sesion</button>
        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

// Coincide con UsuarioLogeoDTO del backend
const credenciales = reactive({ correo: '', contrasena: '' })
const mensaje = ref('')

// PENDIENTE: conectar con POST /api/auth/login cuando el backend tenga autenticacion JWT
const IniciarSesion = ()=> {
  mensaje.value = 'El inicio de sesión estará disponible muy pronto.'
}
</script>

<style scoped src= "./sign-in-style.css"></style>
