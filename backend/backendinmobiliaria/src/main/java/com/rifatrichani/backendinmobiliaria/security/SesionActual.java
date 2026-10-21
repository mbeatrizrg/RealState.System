package com.rifatrichani.backendinmobiliaria.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;

//Acceso a la cuenta autenticada de la peticion actual. El id SIEMPRE se toma del token, nunca del cuerpo o la URL,
//asi un usuario no puede hacerse pasar por otro cambiando un parametro
public final class SesionActual {

    private SesionActual() {}

    //Devuelve la cuenta autenticada o null si la peticion es anonima (endpoints publicos)
    public static CuentaAutenticada cuentaOpcional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CuentaAutenticada cuenta) {
            return cuenta;
        }
        return null;
    }

    public static CuentaAutenticada cuenta() {
        CuentaAutenticada cuenta = cuentaOpcional();
        if (cuenta == null) {
            throw new AccessDeniedException("Debe iniciar sesión.");
        }
        return cuenta;
    }

    public static CuentaAutenticada empleado() {
        CuentaAutenticada cuenta = cuenta();
        if (cuenta.tipo() != TipoCuenta.EMPLEADO) {
            throw new AccessDeniedException("Acceso exclusivo para empleados del CRM.");
        }
        return cuenta;
    }

    public static Integer idEmpleado() {
        return empleado().id();
    }

    public static Integer idUsuario() {
        CuentaAutenticada cuenta = cuenta();
        if (cuenta.tipo() != TipoCuenta.USUARIO) {
            throw new AccessDeniedException("Acceso exclusivo para usuarios del portal.");
        }
        return cuenta.id();
    }
}
