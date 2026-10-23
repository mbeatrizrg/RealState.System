package com.rifatrichani.backendinmobiliaria.repositories;

import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.agregarRango;
import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.patron;
import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.tieneTexto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroPropiedadDTO;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;

import jakarta.persistence.criteria.Predicate;

//WHERE de la tabla del modulo Inmuebles del CRM (FiltroPropiedadDTO). El portal sigue usando InmuebleEspecificaciones
public final class PropiedadEspecificaciones {

    private PropiedadEspecificaciones() {}

    //idEmpleado: asesor autenticado, se usa en la vista "mios" (inmuebles captados por el)
    public static Specification<Inmueble> conFiltros(FiltroPropiedadDTO f, Integer idEmpleado) {
        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();

            if ("mios".equals(f.getVista())) condiciones.add(cb.equal(root.get("empleado").get("id"), idEmpleado));
            if ("disponibles".equals(f.getVista())) condiciones.add(cb.equal(root.get("estado"), Inmueble.ESTADO_DISPONIBLE));

            if (f.getAsesorIds() != null && !f.getAsesorIds().isEmpty()) condiciones.add(root.get("empleado").get("id").in(f.getAsesorIds()));
            if (f.getTipoInmuebleIds() != null && !f.getTipoInmuebleIds().isEmpty()) {
                condiciones.add(root.get("tipoInmueble").get("id").in(f.getTipoInmuebleIds()));
            }
            if (f.getCiudadIds() != null && !f.getCiudadIds().isEmpty()) condiciones.add(root.get("ciudad").get("id").in(f.getCiudadIds()));
            if (tieneTexto(f.getZona())) condiciones.add(cb.like(cb.lower(root.get("ubicacion")), patron(f.getZona()), '\\'));
            if (f.getPrecioMin() != null) condiciones.add(cb.greaterThanOrEqualTo(root.get("precio"), f.getPrecioMin()));
            if (f.getPrecioMax() != null) condiciones.add(cb.lessThanOrEqualTo(root.get("precio"), f.getPrecioMax()));
            if (f.getEstados() != null && !f.getEstados().isEmpty()) condiciones.add(root.get("estado").in(f.getEstados()));
            agregarRango(condiciones, cb, root.get("fechaCaptacion"), f.getCaptadoDesde(), f.getCaptadoHasta());

            if (tieneTexto(f.getQ())) {
                String p = patron(f.getQ());
                condiciones.add(cb.or(
                        cb.like(cb.lower(root.get("titulo")), p, '\\'),
                        cb.like(cb.lower(root.get("rrbr")), p, '\\'),
                        cb.like(cb.lower(root.get("ubicacion")), p, '\\')));
            }

            // ----- Filtros avanzados -----
            if (tieneTexto(f.getCodigo())) condiciones.add(cb.like(cb.lower(root.get("rrbr")), patron(f.getCodigo()), '\\'));
            if (tieneTexto(f.getDueno())) condiciones.add(cb.like(cb.lower(root.get("duenoNombre")), patron(f.getDueno()), '\\'));
            if (f.getOperacion() != null) condiciones.add(cb.equal(root.get("alquiler"), "alquiler".equals(f.getOperacion())));
            if (f.getHabitacionesMin() != null) condiciones.add(cb.greaterThanOrEqualTo(root.get("habitaciones"), f.getHabitacionesMin()));
            if (f.getBanosMin() != null) condiciones.add(cb.greaterThanOrEqualTo(root.get("banos"), f.getBanosMin()));
            if (f.getMetrosMin() != null) condiciones.add(cb.greaterThanOrEqualTo(root.get("metrosCuadrados"), f.getMetrosMin()));
            if (f.getMetrosMax() != null) condiciones.add(cb.lessThanOrEqualTo(root.get("metrosCuadrados"), f.getMetrosMax()));

            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }
}
