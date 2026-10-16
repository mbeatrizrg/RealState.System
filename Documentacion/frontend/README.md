1- Rifat Richani Real State 
Este módulo corresponde a la interfaz web del sistema inmobiliario. Permite a los agentes de ventas administrar los inmuebles, gestionar la agenda de citas y dar seguimiento a los clientes potenciales y a sus necesidades.

los usuarios a los que van dirigidos para administrar la pantalla:
Panel Administrativo (Backoffice): Para administradores, agentes inmobiliarios o gestores.
Portal Público / Cliente: Para compradores o inquilinos que buscan propiedades.

Descripción: Aplicación web pública orientada a los compradores. Incluye el buscador de inmuebles, filtros avanzados y formulario de contacto directo con los agentes.
El modulo de Frontend contiene las siguientes vistas:
- Pagina principal Real State
- Catalogo de inmuebles
- vista personalizada de cada inmueble

Descripción: Interfaz gráfica desarrollada para la plataforma de gestión inmobiliaria. Permite la visualización e interacción con el catálogo de propiedades, registro de leads y panel de control administrativo.
mientras que el sistema de ventas maneja las siguientes vistas
- Dashboard de vendedores
- Portal del cliente vista de favoritos
- Panel del admin
- base de datos de los clientes
- Panel de Leads y gestion de ventas
- gestion de comunicaciones


2- Badgets
El framework principal que utiliza el frontend es Vue.js
Lenguaje: TypeScript
Motor de empaquetado (Build Tool): Vite

requisitos de uso
version de vue.js 3.5.40 o mayor 
gestor de paquetes npm

3- variables de entorno

4- como instalar el sistema de forma local 

5- estructura de las carpetas

REALSTATE.SYSTEM/
├── public/            # Archivos estáticos (favicons, imágenes públicas)
├── src/
│   ├── components/    # Componentes reutilizables de UI (Botones, Modales, Inputs)
│       ├── vendedor/   
│           ├── views/ # vistas del sistema y de la pagina principal 
│   ├── store/         # Manejo de estado global (Pinia, Redux, Zustand)
│   ├── router/        # Configuración de rutas de navegación
│   └── utils/         # Funciones auxiliares y helpers
└── package.json

6- Arquitectura

7-Guia de estilos UI
Lista los colores corporativos o del tema del sistema con sus códigos Hexadecimales y nombres semánticos:
Primarios: El color de marca principal (para botones primarios, enlaces activos, headers).
Secundarios: Colores complementarios y acentos.
Semánticos / Estado:
Éxito (Success): Verde (#10B981)
Error / Peligro (Danger): Rojo (#EF4444)
Advertencia (Warning): Amarillo/Naranja (#F59E0B)
Información (Info): Azul (#3B82F6)

Fuentes tipográficas: Nombre de las fuentes de texto principales y secundarias (ej. Inter, Roboto, Poppins).
Jerarquía de texto: Tamaños base y pesos de fuente (font-weight) configurados para Títulos (h1, h2, h3), subtítulos y texto de cuerpo (body).

Pack de iconos: Nombre de la librería usada (ej. Lucide Icons, FontAwesome, Heroicons, Material Icons).
Formato de imágenes: Indicación de formatos preferidos (SVG para vectores/PNG para fotografías).

Mobile: < 640px
Tablet: 640px - 1024px
Desktop: > 1024px

prototipo de figma
https://www.figma.com/proto/0xnHWUCf7oVoczy6QXwxSH/rrRealtor?node-id=2036-27&t=QqTa7x04SyqHMdZb-1
