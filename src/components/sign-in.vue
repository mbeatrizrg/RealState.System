<template>
  <div class="layout-container">
    <!-- Encabezado[cite: 1] -->
    <header class="CommonHeader">
      <div class="logo">
        <img src="/icon.png" alt="Icono" class="icon" />
        <img src="/brand.png" alt="Rifat Richani" class="brand" />
      </div>
      
      <nav class="primary-nav">
        <!-- cambiar para que se vea como el search btn -->
        <router-link to="/menu" class="menu-btn">Menu</router-link> 
        <button class="inc-btn">Crear cuenta</button>

      </nav>
    </header>

    <!-- Contenido principal-->
    <main class="main-content">
      <div class="image-section">
          <img class="hero-image"  src="/PhotoPrincipal.png" alt="foto de portada" /> 
      </div>

      <div class="form-section">
        <h1 class="form-title">Iniciar sesion</h1>

        <form class="login-form">
          <div class="form-group">
            <label for="phone">teléfono</label>
            <input 
              type="tel" 
              id="phone" 
              placeholder="+58 414-555 5555" 
            />
          </div>

          <div class="form-group">
            <label for="password">Cotraseña</label>
            <input 
              type="password" 
              id="password" 
              placeholder="8 caracteres" 
            />
          </div>

          <button type="submit" class="btn btn-submit">Iniciar sesion</button>
        </form>
      </div>
    </main>
  </div>
</template>

<style scoped src= "./sign-in-style.css"></style>