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

// PaginaResponseDTO con una sola pagina
const pagina = (contenido: unknown[]) => ({ contenido, pagina: 0, tamano: 12, totalElementos: contenido.length, totalPaginas: 1 })

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
    simularBackend({ '/api/inmueble/buscar': pagina([]) })
    const wrapper = await montarEn('/')

    expect(wrapper.text()).toContain('Destacados')
    expect(wrapper.text()).toContain('Aún no hay propiedades publicadas.')
  })

  it('carga los filtros y los destacados desde la API', async () => {
    simularBackend({
      '/api/ciudad': [{ id: 1, nombre: 'Valencia' }],
      '/api/tipo_inmueble': [{ id: 2, nombre: 'Apartamento' }],
      '/api/inmueble/buscar': pagina([
        { id: 7, titulo: 'Residencias Altos Del Mirador', precio: 150000, urlImagenPrincipal: null, ciudad: 'Valencia', tipoInmuebleId: 2, tipoInmueble: 'Apartamento' },
      ]),
    })
    const wrapper = await montarEn('/')

    expect(wrapper.find('#locacion').text()).toContain('Valencia')
    expect(wrapper.find('#tipo').text()).toContain('Apartamento')
    expect(wrapper.text()).toContain('Residencias Altos Del Mirador')
    expect(wrapper.text()).toContain('$150,000')
    expect(wrapper.find('a.details-btn').attributes('href')).toBe('/details/7')
  })

  it('la busqueda envia texto, filtros y pagina al backend', async () => {
    const fetchFalso = simularBackend({ '/api/inmueble/buscar': { ...pagina([]), totalPaginas: 3 } })
    await montarEn('/search?q=mirador&modalidad=alquiler&orden=precio_asc&pagina=2')

    const urls = fetchFalso.mock.calls.map(([url]) => url as string)
    expect(urls).toContain('/api/inmueble/buscar?q=mirador&isAlquiler=true&orden=precio_asc&pagina=1&tamano=12')
  })

  it('el registro envia la cedula como numero', async () => {
    const fetchFalso = simularBackend({ '/api/usuario/registro': 'Usuario registrado. Revise su correo e ingrese el código de verificación.' })
    const wrapper = await montarEn('/create')

    await wrapper.find('#firstName').setValue('Ana')
    await wrapper.find('#lastName').setValue('Pérez')
    await wrapper.find('#cedula').setValue('V-12.345.678')
    await wrapper.find('#phone').setValue('04145555555')
    await wrapper.find('#gmail').setValue('ana@correo.com')
    await wrapper.find('#password').setValue('ClaveSegura123')
    await wrapper.find('#password2').setValue('ClaveSegura123')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    const llamada = fetchFalso.mock.calls.find(([url]) => url === '/api/usuario/registro') as unknown as [string, RequestInit]
    expect(JSON.parse(llamada[1].body as string)).toMatchObject({ cedula: 12345678, correo: 'ana@correo.com' })
  })

  it('el registro no se envia si las contraseñas no coinciden', async () => {
    const fetchFalso = simularBackend({})
    const wrapper = await montarEn('/create')

    await wrapper.find('#firstName').setValue('Ana')
    await wrapper.find('#lastName').setValue('Pérez')
    await wrapper.find('#cedula').setValue('12345678')
    await wrapper.find('#phone').setValue('04145555555')
    await wrapper.find('#gmail').setValue('ana@correo.com')
    await wrapper.find('#password').setValue('ClaveSegura123')
    await wrapper.find('#password2').setValue('OtraClave123')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.text()).toContain('Las contraseñas no coinciden')
    expect(fetchFalso.mock.calls.some(([url]) => url === '/api/usuario/registro')).toBe(false)
  })
})
