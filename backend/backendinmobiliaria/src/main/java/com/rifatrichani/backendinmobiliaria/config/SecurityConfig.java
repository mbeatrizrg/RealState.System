package com.rifatrichani.backendinmobiliaria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity //Activa @PreAuthorize en los controladores, la configuracion JWT se agregara aqui
public class SecurityConfig {

}
