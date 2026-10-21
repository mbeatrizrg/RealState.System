package com.rifatrichani.backendinmobiliaria.security;

import org.springframework.security.access.AccessDeniedException;

import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Lead;

/* Reglas de propiedad del CRM (ademas de los roles): un vendedor solo puede modificar los inmuebles que tiene a cargo
   y los leads/comunicaciones/ventas de esos inmuebles. El administrador puede todo */
public final class ControlAcceso {

    private ControlAcceso() {}

    public static void verificarEncargado(Inmueble inmueble, CuentaAutenticada cuenta) {
        if (cuenta.esAdmin()) {
            return;
        }
        boolean esEncargado = cuenta.tipo() == CuentaAutenticada.TipoCuenta.EMPLEADO
                && inmueble.getEmpleado() != null
                && cuenta.id().equals(inmueble.getEmpleado().getId());
        if (!esEncargado) {
            throw new AccessDeniedException("Solo el vendedor encargado del inmueble o un administrador puede realizar esta acción.");
        }
    }

    public static void verificarEncargado(Lead lead, CuentaAutenticada cuenta) {
        verificarEncargado(lead.getInmueble(), cuenta);
    }
}
