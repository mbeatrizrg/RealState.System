package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.VistoDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.VistosResponseCrmDTO;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.models.Usuario;
import com.rifatrichani.backendinmobiliaria.models.Visto;
import com.rifatrichani.backendinmobiliaria.repositories.FavoritoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.repositories.UsuarioRepository;
import com.rifatrichani.backendinmobiliaria.repositories.VistoRepository;
import com.rifatrichani.backendinmobiliaria.services.VistoService;

import jakarta.transaction.Transactional;

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

    // Se registra la vista desde el sitio web validando si el usuario está registrado o es visitante anónimo
    @Transactional
    @Override
    public void registrarVista(VistoDTO dto) {
        Visto visto = new Visto();
        visto.setInmueble(inmuebleRepository.getReferenceById(dto.getInmueble()));
        if (dto.getUsuario() != null) {
            visto.setUsuario(usuarioRepository.getReferenceById(dto.getUsuario()));
        } else {
            visto.setUsuario(null);
        }
        vistoRepository.save(visto);
    }

    @Override
    public List<VistosResponseCrmDTO> obtenerVistosParaCrm() {
        return vistoRepository.obtenerTodosParaCrm().stream()
                .map(this::mapearAVistosCrmDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VistosResponseCrmDTO> obtenerVistosPorInmuebleCrm(Integer inmuebleId) {
        return vistoRepository.obtenerPorInmuebleParaCrm(inmuebleId).stream()
                .map(this::mapearAVistosCrmDTO)
                .collect(Collectors.toList());
    }

    // Mapea cada Visto hacia VistosResponseCrmDTO e inyecta el booleano isFavoritoFromUser
    private VistosResponseCrmDTO mapearAVistosCrmDTO(Visto visto) {
        VistosResponseCrmDTO dto = new VistosResponseCrmDTO();
        Inmueble inmueble = visto.getInmueble();
        Usuario usuario = visto.getUsuario();
        dto.setId(visto.getId());
        dto.setInmuebleId(inmueble.getId());
        dto.setTitulo(inmueble.getTitulo());
        dto.setFechaVista(visto.getFechaVista());
        if (usuario != null) {
            dto.setUsuarioId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setApellido(usuario.getApellido());
            dto.setCorreo(usuario.getCorreo());
            // Verifica si este usuario registrado también guardó este inmueble en Favoritos
            boolean esFav = favoritoRepository.existsByUsuarioIdAndInmuebleId(usuario.getId(), inmueble.getId());
            dto.setFavoritoFromUser(esFav);
        } else {
            dto.setNombre("Visitante Anónimo");
            dto.setFavoritoFromUser(false);
        }
        return dto;
    }
}