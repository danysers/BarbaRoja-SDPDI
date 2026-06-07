package ar.barbroja.util;

import javax.swing.UIManager;
import java.awt.Font;

public class EstiloUI {
    public static final String ROJO = "#A61C2B";
    public static final String SIDEBAR = "#111820";
    public static final String FONDO = "#F5F6F8";

    public static void instalar() {
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
    }
}
