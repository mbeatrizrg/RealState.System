package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.request.GaleriaRequesDTO;
import com.rifatrichani.backendinmobiliaria.dto.response.ImagenResponseDTO;
import com.rifatrichani.backendinmobiliaria.exceptions.ResourceNotFoundException;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.security.ControlAcceso;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.services.AlmacenImagenesService;
import com.rifatrichani.backendinmobiliaria.services.GaleriaService;

@Service
public class GaleriaServiceImpl implements GaleriaService{
    private final GaleriaRepository galeriaRepository;
    private final InmuebleRepository inmuebleRepository;
    private final AlmacenImagenesService almacen;
    public GaleriaServiceImpl(GaleriaRepository gr, InmuebleRepository ir, AlmacenImagenesService almacen) {
        this.galeriaRepository = gr;
        this.inmuebleRepository = ir;
        this.almacen = almacen;
    }

    //Solo el vendedor encargado del inmueble o un administrador puede cambiar sus fotos
    @Transactional
    @Override
    public Integer agregarImagen(GaleriaRequesDTO dto, CuentaAutenticada empleado) {
        Inmueble inmueble = bloquearInmueble(dto.getInmuebleId());
        ControlAcceso.verificarEncargado(inmueble, empleado);
        verificarCupo(inmueble.getId(), 1);
        return guardarRegistro(inmueble, dto.getUrlImagen().trim()).getId();
    }

    /* Sube varias fotos de una vez. Si algo falla (formato, tamaño, cupo o la base de datos) no se guarda ninguna:
       la transaccion se revierte y los archivos ya escritos se borran del disco */
    @Transactional
    @Override
    public List<ImagenResponseDTO> subirImagenes(Integer inmuebleId, List<MultipartFile> archivos, CuentaAutenticada empleado) {
        if (archivos == null || archivos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una imagen.");
        }
        Inmueble inmueble = bloquearInmueble(inmuebleId);
        ControlAcceso.verificarEncargado(inmueble, empleado);
        verificarCupo(inmuebleId, archivos.size());

        List<String> escritas = new ArrayList<>();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int estado) {
                if (estado != STATUS_COMMITTED) {
                    escritas.forEach(almacen::eliminar);
                }
            }
        });
        List<ImagenResponseDTO> resultado = new ArrayList<>();
        for (MultipartFile archivo : archivos) {
            String url = almacen.guardar(archivo);
            escritas.add(url);
            resultado.add(new ImagenResponseDTO(guardarRegistro(inmueble, url).getId(), url));
        }
        return resultado;
    }

    //El archivo se borra del disco solo cuando el borrado en la base de datos se confirma
    @Transactional
    @Override
    public void eliminarImagen(Integer imagenId, CuentaAutenticada empleado) {
        Galeria galeria = galeriaRepository.findById(imagenId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + imagenId));
        ControlAcceso.verificarEncargado(galeria.getInmueble(), empleado);
        String url = galeria.getUrlImagen();
        galeriaRepository.delete(galeria);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                almacen.eliminar(url);
            }
        });
    }

    //Con la fila del inmueble bloqueada, dos subidas simultaneas no pueden pasarse del maximo
    private Inmueble bloquearInmueble(Integer inmuebleId) {
        return inmuebleRepository.bloquearPorId(inmuebleId)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble no encontrado con ID: " + inmuebleId));
    }

    private void verificarCupo(Integer inmuebleId, int nuevas) {
        long actuales = galeriaRepository.countByInmuebleId(inmuebleId);
        if (actuales + nuevas > MAX_IMAGENES_POR_INMUEBLE) {
            long disponibles = Math.max(0, MAX_IMAGENES_POR_INMUEBLE - actuales);
            throw new IllegalArgumentException("Cada inmueble admite máximo " + MAX_IMAGENES_POR_INMUEBLE + " imágenes. "
                    + (disponibles == 0 ? "Ya no hay espacio disponible." : "Puede agregar " + disponibles + " más."));
        }
    }

    private Galeria guardarRegistro(Inmueble inmueble, String url) {
        Galeria galeria = new Galeria();
        galeria.setUrlImagen(url);
        galeria.setInmueble(inmueble);
        return galeriaRepository.save(galeria);
    }
}
