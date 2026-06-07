package ar.barbroja;

import ar.barbroja.util.EstiloUI;
import ar.barbroja.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        EstiloUI.instalar();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
