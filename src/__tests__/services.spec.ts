import { afterEach, describe, it, expect, vi } from 'vitest'

import { ApiError } from '../services/api'
import { buscarInmuebles, PESO_MAXIMO_IMAGEN, subirImagenes, validarImagen } from '../services/inmuebles'
import { formatearPrecio, IMAGEN_POR_DEFECTO, urlImagen } from '../utils/formato'

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
    const fetchFalso = simularRespuesta(200, '{"contenido": []}')
    await buscarInmuebles({ ciudadId: '3', tipoInmuebleId: '', maxPrecio: 15000, minPrecio: null, isAlquiler: false, pagina: 0 })

    expect(fetchFalso).toHaveBeenCalledWith('/api/inmueble/buscar?ciudadId=3&maxPrecio=15000&isAlquiler=false&pagina=0', expect.anything())
  })

  it('subirImagenes envia multipart sin forzar el Content-Type JSON', async () => {
    const fetchFalso = simularRespuesta(201, '[]')
    const foto = new File([new Uint8Array([0xff, 0xd8, 0xff])], 'foto.jpg', { type: 'image/jpeg' })
    await subirImagenes(7, [foto, foto])

    const [url, opciones] = fetchFalso.mock.calls[0] as unknown as [string, RequestInit]
    expect(url).toBe('/api/galeria/inmueble/7')
    expect(opciones.method).toBe('POST')
    expect((opciones.body as FormData).getAll('archivos')).toHaveLength(2)
    expect(opciones.headers).not.toHaveProperty('Content-Type')
  })

  it('validarImagen rechaza formatos y tamaños no permitidos', () => {
    expect(validarImagen(new File(['x'], 'a.png', { type: 'image/png' }))).toBeNull()
    expect(validarImagen(new File(['x'], 'a.gif', { type: 'image/gif' }))).toContain('JPG, PNG o WEBP')
    const grande = new File([new Uint8Array(PESO_MAXIMO_IMAGEN + 1)], 'grande.jpg', { type: 'image/jpeg' })
    expect(validarImagen(grande)).toContain('5 MB')
  })

  it('urlImagen sirve las fotos subidas desde el backend y usa la imagen por defecto sin foto', () => {
    expect(urlImagen('/uploads/inmuebles/abc.jpg')).toBe('/uploads/inmuebles/abc.jpg')
    expect(urlImagen('https://cdn.com/a.jpg')).toBe('https://cdn.com/a.jpg')
    expect(urlImagen(null)).toBe(IMAGEN_POR_DEFECTO)
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
