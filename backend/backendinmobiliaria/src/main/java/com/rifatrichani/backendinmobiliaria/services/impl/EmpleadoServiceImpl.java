package com.rifatrichani.backendinmobiliaria.services.impl;
import org.springframework.stereotype.Service;

import com.rifatrichani.backendinmobiliaria.dto.request.EmpleadoRequestDTO;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.repositories.EmpleadoRepository;
import com.rifatrichani.backendinmobiliaria.repositories.TipoCedulaRepository;
import com.rifatrichani.backendinmobiliaria.services.EmpleadoService;
import jakarta.transaction.Transactional;
@Service
public class EmpleadoServiceImpl implements EmpleadoService{
    private final EmpleadoRepository empleadoRepository;
    private final TipoCedulaRepository tipoCedulaRepository;
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, TipoCedulaRepository tipoCedulaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.tipoCedulaRepository = tipoCedulaRepository;
    }

    //Registro de empleado
    @Transactional
    @Override 
    public void registrarEmpleado(EmpleadoRequestDTO dto) {
        if (empleadoRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está asignado a otro empleado.");
        }
        if (empleadoRepository.existsByCedula(dto.getCedula())) {
            throw new IllegalArgumentException("La cédula ya se encuentra registrada.");
        }
        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setCedula(dto.getCedula());
        empleado.setCorreo(dto.getCorreo());
        empleado.setEsAdmin(dto.getEsAdmin());
        // HAY QUE METERLE UN ENCRIPTADO A LA CONTRAÑA DEL USUARIO (OSCAR)
        // empleado.setContrasena(dto.getContrasena());
        empleado.setTipoCedula(tipoCedulaRepository.getReferenceById(dto.getTipoCedulaId()));
        empleadoRepository.save(empleado);
    }
}
