<template>
<!-- 8tava pagina -->
    <div class="page8">
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
      
    <main>

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
            class="filter-input"
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
            <img src="/lupa.svg" alt="" />
            <span>Buscar Propiedad</span>
          </button>
      </div>

    </section>
  </main>
</div>
</template>