import { afterEach, beforeEach, describe, it, expect, vi } from 'vitest'

import { flushPromises, mount } from '@vue/test-utils'
import App from '../App.vue'
import router from '../router'

// Simula las respuestas del backend segun la ruta pedida
function simularBackend(respuestas: Record<string, unknown>) {
  const fetchFalso = vi.fn(async (url: string) => {
    const ruta = Object.keys(respuestas).find((clave) => url.startsWith(clave))
    const cuerpo = ruta ? respuestas[ruta] : []
    return { ok: true, status: 200, text: async () => JSON.stringify(cuerpo) }
  })
  vi.stubGlobal('fetch', fetchFalso)
  return fetchFalso
}

async function montarEn(ruta: string) {
  await router.push(ruta)
  await router.isReady()
  const wrapper = mount(App, { global: { plugins: [router] } })
  await flushPromises()
  return wrapper
}

describe('App', () => {
  beforeEach(() => {
    vi.unstubAllGlobals()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('muestra la pagina principal y avisa cuando no hay propiedades', async () => {
    simularBackend({ '/api/inmueble/buscar': [] })
    const wrapper = await montarEn('/')

    expect(wrapper.text()).toContain('Destacados')
    expect(wrapper.text()).toContain('Aún no hay propiedades publicadas.')
  })

  it('carga los filtros y los destacados desde la API', async () => {
    simularBackend({
      '/api/ciudad': [{ id: 1, nombre: 'Valencia' }],
      '/api/tipo_inmueble': [{ id: 2, nombre: 'Apartamento' }],
      '/api/inmueble/buscar': [
        { id: 7, titulo: 'Residencias Altos Del Mirador', precio: 150000, urlImagenPrincipal: null, ciudad: 'Valencia', tipoInmuebleId: 2, tipoInmueble: 'Apartamento' },
      ],
    })
    const wrapper = await montarEn('/')

    expect(wrapper.find('#locacion').text()).toContain('Valencia')
    expect(wrapper.find('#tipo').text()).toContain('Apartamento')
    expect(wrapper.text()).toContain('Residencias Altos Del Mirador')
    expect(wrapper.text()).toContain('$150,000')
    expect(wrapper.find('a.details-btn').attributes('href')).toBe('/details/7')
  })
})
