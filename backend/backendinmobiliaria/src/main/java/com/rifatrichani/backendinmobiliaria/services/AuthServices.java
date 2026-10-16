package com.rifatrichani.backendinmobiliaria.services;
import java.util.Map;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
//Servicio de autenticación para el login de usuarios y empleados
public interface AuthServices {
    Map<String, Object> loginUsuarioWeb(UsuarioLogeoDTO dto);
    Map<String, Object> loginEmpleadoCrm(UsuarioLogeoDTO dto);
    void logoutEmpleadoCrm(Integer idEmpleado);
}