import { afterEach, describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'

import TableroNegocios from '../components/vendedor/negocios/TableroNegocios.vue'
import { crearActividad } from '../services/actividades'
import { api } from '../services/api'
import { buscarNegocios, type ColumnaTablero, type Negocio } from '../services/negocios'
import { buscarPropiedades } from '../services/propiedades'
import { iniciarSesion, limpiarSesion } from '../services/sesion'

const negocio = (id: number, etapaId: number, propietarioId: number | null): Negocio => ({
  id, nombre: `Negocio ${id}`, inmuebleId: 1, codigoInmueble: 'R-1', tituloInmueble: 'Casa', tipoInmueble: 'Casa',
  contactoId: 1, nombreContacto: 'Carla Rojas', tipoOperacion: 'venta', valor: 1000, etapaId, etapa: `Etapa ${etapaId}`,
  etapaAbierta: true, fechaCierreEstimada: null, propietarioId, nombrePropietario: null, fechaCreacion: '2026-10-01T10:00:00',
  fechaActualizacion: '2026-10-01T10:00:00', fechaCierre: null,
})

const columnas = (): ColumnaTablero[] => [
  { etapaId: 1, etapa: 'Prospecto', abierta: true, cantidad: 2, total: 2000, negocios: [negocio(1, 1, 7), negocio(2, 1, 99)] },
  { etapaId: 2, etapa: 'Cita/visita programada', abierta: true, cantidad: 0, total: 0, negocios: [] },
]

describe('modulos de negocios e inmuebles', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
    limpiarSesion()
  })

  it('buscarNegocios y buscarPropiedades envian las listas separadas por coma', async () => {
    const fetchFalso = vi.fn(async () => ({ ok: true, status: 200, text: async () => '{"contenido": []}' }))
    vi.stubGlobal('fetch', fetchFalso)
    await buscarNegocios({ vista: 'sin_asignar', etapas: [1, 2], tiposOperacion: ['alquiler'], valorMin: '' })
    await buscarPropiedades({ vista: 'disponibles', estados: ['disponible', 'reservado'], precioMin: '50000' })

    expect(fetchFalso.mock.calls.map((c) => (c as unknown[])[0])).toEqual([
      '/api/negocios?vista=sin_asignar&etapas=1%2C2&tiposOperacion=alquiler',
      '/api/propiedades?vista=disponibles&estados=disponible%2Creservado&precioMin=50000',
    ])
  })

  it('crearActividad envia los datos como JSON dentro del multipart', async () => {
    const enviar = vi.spyOn(api, 'postForm').mockResolvedValue({} as never)
    const adjunto = new File(['%PDF'], 'contrato.pdf', { type: 'application/pdf' })
    await crearActividad({ tipo: 'NOTA', contactoId: 4, descripcion: 'Llamar el lunes' }, [adjunto])

    const formulario = enviar.mock.calls[0]![1] as FormData
    const datos = formulario.get('datos') as Blob
    expect(datos.type).toBe('application/json')
    expect(JSON.parse(await datos.text())).toEqual({ tipo: 'NOTA', contactoId: 4, descripcion: 'Llamar el lunes' })
    expect((formulario.getAll('archivos')[0] as File).name).toBe('contrato.pdf')
    enviar.mockRestore()
  })

  it('el tablero solo deja arrastrar los negocios propios o sin asignar y emite el cambio de etapa', async () => {
    iniciarSesion({ token: 'x', tipo: 'EMPLEADO', id: 7, rol: 'ROLE_VENDEDOR', nombre: 'Ana', expiraEnMs: 60000 })
    const tablero = mount(TableroNegocios, { props: { columnas: columnas(), etapas: [] } })

    const tarjetas = tablero.findAll('.tarjeta')
    expect(tarjetas[0]!.attributes('draggable')).toBe('true')
    expect(tarjetas[1]!.attributes('draggable')).toBe('false')
    expect(tablero.text()).toContain('$2,000')

    await tarjetas[0]!.trigger('dragstart', { dataTransfer: { setData: () => {} } })
    await tablero.findAll('.columna')[1]!.trigger('drop')
    expect(tablero.emitted('mover')?.[0]).toEqual([expect.objectContaining({ id: 1 }), 2])
  })

  it('las paginas de detalle aceptan ids numericos', async () => {
    const { default: router } = await import('../router')
    expect(router.resolve('/negocios/12').name).toBe('NegocioDetalle')
    expect(router.resolve('/inmuebles/9').name).toBe('PropiedadDetalle')
    expect(router.resolve('/estadisticas/inmuebles/9').name).toBe('EstadisticaInmueble')
    expect(router.resolve('/contact/5').name).toBe('ContactoDetalle')
  }, 20000) // importar el router carga todas las pantallas: con la suite completa puede pasar de 5 s

  it('las caracteristicas del inmueble se escriben en singular o plural y omiten las que faltan', async () => {
    const { textosCaracteristicas } = await import('../services/inmuebles')
    expect(textosCaracteristicas({ habitaciones: 1, banos: 2, metrosCuadrados: 120.5, estacionamientos: 1, piso: 0 })
      .map((c) => c.texto)).toEqual(['1 habitación', '2 baños', '120,5 m²', '1 puesto de estacionamiento', 'Planta baja'])
    expect(textosCaracteristicas({ habitaciones: null, banos: 3, piso: 4 }).map((c) => c.texto)).toEqual(['3 baños', 'Piso 4'])
  })

  it('un 502 sin cuerpo (backend apagado) muestra un mensaje claro', async () => {
    vi.stubGlobal('fetch', vi.fn(async () => ({ ok: false, status: 502, text: async () => '' })))
    await expect(api.get('/api/contactos')).rejects.toThrow('No se pudo conectar con el backend')
  })
})
