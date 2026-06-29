package ar.barbroja.vista;

import ar.barbroja.excepciones.TurnoException;
import ar.barbroja.modelo.*;
import ar.barbroja.repositorio.BarbaRojaRepository;
import ar.barbroja.servicio.TurnoService;
import ar.barbroja.util.EstiloUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Formulario conectado a MySQL mediante TurnoService + TurnoDAO. */
public class PanelRegistrarTurno extends JPanel {
    private final JComboBox<Cliente> cliente;
    private final JComboBox<Sucursal> sucursal;
    private final JComboBox<Barbero> barbero;
    private final JComboBox<Servicio> servicio;
    private final JComboBox<String> hora;
    private final JTextField telefono = new JTextField();
    private final JTextField duracion = new JTextField();
    private final JTextField fecha = new JTextField("23/05/2025");
    private final JTextArea observaciones = new JTextArea("Turno registrado desde el prototipo TP4.", 4, 20);
    private final JLabel fin = new JLabel("Fin estimado: --:--");
    private final DefaultListModel<String> disponibilidadModel = new DefaultListModel<>();
    private final BarbaRojaRepository repository;
    private final Runnable alGuardar;

    public PanelRegistrarTurno(BarbaRojaRepository repository, Runnable alGuardar) {
        this.repository = repository;
        this.alGuardar = alGuardar;
        this.cliente = new JComboBox<>(repository.clientes().toArray(new Cliente[0]));
        this.sucursal = new JComboBox<>(repository.sucursales().toArray(new Sucursal[0]));
        this.barbero = new JComboBox<>(repository.barberosActivos().toArray(new Barbero[0]));
        this.servicio = new JComboBox<>(repository.servicios().toArray(new Servicio[0]));
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
        cliente.addActionListener(e -> actualizarTelefono());
        actualizarTelefono();
        actualizarFin();
        actualizarDisponibilidadVisual();
    }

    private JComponent header() {
        return new JLabel("<html><span style='color:#A61C2B;font-size:24px'><b>Registrar nuevo turno</b></span><br><span style='color:#6B6B6B'>Alta real en MySQL con validacion de superposicion</span></html>");
    }

    private JComponent formulario() {
        JPanel panel = PanelDashboard.caja(new GridLayout(0, 2, 14, 12));
        telefono.setEditable(false);
        duracion.setEditable(false);
        panel.add(campo("Cliente *", cliente));
        panel.add(campo("Telefono", telefono));
        panel.add(campo("Sucursal *", sucursal));
        panel.add(campo("Barbero *", barbero));
        panel.add(campo("Servicio *", servicio));
        panel.add(campo("Duracion", duracion));
        panel.add(campo("Estado inicial", new JTextField("PROGRAMADO")));
        panel.add(campo("Fecha *", fecha));
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
        panel.add(new JLabel("<html><b>Disponibilidad</b><br>Validada contra tabla turno</html>"), BorderLayout.NORTH);
        panel.add(new JScrollPane(new JList<>(disponibilidadModel)), BorderLayout.CENTER);
        JButton actualizar = new JButton("Actualizar disponibilidad");
        actualizar.addActionListener(e -> actualizarDisponibilidadVisual());
        panel.add(actualizar, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent botones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        JButton guardar = new JButton("Guardar turno en MySQL");
        guardar.setBackground(Color.decode("#1565C0"));
        guardar.setForeground(Color.WHITE);
        guardar.addActionListener(e -> guardar());
        JButton limpiar = new JButton("Limpiar");
        limpiar.addActionListener(e -> observaciones.setText(""));
        panel.add(guardar);
        panel.add(limpiar);
        panel.add(MensajesUI.botonProximamente("Cancelar", this));
        return panel;
    }

    private void actualizarTelefono() {
        Cliente c = (Cliente) cliente.getSelectedItem();
        telefono.setText(c == null ? "" : c.getTelefono());
    }

    private void actualizarFin() {
        Servicio s = (Servicio) servicio.getSelectedItem();
        String h = (String) hora.getSelectedItem();
        if (s != null && h != null) {
            duracion.setText(s.getDuracionMinutos() + " minutos");
            fin.setText("Fin estimado: " + LocalTime.parse(h).plusMinutes(s.getDuracionMinutos()));
        }
    }

    private void actualizarDisponibilidadVisual() {
        disponibilidadModel.clear();
        Barbero b = (Barbero) barbero.getSelectedItem();
        Servicio s = (Servicio) servicio.getSelectedItem();
        if (b == null || s == null) return;
        List<String> horarios = List.of("09:00", "09:30", "10:30", "11:30", "12:30", "13:30", "14:30", "15:30", "16:30");
        for (String h : horarios) {
            try {
                LocalTime inicio = LocalTime.parse(h);
                boolean ocupado = repository.getTurnoDAO().existeSuperposicion(b.getId(), PanelDashboard.FECHA_TRABAJO, inicio, inicio.plusMinutes(s.getDuracionMinutos()));
                disponibilidadModel.addElement(h + (ocupado ? " No disponible" : " Disponible"));
            } catch (Exception ex) {
                disponibilidadModel.addElement(h + " No se pudo validar");
            }
        }
    }

    private void guardar() {
        try {
            TurnoService service = repository.usaMySQL() ? new TurnoService(repository.getTurnoDAO()) : new TurnoService(repository.turnosPorFecha(PanelDashboard.FECHA_TRABAJO));
            service.registrar((Cliente) cliente.getSelectedItem(), (Barbero) barbero.getSelectedItem(),
                    (Servicio) servicio.getSelectedItem(), (Sucursal) sucursal.getSelectedItem(),
                    PanelDashboard.FECHA_TRABAJO, LocalTime.parse((String) hora.getSelectedItem()), observaciones.getText());
            JOptionPane.showMessageDialog(this, repository.usaMySQL() ? "Turno guardado en MySQL." : "Turno guardado en datos demo.");
            alGuardar.run();
        } catch (TurnoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validacion", JOptionPane.WARNING_MESSAGE);
        }
    }
}