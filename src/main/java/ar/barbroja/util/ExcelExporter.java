package ar.barbroja.util;

import ar.barbroja.modelo.Turno;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Exporta el turnero del dia a XLSX sin agregar frameworks ni librerias externas.
 * Un archivo XLSX es un paquete ZIP con XML internos; para el TP4 alcanza con
 * generar una hoja simple con hora, cliente, servicio, barbero, sucursal y estado.
 */
public class ExcelExporter {
    public Path exportarTurnos(Path destino, List<Turno> turnos) throws IOException {
        Files.createDirectories(destino.getParent());
        try (OutputStream os = Files.newOutputStream(destino); ZipOutputStream zip = new ZipOutputStream(os, StandardCharsets.UTF_8)) {
            entry(zip, "[Content_Types].xml", """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                      <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                      <Default Extension="xml" ContentType="application/xml"/>
                      <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                      <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                    </Types>
                    """);
            entry(zip, "_rels/.rels", """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                      <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                    </Relationships>
                    """);
            entry(zip, "xl/workbook.xml", """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                      <sheets><sheet name="Turnero" sheetId="1" r:id="rId1"/></sheets>
                    </workbook>
                    """);
            entry(zip, "xl/_rels/workbook.xml.rels", """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                      <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                    </Relationships>
                    """);
            entry(zip, "xl/worksheets/sheet1.xml", hoja(turnos));
        }
        return destino;
    }

    private String hoja(List<Turno> turnos) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
        fila(xml, 1, "Hora", "Cliente", "Servicio", "Barbero", "Sucursal", "Estado");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        int row = 2;
        for (Turno t : turnos) {
            fila(xml, row++, t.getHoraInicio().format(fmt), t.getCliente().getNombreCompleto(), t.getServicio().getNombre(), t.getBarbero().getNombreCompleto(), t.getSucursal().getNombre(), t.getEstado().getEtiqueta());
        }
        xml.append("</sheetData></worksheet>");
        return xml.toString();
    }

    private void fila(StringBuilder xml, int row, String... values) {
        xml.append("<row r=\"").append(row).append("\">");
        for (int i = 0; i < values.length; i++) {
            xml.append("<c r=\"").append((char)('A' + i)).append(row).append("\" t=\"inlineStr\"><is><t>").append(escape(values[i])).append("</t></is></c>");
        }
        xml.append("</row>");
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private void entry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
}
