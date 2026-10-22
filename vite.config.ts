import { fileURLToPath, URL } from 'node:url'

import { defineConfig, type ProxyOptions } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

/* Pasando por el proxy el navegador ve todo como el mismo sitio, asi que no hace falta CORS. Se quita el Origin para
   que el backend no rechace (403 "Invalid CORS request") cuando se abre el frontend en 127.0.0.1, en la IP de la red
   o en otro puerto (Vite usa 5174 si el 5173 esta ocupado). En produccion CORS sigue controlado por CORS_ALLOWED_ORIGINS */
const sinOrigen: ProxyOptions['configure'] = (proxy) => {
  proxy.on('proxyReq', (req) => req.removeHeader('origin'))
}

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
    // En desarrollo las peticiones a /api y las fotos subidas (/uploads) se reenvian al backend de Spring Boot
    proxy: {
      '/api': { target: 'http://localhost:8080', configure: sinOrigen },
      '/uploads': { target: 'http://localhost:8080', configure: sinOrigen },
    },
  },
})
