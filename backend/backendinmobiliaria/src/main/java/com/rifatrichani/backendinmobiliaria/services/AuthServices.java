package com.rifatrichani.backendinmobiliaria.services;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;
//Servicio de autenticación para el login de usuarios y empleados

@Service
public interface AuthServices {
    Map<String, Object> loginUsuarioWeb(UsuarioLogeoDTO dto);
    Map<String, Object> loginEmpleadoCrm(UsuarioLogeoDTO dto);
    void logoutEmpleadoCrm(Integer idEmpleado);
}