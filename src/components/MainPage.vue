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
            <label for="locacion">Selecciona tu locación</label>
            <select id="locacion" name="locacion">
              <option value="" disabled selected>Todas las ciudades</option>
              <option value="valencia">Valencia</option>
              <option value="naguanagua">Naguanagua</option>
              <option value="san_diego">San Diego</option>
              <option value="puerto_cabello">Puerto Cabello</option>
              <option value="guacara">Guacara</option>
              <option value="los_guayos">Los Guayos</option>
              <option value="tocuyito">Tocuyito (Libertador)</option>
              <option value="san_joaquin">San Joaquín</option>
              <option value="diego_ibarra">Mariara (Diego Ibarra)</option>
              <option value="bejuma">Bejuma</option>
              <option value="montalban">Montalbán</option>
              <option value="miranda">Miranda</option>
              <option value="carlos_arvelo">Güigüe (Carlos Arvelo)</option>
              <option value="juan_jose_mora">Morón (Juan José Mora)</option>
            </select>
          </div>

          <div class="filter-group">
            <label for="tipo">Tipo de Propiedad</label>
            <select id="tipo" name="tipo">
              <option value="casa">Casa</option>
              <option value="apartamento">Apartamento</option>
              <option value="townhouse">Townhouse</option>
              <option value="terreno">Terreno</option>
              <option value="local comercial">local comercial</option>
              <option value="galpon">galpon</option>
              <option value="oficina">oficina</option>
              <option value="consultorio medico">consultorio medico</option>
              <option value="club">club</option>
              <option value="negocio">negocio</option>
            </select>
          </div>

          <div class="filter-group">
            <label for="precio">Precio</label>
            <input 
            class="filter-input"
              type="number" 
              id="precio" 
              min="0"
              max="100000000"
              name="precio" 
              placeholder="Ej: 15000"
            />
          </div>

          <div class="filter-group">
            <label for="modalidad">Alquiler o Compra</label>
            <select id="modalidad" name="modalidad">
              <option value="compra">Compra</option>
              <option value="alquiler">Alquiler</option>
            </select>
          </div>

          <button class="search-btn">
            <img src="/lupa.svg" alt="" />
            <span>Buscar Propiedad</span>
          </button>
        </div>
      </section>

      <!-- DESTACADOS 2DA PAGINA -->
      <section class="featured-section">
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
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="/location.png" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="card-details">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">$150,000</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

          <!-- TARJETA 2 -->
          <article class="card">
            <div class="card-image-wrapper">
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="/location.png" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="card-details">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">$150,000</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

          <!-- TARJETA 3 -->
          <article class="card">
            <div class="card-image-wrapper">
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="/location.png" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>
              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="card-details">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">$150,000</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

          <!-- TARJETA 4 -->
          <article class="card">
            <div class="card-image-wrapper">
              <img src="/building.png" alt="Residencias Altos Del Mirador" class="building-img" />
            </div>

            <div class="card-content">
              <div class="card-location">
                <img src="/location.png" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="card-details">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="card-footer">
                <span class="card-price">$150,000</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>
        </div>

        <!-- Botón de Navegación del Carrusel -->
        <button class="next-btn" aria-label="Siguiente">
          <img src="/arrow.png" alt="Siguiente" />
        </button>
      </section>

      <!-- SECCIÓN 3 PÁGINA -->
      <section class="info-section">
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

        <div class="section-content">
          <h1 class="principal-text">24 Años Transformando Espacios</h1>
          
          <p class="little-text">
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