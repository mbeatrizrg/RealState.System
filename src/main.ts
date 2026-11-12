import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import FilterBar from '@/components/FilterBar.vue'

const app = createApp(App)
app.use(router)
app.component('FilterBar', FilterBar) 

app.use(createPinia())

app.mount('#app')
