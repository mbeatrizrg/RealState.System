package com.rifatrichani.backendinmobiliaria.models;

import java.math.BigInteger;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "vistos")
public class Vistos {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn  (name = "id_usuario",nullable = true)
    private Usuario id_usuario;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_inmueble",nullable = false)
    private Inmueble id_inmueble;


}
