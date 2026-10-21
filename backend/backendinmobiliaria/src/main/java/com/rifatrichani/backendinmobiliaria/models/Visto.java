package com.rifatrichani.backendinmobiliaria.models;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
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
public class Visto {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    //En Supabase id_usuario es NOT NULL: solo se registran vistas de usuarios con sesion iniciada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn  (name = "id_usuario",nullable = false)
    private Usuario usuario;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "id_inmueble",nullable = false)
    private Inmueble inmueble;
    @CreationTimestamp
    @Column (name = "fecha_visto", updatable = false)
    private LocalDateTime fechaVista;
    @Column (nullable = false)
    private Boolean visto;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Inmueble getInmueble() {
        return inmueble;
    }
    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }
    public LocalDateTime getFechaVista() {
        return fechaVista;
    }
    public void setFechaVista(LocalDateTime fechaVista) {
        this.fechaVista = fechaVista;
    }
    public Boolean getVisto() {
        return visto;
    }
    public void setVisto(Boolean visto) {
        this.visto = visto;
    }
}
