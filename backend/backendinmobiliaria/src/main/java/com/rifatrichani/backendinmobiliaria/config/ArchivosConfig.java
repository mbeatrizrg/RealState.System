package com.rifatrichani.backendinmobiliaria.config;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.rifatrichani.backendinmobiliaria.services.impl.AlmacenImagenesLocalServiceImpl;

//Sirve las fotos subidas desde el CRM en GET /uploads/inmuebles/<archivo>. Es publico (ver SecurityConfig) porque el portal las muestra
@Configuration
public class ArchivosConfig implements WebMvcConfigurer {
    private final String carpeta;

    public ArchivosConfig(@Value("${app.uploads.dir:uploads}") String directorio) {
        //La barra final es obligatoria para que Spring la trate como carpeta
        this.carpeta = Path.of(directorio, "inmuebles").toAbsolutePath().normalize().toUri().toString();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(AlmacenImagenesLocalServiceImpl.PREFIJO_URL + "**")
                .addResourceLocations(carpeta.endsWith("/") ? carpeta : carpeta + "/")
                .setCachePeriod(60 * 60 * 24 * 30);
    }
}
