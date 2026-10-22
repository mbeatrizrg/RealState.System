package com.rifatrichani.backendinmobiliaria.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroInmuebleDTO;
import com.rifatrichani.backendinmobiliaria.models.Ciudad;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

/* Arma el WHERE de los listados de inmuebles solo con los filtros que vienen con valor.
   Se usa Criteria (consultas parametrizadas): el texto del usuario nunca se concatena al SQL */
public final class InmuebleEspecificaciones {

    private InmuebleEspecificaciones() {}

    public static Specification<Inmueble> conFiltros(FiltroInmuebleDTO f) {
        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();

            if (f.getCiudadId() != null) condiciones.add(cb.equal(root.get("ciudad").get("id"), f.getCiudadId()));
            if (f.getTipoInmuebleId() != null) condiciones.add(cb.equal(root.get("tipoInmueble").get("id"), f.getTipoInmuebleId()));
            if (f.getExtraId() != null) condiciones.add(cb.equal(root.get("extra").get("id"), f.getExtraId()));
            if (f.getEmpleadoId() != null) condiciones.add(cb.equal(root.get("empleado").get("id"), f.getEmpleadoId()));
            if (f.getIsAlquiler() != null) condiciones.add(cb.equal(root.get("alquiler"), f.getIsAlquiler()));
            if (f.getVendido() != null) condiciones.add(cb.equal(root.get("vendido"), f.getVendido()));
            if (f.getMinPrecio() != null) condiciones.add(cb.greaterThanOrEqualTo(root.get("precio"), f.getMinPrecio()));
            if (f.getMaxPrecio() != null) condiciones.add(cb.lessThanOrEqualTo(root.get("precio"), f.getMaxPrecio()));

            if (f.getQ() != null && !f.getQ().isBlank()) {
                String patron = "%" + escaparLike(f.getQ().trim().toLowerCase(Locale.ROOT)) + "%";
                Join<Inmueble, Ciudad> ciudad = root.join("ciudad", JoinType.LEFT);
                condiciones.add(cb.or(
                        cb.like(cb.lower(root.get("titulo")), patron, '\\'),
                        cb.like(cb.lower(root.get("ubicacion")), patron, '\\'),
                        cb.like(cb.lower(root.get("descripcion")), patron, '\\'),
                        cb.like(cb.lower(root.get("rrbr")), patron, '\\'),
                        cb.like(cb.lower(ciudad.get("nombre")), patron, '\\')));
            }
            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }

    //Los comodines % y _ escritos por el usuario se buscan como texto literal
    static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
