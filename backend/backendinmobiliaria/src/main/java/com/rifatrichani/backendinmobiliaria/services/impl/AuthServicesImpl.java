package com.rifatrichani.backendinmobiliaria.services.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.rifatrichani.backendinmobiliaria.services.AuthServices;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioLogeoDTO;

@Service
public class AuthServicesImpl implements AuthServices {

    @Override
    public Map<String, Object> loginUsuarioWeb(UsuarioLogeoDTO dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "¡Conexión exitosa desde el Backend!");
        response.put("status", "OK");
        return response;
    }

    @Override
    public Map<String, Object> loginEmpleadoCrm(UsuarioLogeoDTO dto) {
        return new HashMap<>();
    }

    @Override
    public void logoutEmpleadoCrm(Integer idEmpleado) {
    }
}