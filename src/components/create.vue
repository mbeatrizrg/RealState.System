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
        <button class="inc-btn">Iniciar Sesión</button>

      </nav>
    </header>

    <!-- Contenido principal -->
    <main class="main-content">
      <div class="vision-container">
          <article class="image-article">
            <div class="image-4">
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>
          </article>
        </div>

      <div class="form-section">

        <form class="register-form">
          
          <div class="form-row">
            <div class="form-group half-width">
              <label for="firstName">Nombre</label>
              <input 
                type="text" 
                id="firstName" 
                placeholder="Rifat" 
              />
            </div>
            
            <div class="form-group half-width">
              <label for="lastName">Apellido</label>
              <input 
                type="text" 
                id="lastName" 
                placeholder="Richani" 
              />
            </div>
          </div>

          <div class="form-group">
            <label for="gmail">Gmail</label>
            <input 
              type="email" 
              id="gmail" 
              placeholder="example@email.com" 
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

          <button type="submit" class="btn btn-submit">Crear Cuenta</button>
        </form>
      </div>
    </main>
  </div>
</template>


<style scoped src= "./create-style.css"></style>