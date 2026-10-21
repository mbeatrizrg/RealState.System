import { api } from './api'

// UsuarioRegistroDTO
export interface UsuarioRegistro {
  nombre: string
  apellido: string
  telefono: string
  correo: string
  contrasena: string
}

// POST /api/usuario/registro, responde un texto de confirmacion
export const registrarUsuario = (datos: UsuarioRegistro) => api.post<string>('/api/usuario/registro', datos)
