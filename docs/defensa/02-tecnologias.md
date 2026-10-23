# Punto 2 — Tecnologías

> Parte de la defensa del CRM de Rifat Richani Bienes Raíces. Puntos: [1. Por qué lo hicimos](01-por-que-lo-hicimos.md) · **2. Tecnologías** · [3. Metodología de negocio](03-metodologia.md) · [4. Cómo funciona](04-como-funciona.md)

## Arquitectura en tres capas

```
 Navegador (portal + CRM)  ──►  API REST  ──►  Base de datos
 Vue 3 · TypeScript · Vite      Spring Boot 4    PostgreSQL (Supabase)
                                Java 17
                                  │
                 ┌────────────────┼────────────────┐
           Correo SMTP      Archivos (fotos       JWT (sesión
           (códigos y        y documentos)         segura)
            correos)
```

| Capa | Qué hace |
| --- | --- |
| **Frontend** (navegador) | Lo que ven el cliente (portal) y el vendedor (CRM). Pide y envía datos a la API. |
| **Backend** (API REST) | Contiene las reglas de negocio y la seguridad: quién puede ver o modificar qué. Genera los Excel y PDF y envía correos. |
| **Base de datos** | Guarda todo: usuarios, inmuebles, leads, negocios, actividades, tareas. Una sola fuente de verdad para portal y CRM. |

## Herramientas y para qué las usamos

| Capa | Tecnología | Para qué |
| --- | --- | --- |
| Interfaz | **Vue 3 + TypeScript + Vite** | Portal y CRM rápidos; TypeScript detecta errores antes de ejecutar |
| Navegación | **Vue Router** | Rutas públicas y rutas protegidas por rol |
| Servidor | **Spring Boot 4 (Java 17)** | API REST y reglas de negocio |
| Datos | **Spring Data JPA / Hibernate + PostgreSQL en Supabase** | Persistencia y filtros dinámicos (Criteria API) |
| Seguridad | **Spring Security + JWT + BCrypt** | Sesiones, roles y contraseñas cifradas |
| Reportes | **Apache POI + OpenPDF** | Exportar tablas e informes a Excel y PDF |
| Correo | **Spring Mail (SMTP)** | Códigos de verificación y correos a clientes con adjuntos |
| Pruebas | **JUnit + H2** (backend) · **Vitest** (frontend) | 58 pruebas en el backend y 36 en el frontend |
| Construcción | **Maven** · **npm** | Compilación y manejo de dependencias |

## Seguridad y calidad

- **Acceso por roles:** administrador, vendedor y cliente del portal. El frontend oculta lo que no corresponde y el **backend vuelve a validar cada petición**: por ejemplo, un vendedor no puede modificar negocios de otro.
- **Cuentas protegidas:** contraseñas cifradas con **BCrypt**, verificación del correo con un **código**, recuperación de contraseña y **cierre de sesión que invalida el token**.
- **Documentos privados:** los documentos legales y los adjuntos solo se descargan con sesión iniciada; no son públicos.
- **Base de datos versionada:** 8 migraciones SQL repetibles en `backend/backendinmobiliaria/db/migraciones`. Al arrancar, el servidor avisa en el log si falta alguna.
- **Pruebas automáticas:** las pruebas del backend ejecutan las consultas reales sobre una base en memoria (H2) para comprobar filtros, permisos, exportaciones y estadísticas.

---

## Para estudiar

**Lo que hay que saber decir (en 30 segundos):**
1. Son tres capas: frontend en Vue 3, API REST en Spring Boot con Java 17 y base de datos PostgreSQL en Supabase.
2. La seguridad usa roles, JWT y BCrypt, y el servidor valida cada acción.
3. Hay pruebas automáticas en las dos capas y la base de datos cambia solo con migraciones versionadas.

**Conceptos que hay que poder explicar:**

| Término | Explicación simple |
| --- | --- |
| **API REST** | El "mostrador" del backend: el frontend le pide datos por URL (`GET /api/negocios`) y recibe JSON. |
| **JWT** | Un "pase" firmado que recibe el usuario al iniciar sesión; lo envía en cada petición para demostrar quién es. |
| **BCrypt** | Algoritmo que cifra la contraseña de forma que no se puede descifrar; solo se compara. |
| **ORM (JPA / Hibernate)** | Traduce las clases de Java (Inmueble, Negocio) a tablas de la base de datos. |
| **Migración** | Un archivo SQL numerado que cambia la base de datos de forma controlada y repetible. |
| **Supabase** | Servicio en la nube que aloja la base de datos PostgreSQL, así no hay que administrar un servidor. |

**Preguntas probables del jurado:**

| Pregunta | Respuesta corta |
| --- | --- |
| ¿Por qué Vue y no React? | Curva de aprendizaje más corta, componentes de un solo archivo y buen rendimiento; con TypeScript tenemos tipos en todo el frontend. |
| ¿Por qué Spring Boot? | Es el estándar en Java para APIs empresariales: seguridad, validación y acceso a datos integrados. |
| ¿Por qué PostgreSQL? | Base relacional robusta y gratuita; los datos (clientes, negocios, inmuebles) son muy relacionales. |
| ¿Cómo protegen las contraseñas? | Con BCrypt: se guardan cifradas y nunca en texto plano. |
| ¿Qué pasa si alguien manipula el frontend? | Nada: el backend vuelve a validar el rol y el propietario en cada petición. |
| ¿Cómo saben que funciona? | Por las pruebas automáticas (58 en el backend y 36 en el frontend), que corren sobre las consultas reales. |
