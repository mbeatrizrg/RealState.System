package com.rifatrichani.backendinmobiliaria.services;

import java.util.Map;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;

public interface AuthServices {
    Map<String, Object> loginUsuarioWeb(UsuarioLogeoDTO dto);
    Map<String, Object> loginEmpleadoCrm(UsuarioLogeoDTO dto);
    void logoutEmpleadoCrm(Integer idEmpleado);
    Map<String, Object> registrarUsuario(UsuarioRegistroDTO dto);
    
    // ESTA ES LA LÍNEA QUE FALTA PARA QUITAR EL ERROR DE @Override
    Map<String, Object> confirmarCuenta(String token);
    Map<String, Object> crearVendedorPrueba();
}