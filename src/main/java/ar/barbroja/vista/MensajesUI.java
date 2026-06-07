package ar.barbroja.vista;

import javax.swing.*;
import java.awt.Component;

/**
 * Centraliza mensajes comunes de la interfaz para mantener consistencia visual.
 */
public class MensajesUI {
    private static final String MENSAJE_PROXIMAMENTE = """
            Función próxima a agregarse.

            Esta funcionalidad no entra dentro de la propuesta presentada para el prototipo,
            pero se muestra para visualizar el alcance general del proyecto final.
            """;

    private MensajesUI() {
    }

    public static void mostrarProximamente(Component parent) {
        JOptionPane.showMessageDialog(parent, MENSAJE_PROXIMAMENTE, "Función futura", JOptionPane.INFORMATION_MESSAGE);
    }

    public static JButton botonProximamente(String texto, Component parent) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> mostrarProximamente(parent));
        return boton;
    }
}
