import { afterEach, describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'

import GraficoPastel from '../components/vendedor/estadisticas/GraficoPastel.vue'
import GraficoSerie from '../components/vendedor/estadisticas/GraficoSerie.vue'
import { obtenerEstadisticas } from '../services/estadisticas'
import { buscarTareas } from '../services/tareas'

const porciones = [
  { clave: 'INFORMACION', etiqueta: 'Solo pidió información', cantidad: 3 },
  { clave: 'VISITO', etiqueta: 'Realizó visita física', cantidad: 1 },
  { clave: 'CERRO', etiqueta: 'Compró / alquiló', cantidad: 0 },
]

describe('modulos de tareas y estadisticas', () => {
  afterEach(() => vi.unstubAllGlobals())

  it('buscarTareas y obtenerEstadisticas envian los filtros como query params', async () => {
    const fetchFalso = vi.fn(async () => ({ ok: true, status: 200, text: async () => '{}' }))
    vi.stubGlobal('fetch', fetchFalso)
    await buscarTareas({ vista: 'mias_pendientes', estados: ['vencida', 'pendiente'], prioridades: [], orden: 'prioridad' })
    await obtenerEstadisticas({ desde: '2026-01-01', tipoInmuebleIds: [1, 3], nivel: 'VISITO', vendedorIds: [] })

    expect(fetchFalso.mock.calls.map((c) => (c as unknown[])[0])).toEqual([
      '/api/tareas?vista=mias_pendientes&estados=vencida%2Cpendiente&orden=prioridad',
      '/api/estadisticas?desde=2026-01-01&tipoInmuebleIds=1%2C3&nivel=VISITO',
    ])
  })

  it('el pastel muestra el porcentaje de cada porcion en la leyenda y emite la porcion elegida', async () => {
    const pastel = mount(GraficoPastel, { props: { porciones, colores: ['#86b6ef', '#2a78d6', '#0d366b'] } })

    // Las porciones en 0 no se dibujan pero siguen en la leyenda (deshabilitadas)
    expect(pastel.findAll('.segmento')).toHaveLength(2)
    const filas = pastel.findAll('.leyenda-item')
    expect(filas.map((f) => f.find('.pct').text())).toEqual(['75 %', '25 %', '0 %'])
    expect(filas[2]!.attributes('disabled')).toBeDefined()
    expect(pastel.find('.centro-valor').text()).toBe('4')

    await pastel.findAll('.segmento')[1]!.trigger('click')
    expect(pastel.emitted('elegir')?.[0]).toEqual([porciones[1]])
  })

  it('la grafica de lineas redondea el eje y muestra todas las series en el tooltip', async () => {
    const grafica = mount(GraficoSerie, {
      props: {
        etiquetas: ['ene 2026', 'feb 2026'],
        series: [
          { clave: 'consultas', nombre: 'Consultas', color: '#2a78d6', valores: [7, 3] },
          { clave: 'visitas', nombre: 'Visitas', color: '#eb6834', valores: [1, 2] },
        ],
      },
    })
    // Maximo 7 -> eje limpio de 0 a 8 en pasos de 2
    expect(grafica.findAll('.eje text').slice(0, 5).map((t) => t.text())).toEqual(['0', '2', '4', '6', '8'])

    await grafica.findAll('.zona')[0]!.trigger('focus')
    const tooltip = grafica.find('.tooltip')
    expect(tooltip.text()).toContain('ene 2026')
    expect(tooltip.findAll('.tooltip-fila').map((f) => [f.find('strong').text(), f.findAll('span')[1]!.text()]))
      .toEqual([['7', 'Consultas'], ['1', 'Visitas']])
  })
})
