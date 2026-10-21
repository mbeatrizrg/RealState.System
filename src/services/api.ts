// Cliente HTTP base: todas las llamadas al backend pasan por aqui
const BASE_URL = import.meta.env.VITE_API_URL ?? ''

export class ApiError extends Error {
  status: number
  constructor(mensaje: string, status: number) {
    super(mensaje)
    this.status = status
  }
}

async function request<T>(ruta: string, opciones: RequestInit = {}): Promise<T> {
  let respuesta: Response
  try {
    respuesta = await fetch(`${BASE_URL}${ruta}`, {
      ...opciones,
      headers: { 'Content-Type': 'application/json', ...opciones.headers },
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
    // GlobalExceptionHandler responde {"mensaje": "..."}, algunos controladores responden texto
    let mensaje = `Error ${respuesta.status}`
    if (datos && typeof datos === 'object' && 'mensaje' in datos) {
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
  post: <T>(ruta: string, cuerpo: unknown) =>
    request<T>(ruta, { method: 'POST', body: JSON.stringify(cuerpo) }),
}

export function mensajeDeError(error: unknown): string {
  return error instanceof Error ? error.message : 'Ocurrio un error inesperado'
}
