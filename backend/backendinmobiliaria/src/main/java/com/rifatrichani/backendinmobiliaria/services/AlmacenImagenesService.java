package com.rifatrichani.backendinmobiliaria.services;

import org.springframework.web.multipart.MultipartFile;

//Guarda los archivos de las fotos de los inmuebles y devuelve la URL publica con la que se registran en la galeria
public interface AlmacenImagenesService {
    String guardar(MultipartFile archivo);
    //Borra el archivo si fue subido a este almacen; las URLs externas se ignoran
    void eliminar(String url);
}
