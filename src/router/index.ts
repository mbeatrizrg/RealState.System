import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import MainPage from '@/components/MainPage.vue'
import SearchHome from '@/components/SearchHome.vue'
import HomeDetails from '@/components/HomeDetails.vue'
import Create from '@/components/create.vue'
import signIn from '@/components/sign-in.vue'
import ContactUs from '@/components/ContactUs.vue'
import menuVendedor from '@/components/vendedor/menu-vendedor.vue'
import Contact from '@/components/vendedor/Contact.vue'
import Leads from '@/components/vendedor/Leads.vue'
import Control from '@/components/vendedor/Control.vue'
import profile from '@/components/vendedor/profile.vue'
import ClientProfile from '@/components/vendedor/ClientProfile.vue'
import Comunication from '@/components/vendedor/Comunication.vue'
import VerificarCuenta from '@/components/cuenta/VerificarCuenta.vue'
import RecuperarContrasena from '@/components/cuenta/RecuperarContrasena.vue'
import LoginCrm from '@/components/cuenta/LoginCrm.vue'
import MiCuenta from '@/components/cuenta/MiCuenta.vue'
import { esEmpleado, esUsuario, haySesion } from '@/services/sesion'

/* meta.acceso:
   - 'empleado': solo vendedores y administradores con sesion (CRM)
   - 'usuario':  solo clientes del portal con sesion
   - 'invitado': solo sin sesion (login, registro); con sesion se envia a su pagina de inicio
   Sin meta.acceso la ruta es publica. El backend vuelve a validar todo: esto solo evita mostrar pantallas vacias */
declare module 'vue-router' {
  interface RouteMeta {
    acceso?: 'empleado' | 'usuario' | 'invitado'
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: MainPage
  },
  {
    path: '/search',
    name: 'SearchHome',
    component: SearchHome
  },
  {
    path: '/details/:id',
    name: 'details',
    component: HomeDetails
  },
  {
    path: '/create',
    name: 'create',
    component: Create,
    meta: { acceso: 'invitado' }
  },
  {
    path: '/signin',
    name: 'signIn',
    component: signIn,
    meta: { acceso: 'invitado' }
  },
  {
    path: '/verificar',
    name: 'verificar',
    component: VerificarCuenta,
    meta: { acceso: 'invitado' }
  },
  {
    path: '/recuperar',
    name: 'recuperar',
    component: RecuperarContrasena
  },
  {
    path: '/cuenta',
    name: 'cuenta',
    component: MiCuenta,
    meta: { acceso: 'usuario' }
  },
  {
    path: '/crm/login',
    name: 'crmLogin',
    component: LoginCrm,
    meta: { acceso: 'invitado' }
  },
  {
    path: '/vendedor',
    name: 'menuVendedor',
    component: menuVendedor,
    meta: { acceso: 'empleado' }
  },
  {
    path: '/contacts',
    name: 'ContactUs',
    component: ContactUs
  },
  {
    path: '/contact',
    name: 'Contact',
    component: Contact,
    meta: { acceso: 'empleado' }
  },
  {
    path: '/leads',
    name: 'Leads',
    component: Leads,
    meta: { acceso: 'empleado' }
  },
  {
    path: '/control',
    name: 'control',
    component: Control,
    meta: { acceso: 'empleado' }
  },
  {
    path: '/profile',
    name: 'profile',
    component: profile,
    meta: { acceso: 'empleado' }
  },
  {
    path: '/clientProfile',
    name: 'clientProfile',
    component: ClientProfile,
    meta: { acceso: 'empleado' }
  },
  {
    path: '/comunication',
    name: 'comunication',
    component: Comunication,
    meta: { acceso: 'empleado' }
  },

]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to) => {
  const acceso = to.meta.acceso
  if (acceso === 'empleado' && !esEmpleado.value) {
    return { path: '/crm/login', query: { redirect: to.fullPath } }
  }
  if (acceso === 'usuario' && !esUsuario.value) {
    return { path: '/signin', query: { redirect: to.fullPath } }
  }
  if (acceso === 'invitado' && haySesion.value) {
    return esEmpleado.value ? '/vendedor' : '/cuenta'
  }
  return true
})

// Si el backend rechaza el token (expiro o se cerro la sesion en otro lugar) se envia al login correspondiente
if (typeof window !== 'undefined') {
  window.addEventListener('sesion-expirada', () => {
    const actual = router.currentRoute.value
    if (actual.meta.acceso === 'empleado') {
      router.push({ path: '/crm/login', query: { redirect: actual.fullPath, expirada: '1' } })
    } else if (actual.meta.acceso === 'usuario') {
      router.push({ path: '/signin', query: { redirect: actual.fullPath, expirada: '1' } })
    }
  })
}

export default router
