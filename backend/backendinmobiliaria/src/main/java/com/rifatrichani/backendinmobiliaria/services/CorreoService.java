package com.rifatrichani.backendinmobiliaria.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

//Envio de los correos con codigos (verificacion de cuenta, cambio de correo y recuperacion de contraseña)
//y de los correos que un vendedor escribe a un contacto desde su registro en el CRM
@Service
public class CorreoService {
    private static final Logger log = LoggerFactory.getLogger(CorreoService.class);
    private static final String FIRMA = "\n\nRifat Richani Bienes Raíces";

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remitente;

    public CorreoService(ObjectProvider<JavaMailSender> mailSender, @Value("${spring.mail.username:}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    //Lanza IllegalStateException (503) si el correo no esta configurado o falla, para que la transaccion que lo llama se revierta
    public void enviarCodigo(String destino, String nombre, String asunto, String motivo, String codigo, int minutosVigencia) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || remitente == null || remitente.isBlank()) {
            throw new IllegalStateException("El envío de correos no está configurado (MAIL_USERNAME / MAIL_PASSWORD).");
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destino);
        mensaje.setSubject(asunto + " - Rifat Richani Bienes Raíces");
        mensaje.setText("Hola " + nombre + ",\n\n" + motivo + "\n\nTu código es: " + codigo
                + "\n\nVence en " + minutosVigencia + " minutos. Si no fuiste tú, ignora este mensaje: nadie podrá usarlo sin acceso a tu correo."
                + FIRMA);
        try {
            sender.send(mensaje);
        } catch (MailException e) {
            log.error("No se pudo enviar el correo '{}' a {}", asunto, destino, e);
            throw new IllegalStateException("No se pudo enviar el correo. Intente de nuevo más tarde.");
        }
    }

    //Correo escrito por un vendedor desde el registro del contacto. Igual que enviarCodigo: 503 si no se puede enviar
    public void enviarMensaje(String destino, String asunto, String cuerpo, String nombreVendedor, List<MultipartFile> adjuntos) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || remitente == null || remitente.isBlank()) {
            throw new IllegalStateException("El envío de correos no está configurado (MAIL_USERNAME / MAIL_PASSWORD). Desmarque \"Enviar al contacto\" para solo registrarlo.");
        }
        try {
            MimeMessage mensaje = sender.createMimeMessage();
            MimeMessageHelper ayudante = new MimeMessageHelper(mensaje, !adjuntos.isEmpty(), "UTF-8");
            ayudante.setFrom(remitente);
            ayudante.setTo(destino);
            ayudante.setSubject(asunto);
            ayudante.setText(cuerpo + "\n\n" + nombreVendedor + FIRMA);
            for (MultipartFile adjunto : adjuntos) {
                ayudante.addAttachment(adjunto.getOriginalFilename() != null ? adjunto.getOriginalFilename() : "adjunto", adjunto);
            }
            sender.send(mensaje);
        } catch (MessagingException | MailException e) {
            log.error("No se pudo enviar el correo '{}' a {}", asunto, destino, e);
            throw new IllegalStateException("No se pudo enviar el correo. Intente de nuevo más tarde.");
        }
    }
}
