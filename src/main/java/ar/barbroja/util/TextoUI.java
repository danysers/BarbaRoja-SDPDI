package ar.barbroja.util;

import java.nio.charset.StandardCharsets;

/**
 * Normaliza textos antes de mostrarlos en Swing.
 *
 * La base de datos demo puede haber sido importada con mojibake. Centralizar
 * la correccion evita repetir reemplazos en cada pantalla o consulta DAO.
 */
public class TextoUI {
    private TextoUI() {
    }

    public static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return valor;
        }
        if (!pareceMojibake(valor)) {
            return valor;
        }
        String reparado = new String(valor.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return reparado.indexOf('\uFFFD') >= 0 ? valor : reparado;
    }

    private static boolean pareceMojibake(String valor) {
        return valor.contains("\u00c3") || valor.contains("\u00c2") || valor.contains("\u00e2");
    }
}