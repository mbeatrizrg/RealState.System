import { limpiarSesion, tokenVigente } from './sesion'

// Cliente HTTP base: todas las llamadas al backend pasan por aqui
export const BASE_URL = import.meta.env.VITE_API_URL ?? ''

export class ApiError extends Error {
  status: number
  constructor(mensaje: string, status: number) {
    super(mensaje)
    this.status = status
  }
}

async function request<T>(ruta: string, opciones: RequestInit = {}): Promise<T> {
  // Si hay sesion se envia el token; el backend decide si la ruta lo necesita
  const token = tokenVigente()
  // Con FormData (subida de archivos) el navegador pone el Content-Type con el boundary del multipart
  const headers: Record<string, string> = opciones.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }
  if (token) headers.Authorization = `Bearer ${token}`

  let respuesta: Response
  try {
    respuesta = await fetch(`${BASE_URL}${ruta}`, {
      ...opciones,
      headers: { ...headers, ...(opciones.headers as Record<string, string> | undefined) },
    })
  } catch {
    throw new ApiError('No se pudo conectar con el servidor', 0)
  }

  // El backend responde JSON o texto plano segun el endpoint
  const texto = await respuesta.text()
  let datos: unknown = texto
  try {
    datos = texto ? JSON.parse(texto) : null
  } catch {
    // Respuesta en texto plano
  }

  if (!respuesta.ok) {
    // 401 con token: la sesion expiro o fue cerrada en otro lugar (logout, cambio de contraseña)
    if (respuesta.status === 401 && token) {
      limpiarSesion()
      window.dispatchEvent(new CustomEvent('sesion-expirada'))
    }
    // GlobalExceptionHandler responde {"mensaje": "..."}, algunos controladores responden texto
    let mensaje = `Error ${respuesta.status}`
    if (respuesta.status === 403 && datos === 'Invalid CORS request') {
      // Spring rechaza el dominio desde el que se abrio la pagina (no esta en CORS_ALLOWED_ORIGINS del backend)
      mensaje = `El servidor no acepta peticiones desde ${window.location.origin}. Agrega este dominio a CORS_ALLOWED_ORIGINS en el backend.`
    } else if (datos && typeof datos === 'object' && 'mensaje' in datos) {
      mensaje = String(datos.mensaje)
    } else if (typeof datos === 'string' && datos) {
      mensaje = datos
    }
    throw new ApiError(mensaje, respuesta.status)
  }
  return datos as T
}

export const api = {
  get: <T>(ruta: string) => request<T>(ruta),
  post: <T>(ruta: string, cuerpo?: unknown) =>
    request<T>(ruta, { method: 'POST', body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo) }),
  put: <T>(ruta: string, cuerpo: unknown) => request<T>(ruta, { method: 'PUT', body: JSON.stringify(cuerpo) }),
  patch: <T>(ruta: string, cuerpo?: unknown) =>
    request<T>(ruta, { method: 'PATCH', body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo) }),
  delete: <T>(ruta: string) => request<T>(ruta, { method: 'DELETE' }),
  postForm: <T>(ruta: string, formulario: FormData) => request<T>(ruta, { method: 'POST', body: formulario }),
}

export function mensajeDeError(error: unknown): string {
  return error instanceof Error ? error.message : 'Ocurrio un error inesperado'
}
