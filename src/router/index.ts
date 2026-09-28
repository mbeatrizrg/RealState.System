import { createRouter, createWebHistory } from 'vue-router'
import MainPage from '@/components/MainPage.vue'
import SearchHome from '@/components/SearchHome.vue'
import Menu from '@/components/Menu.vue'
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
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
