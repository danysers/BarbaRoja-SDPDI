package ar.barbroja.vista;

import ar.barbroja.excepciones.ConexionException;
import ar.barbroja.modelo.Barbero;
import ar.barbroja.repositorio.BarbaRojaRepository;
import ar.barbroja.util.EstiloUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Pantalla de barberos alimentada desde BarberoDAO/MySQL. */
public class PanelBarberos extends JPanel {
    private final BarbaRojaRepository repository;
    private final JTable tabla;
    private final JTable horarioTabla;
    private final JTextField nombre = new JTextField();
    private final JTextField especialidad = new JTextField();
    private final JTextField sucursal = new JTextField();
    private final JTextField telefono = new JTextField();
    private final JComboBox<String> estado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
    private final JTextArea notas = new JTextArea(3, 20);

    public PanelBarberos(BarbaRojaRepository repository) {
        this.repository = repository;
        setLayout(new BorderLayout(16, 16));
        setBackground(Color.decode(EstiloUI.FONDO));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        add(new JLabel("<html><span style='color:#A61C2B;font-size:24px'><b>Administracion de barberos</b></span><br><span style='color:#6B6B6B'>Datos reales desde MySQL mediante DAO</span></html>"), BorderLayout.NORTH);
        DefaultTableModel model = modeloBarberos(repository.barberos());
        tabla = new JTable(model);
        tabla.setRowHeight(28);
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        JPanel tablaPanel = PanelDashboard.caja(new BorderLayout(8, 8));
        JPanel filtro = new JPanel(new BorderLayout(8, 8));
        JTextField buscar = new JTextField();
        JButton filtrar = new JButton("Buscar");
        filtrar.addActionListener(e -> tabla.setModel(modeloBarberos(repository.barberos().stream().filter(b -> b.coincideCon(buscar.getText())).toList())));
        filtro.add(buscar, BorderLayout.CENTER);
        filtro.add(filtrar, BorderLayout.EAST);
        tablaPanel.add(filtro, BorderLayout.NORTH);
        tablaPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        tablaPanel.add(new JLabel("Datos cargados desde " + (repository.usaMySQL() ? "MySQL" : "fallback demo")), BorderLayout.SOUTH);
        add(tablaPanel, BorderLayout.CENTER);
        horarioTabla = new JTable(new DefaultTableModel(new Object[]{"Dia", "Hora desde", "Hora hasta", "¿Trabaja?"}, 0));
        add(inferior(), BorderLayout.SOUTH);
        if (tabla.getRowCount() > 0) tabla.setRowSelectionInterval(0, 0);
    }

    private DefaultTableModel modeloBarberos(List<Barbero> barberos) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Especialidad", "Sucursal", "Estado", "Horario", "Telefono"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Barbero b : barberos) {
            model.addRow(new Object[]{b.getId(), b.getNombreCompleto(), b.getEspecialidad(), b.getSucursal(), b.isActivo() ? "Activo" : "Inactivo", "Ver horario semanal", b.getTelefono()});
        }
        return model;
    }

    private JComponent inferior() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);
        JPanel datos = PanelDashboard.caja(new GridLayout(0, 2, 8, 8));
        for (JTextField field : new JTextField[]{nombre, especialidad, sucursal, telefono}) field.setEditable(false);
        notas.setEditable(false);
        datos.add(new JLabel("Nombre completo")); datos.add(nombre);
        datos.add(new JLabel("Especialidad")); datos.add(especialidad);
        datos.add(new JLabel("Sucursal")); datos.add(sucursal);
        datos.add(new JLabel("Telefono")); datos.add(telefono);
        datos.add(new JLabel("Estado")); datos.add(estado);
        datos.add(new JLabel("Notas")); datos.add(new JScrollPane(notas));
        datos.add(MensajesUI.botonProximamente("Nuevo", this)); datos.add(MensajesUI.botonProximamente("Editar", this));
        datos.add(MensajesUI.botonProximamente("Guardar", this)); datos.add(MensajesUI.botonProximamente("Desactivar", this));
        panel.add(datos);
        JPanel horario = PanelDashboard.caja(new BorderLayout(8, 8));
        horario.add(new JLabel("<html><b>Horario semanal real</b></html>"), BorderLayout.NORTH);
        horario.add(new JScrollPane(horarioTabla), BorderLayout.CENTER);
        horario.add(MensajesUI.botonProximamente("Aplicar a toda la semana", this), BorderLayout.SOUTH);
        panel.add(horario);
        return panel;
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        int modelRow = tabla.convertRowIndexToModel(row);
        nombre.setText(String.valueOf(tabla.getModel().getValueAt(modelRow, 1)));
        especialidad.setText(String.valueOf(tabla.getModel().getValueAt(modelRow, 2)));
        sucursal.setText(String.valueOf(tabla.getModel().getValueAt(modelRow, 3)));
        estado.setSelectedItem(String.valueOf(tabla.getModel().getValueAt(modelRow, 4)));
        telefono.setText(String.valueOf(tabla.getModel().getValueAt(modelRow, 6)));
        notas.setText("Datos cargados desde DAO/JDBC. Las acciones de ABM completo quedan para una version posterior.");
        cargarHorario(Integer.parseInt(String.valueOf(tabla.getModel().getValueAt(modelRow, 0))));
    }

    private void cargarHorario(int idBarbero) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Dia", "Hora desde", "Hora hasta", "¿Trabaja?"}, 0);
        try {
            Object[][] filas = repository.getBarberoDAO().listarHorarioSemanal(idBarbero);
            for (Object[] fila : filas) model.addRow(fila);
        } catch (ConexionException e) {
            model.addRow(new Object[]{"Sin datos", "--", "--", false});
        }
        horarioTabla.setModel(model);
    }
}