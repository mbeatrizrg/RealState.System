package com.rifatrichani.backendinmobiliaria.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroContactoDTO;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.models.Usuario;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/* Arma el WHERE del listado de contactos (tabla Usuario) solo con los filtros que vienen con valor.
   Igual que InmuebleEspecificaciones: Criteria con parametros, el texto del usuario nunca se concatena al SQL */
public final class ContactoEspecificaciones {

    private ContactoEspecificaciones() {}

    //idEmpleado: empleado autenticado, se usa en la vista "mios"
    public static Specification<Usuario> conFiltros(FiltroContactoDTO f, Integer idEmpleado) {
        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();
            Expression<Integer> propietarioId = root.get("propietario").get("id");

            if ("mios".equals(f.getVista())) condiciones.add(cb.equal(propietarioId, idEmpleado));
            if ("sin_asignar".equals(f.getVista())) condiciones.add(cb.isNull(root.get("propietario")));

            if (f.getPropietarioIds() != null && !f.getPropietarioIds().isEmpty()) {
                List<Integer> ids = f.getPropietarioIds().stream().filter(id -> id != null && id != FiltroContactoDTO.SIN_VALOR).toList();
                List<Predicate> opciones = new ArrayList<>();
                if (!ids.isEmpty()) opciones.add(propietarioId.in(ids));
                if (f.getPropietarioIds().contains(FiltroContactoDTO.SIN_VALOR)) opciones.add(cb.isNull(root.get("propietario")));
                condiciones.add(cb.or(opciones.toArray(Predicate[]::new)));
            }

            if (f.getEstatus() != null && !f.getEstatus().isEmpty()) {
                condiciones.add(estatusDelUltimoLead(f.getEstatus(), root, query, cb));
            }

            agregarRango(condiciones, cb, root.get("fechaCreacion"), f.getCreadoDesde(), f.getCreadoHasta());
            agregarRango(condiciones, cb, root.get("ultimaActividad"), f.getActividadDesde(), f.getActividadHasta());
            if (f.getSinActividad() != null) {
                condiciones.add(f.getSinActividad() ? cb.isNull(root.get("ultimaActividad")) : cb.isNotNull(root.get("ultimaActividad")));
            }

            if (tieneTexto(f.getQ())) {
                String patron = patron(f.getQ());
                condiciones.add(cb.or(
                        cb.like(cb.lower(cb.concat(cb.concat(root.get("nombre"), " "), root.get("apellido"))), patron, '\\'),
                        cb.like(cb.lower(root.get("correo")), patron, '\\'),
                        cb.like(cb.lower(root.get("telefono")), patron, '\\')));
            }

            // ----- Filtros avanzados -----
            if (tieneTexto(f.getNombre())) {
                condiciones.add(cb.like(cb.lower(cb.concat(cb.concat(root.get("nombre"), " "), root.get("apellido"))), patron(f.getNombre()), '\\'));
            }
            if (tieneTexto(f.getCorreo())) condiciones.add(cb.like(cb.lower(root.get("correo")), patron(f.getCorreo()), '\\'));
            if (tieneTexto(f.getTelefono())) condiciones.add(cb.like(cb.lower(root.get("telefono")), patron(f.getTelefono()), '\\'));
            if (tieneTexto(f.getTipoNegocio())) condiciones.add(cb.like(cb.lower(root.get("tipoNegocio")), patron(f.getTipoNegocio()), '\\'));
            if (f.getCedula() != null) condiciones.add(cb.equal(root.get("cedula"), f.getCedula()));
            if (f.getVerificado() != null) condiciones.add(cb.equal(root.get("verificado"), f.getVerificado()));

            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }

    /* El estatus de un contacto es el de su lead mas reciente. El valor 0 significa "sin ningun lead".
       SQL equivalente: EXISTS (lead l WHERE l.id = (SELECT MAX(id) FROM lead WHERE id_usuario = u.id) AND l.id_estatus IN (...)) */
    private static Predicate estatusDelUltimoLead(List<Integer> estatus, Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Integer> valores = estatus.stream().filter(e -> e != null && e != FiltroContactoDTO.SIN_VALOR).toList();
        List<Predicate> opciones = new ArrayList<>();
        if (!valores.isEmpty()) {
            Subquery<Integer> ultimoLead = query.subquery(Integer.class);
            Root<Lead> l2 = ultimoLead.from(Lead.class);
            ultimoLead.select(cb.max(l2.get("id"))).where(cb.equal(l2.get("usuario"), root));

            Subquery<Integer> conEstatus = query.subquery(Integer.class);
            Root<Lead> l = conEstatus.from(Lead.class);
            conEstatus.select(l.get("id")).where(cb.equal(l.get("id"), ultimoLead), l.get("estatus").in(valores));
            opciones.add(cb.exists(conEstatus));
        }
        if (estatus.contains(FiltroContactoDTO.SIN_VALOR)) {
            Subquery<Integer> algunLead = query.subquery(Integer.class);
            Root<Lead> l = algunLead.from(Lead.class);
            algunLead.select(l.get("id")).where(cb.equal(l.get("usuario"), root));
            opciones.add(cb.not(cb.exists(algunLead)));
        }
        return cb.or(opciones.toArray(Predicate[]::new));
    }

    //Rango de dias completos: desde las 00:00 del primer dia hasta antes de las 00:00 del dia siguiente al ultimo.
    //Lo usan tambien NegocioEspecificaciones y PropiedadEspecificaciones
    static void agregarRango(List<Predicate> condiciones, CriteriaBuilder cb, Expression<LocalDateTime> campo,
                             LocalDate desde, LocalDate hasta) {
        if (desde != null) condiciones.add(cb.greaterThanOrEqualTo(campo, desde.atStartOfDay()));
        if (hasta != null) condiciones.add(cb.lessThan(campo, hasta.plusDays(1).atStartOfDay()));
    }

    static boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }

    //"%texto%" en minusculas y con los comodines de LIKE escapados (se usa con cb.like(..., '\\'))
    static String patron(String texto) {
        return "%" + InmuebleEspecificaciones.escaparLike(texto.trim().toLowerCase(Locale.ROOT)) + "%";
    }
}
