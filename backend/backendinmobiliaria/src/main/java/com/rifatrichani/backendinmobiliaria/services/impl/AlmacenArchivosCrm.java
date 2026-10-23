package com.rifatrichani.backendinmobiliaria.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;

/* Archivos privados del CRM: adjuntos de actividades (correos, grabaciones de llamadas) y documentos legales de inmuebles.
   Se guardan en UPLOADS_DIR/privados y NO se publican en /uploads: se descargan con token en GET /api/archivos/{id}.
   Igual que AlmacenImagenesLocalServiceImpl, el tipo se detecta por los primeros bytes y el nombre en disco es un UUID */
@Component
public class AlmacenArchivosCrm {
    private static final Logger log = LoggerFactory.getLogger(AlmacenArchivosCrm.class);

    public static final long TAMANO_MAXIMO_BYTES = 10L * 1024 * 1024;
    private static final Pattern RUTA_VALIDA = Pattern.compile("^[0-9a-f\\-]{36}\\.[a-z0-9]{2,4}$");

    public record ArchivoGuardado(String ruta, String nombre, String tipoContenido, long tamano) {}

    private record Tipo(String extension, String contenido) {}

    private final Path carpeta;

    public AlmacenArchivosCrm(@Value("${app.uploads.dir:uploads}") String directorio) {
        this.carpeta = Path.of(directorio, "privados").toAbsolutePath().normalize();
    }

    public ArchivoGuardado guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío.");
        }
        if (archivo.getSize() > TAMANO_MAXIMO_BYTES) {
            throw new IllegalArgumentException("Cada archivo puede pesar como máximo 10 MB.");
        }
        String nombre = nombreVisible(archivo.getOriginalFilename());
        Tipo tipo = detectarTipo(archivo, nombre);
        String ruta = UUID.randomUUID() + "." + tipo.extension();
        try (InputStream entrada = archivo.getInputStream()) {
            Files.createDirectories(carpeta);
            Files.copy(entrada, carpeta.resolve(ruta), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo en el servidor.", e);
        }
        return new ArchivoGuardado(ruta, nombre, tipo.contenido(), archivo.getSize());
    }

    public Resource leer(String ruta) {
        if (ruta == null || !RUTA_VALIDA.matcher(ruta).matches() || !Files.isRegularFile(carpeta.resolve(ruta))) {
            throw new ResourceNotFoundException("El archivo ya no existe en el servidor.");
        }
        return new FileSystemResource(carpeta.resolve(ruta));
    }

    public void eliminar(String ruta) {
        //Solo nombres generados por guardar(): cualquier otra cosa (ej. "../") se ignora
        if (ruta == null || !RUTA_VALIDA.matcher(ruta).matches()) {
            return;
        }
        try {
            Files.deleteIfExists(carpeta.resolve(ruta));
        } catch (IOException e) {
            log.warn("No se pudo borrar el archivo {}: {}", ruta, e.getMessage());
        }
    }

    //Sin carpetas ni caracteres de control: solo se usa para mostrar y como nombre sugerido al descargar
    private static String nombreVisible(String original) {
        String nombre = original == null ? "" : original.replace('\\', '/');
        nombre = nombre.substring(nombre.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}\"]", "").trim();
        if (nombre.isEmpty()) nombre = "archivo";
        return nombre.length() > 200 ? nombre.substring(nombre.length() - 200) : nombre;
    }

    /* Formatos permitidos segun su firma (magic bytes): PDF, imagenes, documentos de Office y audio de grabaciones.
       Los formatos de Office modernos son un zip: la extension del nombre decide si es docx, xlsx o pptx */
    private static Tipo detectarTipo(MultipartFile archivo, String nombre) {
        byte[] c = new byte[12];
        int leidos;
        try (InputStream entrada = archivo.getInputStream()) {
            leidos = entrada.readNBytes(c, 0, c.length);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo.");
        }
        String extension = nombre.contains(".") ? nombre.substring(nombre.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "";
        if (leidos >= 4 && c[0] == '%' && c[1] == 'P' && c[2] == 'D' && c[3] == 'F') return new Tipo("pdf", "application/pdf");
        if (leidos >= 3 && (c[0] & 0xFF) == 0xFF && (c[1] & 0xFF) == 0xD8 && (c[2] & 0xFF) == 0xFF) return new Tipo("jpg", "image/jpeg");
        if (leidos >= 8 && (c[0] & 0xFF) == 0x89 && c[1] == 'P' && c[2] == 'N' && c[3] == 'G') return new Tipo("png", "image/png");
        if (leidos >= 12 && c[0] == 'R' && c[1] == 'I' && c[2] == 'F' && c[3] == 'F') {
            if (c[8] == 'W' && c[9] == 'E' && c[10] == 'B' && c[11] == 'P') return new Tipo("webp", "image/webp");
            if (c[8] == 'W' && c[9] == 'A' && c[10] == 'V' && c[11] == 'E') return new Tipo("wav", "audio/wav");
        }
        if (leidos >= 4 && c[0] == 'P' && c[1] == 'K' && c[2] == 3 && c[3] == 4) {
            return switch (extension) {
                case "docx" -> new Tipo("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                case "xlsx" -> new Tipo("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                case "pptx" -> new Tipo("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
                default -> throw new IllegalArgumentException("Formato no permitido: los archivos comprimidos solo se aceptan como .docx, .xlsx o .pptx.");
            };
        }
        if (leidos >= 4 && (c[0] & 0xFF) == 0xD0 && (c[1] & 0xFF) == 0xCF && (c[2] & 0xFF) == 0x11 && (c[3] & 0xFF) == 0xE0) {
            return "xls".equals(extension) ? new Tipo("xls", "application/vnd.ms-excel") : new Tipo("doc", "application/msword");
        }
        if (leidos >= 3 && c[0] == 'I' && c[1] == 'D' && c[2] == '3') return new Tipo("mp3", "audio/mpeg");
        if (leidos >= 2 && (c[0] & 0xFF) == 0xFF && (c[1] & 0xE0) == 0xE0) return new Tipo("mp3", "audio/mpeg");
        if (leidos >= 4 && c[0] == 'O' && c[1] == 'g' && c[2] == 'g' && c[3] == 'S') return new Tipo("ogg", "audio/ogg");
        if (leidos >= 8 && c[4] == 'f' && c[5] == 't' && c[6] == 'y' && c[7] == 'p') return new Tipo("m4a", "audio/mp4");
        throw new IllegalArgumentException("Formato no permitido: se aceptan PDF, imágenes, documentos de Office y audios (MP3, WAV, OGG, M4A).");
    }
}
