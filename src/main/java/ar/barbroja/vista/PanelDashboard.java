package ar.barbroja.vista;

import ar.barbroja.excepciones.TurnoException;
import ar.barbroja.modelo.EstadoTurno;
import ar.barbroja.modelo.Turno;
import ar.barbroja.repositorio.BarbaRojaRepository;
import ar.barbroja.servicio.TurnoService;
import ar.barbroja.util.ExcelExporter;
import ar.barbroja.util.EstiloUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Dashboard conectado a MySQL mediante repository/DAO. */
public class PanelDashboard extends JPanel {
    public static final LocalDate FECHA_TRABAJO = LocalDate.of(2025, 5, 23);
    private final BarbaRojaRepository repository;
    private final Runnable abrirNuevoTurno;
    private final Runnable refrescar;
    private JTable tabla;
    private List<Turno> turnos;

    public PanelDashboard(BarbaRojaRepository repository, Runnable abrirNuevoTurno, Runnable refrescar) {
        this.repository = repository;
        this.abrirNuevoTurno = abrirNuevoTurno;
        this.refrescar = refrescar;
        this.turnos = repository.turnosPorFecha(FECHA_TRABAJO);
        setLayout(new BorderLayout(16, 16));
        setBackground(Color.decode(EstiloUI.FONDO));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        add(header(), BorderLayout.NORTH);
        add(centro(), BorderLayout.CENTER);
        add(derecha(), BorderLayout.EAST);
    }

    private JComponent header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel titulo = new JLabel("<html><span style='color:#A61C2B;font-size:26px'><b>Barba Roja Turnos</b></span><br><span style='color:#6B6B6B'>Sistema de gestión de turnos y agenda operativa - " + (repository.usaMySQL() ? "datos reales MySQL" : "fallback demo") + "</span></html>");
        JLabel fecha = new JLabel("viernes, 23 de mayo de 2025");
        panel.add(titulo, BorderLayout.WEST);
        panel.add(fecha, BorderLayout.EAST);
        return panel;
    }

    private JComponent centro() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setOpaque(false);
        cards.add(card("Turnos del día", String.valueOf(repository.totalTurnos(FECHA_TRABAJO)), "MySQL"));
        cards.add(card("Clientes", String.valueOf(repository.totalClientes()), "Registrados"));
        cards.add(card("Barberos activos", String.valueOf(repository.totalBarberosActivos()), "En servicio"));
        cards.add(card("Sucursales", String.valueOf(repository.totalSucursalesActivas()), "Operativas"));
        panel.add(cards, BorderLayout.NORTH);

        String[] cols = {"ID", "Hora", "Cliente", "Servicio", "Barbero", "Sucursal", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm");
        for (Turno turno : turnos) {
            model.addRow(new Object[]{turno.getId(), turno.getHoraInicio().format(hora), turno.getCliente(), turno.getServicio(), turno.getBarbero(), turno.getSucursal(), turno.getEstado().getEtiqueta()});
        }
        tabla = new JTable(model);
        tabla.setRowHeight(30);
        tabla.getColumnModel().getColumn(6).setCellRenderer(new EstadoRenderer());
        JPanel tablaPanel = caja(new BorderLayout());
        tablaPanel.add(new JLabel("Agenda del día - Viernes 23 de mayo de 2025"), BorderLayout.NORTH);
        tablaPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        tablaPanel.add(botones(), BorderLayout.SOUTH);
        panel.add(tablaPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel botones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton nuevo = new JButton("+ Nuevo turno");
        nuevo.addActionListener(e -> abrirNuevoTurno.run());
        JButton cancelar = new JButton("Cancelar turno real");
        cancelar.addActionListener(e -> cancelarSeleccionado());
        JButton actualizar = new JButton("Actualizar desde MySQL");
        actualizar.addActionListener(e -> refrescar.run());
        botones.add(nuevo);
        botones.add(cancelar);
        botones.add(actualizar);
        return botones;
    }

    private JComponent derecha() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 12));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 0));
        JPanel calendario = caja(new BorderLayout());
        calendario.add(new JLabel("<html><b>Calendario</b><br>Mayo de 2025</html>"), BorderLayout.NORTH);
        calendario.add(new JLabel("<html><br>Lu Ma Mi Ju Vi Sa Do<br><br>19 20 21 22 <b style='color:#A61C2B'>23</b> 24 25</html>"), BorderLayout.CENTER);
        panel.add(calendario);
        JPanel proximos = caja(new BorderLayout());
        proximos.add(new JLabel("<html><b>Pr\u00f3ximos turnos</b></html>"), BorderLayout.NORTH);
        DefaultListModel<String> lista = new DefaultListModel<>();
        turnos.stream().filter(t -> t.getEstado() == EstadoTurno.CONFIRMADO || t.getEstado() == EstadoTurno.PROGRAMADO).limit(5)
                .forEach(t -> lista.addElement(t.getHoraInicio() + " " + t.getCliente() + " - " + t.getServicio() + " - " + t.getBarbero()));
        proximos.add(new JScrollPane(new JList<>(lista)), BorderLayout.CENTER);
        proximos.add(new JLabel("Ver todos los turnos ->"), BorderLayout.SOUTH);
        panel.add(proximos);
        return panel;
    }

    private void cancelarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Debe seleccionar un turno."); return; }
        int id = Integer.parseInt(String.valueOf(tabla.getValueAt(row, 0)));
        try {
            new TurnoService(repository.getTurnoDAO()).cancelar(id);
            JOptionPane.showMessageDialog(this, "Turno cancelado en MySQL.");
            refrescar.run();
        } catch (TurnoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel card(String titulo, String numero, String texto) {
        JPanel card = caja(new GridLayout(3, 1));
        card.add(new JLabel(titulo));
        JLabel valor = new JLabel(numero);
        valor.setFont(valor.getFont().deriveFont(Font.BOLD, 28f));
        card.add(valor);
        card.add(new JLabel(texto));
        return card;
    }

    static JPanel caja(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.decode("#DADDE2")), BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    private static class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            String estado = String.valueOf(value).toLowerCase(Locale.ROOT);
            c.setForeground(Color.WHITE);
            if (estado.contains("completado")) c.setBackground(Color.decode("#2E7D32"));
            else if (estado.contains("curso")) c.setBackground(Color.decode("#1565C0"));
            else if (estado.contains("confirmado")) c.setBackground(Color.decode("#F9A825"));
            else if (estado.contains("cancelado")) c.setBackground(Color.decode("#C62828"));
            else c.setBackground(Color.decode("#757575"));
            return c;
        }
    }
}