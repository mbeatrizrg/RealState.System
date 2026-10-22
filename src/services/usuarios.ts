import { api } from './api'

// UsuarioRegistroDTO
export interface UsuarioRegistro {
  nombre: string
  apellido: string
  telefono: string
  // Numero de cedula sin prefijo V/E (columna ci)
  cedula: number
  correo: string
  contrasena: string
}

// POST /api/usuario/registro, responde un texto de confirmacion
export const registrarUsuario = (datos: UsuarioRegistro) => api.post<string>('/api/usuario/registro', datos)

// UsuarioResponseDTO (clientes del portal, vistos desde el CRM)
export interface Cliente {
  id: number
  nombre: string
  apellido: string
  correo: string
  telefono: string
  cedula: number | null
}

// CRM: clientes registrados (para vincularlos a un inmueble como lead)
export const obtenerClientesCrm = () => api.get<Cliente[]>('/api/usuario/crm')

// CRM: crea un lead del cliente para el inmueble (estatus 1 = primer paso del embudo)
export const vincularCliente = (usuarioId: number, inmuebleId: number) =>
  api.post<{ id: number; mensaje: string }>('/api/lead', { usuarioId, inmuebleId, estatus: 1 })
