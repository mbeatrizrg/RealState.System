import { computed, reactive, readonly } from 'vue'

// Sesion del navegador: el token JWT y los datos basicos de la cuenta conectada.
// Se guarda en localStorage para que la sesion sobreviva a recargar la pagina.
const CLAVE = 'rr_sesion'

export type TipoCuenta = 'USUARIO' | 'EMPLEADO'

// Coincide con LoginResponseDTO del backend
export interface DatosLogin {
  token: string
  tipo: TipoCuenta
  rol: 'ROLE_USUARIO' | 'ROLE_VENDEDOR' | 'ROLE_ADMIN'
  id: number
  nombre: string
  expiraEnMs: number
}

interface EstadoSesion {
  token: string | null
  tipo: TipoCuenta | null
  rol: string | null
  id: number | null
  nombre: string | null
  expiraEn: number | null // timestamp en ms
}

const vacio = (): EstadoSesion => ({ token: null, tipo: null, rol: null, id: null, nombre: null, expiraEn: null })

const estado = reactive<EstadoSesion>(leerGuardada())

function leerGuardada(): EstadoSesion {
  try {
    const guardada = JSON.parse(localStorage.getItem(CLAVE) ?? 'null') as EstadoSesion | null
    if (guardada?.token && guardada.expiraEn && guardada.expiraEn > Date.now()) return guardada
  } catch {
    // localStorage no disponible o con datos corruptos: se empieza sin sesion
  }
  return vacio()
}

function persistir() {
  try {
    if (estado.token) localStorage.setItem(CLAVE, JSON.stringify(estado))
    else localStorage.removeItem(CLAVE)
  } catch {
    // Sin localStorage la sesion dura hasta recargar la pagina
  }
}

export const sesion = readonly(estado)

export function iniciarSesion(datos: DatosLogin) {
  Object.assign(estado, {
    token: datos.token,
    tipo: datos.tipo,
    rol: datos.rol,
    id: datos.id,
    nombre: datos.nombre,
    expiraEn: Date.now() + datos.expiraEnMs,
  })
  persistir()
}

// Borra la sesion del navegador (el aviso al backend lo hace cerrarSesion en services/auth.ts)
export function limpiarSesion() {
  Object.assign(estado, vacio())
  persistir()
}

export function tokenVigente(): string | null {
  if (estado.token && estado.expiraEn && estado.expiraEn > Date.now()) return estado.token
  if (estado.token) limpiarSesion()
  return null
}

export const haySesion = computed(() => !!estado.token && !!estado.expiraEn && estado.expiraEn > Date.now())
export const esUsuario = computed(() => haySesion.value && estado.tipo === 'USUARIO')
export const esEmpleado = computed(() => haySesion.value && estado.tipo === 'EMPLEADO')
export const esAdmin = computed(() => esEmpleado.value && estado.rol === 'ROLE_ADMIN')
