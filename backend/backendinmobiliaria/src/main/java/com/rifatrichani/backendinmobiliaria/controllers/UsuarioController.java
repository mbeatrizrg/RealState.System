package com.rifatrichani.backendinmobiliaria.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioCorreoRequestDTO;
import com.rifatrichani.backendinmobiliaria.dto.request.UsuarioRegistroDTO;
import com.rifatrichani.backendinmobiliaria.services.UsuarioService;

@RestController 
@RequestMapping ("api/usuario")
@CrossOrigin (origins = "*")
public class UsuarioController {
    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService us){
        this.usuarioService = us;
    }
    @PostMapping ("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioRegistroDTO usuario){
        try{
            usuarioService.registrarUsuario(usuario);
            return  ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PutMapping ("/{id}/correo")
    public ResponseEntity<String> editarCorreo(@PathVariable ("id") Long idUsuarioObjetivo, @RequestHeader Long idUsuarioAuth,@RequestBody UsuarioCorreoRequestDTO usuario){
        try{
            usuarioService.actualizarCorreo(idUsuarioObjetivo, idUsuarioAuth, usuario);
            return ResponseEntity.ok("Correo actualizado exitosamente");
        } catch(SecurityException sec){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(sec.getMessage());//Problemas de seguridad un usuario "A" quiere cambiar el correo de usuario "B"
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // Errores normales
        }
    }
}
