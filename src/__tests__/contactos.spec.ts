import { afterEach, describe, it, expect, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'

import FiltroLista from '../components/vendedor/contactos/FiltroLista.vue'
import { buscarContactos, exportarContactos } from '../services/contactos'
import { rangoAFechas } from '../utils/fechas'

describe('modulo de contactos', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('buscarContactos envia las listas separadas por coma y omite los filtros vacios', async () => {
    const fetchFalso = vi.fn(async () => ({ ok: true, status: 200, text: async () => '{"contenido": []}' }))
    vi.stubGlobal('fetch', fetchFalso)
    await buscarContactos({ vista: 'mios', q: '', propietarioIds: [2, 0], estatus: [], creadoDesde: '2026-10-01', pagina: 0 })

    expect(fetchFalso).toHaveBeenCalledWith(
      '/api/contactos?vista=mios&propietarioIds=2%2C0&creadoDesde=2026-10-01&pagina=0', expect.anything())
  })

  it('exportarContactos descarga el archivo con el nombre que envia el backend', async () => {
    const respuesta = {
      ok: true,
      status: 200,
      headers: new Headers({ 'Content-Disposition': 'attachment; filename="contactos-todos-2026-10-21.xlsx"' }),
      blob: async () => new Blob(['PK']),
    }
    vi.stubGlobal('fetch', vi.fn(async () => respuesta))
    URL.createObjectURL = vi.fn(() => 'blob:prueba')
    URL.revokeObjectURL = vi.fn()
    const clic = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (this: HTMLAnchorElement) {
      expect(this.download).toBe('contactos-todos-2026-10-21.xlsx')
    })

    await exportarContactos({ vista: 'todos' }, { formato: 'xlsx', alcance: 'todos', columnas: ['nombre', 'correo'] })

    expect(clic).toHaveBeenCalledOnce()
    const url = (fetch as unknown as ReturnType<typeof vi.fn>).mock.calls[0]?.[0]
    expect(url).toBe('/api/contactos/exportar?vista=todos&formato=xlsx&alcance=todos&columnas=nombre%2Ccorreo')
    clic.mockRestore()
  })

  it('rangoAFechas convierte los rangos predefinidos en fechas incluidas', () => {
    const hoy = new Date(2026, 9, 21) // 21 de octubre de 2026
    expect(rangoAFechas('hoy', '', '', hoy)).toEqual({ desde: '2026-10-21', hasta: '2026-10-21' })
    expect(rangoAFechas('ultimos7', '', '', hoy)).toEqual({ desde: '2026-10-15', hasta: '2026-10-21' })
    expect(rangoAFechas('mesPasado', '', '', hoy)).toEqual({ desde: '2026-09-01', hasta: '2026-09-30' })
    expect(rangoAFechas('personalizado', '2026-01-05', '', hoy)).toEqual({ desde: '2026-01-05', hasta: undefined })
    expect(rangoAFechas('', '', '', hoy)).toEqual({})
  })

  it('FiltroLista busca sin distinguir acentos y emite la seleccion', async () => {
    const filtro = mount(FiltroLista, {
      props: {
        etiqueta: 'Propietario',
        modelValue: [],
        opciones: [{ valor: 1, texto: 'María Pérez' }, { valor: 2, texto: 'Luis Gómez' }],
      },
      attachTo: document.body,
    })
    await filtro.find('.filtro-boton').trigger('click')
    await filtro.find('.filtro-buscar').setValue('maria')
    const opciones = filtro.findAll('.filtro-opcion')
    expect(opciones).toHaveLength(1)

    await opciones[0]!.find('input').trigger('change')
    await flushPromises()
    expect(filtro.emitted('update:modelValue')?.[0]).toEqual([[1]])
    filtro.unmount()
  })
})
