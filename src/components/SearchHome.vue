<template>
  <div class="home-search">
    <!-- Header Principal -->
    <header class="CommonHeader">
      <div class="logo">
        <img src="/icon.png" alt="Icono" class="icon" />
        <img src="/brand.png" alt="Rifat Richani" class="brand" />
      </div>

      <nav class="primary-nav">
        <button class="menu-btn">Menu</button>
        <button class="inc-btn">Iniciar Sesion</button>
      </nav>
    </header>

    <main class="bar">
      <!-- Barra de Filtros / Búsqueda -->
      <section class="background">
        <div class="filter-bar2">
          <div class="filter-group2">
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

          <div class="filter-group2">
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

          <div class="filter-group2">
            <label for="precio">Precio</label>
            <input
            class="filter-input2"
              type="number" 
              id="precio" 
              min="0"
              max="100000000"
              name="precio" 
              placeholder="Ej: 15000"
              />
          </div>

          <div class="filter-group2">
            <label for="modalidad">Alquiler o Compra</label>
            <select id="modalidad" name="modalidad">
              <option value="compra">Compra</option>
              <option value="alquiler">Alquiler</option>
            </select>
          </div>

          <button class="search-btn">
            <img src="/lupa.svg" alt="Buscar" />
            <span>Buscar Propiedad</span>
          </button>
        </div> 
      </section>
    </main>
      <!-- Grid de Tarjetas de Propiedades -->
      <section class="properties-grid">
        <!-- Tarjeta 1 -->
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

        <!-- Tarjeta 2 -->
        <article class="property-card">
          <div class="card-image">
            <img src="/PhotoPrincipal.png" alt="Residencias Altos Del Mirador" />
          </div>
          <div class="card-content">
            <div class="card-header">
              <span class="location">
                <img src="/location.png" alt="Ubicación" />
                Valencia, Vía Guataparo
              </span>
              <button class="favorite-btn" aria-label="Guardar en favoritos">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l8.78-8.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
              </button> 
            </div>
            <h3 class="property-title">Residencias Altos Del Mirador</h3>
            <div class="property-specs">
              <span>4 habitaciones</span>
              <span class="separator">|</span>
              <span>3 baños</span>
              <span>67 m² Totales</span>
            </div>
            <div class="card-footer">
              <span class="price">$150,000</span>
              <button class="details-btn">
                Detalles <span>&gt;</span>
              </button>
            </div>
          </div>
        </article>

        <!-- Tarjeta 3 -->
        <article class="property-card">
          <div class="card-image">
            <img src="/PhotoPrincipal.png" alt="Residencias Altos Del Mirador" />
          </div>
          <div class="card-content">
            <div class="card-header">
              <span class="location">
                <img src="/location.png" alt="Ubicación" />
                Valencia, Vía Guataparo
              </span>
              <button class="favorite-btn" aria-label="Guardar en favoritos">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l8.78-8.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
              </button>
            </div>
            <h3 class="property-title">Residencias Altos Del Mirador</h3>
            <div class="property-specs">
              <span>4 habitaciones</span>
              <span class="separator">|</span>
              <span>3 baños</span>
              <span>67 m² Totales</span>
            </div>
            <div class="card-footer">
              <span class="price">$150,000</span>
              <button class="details-btn">
                Detalles <span>&gt;</span>
              </button>
            </div>
          </div>
        </article>

        <!-- Tarjeta 4 -->
        <article class="property-card">
          <div class="card-image">
            <img src="/PhotoPrincipal.png" alt="Residencias Altos Del Mirador" />
          </div>
          <div class="card-content">
            <div class="card-header">
              <span class="location">
                <img src="/location.png" alt="Ubicación" />
                Valencia, Vía Guataparo
              </span>
              <button class="favorite-btn" aria-label="Guardar en favoritos">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l8.78-8.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
              </button>
            </div>
            <h3 class="property-title">Residencias Altos Del Mirador</h3>
            <div class="property-specs">
              <span>4 habitaciones</span>
              <span class="separator">|</span>
              <span>3 baños</span>
              <span>67 m² Totales</span>
            </div>
            <div class="card-footer">
              <span class="price">$150,000</span>
              <button class="details-btn">
                Detalles <span>&gt;</span>
              </button>
            </div>
          </div>
        </article>
      </section>
  </div>
</template>

<style scoped src= "./style2.css"></style>