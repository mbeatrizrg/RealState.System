package com.rifatrichani.backendinmobiliaria.exceptions;
//Manejador de excepciones para recursos no encontrados
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}