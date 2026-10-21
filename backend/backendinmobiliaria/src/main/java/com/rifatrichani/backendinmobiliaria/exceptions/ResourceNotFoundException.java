package com.rifatrichani.backendinmobiliaria.exceptions;

//Se lanza cuando se busca un registro por id y no existe, el GlobalExceptionHandler la convierte en un 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
