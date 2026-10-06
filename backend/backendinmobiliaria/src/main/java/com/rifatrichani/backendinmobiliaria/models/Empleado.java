package com.rifatrichani.backendinmobiliaria.models;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table 
public class Empleado {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (length = 255)
    private String nombre;
    @Column (length = 255)
    private String apellido;
    @Column (unique = true)
    private BigInteger cedula;
    @Column (length = 255)
    private String contraseña;
    @Column (length = 500)
    private String correo;
    @Column
    private Boolean isAdmin;
    @CreationTimestamp 
    @Column 
    private LocalDateTime fechaEntrada;
    @CreationTimestamp 
    @Column 
    private LocalDateTime salida;
    
}
