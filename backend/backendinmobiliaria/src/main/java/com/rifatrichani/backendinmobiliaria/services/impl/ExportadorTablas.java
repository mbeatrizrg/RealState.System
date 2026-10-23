package com.rifatrichani.backendinmobiliaria.services.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rifatrichani.backendinmobiliaria.services.ColumnaExportable;

/* Genera las exportaciones de las tablas del CRM (contactos, negocios, inmuebles): Excel con Apache POI y PDF con OpenPDF.
   Cada modulo pasa sus filas y las columnas elegidas (ver ColumnaExportable) */
@Component
public class ExportadorTablas {
    //Azul oscuro del CRM (barra lateral y botones)
    private static final Color AZUL_CRM = new Color(0x0b, 0x1f, 0x46);
    private static final Color FILA_ALTERNA = new Color(0xf1, 0xf4, 0xf6);
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /* Seccion de un informe de varias tablas (Estadisticas): una hoja del Excel o una tabla del PDF.
       ColumnaSimple sirve para las tablas que no tienen un enum de columnas */
    public record Seccion<T>(String titulo, List<T> filas, List<? extends ColumnaExportable<T>> columnas) {}

    public record ColumnaSimple<T>(String getClave, String getTitulo, Function<T, Object> valor) implements ColumnaExportable<T> {
        public ColumnaSimple(String titulo, Function<T, Object> valor) {
            this(titulo, titulo, valor);
        }
        @Override public Object valorDe(T fila) { return valor.apply(fila); }
    }

    //hoja: nombre de la pestaña del libro (ej. "Contactos")
    public <T> byte[] excel(String hoja, List<T> filas, List<? extends ColumnaExportable<T>> columnas) {
        return excelInforme(List.of(new Seccion<>(hoja, filas, columnas)));
    }

    //Un libro con una hoja por seccion
    public byte[] excelInforme(List<Seccion<?>> secciones) {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            XSSFFont fuenteEncabezado = libro.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
            XSSFCellStyle encabezado = libro.createCellStyle();
            encabezado.setFont(fuenteEncabezado);
            encabezado.setFillForegroundColor(new XSSFColor(AZUL_CRM, null));
            encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            encabezado.setBorderBottom(BorderStyle.THIN);
            CellStyle fechaHora = libro.createCellStyle();
            fechaHora.setDataFormat(libro.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
            CellStyle fecha = libro.createCellStyle();
            fecha.setDataFormat(libro.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy"));

            for (Seccion<?> seccion : secciones) {
                escribirHoja(libro, seccion, encabezado, fechaHora, fecha);
            }
            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el archivo Excel", e);
        }
    }

    private static <T> void escribirHoja(XSSFWorkbook libro, Seccion<T> seccion, CellStyle encabezado, CellStyle fechaHora, CellStyle fecha) {
        //Excel no admite algunos caracteres ni mas de 31 letras en el nombre de la hoja
        String nombre = seccion.titulo().replaceAll("[\\\\/?*\\[\\]:]", " ");
        Sheet pestana = libro.createSheet(nombre.length() > 31 ? nombre.substring(0, 31) : nombre);
        List<? extends ColumnaExportable<T>> columnas = seccion.columnas();
        List<T> filas = seccion.filas();

        Row filaTitulos = pestana.createRow(0);
        int[] anchos = new int[columnas.size()];
        for (int i = 0; i < columnas.size(); i++) {
            Cell celda = filaTitulos.createCell(i);
            celda.setCellValue(columnas.get(i).getTitulo());
            celda.setCellStyle(encabezado);
            anchos[i] = columnas.get(i).getTitulo().length();
        }

        int numeroFila = 1;
        for (T dato : filas) {
            Row fila = pestana.createRow(numeroFila++);
            for (int i = 0; i < columnas.size(); i++) {
                Object valor = columnas.get(i).valorDe(dato);
                Cell celda = fila.createCell(i);
                if (valor instanceof LocalDateTime f) {
                    celda.setCellValue(f);
                    celda.setCellStyle(fechaHora);
                    anchos[i] = Math.max(anchos[i], 16);
                } else if (valor instanceof LocalDate f) {
                    celda.setCellValue(f);
                    celda.setCellStyle(fecha);
                    anchos[i] = Math.max(anchos[i], 11);
                } else if (valor instanceof Number n) {
                    celda.setCellValue(n.doubleValue());
                    anchos[i] = Math.max(anchos[i], n.toString().length());
                } else if (valor != null) {
                    celda.setCellValue(valor.toString());
                    anchos[i] = Math.max(anchos[i], valor.toString().length());
                }
            }
        }

        //Ancho segun el texto mas largo (autoSizeColumn necesita las fuentes instaladas en el servidor)
        for (int i = 0; i < columnas.size(); i++) {
            pestana.setColumnWidth(i, Math.min(anchos[i] + 3, 60) * 256);
        }
        pestana.createFreezePane(0, 1);
        if (!filas.isEmpty()) {
            pestana.setAutoFilter(new CellRangeAddress(0, filas.size(), 0, columnas.size() - 1));
        }
    }

    //titulo: nombre del modulo; subtitulo: pestaña o vista exportada; unidad: "contactos", "negocios"...
    public <T> byte[] pdf(String titulo, String subtitulo, String unidad, List<T> filas, List<? extends ColumnaExportable<T>> columnas) {
        return pdfInforme(titulo, subtitulo + " · " + filas.size() + " " + unidad, List.of(new Seccion<>(null, filas, columnas)));
    }

    //Un PDF con una tabla por seccion (con su titulo si lo tiene)
    public byte[] pdfInforme(String titulo, String subtitulo, List<Seccion<?>> secciones) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4.rotate(), 28, 28, 32, 32);
        PdfWriter.getInstance(documento, salida);
        documento.open();

        documento.add(new Paragraph(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, AZUL_CRM)));
        Paragraph detalle = new Paragraph(subtitulo + " · Generado el " + LocalDateTime.now().format(FORMATO_FECHA_HORA),
                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY));
        detalle.setSpacingAfter(10);
        documento.add(detalle);
        for (Seccion<?> seccion : secciones) {
            agregarTabla(documento, seccion);
        }
        documento.close();
        return salida.toByteArray();
    }

    private static <T> void agregarTabla(Document documento, Seccion<T> seccion) {
        if (seccion.titulo() != null) {
            Paragraph titulo = new Paragraph(seccion.titulo(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, AZUL_CRM));
            titulo.setSpacingBefore(12);
            titulo.setSpacingAfter(6);
            documento.add(titulo);
        }
        List<? extends ColumnaExportable<T>> columnas = seccion.columnas();
        PdfPTable tabla = new PdfPTable(columnas.size());
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1); //El encabezado se repite en cada pagina
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font fuenteCelda = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, Color.BLACK);

        for (ColumnaExportable<T> columna : columnas) {
            PdfPCell celda = new PdfPCell(new Phrase(columna.getTitulo(), fuenteEncabezado));
            celda.setBackgroundColor(AZUL_CRM);
            celda.setPadding(5);
            celda.setBorderColor(AZUL_CRM);
            tabla.addCell(celda);
        }
        boolean alterna = false;
        for (T dato : seccion.filas()) {
            for (ColumnaExportable<T> columna : columnas) {
                PdfPCell celda = new PdfPCell(new Phrase(texto(columna.valorDe(dato)), fuenteCelda));
                celda.setPadding(4);
                celda.setBorderColor(new Color(0xdd, 0xe2, 0xea));
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                if (alterna) celda.setBackgroundColor(FILA_ALTERNA);
                tabla.addCell(celda);
            }
            alterna = !alterna;
        }
        documento.add(tabla);
    }

    private static String texto(Object valor) {
        if (valor == null) return "";
        if (valor instanceof LocalDateTime f) return f.format(FORMATO_FECHA_HORA);
        if (valor instanceof LocalDate f) return f.format(FORMATO_FECHA);
        return valor.toString();
    }
}
