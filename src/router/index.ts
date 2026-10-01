import { createRouter, createWebHistory } from 'vue-router'
import MainPage from '@/components/MainPage.vue'
import SearchHome from '@/components/SearchHome.vue'
import Menu from '@/components/Menu.vue'
import HomeDetails from '@/components/HomeDetails.vue'
import Create from '@/components/create.vue'
import signIn from '@/components/sign-in.vue'
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
    path: '/details',
    name: 'details',
    component: HomeDetails

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
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
