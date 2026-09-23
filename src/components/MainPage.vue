<template>
  <div class="page1">
    <!-- primera pagina header -->
    <header class="CommonHeader">
      <div class="logo">
        <img src="/icon.png" alt="Icono" class="icon" />
        <img src="/brand.png" alt="Rifat Richani" class="brand" />
      </div>
      
      <nav class="primary-nav">
        <button class="menu-btn">Menu</button>
        <button class="inc-btn">Iniciar Sesión</button>

      </nav>
    </header>

    <!-- Sección principal -->
    <main>
      <section class="background">
        <!-- Fondo 1 -->
        <div class="imagen">
          <img src="/PhotoPrincipal.png" alt="foto de portada" />
        </div>

        <!-- Menú flotante -->
        <aside class="side-menu">
            <button class="icon-btn"><img src="/arrow.png" alt="flecha" class="icon-arrow" /></button>
            <button class="icon-btn"><img src="/phone.png" alt="telefono" class="icon-phone" /></button>
            <button class="icon-btn"><img src="/gmail.png" alt="gmail" class="icon-gmail" /></button>
            <button class="icon-btn"><img src="/whatsapp.svg" alt="whatsapp" class="icon-whatsapp" /></button>
        </aside>

        <!-- Barra de filtros -->
        <div class="filter-bar">
          <div class="filter-group">
            <label>Selecciona tu locación</label>
            <select name="locacion">
              <option value="valencia">Valencia</option>
            </select>
          </div>

          <div class="filter-group">
            <label>Tipo de Propiedad</label>
            <select name="tipo">
              <option value="casa">Casa</option>
            </select>
          </div>

          <div class="filter-group">
            <label>Precio</label>
            <select name="precio">
              <option value="150000">150.000$</option>
            </select>
          </div>

          <div class="filter-group">
            <label>Alquiler o Compra</label>
            <select name="modalidad">
              <option value="compra">Compra</option>
            </select>
          </div>

          <button class="btn-search">
            <img src="https://via.placeholder.com/20" alt="" />
            Buscar Propiedad
          </button>
        </div>
      </section>

      <!-- DESTACADOS 2DA PAGINA -->
      <section class="destacados-section">
        <!-- Encabezado de la sección -->
        <header class="section-header">
          <h2>Destacados</h2>
          <p>Selecciona tu locación</p>
        </header>

        <!-- Contenedor del Grid/Carrusel de tarjetas -->
        <div class="cards-grid">
          <!-- TARJETA 1 -->
          <article class="card">
            <div class="card-image-wrapper">
              <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/unnamed-1-3.png" alt="Residencias Altos Del Mirador" class="card-img" />
              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/heart.svg" alt="Favorito" />
              </button>
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/location-on.svg" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="card-details">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">$150,000</span>
                <a href="#" class="btn-detalles">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

          <!-- TARJETA 2 -->
          <article class="card">
            <div class="card-image-wrapper">
              <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/unnamed-1-3-1.png" alt="Residencias Altos Del Mirador" class="card-img" />
              <button class="favorite-btn active" aria-label="Añadir a favoritos">
                <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/heart-3.svg" alt="Favorito" />
              </button>
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/location-on.svg" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="card-details">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">$150,000</span>
                <a href="#" class="btn-detalles">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

          <!-- TARJETA 3 -->
          <article class="card">
            <div class="card-image-wrapper">
              <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/unnamed-1-3-2.png" alt="Residencias Altos Del Mirador" class="card-img" />
              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/heart-1.svg" alt="Favorito" />
              </button>
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/location-on.svg" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="card-details">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">$150,000</span>
                <a href="#" class="btn-detalles">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

          <!-- TARJETA 4 -->
          <article class="card">
            <div class="card-image-wrapper">
              <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/unnamed-1-3-3.png" alt="Residencias Altos Del Mirador" class="card-img" />
              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/heart-1.svg" alt="Favorito" />
              </button>
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/location-on.svg" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="card-details">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">$150,000</span>
                <a href="#" class="btn-detalles">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>
        </div>

        <!-- Botón de Navegación del Carrusel -->
        <button class="carousel-next-btn" aria-label="Siguiente">
          <img src="https://c.animaapp.com/5BXuPLFojmZUII9wyDPqxg/img/boton1.svg" alt="Siguiente" />
        </button>
      </section>

      <!-- SECCIÓN 3 PÁGINA -->
      <section class="info-section">
        <header class="head">
          <div class="logo"></div>
          
          <img 
            class="nombre-marca" 
            src="https://c.animaapp.com/L565bYk0ekRFb-3uewjQIg/img/nombre-marca.png" 
            alt="Rifat Richani" 
          />

          <nav class="barra-menu">
            <span class="text-wrapper-2">Menu</span>
            <button class="barra-inicio-sesion">
              <span class="text-wrapper">Iniciar Sesion</span>
            </button>
          </nav>
        </header>

        <div class="section-content">
          <h1 class="text-wrapper-3">24 Años Transformando Espacios</h1>
          
          <p class="durante-m-s-de-a">
            "Durante más de 15 años hemos trabajado para que tus proyectos sean materializados. Logrando consolidar cientos de hogares y establecimientos comerciales, atendiendo sus requerimientos de la mano de nuestro equipo de trabajo."
          </p>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
// Lógica de JavaScript / Vue (si es necesaria)
</script>

<style scoped>
/* Los estilos específicos pueden ir aquí o importarse desde un archivo global */
</style>