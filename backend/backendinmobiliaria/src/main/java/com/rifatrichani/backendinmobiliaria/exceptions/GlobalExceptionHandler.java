package com.rifatrichani.backendinmobiliaria.exceptions;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//Manejador principal de errores, el @RestControllerAdvice permite manejar excepciones de manera global en formato json
@RestControllerAdvice
public class GlobalExceptionHandler {
    //Manejador de excepciones para ResourceNotFoundException, devuelve un estado 404 con un mensaje de error
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(ResourceNotFoundException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    //Manejador de excepciones para IllegalArgumentException, devuelve un estado 400 con un mensaje de error
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    //Manejador de excepciones para SecurityException y AccessDeniedException, devuelve un estado 403 con un mensaje de error
    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public ResponseEntity<Map<String, Object>> manejarAccesoDenegado(Exception ex) {
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    //Manejador de excepciones para MethodArgumentNotValidException, devuelve un estado 400 con un mapa de errores de validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacionesDTO(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCampos = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresCampos.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.BAD_REQUEST.value());
        cuerpo.put("errores", erroresCampos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }
    //Método auxiliar privado que centraliza la creación del mapa JSON (timestamp, status, mensaje) para no repetir código en los manejadores de 
    // arriba :)
    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", status.value());
        cuerpo.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(cuerpo);
    }
}