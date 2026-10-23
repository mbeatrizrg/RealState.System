import type { NivelInteraccion } from '@/services/estadisticas'

/* Colores de las graficas de Estadisticas, validados con el script de la guia de visualizacion (contraste y separacion
   para daltonismo sobre el fondo blanco). Los tonos claros (verde agua, amarillo, rosa) quedan por debajo de 3:1:
   por eso las graficas muestran siempre leyenda con el valor y la tabla de datos debajo */

// Series de la grafica de interacciones, en orden fijo: consultas, visitas, cierres
export const COLOR_SERIE = ['#2a78d6', '#eb6834', '#1baf7a']

// Niveles del embudo: una sola rampa azul de claro a oscuro (orden), "descarto" en gris neutro fuera del orden
export const COLOR_NIVEL: Record<NivelInteraccion, string> = {
  INFORMACION: '#86b6ef',
  AGENDO: '#5598e7',
  VISITO: '#2a78d6',
  OFERTA: '#1c5cab',
  CERRO: '#0d366b',
  DESCARTO: '#b8bcc4',
}

// Tipos de inmueble (categorico): el color sigue al tipo segun su posicion en el catalogo, nunca a su ranking
export const COLOR_TIPO = ['#2a78d6', '#eb6834', '#1baf7a', '#eda100', '#e87ba4']

export const GRIS_OTROS = '#b8bcc4'
