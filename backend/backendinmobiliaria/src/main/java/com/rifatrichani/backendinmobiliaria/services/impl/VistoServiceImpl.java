package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rifatrichani.backendinmobiliaria.dto.response.VistosResponseCrmDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.models.Visto;
import com.rifatrichani.backendinmobiliaria.repositories.FavoritoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.VistoRepository;
import com.rifatrichani.backendinmobiliaria.security.ControlAcceso;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.VistoService;

@Service
public class VistoServiceImpl implements VistoService {
    private final VistoRepository vistoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InmuebleRepository inmuebleRepository;
    private final FavoritoRepository favoritoRepository;

    public VistoServiceImpl(VistoRepository vr, UsuarioRepository ur, InmuebleRepository ir, FavoritoRepository fr) {
        this.vistoRepository = vr;
        this.usuarioRepository = ur;
        this.inmuebleRepository = ir;
        this.favoritoRepository = fr;
    }

    //En Supabase id_usuario es NOT NULL: solo se llama con un usuario autenticado (lo decide VistoController)
    @Override
    @Transactional
    public void registrarVista(Integer inmuebleId, Integer idUsuario) {
        if (!inmuebleRepository.existsById(inmuebleId)) {
            throw new ResourceNotFoundException("Inmueble no encontrado con ID: " + inmuebleId);
        }
        Visto visto = new Visto();
        visto.setInmueble(inmuebleRepository.getReferenceById(inmuebleId));
        visto.setUsuario(usuarioRepository.getReferenceById(idUsuario));
        visto.setVisto(true);
        vistoRepository.save(visto);
        usuarioRepository.registrarActividad(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VistosResponseCrmDTO> obtenerVistosParaCrm(CuentaAutenticada empleado) {
        List<Visto> vistos = empleado.esAdmin()
                ? vistoRepository.obtenerTodosParaCrm()
                : vistoRepository.obtenerPorEmpleadoParaCrm(empleado.id());
        return vistos.stream().map(this::mapearAVistosCrmDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VistosResponseCrmDTO> obtenerVistosPorInmuebleCrm(Integer inmuebleId, CuentaAutenticada empleado) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + inmuebleId));
        ControlAcceso.verificarEncargado(inmueble, empleado);
        return vistoRepository.obtenerPorInmuebleParaCrm(inmuebleId).stream()
                .map(this::mapearAVistosCrmDTO)
                .collect(Collectors.toList());
    }

    // Mapea cada Visto e indica si ese usuario también guardó el inmueble en favoritos
    private VistosResponseCrmDTO mapearAVistosCrmDTO(Visto visto) {
        VistosResponseCrmDTO dto = new VistosResponseCrmDTO();
        Inmueble inmueble = visto.getInmueble();
        Usuario usuario = visto.getUsuario();
        dto.setId(visto.getId());
        dto.setInmuebleId(inmueble.getId());
        dto.setTitulo(inmueble.getTitulo());
        dto.setFechaVista(visto.getFechaVista());
        dto.setUsuarioId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        dto.setFavoritoDelUsuario(favoritoRepository.existsByUsuarioIdAndInmuebleIdAndFavoritoTrue(usuario.getId(), inmueble.getId()));
        return dto;
    }
}
