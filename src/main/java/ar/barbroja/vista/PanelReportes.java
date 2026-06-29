package ar.barbroja.vista;

import ar.barbroja.modelo.Turno;
import ar.barbroja.repositorio.BarbaRojaRepository;
import ar.barbroja.util.EstiloUI;
import ar.barbroja.util.ExcelExporter;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Pantalla de reportes del prototipo TP4.
 * Centraliza la Exportaci\u00f3n del turnero para que la acci\u00f3n este en el modulo
 * Reportes y no mezclada con las operaciones diarias del dashboard.
 */
public class PanelReportes extends JPanel {
    private static final LocalDate FECHA_REPORTE = LocalDate.of(2025, 5, 23);
    private final BarbaRojaRepository repository;
    private final JLabel estado;

    public PanelReportes(BarbaRojaRepository repository) {
        this.repository = repository;
        this.estado = new JLabel("Seleccione una acci\u00f3n de reporte.");
        setLayout(new BorderLayout(16, 16));
        setBackground(Color.decode(EstiloUI.FONDO));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        add(header(), BorderLayout.NORTH);
        add(contenido(), BorderLayout.CENTER);
    }

    private JComponent header() {
        return new JLabel("<html><span style='color:#A61C2B;font-size:24px'><b>Reportes</b></span><br>Exportaci\u00f3n de datos reales desde MySQL</html>");
    }

    private JComponent contenido() {
        JPanel panel = PanelDashboard.caja(new BorderLayout(12, 12));
        JLabel descripcion = new JLabel("Exportar turnero del viernes 23 de mayo de 2025 a Excel (.xlsx).");
        JButton exportar = new JButton("Exportar turnero del d\u00eda a Excel");
        exportar.addActionListener(e -> exportarTurnero());
        JPanel acci\u00f3nes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acci\u00f3nes.setOpaque(false);
        acci\u00f3nes.add(exportar);
        panel.add(descripcion, BorderLayout.NORTH);
        panel.add(acci\u00f3nes, BorderLayout.CENTER);
        panel.add(estado, BorderLayout.SOUTH);
        return panel;
    }

    private void exportarTurnero() {
        try {
            List<Turno> turnos = repository.turnosPorFecha(FECHA_REPORTE);
            Path destino = Path.of("exports", "turnos_2025-05-23.xlsx");
            Path generado = new ExcelExporter().exportarTurnos(destino, turnos);
            estado.setText("Archivo generado: " + generado.toAbsolutePath());
            JOptionPane.showMessageDialog(this, "Turnero exportado: " + generado.toAbsolutePath());
        } catch (Exception ex) {
            estado.setText("No se pudo exportar el turnero.");
            JOptionPane.showMessageDialog(this, "No se pudo exportar Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}