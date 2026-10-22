package com.rifatrichani.backendinmobiliaria.services.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import com.rifatrichani.backendinmobiliaria.dto.response.ContactoResponseDTO;
import com.rifatrichani.backendinmobiliaria.services.ColumnaContacto;

//Genera los archivos de la exportacion de contactos: Excel con Apache POI y PDF con OpenPDF
@Component
public class ExportadorContactos {
    //Azul oscuro del CRM (barra lateral y botones)
    private static final Color AZUL_CRM = new Color(0x0b, 0x1f, 0x46);
    private static final Color FILA_ALTERNA = new Color(0xf1, 0xf4, 0xf6);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] excel(List<ContactoResponseDTO> contactos, List<ColumnaContacto> columnas) {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet("Contactos");

            XSSFFont fuenteEncabezado = libro.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
            XSSFCellStyle encabezado = libro.createCellStyle();
            encabezado.setFont(fuenteEncabezado);
            encabezado.setFillForegroundColor(new XSSFColor(AZUL_CRM, null));
            encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            encabezado.setBorderBottom(BorderStyle.THIN);
            CellStyle fecha = libro.createCellStyle();
            fecha.setDataFormat(libro.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

            Row filaTitulos = hoja.createRow(0);
            int[] anchos = new int[columnas.size()];
            for (int i = 0; i < columnas.size(); i++) {
                Cell celda = filaTitulos.createCell(i);
                celda.setCellValue(columnas.get(i).getTitulo());
                celda.setCellStyle(encabezado);
                anchos[i] = columnas.get(i).getTitulo().length();
            }

            int numeroFila = 1;
            for (ContactoResponseDTO contacto : contactos) {
                Row fila = hoja.createRow(numeroFila++);
                for (int i = 0; i < columnas.size(); i++) {
                    Object valor = columnas.get(i).valorDe(contacto);
                    Cell celda = fila.createCell(i);
                    if (valor instanceof LocalDateTime f) {
                        celda.setCellValue(f);
                        celda.setCellStyle(fecha);
                        anchos[i] = Math.max(anchos[i], 16);
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
                hoja.setColumnWidth(i, Math.min(anchos[i] + 3, 60) * 256);
            }
            hoja.createFreezePane(0, 1);
            if (!contactos.isEmpty()) {
                hoja.setAutoFilter(new CellRangeAddress(0, contactos.size(), 0, columnas.size() - 1));
            }
            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el archivo Excel", e);
        }
    }

    public byte[] pdf(List<ContactoResponseDTO> contactos, List<ColumnaContacto> columnas, String subtitulo) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4.rotate(), 28, 28, 32, 32);
        PdfWriter.getInstance(documento, salida);
        documento.open();

        documento.add(new Paragraph("Contactos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, AZUL_CRM)));
        Paragraph detalle = new Paragraph(subtitulo + " · " + contactos.size() + " contactos · Generado el "
                + LocalDateTime.now().format(FORMATO_FECHA), FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY));
        detalle.setSpacingAfter(10);
        documento.add(detalle);

        PdfPTable tabla = new PdfPTable(columnas.size());
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1); //El encabezado se repite en cada pagina
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font fuenteCelda = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, Color.BLACK);

        for (ColumnaContacto columna : columnas) {
            PdfPCell celda = new PdfPCell(new Phrase(columna.getTitulo(), fuenteEncabezado));
            celda.setBackgroundColor(AZUL_CRM);
            celda.setPadding(5);
            celda.setBorderColor(AZUL_CRM);
            tabla.addCell(celda);
        }
        boolean alterna = false;
        for (ContactoResponseDTO contacto : contactos) {
            for (ColumnaContacto columna : columnas) {
                PdfPCell celda = new PdfPCell(new Phrase(texto(columna.valorDe(contacto)), fuenteCelda));
                celda.setPadding(4);
                celda.setBorderColor(new Color(0xdd, 0xe2, 0xea));
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                if (alterna) celda.setBackgroundColor(FILA_ALTERNA);
                tabla.addCell(celda);
            }
            alterna = !alterna;
        }
        documento.add(tabla);
        documento.close();
        return salida.toByteArray();
    }

    private static String texto(Object valor) {
        if (valor == null) return "";
        if (valor instanceof LocalDateTime f) return f.format(FORMATO_FECHA);
        return valor.toString();
    }
}
