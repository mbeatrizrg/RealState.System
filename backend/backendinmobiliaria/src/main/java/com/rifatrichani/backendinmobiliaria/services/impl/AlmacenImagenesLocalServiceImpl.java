package com.rifatrichani.backendinmobiliaria.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.services.AlmacenImagenesService;

/* Guarda las fotos en el disco del servidor (carpeta UPLOADS_DIR/inmuebles) y se sirven en /uploads/inmuebles/**
   (ver config/ArchivosConfig). Medidas de seguridad:
   - El tipo se detecta por los primeros bytes del archivo, nunca por el nombre ni el Content-Type que manda el cliente
   - Solo JPG, PNG y WEBP, maximo 5 MB
   - El nombre en disco es un UUID generado aqui: el nombre original no se usa (evita rutas como ../../) */
@Service
public class AlmacenImagenesLocalServiceImpl implements AlmacenImagenesService {
    private static final Logger log = LoggerFactory.getLogger(AlmacenImagenesLocalServiceImpl.class);

    public static final String PREFIJO_URL = "/uploads/inmuebles/";
    static final long TAMANO_MAXIMO_BYTES = 5L * 1024 * 1024;
    private static final Pattern NOMBRE_VALIDO = Pattern.compile("^[0-9a-f\\-]{36}\\.(jpg|png|webp)$");

    private final Path carpeta;

    public AlmacenImagenesLocalServiceImpl(@Value("${app.uploads.dir:uploads}") String directorio) {
        this.carpeta = Path.of(directorio, "inmuebles").toAbsolutePath().normalize();
    }

    @Override
    public String guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("La imagen está vacía.");
        }
        if (archivo.getSize() > TAMANO_MAXIMO_BYTES) {
            throw new IllegalArgumentException("Cada imagen puede pesar como máximo 5 MB.");
        }
        String extension = detectarExtension(archivo);
        String nombre = UUID.randomUUID() + "." + extension;
        try (InputStream entrada = archivo.getInputStream()) {
            Files.createDirectories(carpeta);
            Files.copy(entrada, carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la imagen en el servidor.", e);
        }
        return PREFIJO_URL + nombre;
    }

    @Override
    public void eliminar(String url) {
        if (url == null || !url.startsWith(PREFIJO_URL)) {
            return;
        }
        String nombre = url.substring(PREFIJO_URL.length());
        //Solo nombres generados por guardar(): cualquier otra cosa (ej. "../") se ignora
        if (!NOMBRE_VALIDO.matcher(nombre).matches()) {
            return;
        }
        try {
            Files.deleteIfExists(carpeta.resolve(nombre));
        } catch (IOException e) {
            log.warn("No se pudo borrar la imagen {}: {}", nombre, e.getMessage());
        }
    }

    //Firma (magic bytes) de cada formato permitido
    private String detectarExtension(MultipartFile archivo) {
        byte[] cabecera = new byte[12];
        int leidos;
        try (InputStream entrada = archivo.getInputStream()) {
            leidos = entrada.readNBytes(cabecera, 0, cabecera.length);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer la imagen.");
        }
        if (leidos >= 3 && (cabecera[0] & 0xFF) == 0xFF && (cabecera[1] & 0xFF) == 0xD8 && (cabecera[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (leidos >= 8 && (cabecera[0] & 0xFF) == 0x89 && cabecera[1] == 'P' && cabecera[2] == 'N' && cabecera[3] == 'G'
                && cabecera[4] == 0x0D && cabecera[5] == 0x0A && cabecera[6] == 0x1A && cabecera[7] == 0x0A) {
            return "png";
        }
        if (leidos >= 12 && cabecera[0] == 'R' && cabecera[1] == 'I' && cabecera[2] == 'F' && cabecera[3] == 'F'
                && cabecera[8] == 'W' && cabecera[9] == 'E' && cabecera[10] == 'B' && cabecera[11] == 'P') {
            return "webp";
        }
        throw new IllegalArgumentException("Formato no permitido: solo se aceptan imágenes JPG, PNG o WEBP.");
    }
}
