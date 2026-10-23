package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.FichaPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FiltroPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ArchivoResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PropiedadResponseDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.ColumnaPropiedad;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.PropiedadService;

import jakarta.validation.Valid;

/* CRM (vendedor y administrador): modulo Inmuebles / Propiedades. El registro y la edicion de los datos basicos
   siguen en /api/inmueble (pantalla "Añadir inmueble") */
@RestController
@RequestMapping("/api/propiedades")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class PropiedadController {
    private final PropiedadService propiedadService;
    public PropiedadController(PropiedadService ps) {
        this.propiedadService = ps;
    }

    /* 1. Tabla de inmuebles con filtros y paginacion (ver FiltroPropiedadDTO). Ej:
          GET /api/propiedades?vista=disponibles&tipoInmuebleIds=1,3&precioMin=50000&estados=disponible,reservado */
    @GetMapping
    public ResponseEntity<PaginaResponseDTO<PropiedadResponseDTO>> buscar(@Valid @ModelAttribute FiltroPropiedadDTO filtro) {
        return ResponseEntity.ok(propiedadService.buscar(filtro, SesionActual.empleado()));
    }

    // 2. Cantidad de cada pestaña: todos, mis inmuebles captados y disponibles
    @GetMapping("/conteos")
    public ResponseEntity<ConteoPestanasDTO> contar(@Valid @ModelAttribute FiltroPropiedadDTO filtro) {
        return ResponseEntity.ok(propiedadService.contar(filtro, SesionActual.empleado()));
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@Valid @ModelAttribute FiltroPropiedadDTO filtro,
                                           @RequestParam(defaultValue = "xlsx") String formato,
                                           @RequestParam(required = false) List<String> columnas,
                                           @RequestParam(defaultValue = "todos") String alcance) {
        return ContactoController.descarga(propiedadService.exportar(filtro, ColumnaPropiedad.desdeClaves(columnas),
                FormatoExportacion.desde(formato), ContactoController.soloPagina(alcance), SesionActual.empleado()));
    }

    // 3. Ficha del inmueble: fotos, caracteristicas, documentos legales, negocios y clientes interesados
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadDetalleResponseDTO> obtenerDetalle(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(propiedadService.obtenerDetalle(id, SesionActual.empleado()));
    }

    // 4. Estado comercial, dueño y caracteristicas fisicas (asesor captador o administrador)
    @PutMapping("/{id}/ficha")
    public ResponseEntity<Map<String, String>> actualizarFicha(@PathVariable("id") Integer id, @Valid @RequestBody FichaPropiedadDTO dto) {
        propiedadService.actualizarFicha(id, dto, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Ficha del inmueble actualizada."));
    }

    // 5. Documentos legales: multipart con el campo "archivos" repetido (PDF, imagenes u Office, max 10 MB c/u)
    @PostMapping(value = "/{id}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ArchivoResponseDTO>> subirDocumentos(@PathVariable("id") Integer id,
                                                                    @RequestParam("archivos") List<MultipartFile> archivos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propiedadService.subirDocumentos(id, archivos, SesionActual.empleado()));
    }

    @DeleteMapping("/documentos/{documentoId}")
    public ResponseEntity<Map<String, String>> eliminarDocumento(@PathVariable("documentoId") Integer documentoId) {
        propiedadService.eliminarDocumento(documentoId, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Documento eliminado."));
    }
}
