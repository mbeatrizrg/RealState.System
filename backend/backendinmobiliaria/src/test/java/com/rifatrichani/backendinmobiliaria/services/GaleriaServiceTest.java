package com.rifatrichani.backendinmobiliaria.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.rifatrichani.backendinmobiliaria.dto.response.ImagenResponseDTO;
import com.rifatrichani.backendinmobiliaria.models.Empleado;
import com.rifatrichani.backendinmobiliaria.models.Galeria;
import com.rifatrichani.backendinmobiliaria.models.Inmueble;
import com.rifatrichani.backendinmobiliaria.repositories.GaleriaRepository;
import com.rifatrichani.backendinmobiliaria.repositories.InmuebleRepository;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada;
import com.rifatrichani.backendinmobiliaria.security.CuentaAutenticada.TipoCuenta;
import com.rifatrichani.backendinmobiliaria.services.impl.AlmacenImagenesLocalServiceImpl;
import com.rifatrichani.backendinmobiliaria.services.impl.GaleriaServiceImpl;

//Maximo de fotos por inmueble y validacion de los archivos subidos, sin base de datos
class GaleriaServiceTest {
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    private static final byte[] JPG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0};

    @TempDir Path carpeta;
    private GaleriaRepository galeriaRepository;
    private AlmacenImagenesLocalServiceImpl almacen;
    private GaleriaServiceImpl servicio;
    private final CuentaAutenticada vendedor = new CuentaAutenticada(3, TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_VENDEDOR);

    @BeforeEach
    void preparar() {
        galeriaRepository = mock(GaleriaRepository.class);
        InmuebleRepository inmuebleRepository = mock(InmuebleRepository.class);
        almacen = new AlmacenImagenesLocalServiceImpl(carpeta.toString());
        servicio = new GaleriaServiceImpl(galeriaRepository, inmuebleRepository, almacen);

        Empleado encargado = new Empleado();
        encargado.setId(3);
        Inmueble inmueble = new Inmueble();
        inmueble.setId(7);
        inmueble.setEmpleado(encargado);
        when(inmuebleRepository.bloquearPorId(7)).thenReturn(Optional.of(inmueble));
        when(galeriaRepository.save(any())).thenAnswer(inv -> {
            Galeria g = inv.getArgument(0);
            g.setId(100);
            return g;
        });
        //subirImagenes registra un callback de transaccion; en la prueba se simula una transaccion activa
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void limpiar() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    private MultipartFile archivo(String nombre, byte[] contenido) {
        return new MockMultipartFile("archivos", nombre, "image/png", contenido);
    }

    @Test
    void subeHastaSeisImagenes() throws Exception {
        when(galeriaRepository.countByInmuebleId(7)).thenReturn(4L);
        List<ImagenResponseDTO> subidas = servicio.subirImagenes(7, List.of(archivo("a.png", PNG), archivo("b.jpg", JPG)), vendedor);

        assertEquals(2, subidas.size());
        assertTrue(subidas.get(0).url().matches("/uploads/inmuebles/[0-9a-f\\-]{36}\\.png"));
        assertTrue(subidas.get(1).url().endsWith(".jpg"));
        try (var archivos = Files.list(carpeta.resolve("inmuebles"))) {
            assertEquals(2, archivos.count());
        }
    }

    @Test
    void rechazaPasarseDelMaximo() {
        when(galeriaRepository.countByInmuebleId(7)).thenReturn(5L);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.subirImagenes(7, List.of(archivo("a.png", PNG), archivo("b.png", PNG)), vendedor));

        assertTrue(error.getMessage().contains("máximo 6"));
        verify(galeriaRepository, never()).save(any());
    }

    @Test
    void rechazaArchivosQueNoSonImagenAunqueDiganSerlo() {
        when(galeriaRepository.countByInmuebleId(7)).thenReturn(0L);
        byte[] html = "<script>alert(1)</script>".getBytes();

        assertThrows(IllegalArgumentException.class, () -> servicio.subirImagenes(7, List.of(archivo("foto.png", html)), vendedor));
    }

    @Test
    void otroVendedorNoPuedeSubirFotos() {
        when(galeriaRepository.countByInmuebleId(7)).thenReturn(0L);
        CuentaAutenticada otro = new CuentaAutenticada(9, TipoCuenta.EMPLEADO, CuentaAutenticada.ROL_VENDEDOR);

        assertThrows(AccessDeniedException.class, () -> servicio.subirImagenes(7, List.of(archivo("a.png", PNG)), otro));
    }

    @Test
    void eliminarIgnoraRutasFueraDeLaCarpeta() throws Exception {
        Path secreto = Files.writeString(carpeta.resolve("secreto.txt"), "no borrar");
        almacen.eliminar("/uploads/inmuebles/../secreto.txt");
        almacen.eliminar("https://otro-sitio.com/foto.png");

        assertTrue(Files.exists(secreto));
        assertFalse(Files.exists(carpeta.resolve("inmuebles")));
    }
}
