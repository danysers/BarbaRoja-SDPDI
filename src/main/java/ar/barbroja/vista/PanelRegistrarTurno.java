package ar.barbroja.vista;

import ar.barbroja.excepciones.TurnoException;
import ar.barbroja.modelo.*;
import ar.barbroja.servicio.TurnoService;
import ar.barbroja.util.DatosDemo;
import ar.barbroja.util.EstiloUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Formulario de alta de turno. Toma datos de la interfaz y delega la regla
 * de negocio al TurnoService.
 */
public class PanelRegistrarTurno extends JPanel {
    private final JComboBox<Cliente> cliente;
    private final JComboBox<Sucursal> sucursal;
    private final JComboBox<Barbero> barbero;
    private final JComboBox<Servicio> servicio;
    private final JComboBox<String> hora;
    private final JTextArea observaciones = new JTextArea("Cliente solicita degradado medio en los laterales.", 4, 20);
    private final JLabel fin = new JLabel("Fin estimado: 11:30");
    private final TurnoService turnoService;
    private final Runnable alGuardar;

    public PanelRegistrarTurno(DatosDemo datos, Runnable alGuardar) {
        this.alGuardar = alGuardar;
        this.turnoService = new TurnoService(datos.getTurnos());
        this.cliente = new JComboBox<>(datos.getClientes().toArray(new Cliente[0]));
        this.sucursal = new JComboBox<>(datos.getSucursales().toArray(new Sucursal[0]));
        this.barbero = new JComboBox<>(datos.getBarberos().stream().filter(Barbero::isActivo).toArray(Barbero[]::new));
        this.servicio = new JComboBox<>(datos.getServicios().toArray(new Servicio[0]));
        this.hora = new JComboBox<>(new String[]{"09:00", "09:30", "10:30", "11:30", "12:30", "13:30", "14:30", "15:30", "16:30"});

        setLayout(new BorderLayout(16, 16));
        setBackground(Color.decode(EstiloUI.FONDO));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        add(header(), BorderLayout.NORTH);
        add(formulario(), BorderLayout.CENTER);
        add(disponibilidad(), BorderLayout.EAST);
        add(botones(), BorderLayout.SOUTH);
        servicio.addActionListener(e -> actualizarFin());
        hora.addActionListener(e -> actualizarFin());
        cliente.setSelectedIndex(1);
        hora.setSelectedItem("10:30");
        actualizarFin();
    }

    private JComponent header() {
        return new JLabel("<html><span style='color:#A61C2B;font-size:24px'><b>Registrar nuevo turno</b></span><br><span style='color:#6B6B6B'>Complete los datos para agendar un nuevo turno</span></html>");
    }

    private JComponent formulario() {
        JPanel panel = PanelDashboard.caja(new GridLayout(0, 2, 14, 12));
        panel.add(campo("Cliente *", cliente));
        panel.add(campo("Teléfono", new JTextField("(11) 5555-1234")));
        panel.add(campo("Sucursal *", sucursal));
        panel.add(campo("Barbero *", barbero));
        panel.add(campo("Servicio *", servicio));
        panel.add(campo("Duración", new JTextField("60 minutos")));
        panel.add(campo("Estado *", new JTextField("Programado")));
        panel.add(campo("Fecha *", new JTextField("23/05/2025")));
        panel.add(campo("Hora *", hora));
        panel.add(fin);
        panel.add(campo("Observaciones", new JScrollPane(observaciones)));
        panel.add(new JLabel("* Campos obligatorios"));
        return panel;
    }

    private JComponent campo(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setOpaque(false);
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JComponent disponibilidad() {
        JPanel panel = PanelDashboard.caja(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.add(new JLabel("<html><b>Disponibilidad</b><br>Barbero: Juan Pérez<br>Fecha: 23/05/2025<br><br>Tiempos disponibles para Centro</html>"), BorderLayout.NORTH);
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String item : new String[]{"09:00 Disponible", "09:30 Disponible", "10:30 Disponible - seleccionado", "11:30 Disponible", "12:30 Disponible", "13:30 Disponible", "14:30 Disponible", "15:30 Disponible", "16:30 No disponible"}) {
            model.addElement(item);
        }
        panel.add(new JScrollPane(new JList<>(model)), BorderLayout.CENTER);
        return panel;
    }

    private JComponent botones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        JButton guardar = new JButton("Guardar turno");
        guardar.setBackground(Color.decode("#1565C0"));
        guardar.setForeground(Color.WHITE);
        guardar.addActionListener(e -> guardar());
        panel.add(guardar);
        panel.add(MensajesUI.botonProximamente("Limpiar", this));
        panel.add(MensajesUI.botonProximamente("Cancelar", this));
        panel.add(MensajesUI.botonProximamente("Actualizar disponibilidad", this));
        return panel;
    }

    private void actualizarFin() {
        Servicio s = (Servicio) servicio.getSelectedItem();
        String h = (String) hora.getSelectedItem();
        if (s != null && h != null) {
            // Calculo automatico de fin segun la duracion del servicio.
            fin.setText("Fin estimado: " + LocalTime.parse(h).plusMinutes(s.getDuracionMinutos()));
        }
    }

    private void guardar() {
        try {
            // El panel no valida disponibilidad directamente: delega esa responsabilidad al servicio.
            turnoService.registrar((Cliente) cliente.getSelectedItem(), (Barbero) barbero.getSelectedItem(),
                    (Servicio) servicio.getSelectedItem(), (Sucursal) sucursal.getSelectedItem(),
                    LocalDate.of(2025, 5, 23), LocalTime.parse((String) hora.getSelectedItem()), observaciones.getText());
            JOptionPane.showMessageDialog(this, "Turno guardado correctamente.");
            alGuardar.run();
        } catch (TurnoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }
}
