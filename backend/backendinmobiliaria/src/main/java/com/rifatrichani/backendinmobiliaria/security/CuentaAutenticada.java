package com.rifatrichani.backendinmobiliaria.security;

//Principal que el JwtAuthenticationFilter guarda en el SecurityContext despues de validar el token.
//El id corresponde a la tabla Empleado o Usuario segun el tipo, por eso nunca se debe usar el id sin revisar el tipo
public record CuentaAutenticada(Integer id, TipoCuenta tipo, String rol) {

    public enum TipoCuenta { EMPLEADO, USUARIO }

    public static final String ROL_ADMIN = "ROLE_ADMIN";
    public static final String ROL_VENDEDOR = "ROLE_VENDEDOR";
    public static final String ROL_USUARIO = "ROLE_USUARIO";

    public boolean esAdmin() {
        return tipo == TipoCuenta.EMPLEADO && ROL_ADMIN.equals(rol);
    }
}
