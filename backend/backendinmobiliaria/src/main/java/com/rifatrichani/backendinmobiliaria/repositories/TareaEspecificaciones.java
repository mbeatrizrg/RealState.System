package com.rifatrichani.backendinmobiliaria.repositories;

import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.agregarRango;
import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.patron;
import static com.rifatrichani.backendinmobiliaria.repositories.ContactoEspecificaciones.tieneTexto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.rifatrichani.backendinmobiliaria.dto.request.FiltroTareaDTO;
import com.rifatrichani.backendinmobiliaria.models.Actividad;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/* WHERE de la tabla de tareas: actividades de tipo TAREA con los filtros que vienen con valor (FiltroTareaDTO).
   "vencida" = no completada y con la fecha de vencimiento anterior a ahora. ordenPrioridad agrega el ORDER BY alta > media > baja,
   que Sort no puede expresar (el servicio lo usa con una paginacion sin orden) */
public final class TareaEspecificaciones {

    private TareaEspecificaciones() {}

    public static Specification<Actividad> conFiltros(FiltroTareaDTO f, Integer idEmpleado, LocalDateTime ahora, boolean ordenPrioridad, boolean asc) {
        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();
            condiciones.add(cb.equal(root.get("tipo"), Actividad.Tipo.TAREA));
            Join<?, ?> propietario = root.join("empleado", JoinType.LEFT);
            Join<?, ?> contacto = root.join("contacto", JoinType.LEFT);
            Join<?, ?> negocio = root.join("negocio", JoinType.LEFT);
            Join<?, ?> inmueble = root.join("inmueble", JoinType.LEFT);
            Join<?, ?> inmuebleNegocio = negocio.join("inmueble", JoinType.LEFT);
            Path<LocalDateTime> vence = root.get("fechaProgramada");
            Predicate completada = cb.equal(root.get("estadoTarea"), Actividad.COMPLETADA);
            Predicate vencida = cb.and(cb.not(completada), cb.lessThan(vence, ahora));

            switch (f.getVista()) {
                case "mias_pendientes" -> {
                    condiciones.add(cb.equal(propietario.get("id"), idEmpleado));
                    condiciones.add(cb.not(completada));
                }
                case "completadas_vencidas" -> condiciones.add(cb.or(completada, vencida));
                default -> { }
            }

            if (f.getPropietarioIds() != null && !f.getPropietarioIds().isEmpty()) {
                List<Integer> ids = f.getPropietarioIds().stream().filter(id -> id != null && id != FiltroTareaDTO.SIN_VALOR).toList();
                List<Predicate> opciones = new ArrayList<>();
                if (!ids.isEmpty()) opciones.add(propietario.get("id").in(ids));
                if (f.getPropietarioIds().contains(FiltroTareaDTO.SIN_VALOR)) opciones.add(cb.isNull(root.get("empleado")));
                condiciones.add(cb.or(opciones.toArray(Predicate[]::new)));
            }
            if (f.getTiposTarea() != null && !f.getTiposTarea().isEmpty()) condiciones.add(root.get("tipoTarea").in(f.getTiposTarea()));
            if (f.getPrioridades() != null && !f.getPrioridades().isEmpty()) condiciones.add(root.get("prioridad").in(f.getPrioridades()));
            if (f.getEstados() != null && !f.getEstados().isEmpty()) {
                List<Predicate> opciones = new ArrayList<>();
                Predicate aTiempo = cb.or(cb.isNull(vence), cb.greaterThanOrEqualTo(vence, ahora));
                for (String estado : f.getEstados()) {
                    switch (estado) {
                        case "vencida" -> opciones.add(vencida);
                        case "completada" -> opciones.add(completada);
                        default -> opciones.add(cb.and(cb.equal(root.get("estadoTarea"), estado), aTiempo));
                    }
                }
                condiciones.add(cb.or(opciones.toArray(Predicate[]::new)));
            }
            agregarRango(condiciones, cb, vence, f.getVenceDesde(), f.getVenceHasta());

            Expression<String> nombreContacto = cb.lower(cb.concat(cb.concat(contacto.get("nombre"), " "), contacto.get("apellido")));
            if (tieneTexto(f.getQ())) {
                String p = patron(f.getQ());
                condiciones.add(cb.or(
                        cb.like(cb.lower(root.get("asunto")), p, '\\'),
                        cb.like(cb.lower(root.get("descripcion")), p, '\\'),
                        cb.like(nombreContacto, p, '\\')));
            }

            // ----- Filtros avanzados -----
            if (tieneTexto(f.getContacto())) condiciones.add(cb.like(nombreContacto, patron(f.getContacto()), '\\'));
            if (tieneTexto(f.getCodigoInmueble())) {
                String p = patron(f.getCodigoInmueble());
                condiciones.add(cb.or(cb.like(cb.lower(inmueble.get("rrbr")), p, '\\'), cb.like(cb.lower(inmuebleNegocio.get("rrbr")), p, '\\')));
            }
            if (tieneTexto(f.getNegocio())) condiciones.add(cb.like(cb.lower(negocio.get("nombre")), patron(f.getNegocio()), '\\'));
            if (Boolean.TRUE.equals(f.getSinContacto())) condiciones.add(cb.isNull(root.get("contacto")));
            if (f.getContactoId() != null) condiciones.add(cb.equal(contacto.get("id"), f.getContactoId()));
            if (f.getNegocioId() != null) condiciones.add(cb.equal(negocio.get("id"), f.getNegocioId()));
            if (f.getInmuebleId() != null) {
                condiciones.add(cb.or(cb.equal(inmueble.get("id"), f.getInmuebleId()), cb.equal(inmuebleNegocio.get("id"), f.getInmuebleId())));
            }

            //Solo en la consulta de las filas (no en la de conteo)
            if (ordenPrioridad && query.getResultType() != Long.class && query.getResultType() != long.class) {
                ordenarPorPrioridad(root, query, cb, asc);
            }
            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }

    private static void ordenarPorPrioridad(Root<Actividad> root, jakarta.persistence.criteria.CriteriaQuery<?> query, CriteriaBuilder cb, boolean asc) {
        Expression<Integer> peso = cb.<Integer>selectCase()
                .when(cb.equal(root.get("prioridad"), "alta"), 3)
                .when(cb.equal(root.get("prioridad"), "media"), 2)
                .otherwise(1);
        query.orderBy(asc ? cb.asc(peso) : cb.desc(peso), cb.asc(root.get("fechaProgramada")), cb.asc(root.get("id")));
    }
}
