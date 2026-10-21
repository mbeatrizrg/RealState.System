package com.rifatrichani.backendinmobiliaria.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.rifatrichani.backendinmobiliaria.security.LimitadorIntentosLogin.IntentosExcedidosException;

//Atrapa las excepciones de todos los controladores y responde siempre con el formato {"mensaje": "...", "status": ..., "timestamp": ...}
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(ResourceNotFoundException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    //Credenciales incorrectas en el login: siempre el mismo mensaje, para no revelar si el correo existe
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> manejarCredenciales(BadCredentialsException ex) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(IntentosExcedidosException.class)
    public ResponseEntity<Map<String, Object>> manejarIntentosExcedidos(IntentosExcedidosException ex) {
        return construirRespuesta(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    //Un servicio externo no disponible o sin configurar (ej. el envio de correos)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> manejarServicioNoDisponible(IllegalStateException ex) {
        log.warn("Servicio no disponible: {}", ex.getMessage());
        return construirRespuesta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    //Un rol sin permiso o una cuenta intentando modificar datos de otra
    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public ResponseEntity<Map<String, Object>> manejarAccesoDenegado(Exception ex) {
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    //Errores de @Valid en los DTO: se devuelve el primer error en "mensaje" (lo muestra el frontend) y el detalle por campo en "errores"
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacionesDTO(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCampos = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresCampos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        String primero = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(FieldError::getDefaultMessage).orElse("Datos inválidos.");
        ResponseEntity<Map<String, Object>> respuesta = construirRespuesta(HttpStatus.BAD_REQUEST, primero);
        respuesta.getBody().put("errores", erroresCampos);
        return respuesta;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> manejarPeticionMalFormada(Exception ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "La petición tiene un formato inválido.");
    }

    //Ej. una llave foranea que no existe o un valor unico repetido. No se expone el detalle SQL al cliente
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return construirRespuesta(HttpStatus.CONFLICT, "Los datos enviados entran en conflicto con registros existentes.");
    }

    //Cualquier otro error: se registra en el log y al cliente se le responde un mensaje generico (sin stacktrace)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorInesperado(Exception ex) {
        //Errores HTTP propios de Spring (404 ruta inexistente, 405 metodo no permitido, etc.) conservan su status
        if (ex instanceof ErrorResponse errorSpring) {
            HttpStatus status = HttpStatus.resolve(errorSpring.getStatusCode().value());
            if (status != null && status.is4xxClientError()) {
                return construirRespuesta(status, status.getReasonPhrase());
            }
        }
        log.error("Error no controlado", ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor.");
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", status.value());
        cuerpo.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(cuerpo);
    }
}
