package com.rifatrichani.backendinmobiliaria.controllers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.ActividadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ActividadResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ArchivoResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.ActividadService;
import com.rifatrichani.backendinmobiliaria.services.ActividadService.ArchivoDescarga;

import jakarta.validation.Valid;

/* CRM (vendedor y administrador): historial de actividad del registro del contacto (accesos rapidos: nota, correo,
   llamada, tarea y reunion) y descarga de los archivos privados del CRM */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class ActividadController {
    private final ActividadService actividadService;
    public ActividadController(ActividadService as) {
        this.actividadService = as;
    }

    // 1. Historial: GET /api/actividades?contactoId=5 (con las comunicaciones de sus leads) o ?negocioId=3
    @GetMapping("/actividades")
    public ResponseEntity<List<ActividadResponseDTO>> historial(@RequestParam(required = false) Integer contactoId,
                                                                @RequestParam(required = false) Integer negocioId) {
        if (contactoId != null) return ResponseEntity.ok(actividadService.historialContacto(contactoId, SesionActual.empleado()));
        if (negocioId != null) return ResponseEntity.ok(actividadService.historialNegocio(negocioId));
        throw new IllegalArgumentException("Indique contactoId o negocioId.");
    }

    // 2. Adjuntos de todas las actividades del contacto
    @GetMapping("/actividades/adjuntos")
    public ResponseEntity<List<ArchivoResponseDTO>> adjuntos(@RequestParam Integer contactoId) {
        return ResponseEntity.ok(actividadService.adjuntosContacto(contactoId));
    }

    /* 3. Crear una actividad: multipart con la parte "datos" (JSON de ActividadRequestDTO, Content-Type application/json)
          y opcionalmente "archivos" repetido (max 5, 10 MB c/u). Un CORREO con enviarCorreo=true se envia al contacto */
    @PostMapping(value = "/actividades", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActividadResponseDTO> crear(@Valid @RequestPart("datos") ActividadRequestDTO dto,
                                                      @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actividadService.crear(dto, archivos, SesionActual.empleado()));
    }

    // 4. Marcar una tarea o reunion como hecha / pendiente. Body: {"completada": true}
    @PatchMapping("/actividades/{id}/completada")
    public ResponseEntity<Map<String, String>> completar(@PathVariable("id") Integer id, @RequestBody Map<String, Boolean> cuerpo) {
        boolean completada = Boolean.TRUE.equals(cuerpo.get("completada"));
        actividadService.marcarCompletada(id, completada, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", completada ? "Marcada como completada." : "Marcada como pendiente."));
    }

    @DeleteMapping("/actividades/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("id") Integer id) {
        actividadService.eliminar(id, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Actividad eliminada."));
    }

    // 5. Descarga de un adjunto o documento legal (no son publicos: requieren token)
    @GetMapping("/archivos/{id}")
    public ResponseEntity<Resource> descargar(@PathVariable("id") Integer id) {
        ArchivoDescarga archivo = actividadService.descargarArchivo(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.tipoContenido()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(archivo.nombre(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(archivo.contenido());
    }
}
