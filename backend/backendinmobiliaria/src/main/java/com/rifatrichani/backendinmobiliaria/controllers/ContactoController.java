package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroContactoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.PropietarioContactoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoContactosDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.OpcionFiltroDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.ColumnaContacto;
import com.rifatrichani.backendinmobiliaria.services.ContactoService;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.ArchivoExportado;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;

import jakarta.validation.Valid;

// CRM (vendedor y administrador): modulo de Contactos. Un contacto es un usuario del portal o un cliente registrado
@RestController
@RequestMapping("/api/contactos")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class ContactoController {
    private final ContactoService contactoService;
    public ContactoController(ContactoService cs) {
        this.contactoService = cs;
    }

    /* 1. Tabla de contactos con filtros y paginacion (ver FiltroContactoDTO). Ej:
          GET /api/contactos?vista=sin_asignar&estatus=1,2&creadoDesde=2026-10-01&orden=nombre&direccion=asc&pagina=0&tamano=25 */
    @GetMapping
    public ResponseEntity<PaginaResponseDTO<ContactoResponseDTO>> buscar(@Valid @ModelAttribute FiltroContactoDTO filtro) {
        return ResponseEntity.ok(contactoService.buscar(filtro, SesionActual.empleado()));
    }

    // 2. Cantidad de contactos de cada pestaña (todos, mios, sin asignar) con los mismos filtros
    @GetMapping("/conteos")
    public ResponseEntity<ConteoContactosDTO> contar(@Valid @ModelAttribute FiltroContactoDTO filtro) {
        return ResponseEntity.ok(contactoService.contar(filtro, SesionActual.empleado()));
    }

    /* 3. Exporta la tabla con los mismos filtros. formato=xlsx|pdf, columnas=nombre,correo,... (en ese orden),
          alcance=todos (todos los resultados) o pagina (solo la pagina indicada en pagina/tamano) */
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@Valid @ModelAttribute FiltroContactoDTO filtro,
                                           @RequestParam(defaultValue = "xlsx") String formato,
                                           @RequestParam(required = false) List<String> columnas,
                                           @RequestParam(defaultValue = "todos") String alcance) {
        FormatoExportacion tipo = switch (formato.toLowerCase(Locale.ROOT)) {
            case "xlsx", "excel" -> FormatoExportacion.XLSX;
            case "pdf" -> FormatoExportacion.PDF;
            default -> throw new IllegalArgumentException("Formato inválido (xlsx o pdf)");
        };
        if (!alcance.equals("todos") && !alcance.equals("pagina")) {
            throw new IllegalArgumentException("Alcance inválido (todos o pagina)");
        }
        ArchivoExportado archivo = contactoService.exportar(filtro, ColumnaContacto.desdeClaves(columnas), tipo,
                alcance.equals("pagina"), SesionActual.empleado());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.tipoContenido()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(archivo.nombre()).build().toString())
                .body(archivo.contenido());
    }

    // 4. Opciones de los filtros: empleados que pueden ser propietarios y catalogo de estatus del lead
    @GetMapping("/propietarios")
    public ResponseEntity<List<OpcionFiltroDTO>> propietarios() {
        return ResponseEntity.ok(contactoService.obtenerPropietarios());
    }

    @GetMapping("/estatus")
    public ResponseEntity<List<OpcionFiltroDTO>> estatus() {
        return ResponseEntity.ok(contactoService.obtenerEstatusLead());
    }

    // 5. Ficha del contacto: datos, leads, comunicaciones, favoritos y vistas recientes
    @GetMapping("/{id}")
    public ResponseEntity<ContactoDetalleResponseDTO> obtenerDetalle(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(contactoService.obtenerDetalle(id, SesionActual.empleado()));
    }

    // 6. Asignar, tomar o liberar el contacto. Body: {"empleadoId": 3} o {"empleadoId": null}
    @PatchMapping("/{id}/propietario")
    public ResponseEntity<Map<String, String>> cambiarPropietario(@PathVariable("id") Integer id, @RequestBody PropietarioContactoDTO dto) {
        contactoService.cambiarPropietario(id, dto.empleadoId(), SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", dto.empleadoId() == null ? "El contacto quedó sin asignar." : "Propietario del contacto actualizado."));
    }
}
