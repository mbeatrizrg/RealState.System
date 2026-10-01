import { createRouter, createWebHistory } from 'vue-router'
import MainPage from '@/components/MainPage.vue'
import SearchHome from '@/components/SearchHome.vue'
import Menu from '@/components/Menu.vue'
<<<<<<< HEAD
import HomeDetails from '@/components/HomeDetails.vue'

=======
import Create from '@/components/create.vue'
import signIn from '@/components/sign-in.vue'
>>>>>>> origin/crear-cuenta
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
<<<<<<< HEAD
    path: '/details',
    name: 'details',
    component: HomeDetails
=======
    path: '/create',
    name: 'create',
    component: Create
  },
  {
    path: '/signin',
    name: 'signIn',
    component: signIn
>>>>>>> origin/crear-cuenta
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
