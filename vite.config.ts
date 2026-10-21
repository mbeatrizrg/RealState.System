import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue({
      // En los tests (Vitest) las rutas absolutas como /icon.png no deben convertirse en imports,
      // porque el runner no resuelve la carpeta public/ y la suite falla al cargar
      template: { transformAssetUrls: { includeAbsolute: !process.env.VITEST } },
    }),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    // En desarrollo las peticiones a /api se reenvian al backend de Spring Boot
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
