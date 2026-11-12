<template>
<div class="details-container">
  <div class="detalle-contenido">
    <!-- Enlace de regreso -->
    <button @click="Atras" class="back-btn">
      <span>&larr; Atras</span>
    </button>

    <p v-if="cargando" class="estado-msg">Cargando propiedad...</p>
    <p v-else-if="error" class="estado-msg">{{ error }}</p>

    <template v-else-if="inmueble">
      <!-- Galería de imágenes: se acomoda a la cantidad de fotos -->
      <GaleriaMosaico :fotos="imagenes" :titulo="inmueble.titulo" />

      <!-- Información principal del inmueble -->
      <section class="info-section">
        <div class="info-left">
          <p class="property-type">{{ inmueble.nombreTipoInmueble }} · {{ inmueble.isAlquiler ? 'Alquiler' : 'Venta' }}</p>
          <h1 class="property-title">{{ inmueble.titulo }}</h1>
          <p class="property-location">
            <img src="/location.png" alt="Ubicación" class="location-icon" />
            <span>{{ direccion || 'Carabobo' }}</span>
          </p>
          <!-- Caracteristicas: habitaciones, baños, metros, estacionamiento y piso (solo las registradas) -->
          <ul v-if="caracteristicas.length" class="property-features">
            <li v-for="c in caracteristicas" :key="c.clave">
              <img :src="ICONOS[c.clave]" alt="" class="feature-icon" />
              <span>{{ c.texto }}</span>
            </li>
          </ul>
          <p class="property-price">
            {{ formatearPrecio(inmueble.precio) }}<small v-if="inmueble.isAlquiler"> / mes</small>
          </p>
          <p class="property-description">
            {{ inmueble.descripcion || 'Pronto agregaremos la descripción de esta propiedad.' }}
          </p>
            <!-- PENDIENTE: este boton debe crear un Lead en el CRM -->
            <button @click="create" class="contact-btn">
              <span>Contactanos</span>
            </button>
        </div>

        <div class="info-right">
          <h2 class="details-title">Detalles</h2>
          <dl class="details-list">
            <dt>Tipo</dt><dd>{{ inmueble.nombreTipoInmueble }}</dd>
            <dt>Estatus</dt><dd>{{ inmueble.nombreEstatus }}</dd>
            <dt>Ciudad</dt><dd>{{ inmueble.nombreCiudad || 'Carabobo' }}</dd>
            <dt v-if="inmueble.habitaciones != null || inmueble.banos != null">Distribución</dt>
            <dd v-if="inmueble.habitaciones != null || inmueble.banos != null">{{ distribucion }}</dd>
            <dt v-if="inmueble.metrosCuadrados != null">Superficie</dt>
            <dd v-if="inmueble.metrosCuadrados != null">{{ Number(inmueble.metrosCuadrados).toLocaleString('es-VE') }} m²</dd>
            <dt v-if="inmueble.estacionamientos != null">Estacionamiento</dt>
            <dd v-if="inmueble.estacionamientos != null">{{ inmueble.estacionamientos }} {{ inmueble.estacionamientos === 1 ? 'puesto' : 'puestos' }}</dd>
            <dt v-if="inmueble.piso != null">Piso</dt>
            <dd v-if="inmueble.piso != null">{{ inmueble.piso === 0 ? 'Planta baja' : inmueble.piso }}</dd>
            <dt>Código</dt><dd>{{ inmueble.rrbr }}</dd>
          </dl>
          <template v-if="inmueble.extras?.length">
            <h3 class="extras-title">Extras</h3>
            <ul class="extras-list">
              <li v-for="extra in inmueble.extras" :key="extra">{{ extra }}</li>
            </ul>
          </template>
        </div>
      </section>

      <section class="info-container">
        <div class="map-section">
        <h2 class="details-title">Ubicación</h2>
        <p v-if="direccion" class="map-address">{{ direccion }}</p>
        <!-- El mapa se genera con la ubicacion del inmueble; sin ubicacion se muestra la imagen de referencia -->
        <iframe
          v-if="urlMapa"
          :src="urlMapa"
          class="map-image"
          allowfullscreen=""
          loading="lazy"
          referrerpolicy="no-referrer-when-downgrade"
          title="Ubicación en el mapa">
        </iframe>
        <img v-else src="/reference-img.png" alt="Ubicación en el mapa" class="map-image" />
        </div>

        <div id="formulario-contacto" class="info-contactos">
          <h2>Contactanos</h2>
    <form class="register-form" novalidate @submit.prevent>
  <!-- PASO 1: Datos Básicos (Se bloquean si ya se envió el código) -->
  <div class="form-row">
    <div class="form-group">
      <label for="firstName">Nombre</label>
      <input type="text" id="firstName" placeholder="Rifat" v-model.trim="usuario.nombre" :disabled="pasoVerificacion" required />
    </div>
    <div class="form-group">
      <label for="lastName">Apellido</label>
      <input type="text" id="lastName" placeholder="Richani" v-model.trim="usuario.apellido" :disabled="pasoVerificacion" required />
    </div>
  </div>

  <div class="form-row">
    <div class="form-group">
      <label for="phone">Teléfono</label>
      <input type="tel" id="phone" placeholder="0414-555 5555" v-model.trim="usuario.telefono" :disabled="pasoVerificacion" required />
    </div>
  </div>

  <div class="form-group">
    <label for="gmail">Correo electrónico</label>
    <input type="email" id="gmail" placeholder="ejemplo@gmail.com" v-model.trim="usuario.correo" :disabled="pasoVerificacion" required />
    <span v-if="!pasoVerificacion" class="campo-ayuda">Te enviaremos un código para confirmar tus datos.</span>
  </div>

  <!-- PASO 2: Código de verificación (Solo aparece si pasoVerificacion es true) -->
  <div v-if="pasoVerificacion" class="form-group">
    <label for="codigo">Código de Verificación</label>
    <input type="text" id="codigo" placeholder="123456" maxlength="6" v-model.trim="codigo" required />
    <span class="campo-ayuda">Revisa tu correo e ingresa el código numérico.</span>
  </div>

  <!-- Mensajes de error o éxito -->
  <p v-if="mensaje" class="form-mensaje" :class="{ 'form-mensaje-error': esError, 'form-mensaje-exito': exito }" role="alert">{{ mensaje }}</p>

  <!-- Botones Dinámicos -->
  <button v-if="!pasoVerificacion && !exito" type="button" @click="solicitarCodigo" class="btn-submit" :disabled="enviando">
    {{ enviando ? 'Enviando código...' : 'Contactar' }}
  </button>

  <button v-else-if="pasoVerificacion && !exito" type="button" @click="confirmarLead" class="btn-submit" :disabled="enviando">
    {{ enviando ? 'Verificando...' : 'Confirmar y Enviar' }}
  </button>
</form>
        </div>
      </section>
    </template>
  </div>

    
    <footer class="page-6">
    <!-- boton superior derecho-->
    <!--contenido central-->
    <div class="footer-content">
      <div class="logo">
        <img src="/icon.png" alt="Rifat Richani Bienes Raices" class="footer-brand-img" />
      </div>
      <!-- Direccion-->
      <p class="footer-address">
        C.C.P. Otama, urb. El Viñedo, PB<br />
        Local 10, Valencia, Carabobo
      </p>
      <!--Titulo de contacto-->

      <h3 class="contact-title">CONTACTANOS</h3>
      <!--Iconos de redes sociales-->
      <div class="social-icons">
        <a href="https://x.com/rifatrichanibr"><img src="/x.png" alt="X (Twitter)" /></a>
        <a href="https://www.instagram.com/rifatrichanibienesraices?stkn=c2YydWl4c3Q4anNr"><img src="/inst.png" alt="Instagram" /></a>
        <a href="https://www.facebook.com/share/19A5MuKs4X/?mibextid=wwXIfr"><img src="/facebook.png" alt="Facebook" /></a>
        <a href="https://wa.me/584145936070"><img src="/whatsapp.png" alt="WhatsApp"></a>
      </div>
      </div>
    </footer>
</div>

</template>

<script setup>
import { computed, ref, watch, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api'
import GaleriaMosaico from '@/components/GaleriaMosaico.vue'
import { mensajeDeError } from '@/services/api'
import { obtenerInmueble, registrarVista, textosCaracteristicas } from '@/services/inmuebles'
import { formatearPrecio, IMAGEN_POR_DEFECTO, urlImagen } from '@/utils/formato'

const route = useRoute()
const router = useRouter()
const idInmueble = Number(route.params.id)
const inmueble = ref(null)
const cargando = ref(true)
const error = ref('')

// Hasta 6 fotos por inmueble; sin fotos se muestra la imagen por defecto
const imagenes = computed(() =>
  inmueble.value?.urlsGaleria?.length ? inmueble.value.urlsGaleria.map(urlImagen) : [IMAGEN_POR_DEFECTO]
)

const caracteristicas = computed(() => (inmueble.value ? textosCaracteristicas(inmueble.value) : []))

// "2 habitaciones | 2 baños" (como en el diseño de la ficha)
const distribucion = computed(() => textosCaracteristicas({ habitaciones: inmueble.value?.habitaciones, banos: inmueble.value?.banos })
  .map((c) => c.texto).join(' | '))

// Iconos de linea (SVG en data: no requieren archivos) en el azul del portal
const icono = (ruta) =>
  `data:image/svg+xml,${encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="#0b1f46" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="${ruta}"/></svg>`)}`
const ICONOS = {
  habitaciones: icono('M3 18v-6a2 2 0 012-2h14a2 2 0 012 2v6M3 18h18M3 18v2m18-2v2M6 10V7a2 2 0 012-2h8a2 2 0 012 2v3'),
  banos: icono('M4 12h16v2a5 5 0 01-5 5H9a5 5 0 01-5-5v-2zM6 12V6a2 2 0 014 0M7 19l-1 2m11-2l1 2'),
  metros: icono('M4 4h16v16H4zM4 9h3M4 14h3M9 4v3M14 4v3'),
  estacionamientos: icono('M5 17h14M6 17l1.5-5h9l1.5 5M7 17v2m10-2v2M8.5 12l1-3h5l1 3'),
  piso: icono('M5 21V4h14v17M3 21h18M9 8h2m2 0h2M9 12h2m2 0h2M9 16h2m2 0h2'),
}

const direccion = computed(() => [inmueble.value?.ubicacion, inmueble.value?.nombreCiudad].filter(Boolean).join(', '))

const urlMapa = computed(() =>
  direccion.value ? `https://www.google.com/maps?q=${encodeURIComponent(direccion.value)}&output=embed` : null
)

watch(() => route.params.id, async (id) => {
  if (!id) return
  cargando.value = true
  error.value = ''
  try {
    inmueble.value = await obtenerInmueble(id)
    // La vista es telemetria para el CRM, si falla no debe afectar al usuario
    registrarVista(inmueble.value.id).catch(() => {})
  } catch (e) {
    error.value = mensajeDeError(e)
  } finally {
    cargando.value = false
  }
}, { immediate: true })

// Vuelve a los resultados de busqueda (con sus filtros) o a /search si se entro directo al detalle
const Atras = ()=> {
  if (window.history.state?.back) router.back()
  else router.push('/search')
}

const create = ()=> {
  const seccion = document.getElementById('formulario-contacto')
  if (seccion) {
    seccion.scrollIntoView({ behavior: 'smooth', block: 'center' })
    document.getElementById('firstName')?.focus()
  }
}

const usuario = reactive({ nombre: '', apellido: '', telefono: '', correo: '' })
const codigo = ref('') // Separado del objeto usuario según tu template

const pasoVerificacion = ref(false)
const enviando = ref(false)
const mensaje = ref('')
const esError = ref(false)
const exito = ref(false)

// Validación básica del frontend
function validar() {
  if (!usuario.nombre || !usuario.apellido) return 'Ingresa tu nombre y apellido.'
  if (usuario.telefono.length < 10) return 'El teléfono debe tener al menos 10 caracteres.'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(usuario.correo)) return 'Ingresa un correo electrónico válido.'
  return ''
}

// 1. Envía los datos al backend y solicita el código por Gmail
const solicitarCodigo = async () => {
  const errorValidacion = validar()
  if (errorValidacion) {
    esError.value = true
    mensaje.value = errorValidacion
    return
  }

  esError.value = false
  mensaje.value = ''
  enviando.value = true

  try {
    const { data } = await api.post('/public/contactos/solicitar-codigo', {
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      telefono: usuario.telefono,
      correo: usuario.correo,
      idInmueble: Number(route.params.id)
    })

    if (data?.yaVerificado) {
      exito.value = true
      pasoVerificacion.value = false
      esError.value = false
      mensaje.value = data.mensaje || 'Tu correo ya está registrado. Un asesor se comunicará contigo pronto.'
      usuario.nombre = ''
      usuario.apellido = ''
      usuario.telefono = ''
      usuario.correo = ''
      codigo.value = ''
    } else {
      // Cambia la vista para mostrar el input del código
      pasoVerificacion.value = true
      esError.value = false
      mensaje.value = 'Código enviado. Revisa tu bandeja de entrada o carpeta de Spam.'
    }
  } catch (error) {
    esError.value = true
    mensaje.value = error.response?.data?.mensaje || error.response?.data?.error || 'Error al enviar el código de verificación.'
  } finally {
    enviando.value = false
  }
}

// 2. Envía el código de 6 dígitos para validar y crear el Lead en el CRM
const confirmarLead = async () => {
  if (!codigo.value || codigo.value.length !== 6) {
    esError.value = true
    mensaje.value = 'Ingresa el código completo de 6 dígitos.'
    return
  }

  esError.value = false
  mensaje.value = ''
  enviando.value = true

  try {
    await api.post('/public/contactos/validar-codigo', {
      correo: usuario.correo,
      codigo: codigo.value,
      idInmueble: Number(route.params.id)
    })
    
    // Estado de éxito
    exito.value = true
    esError.value = false
    mensaje.value = '¡Datos confirmados! Un asesor se comunicará contigo pronto.'
    
    // Opcional: Limpiar los campos para evitar reenvíos
    pasoVerificacion.value = false 
    usuario.nombre = ''
    usuario.apellido = ''
    usuario.telefono = ''
    usuario.correo = ''
    codigo.value = ''
    
  } catch (error) {
    esError.value = true
    mensaje.value = error.response?.data?.mensaje || error.response?.data?.error || 'El código es incorrecto o ha expirado.'
  } finally {
    enviando.value = false
  }
}

</script>
<style scoped src= "./cuenta/cuenta-style.css"></style>
<style scoped src= "./detailstyle.css"></style>