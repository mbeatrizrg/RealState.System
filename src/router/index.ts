import { createRouter, createWebHistory } from 'vue-router'
import MainPage from '@/components/MainPage.vue'
import SearchHome from '@/components/SearchHome.vue'
import Menu from '@/components/Menu.vue'
import Create from '@/components/create.vue'
import signIn from '@/components/sign-in.vue'
import menuVendedor from '@/components/menu-vendedor.vue'
const routes = [
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
    path: '/menu',
    name: 'menu',
    component: Menu
  },
  {
    path: '/create',
    name: 'create',
    component: Create
  },
  {
    path: '/signin',
    name: 'signIn',
    component: signIn
  },
  {
    path: '/menu-vendedor',
    name: 'menuVendedor',
    component: menuVendedor
  }

]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
