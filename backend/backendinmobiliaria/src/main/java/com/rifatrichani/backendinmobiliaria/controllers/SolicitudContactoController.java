package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.Map; // <-- Importación crucial para Map.of

import org.springframework.http.ResponseEntity; // <-- Importación crucial
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.SolicitarCodigoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.ValidarCodigoDTO;
import com.rifatrichani.backendinmobiliaria.services.ContactoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public/contactos")
public class SolicitudContactoController {

    private final ContactoService contactoService;

    public SolicitudContactoController(ContactoService cs) {
        this.contactoService = cs;
    }

    // 1. Recibe datos del cliente web y le envía el código de 6 dígitos por Gmail
    @PostMapping("/solicitar-codigo")
    public ResponseEntity<?> solicitarCodigo(@Valid @RequestBody SolicitarCodigoDTO dto) {
        contactoService.generarYEnviarCodigo(dto);
        return ResponseEntity.ok(Map.of("mensaje", "Código de verificación enviado a tu correo electrónico."));
    }

    // 2. Valida el código de 6 dígitos ingresado en el frontend y crea el Lead
    @PostMapping("/validar-codigo")
    public ResponseEntity<?> validarCodigo(@Valid @RequestBody ValidarCodigoDTO dto) {
        boolean esValido = contactoService.validarCodigoYCrearLead(dto);
        if (!esValido) {
            return ResponseEntity.badRequest().body(Map.of(
                    "mensaje", "El código ingresado es incorrecto o ha expirado.",
                    "error", "El código ingresado es incorrecto o ha expirado."));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Contacto verificado exitosamente."));
    }
}