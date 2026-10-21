import { afterEach, describe, it, expect, vi } from 'vitest'

import { ApiError } from '../services/api'
import { buscarInmuebles } from '../services/inmuebles'
import { formatearPrecio } from '../utils/formato'

function simularRespuesta(status: number, cuerpo: string) {
  const fetchFalso = vi.fn(async () => ({ ok: status < 400, status, text: async () => cuerpo }))
  vi.stubGlobal('fetch', fetchFalso)
  return fetchFalso
}

describe('servicios', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('buscarInmuebles solo envia los filtros con valor', async () => {
    const fetchFalso = simularRespuesta(200, '[]')
    await buscarInmuebles({ ciudadId: '3', tipoInmuebleId: '', maxPrecio: 15000, minPrecio: null })

    expect(fetchFalso).toHaveBeenCalledWith('/api/inmueble/buscar?ciudadId=3&maxPrecio=15000', expect.anything())
  })

  it('usa el mensaje del GlobalExceptionHandler cuando el backend responde error', async () => {
    simularRespuesta(404, JSON.stringify({ mensaje: 'Inmueble no encontrado' }))

    await expect(buscarInmuebles()).rejects.toEqual(new ApiError('Inmueble no encontrado', 404))
  })

  it('usa el texto plano cuando el controlador no responde JSON', async () => {
    simularRespuesta(400, 'El correo ya se encuentra registrado.')

    await expect(buscarInmuebles()).rejects.toThrow('El correo ya se encuentra registrado.')
  })

  it('formatea precios en dolares sin decimales', () => {
    expect(formatearPrecio(150000)).toBe('$150,000')
    expect(formatearPrecio(null)).toBe('Consultar')
  })
})
