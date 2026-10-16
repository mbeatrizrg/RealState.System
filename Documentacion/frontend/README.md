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
