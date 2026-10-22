import { beforeAll, describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'

import Paginacion from '../components/Paginacion.vue'
import GaleriaFotos from '../components/vendedor/GaleriaFotos.vue'

const foto = (nombre: string) => new File(['x'], nombre, { type: 'image/png' })

// Simula la eleccion de archivos en el <input type="file"> oculto
async function elegirArchivos(wrapper: ReturnType<typeof mount>, archivos: File[]) {
  const input = wrapper.find('input[type="file"]')
  Object.defineProperty(input.element, 'files', { value: archivos, configurable: true })
  await input.trigger('change')
}

describe('GaleriaFotos', () => {
  beforeAll(() => {
    // jsdom no implementa las URL temporales de los archivos
    URL.createObjectURL = () => 'blob:previa'
    URL.revokeObjectURL = () => {}
  })

  it('no deja pasar de 6 fotos entre guardadas y nuevas', async () => {
    const existentes = [1, 2, 3, 4].map((id) => ({ id, url: `/uploads/inmuebles/${id}.png` }))
    const wrapper = mount(GaleriaFotos, { props: { modelValue: [], existentes } })

    await elegirArchivos(wrapper, [foto('a.png'), foto('b.png'), foto('c.png')])

    const emitido = wrapper.emitted('update:modelValue')?.[0]?.[0] as File[]
    expect(emitido.map((f) => f.name)).toEqual(['a.png', 'b.png'])
    expect(wrapper.text()).toContain('Solo se pueden agregar 2 foto(s) más')
  })

  it('oculta el boton de agregar al llegar al maximo', () => {
    const wrapper = mount(GaleriaFotos, { props: { modelValue: [foto('a.png'), foto('b.png')], existentes: [1, 2, 3, 4].map((id) => ({ id, url: 'x' })) } })

    expect(wrapper.text()).toContain('6 / 6')
    expect(wrapper.find('.galeria-agregar').exists()).toBe(false)
  })

  it('rechaza archivos que no son imagen', async () => {
    const wrapper = mount(GaleriaFotos, { props: { modelValue: [] } })
    await elegirArchivos(wrapper, [new File(['x'], 'virus.exe', { type: 'application/octet-stream' })])

    expect(wrapper.emitted('update:modelValue')).toBeUndefined()
    expect(wrapper.text()).toContain('solo se aceptan imágenes')
  })
})

describe('Paginacion', () => {
  it('muestra primera, ultima y vecinas de la pagina actual', () => {
    const wrapper = mount(Paginacion, { props: { pagina: 5, totalPaginas: 10 } })
    const numeros = wrapper.findAll('.pag-numero').map((b) => b.text())

    expect(numeros).toEqual(['1', '4', '5', '6', '10'])
    expect(wrapper.findAll('.pag-puntos')).toHaveLength(2)
  })

  it('emite la pagina elegida y no se muestra con una sola pagina', async () => {
    const wrapper = mount(Paginacion, { props: { pagina: 1, totalPaginas: 3 } })
    const botones = wrapper.findAll('.pag-btn')
    await botones[botones.length - 1]!.trigger('click')
    expect(wrapper.emitted('cambiar')?.[0]).toEqual([2])

    const unaPagina = mount(Paginacion, { props: { pagina: 1, totalPaginas: 1 } })
    expect(unaPagina.find('nav').exists()).toBe(false)
  })
})
