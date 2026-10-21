package com.rifatrichani.backendinmobiliaria.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

//Codigos numericos de un solo uso que se envian por correo
public final class CodigosSeguridad {
    private static final SecureRandom RANDOM = new SecureRandom();

    private CodigosSeguridad() {}

    //Codigo de 6 digitos generado con un generador criptografico
    public static String generarCodigo() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }

    //Los codigos que permiten cambiar la contraseña se guardan como hash: si alguien lee la tabla no puede usarlos
    public static String hash(String codigo) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(codigo.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    //Comparacion en tiempo constante (no revela cuantos caracteres coinciden)
    public static boolean coinciden(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
