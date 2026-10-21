package com.rifatrichani.backendinmobiliaria.dto.response;

//Respuesta del login. El frontend guarda el token y lo envia en el header "Authorization: Bearer <token>"
public class LoginResponseDTO {
    private String token;
    private String tipo; //EMPLEADO o USUARIO
    private String rol;  //ROLE_ADMIN, ROLE_VENDEDOR o ROLE_USUARIO
    private Integer id;
    private String nombre;
    private long expiraEnMs;

    public LoginResponseDTO(String token, String tipo, String rol, Integer id, String nombre, long expiraEnMs) {
        this.token = token;
        this.tipo = tipo;
        this.rol = rol;
        this.id = id;
        this.nombre = nombre;
        this.expiraEnMs = expiraEnMs;
    }

    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public String getRol() { return rol; }
    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public long getExpiraEnMs() { return expiraEnMs; }
}
