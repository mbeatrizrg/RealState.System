import { api } from './api'
import { iniciarSesion, limpiarSesion, type DatosLogin, type TipoCuenta } from './sesion'

// Endpoints de /api/auth y de la cuenta del usuario (/api/usuario/perfil)

interface Mensaje {
  mensaje: string
}

// PerfilSesionDTO: los campos que no aplican al tipo de cuenta llegan en null
export interface PerfilSesion {
  id: number
  tipo: TipoCuenta
  rol: string
  nombre: string
  apellido: string | null
  correo: string
  telefono: string | null
  tipoNegocio: string | null
  correoPendiente: string | null
  cedula: number | null
  esAdmin: boolean | null
}

export interface Credenciales {
  correo: string
  contrasena: string
}

// ----- Login / logout -----

export async function loginUsuario(credenciales: Credenciales) {
  const datos = await api.post<DatosLogin>('/api/auth/login/usuario', credenciales)
  iniciarSesion(datos)
  return datos
}

export async function loginEmpleado(credenciales: Credenciales) {
  const datos = await api.post<DatosLogin>('/api/auth/login/empleado', credenciales)
  iniciarSesion(datos)
  return datos
}

// Avisa al backend (invalida el token) y borra la sesion local aunque el aviso falle
export async function cerrarSesion() {
  try {
    await api.post<Mensaje>('/api/auth/logout')
  } catch {
    // Token ya vencido o sin conexion: igual se cierra la sesion en el navegador
  } finally {
    limpiarSesion()
  }
}

export const obtenerPerfilSesion = () => api.get<PerfilSesion>('/api/auth/me')

// ----- Verificacion de la cuenta (registro) -----

export const confirmarCuenta = (correo: string, codigo: string) =>
  api.post<Mensaje>('/api/auth/confirmar', { correo, codigo })

export const reenviarCodigo = (correo: string) => api.post<Mensaje>('/api/auth/reenviar-codigo', { correo })

// ----- Olvide mi contraseña -----

export const solicitarRecuperacion = (correo: string, tipo: TipoCuenta) =>
  api.post<Mensaje>('/api/auth/recuperar', { correo, tipo })

export const restablecerContrasena = (datos: { correo: string; tipo: TipoCuenta; codigo: string; contrasenaNueva: string }) =>
  api.post<Mensaje>('/api/auth/recuperar/confirmar', datos)

// ----- Cambio de contraseña con sesion iniciada (cierra todas las sesiones) -----

export async function cambiarContrasena(tipo: TipoCuenta, contrasenaActual: string, contrasenaNueva: string) {
  const ruta = tipo === 'EMPLEADO' ? '/api/auth/empleado/contrasena' : '/api/auth/usuario/contrasena'
  const respuesta = await api.put<Mensaje>(ruta, { contrasenaActual, contrasenaNueva })
  limpiarSesion()
  return respuesta
}

// ----- Perfil del usuario del portal -----

export interface DatosPerfil {
  nombre: string
  apellido: string
  telefono: string
  tipoNegocio?: string
}

export const actualizarPerfil = (datos: DatosPerfil) => api.put<unknown>('/api/usuario/perfil', datos)

export const solicitarCambioCorreo = (correoNuevo: string, contrasena: string) =>
  api.post<Mensaje>('/api/usuario/perfil/correo', { correoNuevo, contrasena })

export const confirmarCambioCorreo = (codigo: string) =>
  api.post<Mensaje>('/api/usuario/perfil/correo/confirmar', { codigo })
