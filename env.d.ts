/// <reference types="vite/client" />

declare module '*.vue'{
    import type { DefineComponent} from 'vue'
    const component: DefineComponent<{},{}, any>
    export default component
}

interface ImportMetaEnv {
    // URL del backend en produccion, ej: https://api.midominio.com (vacio = mismo dominio / proxy de Vite)
    readonly VITE_API_URL?: string
}
