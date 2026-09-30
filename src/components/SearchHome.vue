<template>
  <div class="home-search">
    <!-- Header Principal -->
    <header class="CommonHeader">
      <div class="logo">
        <img src="/icon.png" alt="Icono" class="icon" />
        <img src="/brand.png" alt="Rifat Richani" class="brand" />
      </div>

      <nav class="primary-nav">
        <!-- cambiar para que se vea como el search btn -->
        <button @click="Menu" class="menu-btn">
          <span>Menu</span>
        </button> 
        <button class="inc-btn">Iniciar Sesion</button>
      </nav>
    </header>

    <main class="bar">
      <!-- Barra de Filtros / Búsqueda -->
      <section class="background">
        <div class="filter-bar2">
          <div class="filter-group2">
            <label for="locacion">Locación</label>
            <select id="locacion" name="locacion">
              <option value="" disabled selected>Ciudades</option>
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
              <option value="galpon">Galpon</option>
              <option value="oficina">Oficina</option>
              <option value="consultorio medico">Consultorio medico</option>
              <option value="club">Club</option>
              <option value="negocio">Negocio</option>
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

          <button @click="Search" class="search-btn">
            <img src="/lupa.svg" alt="" />
            <span>Buscar Propiedad</span>
          </button>
        </div> 
      </section>
    </main>
      <!-- Grid de Tarjetas de Propiedades -->
      <section class="properties-grid">
        <!-- Tarjeta 1 -->
        <article class="card2">
            <div class="img-card">
              <img src="/building2.png" alt="Residencias Altos Del Mirador" class="building2-img" />
            </div>

            <div class="content2">
              <div class="location-card">
                <img src="/location.png" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="details-card">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="footer-card">
                <span class="card-price">$150,000</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

        <!-- Tarjeta 2 -->
       <article class="card2">
            <div class="img-card">
              <img src="/building2.png" alt="Residencias Altos Del Mirador" class="building2-img" />
            </div>

            <div class="content2">
              <div class="location-card">
                <img src="/location.png" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="details-card">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="footer-card">
                <span class="card-price">$150,000</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

        <!-- Tarjeta 3 -->
        <article class="card2">
            <div class="img-card">
              <img src="/building2.png" alt="Residencias Altos Del Mirador" class="building2-img" />
            </div>

            <div class="content2">
              <div class="location-card">
                <img src="/location.png" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="details-card">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="footer-card">
                <span class="card-price">$150,000</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>

        <!-- Tarjeta 4 -->
        <article class="card2">
            <div class="img-card">
              <img src="/building2.png" alt="Residencias Altos Del Mirador" class="building2-img" />
            </div>

            <div class="content2">
              <div class="location-card">
                <img src="/location.png" alt="Ubicación" />
                <span>Valencia, Via Guataparo</span>
              </div>

              <button class="favorite-btn" aria-label="Añadir a favoritos">
                <img src="/heart.png" alt="Favorito" />
              </button>

              <h3 class="card-title">Residencias Altos Del Mirador</h3>

              <div class="details-card">
                <span>4 habitaciones | 3 baños</span>
                <span>67 m² Totales</span>
              </div>

              <div class="footer-card">
                <span class="card-price">$150,000</span>
                <a href="#" class="details-btn">Ver Detalles &gt;</a>
              </div>
            </div>
          </article>
      </section>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router'

const router = useRouter()

const Search = ()=> {
  router.push('/search')
}

const Menu = ()=> {
  router.push('/menu')
}

</script>

<style scoped src= "./style2.css"></style>