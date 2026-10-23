package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.CambioEtapaDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.FiltroNegocioDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.NegocioRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ConteoPestanasDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.EtapaNegocioDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.NegocioDetalleResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.NegocioResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.PaginaResponseDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.TableroNegociosDTO;
import com.rifatrichani.backendinmobiliaria.security.SesionActual;
import com.rifatrichani.backendinmobiliaria.services.ColumnaNegocio;
import com.rifatrichani.backendinmobiliaria.services.ContactoService.FormatoExportacion;
import com.rifatrichani.backendinmobiliaria.services.NegocioService;

import jakarta.validation.Valid;

// CRM (vendedor y administrador): modulo de Negocios, oportunidades de venta o alquiler de un inmueble con un contacto
@RestController
@RequestMapping("/api/negocios")
@PreAuthorize("hasAnyAuthority('ROLE_VENDEDOR', 'ROLE_ADMIN')")
public class NegocioController {
    private final NegocioService negocioService;
    public NegocioController(NegocioService ns) {
        this.negocioService = ns;
    }

    /* 1. Tabla de negocios con filtros y paginacion (ver FiltroNegocioDTO). Ej:
          GET /api/negocios?vista=mios&etapas=1,2&tiposOperacion=venta&orden=valor&direccion=desc */
    @GetMapping
    public ResponseEntity<PaginaResponseDTO<NegocioResponseDTO>> buscar(@Valid @ModelAttribute FiltroNegocioDTO filtro) {
        return ResponseEntity.ok(negocioService.buscar(filtro, SesionActual.empleado()));
    }

    // 2. Cantidad de negocios de cada pestaña (todos, mios, sin asignar) con los mismos filtros
    @GetMapping("/conteos")
    public ResponseEntity<ConteoPestanasDTO> contar(@Valid @ModelAttribute FiltroNegocioDTO filtro) {
        return ResponseEntity.ok(negocioService.contar(filtro, SesionActual.empleado()));
    }

    // 3. Vista de tablero (pipeline): una columna por etapa con sus tarjetas y el monto total
    @GetMapping("/tablero")
    public ResponseEntity<TableroNegociosDTO> tablero(@Valid @ModelAttribute FiltroNegocioDTO filtro) {
        return ResponseEntity.ok(negocioService.tablero(filtro, SesionActual.empleado()));
    }

    // 4. Exportacion Excel o PDF, igual que /api/contactos/exportar
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@Valid @ModelAttribute FiltroNegocioDTO filtro,
                                           @RequestParam(defaultValue = "xlsx") String formato,
                                           @RequestParam(required = false) List<String> columnas,
                                           @RequestParam(defaultValue = "todos") String alcance) {
        return ContactoController.descarga(negocioService.exportar(filtro, ColumnaNegocio.desdeClaves(columnas),
                FormatoExportacion.desde(formato), ContactoController.soloPagina(alcance), SesionActual.empleado()));
    }

    // 5. Catalogo de etapas (filtros, select del formulario y columnas del tablero)
    @GetMapping("/etapas")
    public ResponseEntity<List<EtapaNegocioDTO>> etapas() {
        return ResponseEntity.ok(negocioService.obtenerEtapas());
    }

    // 6. Pagina del negocio: datos, inmueble con fotos, cliente e historial de seguimiento
    @GetMapping("/{id}")
    public ResponseEntity<NegocioDetalleResponseDTO> obtenerDetalle(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(negocioService.obtenerDetalle(id, SesionActual.empleado()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody NegocioRequestDTO dto) {
        Integer id = negocioService.crear(dto, SesionActual.empleado());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id, "mensaje", "Negocio creado."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizar(@PathVariable("id") Integer id, @Valid @RequestBody NegocioRequestDTO dto) {
        negocioService.actualizar(id, dto, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Negocio actualizado."));
    }

    // 7. Mover de etapa (arrastrar y soltar en el tablero). Body: {"etapaId": 3}
    @PatchMapping("/{id}/etapa")
    public ResponseEntity<Map<String, String>> cambiarEtapa(@PathVariable("id") Integer id, @Valid @RequestBody CambioEtapaDTO dto) {
        negocioService.cambiarEtapa(id, dto.etapaId(), SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Etapa del negocio actualizada."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("id") Integer id) {
        negocioService.eliminar(id, SesionActual.empleado());
        return ResponseEntity.ok(Map.of("mensaje", "Negocio eliminado."));
    }
}
