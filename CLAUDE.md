# RealState.System — Frontend & Architectural Summary

This document outlines the technical context, UI/UX structure, and styling standards established for the **RealState.System** (PropTech management platform) frontend.

---

## 1. Technological Stack & Structure

- **Framework**: Vue 3 (Composition API / Options API)
- **Build Tool**: Vite
- **Styling**: Custom CSS / Tailwind CSS (`control.css`, `newLeads.css`)
- **Version Control**: Git (Branch: `root`)

### Key Components & Views
- `newLeads.vue` / `control.vue`: Main dashboard interface for registering new leads and managing property listings.
- `Leads.vue`: Overview and tracking component for commercial interactions.

---

## 2. UI/UX Design System (Figma Specifications)

The interface follows a modern dark-mode-inspired theme using cohesive rounded components, dark violet/blue accents, and defined CSS grid structures.

### Design Tokens & Color Palette
- **Primary Cards & Containers**: `#1a1c2e` / Deep dark slate-blue
- **Active Selection/Buttons**: Vivid Indigo / Royal Blue `#3b4cca`
- **Inactive Controls**: Muted slate `#2d325a`
- **Text Labels & Typography**: Neutral white/light gray with clear hierarchy
- **Input Fields**: Embedded dark contrast inputs with rounded corners (`border-radius: 8px`–`12px`)

---

## 3. Form & Layout Architecture

The system relies on clear dynamic tab switching (`activeSection === 'usuario'` vs. `activeSection === 'inmueble'`).

### "Añadir Inmueble" Layout Spec (2-Column Grid)
The property addition view uses a strict two-column grid layout to maintain proper alignment and usability:

```
[ Form Container ]
┌──────────────────────────────┬───────────────────────────────────────────┐
│ Left Column (Avatar/User)   │ Right Column (2-Column Field Grid)        │
├──────────────────────────────┼───────────────────────────────────────────┤
│ • Profile Photo Avatar       │ • Tipo            │ • Inmueble            │
│   (Upload Area)              │ • Codigo RRBR     │ • Precio              │
│ • Usuario Vinculado Input    │ • Direccion       │ • Extra               │
└──────────────────────────────┴───────────────────────────────────────────┘
```

### CSS Implementation Pattern
```css
/* Container Layout */
.inmueble-container {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 2.5rem;
  align-items: start;
}

.inmueble-col-left {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* 2-Column Inputs Grid */
.inmueble-fields-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.2rem 1.8rem;
}
```

---

## 4. Frontend-to-Database Mapping

The frontend forms map directly to the normalized relational database model:

| Frontend Input Component | Target Database Table | Attribute |
| :--- | :--- | :--- |
| **Nombre, Apellido, Phone, Email** | `Usuario` | `nombre`, `apellido`, `telefono`, `correo` |
| **Foto** | `galeria` / `Usuario` | `foto` / `fotoUrl` |
| **Tipo** | `tipo_inmueble` | `tipo` |
| **Codigo RRBR** | `Inmueble` | `RRBR` |
| **Direccion** | `Inmueble` | `ubicacion` |
| **Inmueble** | `Inmueble` | `id_tipo` |
| **Precio** | `Inmueble` | `precio` |
| **Usuario Vinculado** | `lead` / `Usuario` | `id_usuario` |

---

## 5. Development Guidelines & Best Practices

1. **HTML Tag Hygiene**:
   - Ensure every `<div class="form-section">` closes cleanly before declaring parallel dynamic sections (`v-if`).
   - Keep `.group` field components nested directly within their parent column containers (`.inmueble-col-left` or `.inmueble-fields-grid`) to preserve responsiveness.
2. **Component Isolation**:
   - Keep shared form styling within standard utility CSS files (`control.css` / `newLeads.css`) to maintain consistent button states, select dropdown styles, and typography across all views.