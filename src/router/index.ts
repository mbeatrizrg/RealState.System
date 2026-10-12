import { createRouter, createWebHistory } from 'vue-router'
import MainPage from '@/components/MainPage.vue'
import SearchHome from '@/components/SearchHome.vue'
import HomeDetails from '@/components/HomeDetails.vue'
import Create from '@/components/create.vue'
import signIn from '@/components/sign-in.vue'
import ContactUs from '@/components/ContactUs.vue'
import menuVendedor from '@/components/vendedor/menu-vendedor.vue'
import Contact from '@/components/vendedor/Contact.vue'

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
  },
  {
    path: '/vendedor',
    name: 'menuVendedor',
    component: menuVendedor
  },
  {
    path: '/contacts',
    name: 'ContactUs',
    component: ContactUs
  },
  {
    path: '/contact',
    name: 'Contact',
    component: Contact
  }

]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
