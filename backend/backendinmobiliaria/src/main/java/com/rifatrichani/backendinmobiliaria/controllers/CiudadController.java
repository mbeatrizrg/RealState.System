package com.rifatrichani.backendinmobiliaria.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rifatrichani.backendinmobiliaria.dto.response.CiudadResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.CiudadService;
import org.springframework.web.bind.annotation.GetMapping;



@RestController //Este es el primer controlador de peticiones HTTP del sistema, voy a documentarlo para que vean como funciona cada cosa
@RequestMapping ("/api/ciudades") //El request mapping es la ruta en donde va a estar este objeto controlador de ciudades
@CrossOrigin (origins = "*") //El cross origin lo que hace es definir desde donde puede ser 'invocado' este objeto

public class CiudadController {
    private final CiudadService ciudadService; //Aca se hace la inyeccion de la interfaz donde tenemos todas las funciones o metodos de Ciudad
    public CiudadController(CiudadService cs){
        this.ciudadService = cs;
    }

    @GetMapping /*El get mapping se usa para peticiones GET, puede tener parametros para definir rutas especificas 
    ejemplo @Get('/1'), entonces la ruta completa sería (blahblah.com/api/ciudades/1) y ahi ejemplo obtenemos una ciudad*/
    public List<CiudadResponseDTO> getCiudades() {
        return ciudadService.obtenerCiudades();
    }
    
}
