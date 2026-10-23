package com.rifatrichani.backendinmobiliaria.repositories;

import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.agregarRango;
import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.patron;
import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.tieneTexto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroNegocioDTO;
import com.rifatrichani.backendinmobiliaria.models.Negocio;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

/* WHERE de la tabla y el tablero de negocios, solo con los filtros que vienen con valor.
   Igual que ContactoEspecificaciones: Criteria con parametros, el texto del usuario nunca se concatena al SQL */
public final class NegocioEspecificaciones {

    private NegocioEspecificaciones() {}

    //idEmpleado: empleado autenticado, se usa en la vista "mios"
    public static Specification<Negocio> conFiltros(FiltroNegocioDTO f, Integer idEmpleado) {
        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();
            //LEFT para no perder los negocios sin propietario
            Join<?, ?> propietario = root.join("propietario", JoinType.LEFT);
            Join<?, ?> inmueble = root.join("inmueble");
            Join<?, ?> contacto = root.join("contacto");
            Expression<Integer> propietarioId = propietario.get("id");

            if ("mios".equals(f.getVista())) condiciones.add(cb.equal(propietarioId, idEmpleado));
            if ("sin_asignar".equals(f.getVista())) condiciones.add(cb.isNull(root.get("propietario")));

            if (f.getPropietarioIds() != null && !f.getPropietarioIds().isEmpty()) {
                List<Integer> ids = f.getPropietarioIds().stream().filter(id -> id != null && id != FiltroNegocioDTO.SIN_VALOR).toList();
                List<Predicate> opciones = new ArrayList<>();
                if (!ids.isEmpty()) opciones.add(propietarioId.in(ids));
                if (f.getPropietarioIds().contains(FiltroNegocioDTO.SIN_VALOR)) opciones.add(cb.isNull(root.get("propietario")));
                condiciones.add(cb.or(opciones.toArray(Predicate[]::new)));
            }
            if (f.getEtapas() != null && !f.getEtapas().isEmpty()) condiciones.add(root.get("etapa").get("id").in(f.getEtapas()));
            if (f.getTiposOperacion() != null && !f.getTiposOperacion().isEmpty()) {
                condiciones.add(root.get("tipoOperacion").in(f.getTiposOperacion()));
            }
            if (f.getTipoInmuebleIds() != null && !f.getTipoInmuebleIds().isEmpty()) {
                condiciones.add(inmueble.get("tipoInmueble").get("id").in(f.getTipoInmuebleIds()));
            }

            agregarRango(condiciones, cb, root.get("fechaCreacion"), f.getCreadoDesde(), f.getCreadoHasta());
            if (f.getCierreDesde() != null) condiciones.add(cb.greaterThanOrEqualTo(root.get("fechaCierreEstimada"), f.getCierreDesde()));
            if (f.getCierreHasta() != null) condiciones.add(cb.lessThanOrEqualTo(root.get("fechaCierreEstimada"), f.getCierreHasta()));

            Expression<String> nombreContacto = cb.lower(cb.concat(cb.concat(contacto.get("nombre"), " "), contacto.get("apellido")));
            if (tieneTexto(f.getQ())) {
                String p = patron(f.getQ());
                condiciones.add(cb.or(
                        cb.like(cb.lower(root.get("nombre")), p, '\\'),
                        cb.like(nombreContacto, p, '\\'),
                        cb.like(cb.lower(inmueble.get("titulo")), p, '\\'),
                        cb.like(cb.lower(inmueble.get("rrbr")), p, '\\')));
            }

            // ----- Filtros avanzados -----
            if (f.getValorMin() != null) condiciones.add(cb.greaterThanOrEqualTo(root.get("valor"), f.getValorMin()));
            if (f.getValorMax() != null) condiciones.add(cb.lessThanOrEqualTo(root.get("valor"), f.getValorMax()));
            if (tieneTexto(f.getContacto())) condiciones.add(cb.like(nombreContacto, patron(f.getContacto()), '\\'));
            if (tieneTexto(f.getCodigoInmueble())) condiciones.add(cb.like(cb.lower(inmueble.get("rrbr")), patron(f.getCodigoInmueble()), '\\'));
            if (f.getAbiertos() != null) condiciones.add(cb.equal(root.get("etapa").get("abierto"), f.getAbiertos()));
            if (f.getContactoId() != null) condiciones.add(cb.equal(contacto.get("id"), f.getContactoId()));
            if (f.getInmuebleId() != null) condiciones.add(cb.equal(inmueble.get("id"), f.getInmuebleId()));

            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }
}
