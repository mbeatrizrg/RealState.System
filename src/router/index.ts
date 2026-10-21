import { createRouter, createWebHistory } from 'vue-router'
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
    path: '/details/:id',
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
  },
  {
    path: '/leads',
    name: 'Leads',
    component: Leads
  },
  {
    path: '/control',
    name: 'control',
    component: Control
  },
  {
    path: '/profile',
    name: 'profile',
    component: profile
  },
  {
    path: '/clientProfile',
    name: 'clientProfile',
    component: ClientProfile
  },
  {
    path: '/comunication',
    name: 'comunication',
    component: Comunication
  },

]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
