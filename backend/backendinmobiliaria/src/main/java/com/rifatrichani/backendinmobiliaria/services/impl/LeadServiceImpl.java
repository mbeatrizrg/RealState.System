package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.request.LeadRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.LeadResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Lead;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.repositories.EstatusLeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.LeadRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.security.ControlAcceso;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.LeadService;

@Service
public class LeadServiceImpl implements LeadService {
    //Estatus con el que entra un lead generado desde el portal ("Nuevo" en el catalogo estatus_lead)
    public static final int ESTATUS_INICIAL = 1;

    private final LeadRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final InmuebleRepository inmuebleRepository;
    private final EstatusLeadRepository estatusLeadRepository;

    public LeadServiceImpl(LeadRepository lr, UsuarioRepository ur, InmuebleRepository ir, EstatusLeadRepository elr) {
        this.leadRepository = lr;
        this.usuarioRepository = ur;
        this.inmuebleRepository = ir;
        this.estatusLeadRepository = elr;
    }

    //CRM: un vendedor solo puede crear leads de sus propios inmuebles
    @Override
    @Transactional
    public Integer registrarLead(LeadRequestDTO dto, CuentaAutenticada empleado) {
        Inmueble inmueble = inmuebleRepository.findById(dto.getInmuebleId())
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + dto.getInmuebleId()));
        ControlAcceso.verificarEncargado(inmueble, empleado);
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + dto.getUsuarioId()));
        if (leadRepository.existsByUsuarioIdAndInmuebleId(usuario.getId(), inmueble.getId())) {
            throw new IllegalArgumentException("Este cliente ya tiene un lead para este inmueble.");
        }
        verificarEstatus(dto.getEstatus());
        Lead lead = new Lead();
        lead.setUsuario(usuario);
        lead.setInmueble(inmueble);
        lead.setEstatus(dto.getEstatus());
        Integer id = leadRepository.save(lead).getId();
        actualizarContacto(usuario.getId(), inmueble);
        return id;
    }

    //Portal: "Contáctanos" en el detalle de un inmueble. Si ya existe el lead no se duplica
    @Override
    @Transactional
    public boolean registrarInteres(Integer inmuebleId, Integer idUsuario) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + inmuebleId));
        if (Boolean.TRUE.equals(inmueble.getVendido())) {
            throw new IllegalArgumentException("Este inmueble ya no está disponible.");
        }
        if (leadRepository.existsByUsuarioIdAndInmuebleId(idUsuario, inmuebleId)) {
            return false;
        }
        Lead lead = new Lead();
        lead.setUsuario(usuarioRepository.getReferenceById(idUsuario));
        lead.setInmueble(inmueble);
        lead.setEstatus(ESTATUS_INICIAL);
        leadRepository.save(lead);
        actualizarContacto(idUsuario, inmueble);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadResponseDTO> obtenerLeads(CuentaAutenticada empleado) {
        List<Lead> leads = empleado.esAdmin()
                ? leadRepository.obtenerTodosConRelaciones()
                : leadRepository.obtenerPorEmpleadoId(empleado.id());
        return leads.stream().map(LeadServiceImpl::mapearALeadResponseDTO).collect(Collectors.toList());
    }

    //Un vendedor solo puede consultar sus propios leads; el admin los de cualquier vendedor
    @Override
    @Transactional(readOnly = true)
    public List<LeadResponseDTO> obtenerLeadsPorVendedor(Integer empleadoId, CuentaAutenticada empleado) {
        if (!empleado.esAdmin() && !empleado.id().equals(empleadoId)) {
            throw new SecurityException("Solo puede consultar sus propios leads.");
        }
        return leadRepository.obtenerPorEmpleadoId(empleadoId).stream()
                .map(LeadServiceImpl::mapearALeadResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LeadResponseDTO obtenerLead(Integer leadId, CuentaAutenticada empleado) {
        Lead lead = buscarLead(leadId);
        ControlAcceso.verificarEncargado(lead, empleado);
        return mapearALeadResponseDTO(lead);
    }

    @Override
    @Transactional
    public void actualizarEstatus(Integer leadId, Integer estatus, CuentaAutenticada empleado) {
        Lead lead = buscarLead(leadId);
        ControlAcceso.verificarEncargado(lead, empleado);
        verificarEstatus(estatus);
        lead.setEstatus(estatus);
        leadRepository.save(lead);
        usuarioRepository.registrarActividad(lead.getUsuario().getId());
    }

    //Modulo de Contactos: un lead nuevo es actividad del contacto y, si no tenia propietario, queda a cargo del vendedor del inmueble
    private void actualizarContacto(Integer idUsuario, Inmueble inmueble) {
        usuarioRepository.registrarActividad(idUsuario);
        if (inmueble.getEmpleado() != null) {
            usuarioRepository.asignarPropietarioSiNoTiene(idUsuario, inmueble.getEmpleado());
        }
    }

    //Catalogo estatus_lead (db/migraciones/005_contactos.sql)
    private void verificarEstatus(Integer estatus) {
        if (estatus == null || !estatusLeadRepository.existsById(estatus)) {
            throw new IllegalArgumentException("Estatus de lead inválido: " + estatus);
        }
    }

    private Lead buscarLead(Integer leadId) {
        return leadRepository.obtenerConRelaciones(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con ID: " + leadId));
    }

    //Tambien lo usa la ficha de contactos (ContactoServiceImpl)
    static LeadResponseDTO mapearALeadResponseDTO(Lead l) {
        LeadResponseDTO dto = new LeadResponseDTO();
        Usuario cliente = l.getUsuario();
        Inmueble inmueble = l.getInmueble();
        Empleado vendedor = inmueble.getEmpleado();
        dto.setId(l.getId());
        dto.setUsuarioId(cliente.getId());
        dto.setNombreCliente(cliente.getNombre() + " " + cliente.getApellido());
        dto.setCorreoCliente(cliente.getCorreo());
        dto.setTelefonoCliente(cliente.getTelefono());
        dto.setInmuebleId(inmueble.getId());
        dto.setTituloInmueble(inmueble.getTitulo());
        dto.setRrbrInmueble(inmueble.getRrbr());
        dto.setEmpleadoId(vendedor.getId());
        dto.setNombreVendedor(vendedor.getNombre() + (vendedor.getApellido() != null ? " " + vendedor.getApellido() : ""));
        dto.setEstatus(l.getEstatus());
        return dto;
    }
}
