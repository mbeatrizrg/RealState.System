import { afterEach, describe, it, expect, vi } from 'vitest'

import router from '../router'
import { api } from '../services/api'
import { loginEmpleado, loginUsuario } from '../services/auth'
import { esEmpleado, haySesion, limpiarSesion, sesion } from '../services/sesion'

// Simula una respuesta del backend y devuelve el fetch falso para revisar con que se llamo
function simularRespuesta(status: number, cuerpo: unknown) {
  const fetchFalso = vi.fn(async () => ({ ok: status < 400, status, text: async () => JSON.stringify(cuerpo) }))
  vi.stubGlobal('fetch', fetchFalso)
  return fetchFalso
}

const loginVendedor = {
  token: 'token-de-prueba',
  tipo: 'EMPLEADO',
  rol: 'ROLE_VENDEDOR',
  id: 3,
  nombre: 'Carlos Vendedor',
  expiraEnMs: 60_000,
}

describe('sesion', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
    limpiarSesion()
  })

  it('guarda la sesion al iniciar sesion y envia el token en las siguientes peticiones', async () => {
    simularRespuesta(200, loginVendedor)
    await loginEmpleado({ correo: 'carlos@crm.com', contrasena: 'Secreta123!' })

    expect(esEmpleado.value).toBe(true)
    expect(sesion.nombre).toBe('Carlos Vendedor')

    const fetchFalso = simularRespuesta(200, [])
    await api.get('/api/lead')
    const [, opciones] = fetchFalso.mock.calls[0] as unknown as [string, RequestInit]
    expect((opciones.headers as Record<string, string>).Authorization).toBe('Bearer token-de-prueba')
  })

  it('no guarda nada si el login falla', async () => {
    simularRespuesta(401, { mensaje: 'Correo o contraseña incorrectos.' })

    await expect(loginUsuario({ correo: 'a@b.com', contrasena: 'mala' })).rejects.toThrow('Correo o contraseña incorrectos.')
    expect(haySesion.value).toBe(false)
  })

  it('cierra la sesion local cuando el backend rechaza el token', async () => {
    simularRespuesta(200, loginVendedor)
    await loginEmpleado({ correo: 'carlos@crm.com', contrasena: 'Secreta123!' })

    simularRespuesta(401, { mensaje: 'No autorizado.' })
    await expect(api.get('/api/lead')).rejects.toThrow()
    expect(haySesion.value).toBe(false)
  })

  it('las paginas del CRM envian al login de asesores si no hay sesion de empleado', async () => {
    await router.push('/leads')
    expect(router.currentRoute.value.path).toBe('/crm/login')
    expect(router.currentRoute.value.query.redirect).toBe('/leads')
  })

  it('Mi cuenta envia al login del portal si no hay sesion de usuario', async () => {
    await router.push('/cuenta')
    expect(router.currentRoute.value.path).toBe('/signin')
  })

  it('con sesion de empleado el login redirige al CRM', async () => {
    simularRespuesta(200, loginVendedor)
    await loginEmpleado({ correo: 'carlos@crm.com', contrasena: 'Secreta123!' })

    await router.push('/crm/login')
    expect(router.currentRoute.value.path).toBe('/vendedor')
  })
})
