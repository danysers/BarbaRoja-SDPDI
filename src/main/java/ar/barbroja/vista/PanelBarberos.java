package ar.barbroja.vista;

import ar.barbroja.modelo.Barbero;
import ar.barbroja.util.DatosDemo;
import ar.barbroja.util.EstiloUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelBarberos extends JPanel {
    private final JTable tabla;
    private final JTextField nombre = new JTextField();
    private final JTextField especialidad = new JTextField();
    private final JTextField sucursal = new JTextField();
    private final JTextField telefono = new JTextField();
    private final JComboBox<String> estado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
    private final JTextArea notas = new JTextArea(3, 20);

    public PanelBarberos(DatosDemo datos) {
        setLayout(new BorderLayout(16, 16));
        setBackground(Color.decode(EstiloUI.FONDO));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        add(new JLabel("<html><span style='color:#A61C2B;font-size:24px'><b>Administración de barberos</b></span><br><span style='color:#6B6B6B'>Gestione la información, disponibilidad y horarios de barberos</span></html>"), BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Especialidad", "Sucursal", "Estado", "Horario", "Teléfono"}, 0);
        for (Barbero b : datos.getBarberos()) {
            model.addRow(new Object[]{b.getId(), b.getNombreCompleto(), b.getEspecialidad(), b.getSucursal(),
                    b.isActivo() ? "Activo" : "Inactivo", "Lun - Sáb: 10:00 - 19:00", b.getTelefono()});
        }
        tabla = new JTable(model);
        tabla.setRowHeight(28);
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        JPanel tablaPanel = PanelDashboard.caja(new BorderLayout(8, 8));
        JPanel filtro = new JPanel(new BorderLayout(8, 8));
        filtro.add(new JTextField("Buscar barbero..."), BorderLayout.CENTER);
        filtro.add(MensajesUI.botonProximamente("Filtro", this), BorderLayout.EAST);
        tablaPanel.add(filtro, BorderLayout.NORTH);
        tablaPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        tablaPanel.add(new JLabel("Mostrando 1 a 6 de 6 barberos"), BorderLayout.SOUTH);
        add(tablaPanel, BorderLayout.CENTER);
        add(inferior(), BorderLayout.SOUTH);
        tabla.setRowSelectionInterval(1, 1);
    }

    private JComponent inferior() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);
        JPanel datos = PanelDashboard.caja(new GridLayout(0, 2, 8, 8));
        datos.add(new JLabel("Nombre completo")); datos.add(nombre);
        datos.add(new JLabel("Especialidad")); datos.add(especialidad);
        datos.add(new JLabel("Sucursal")); datos.add(sucursal);
        datos.add(new JLabel("Teléfono")); datos.add(telefono);
        datos.add(new JLabel("Estado")); datos.add(estado);
        datos.add(new JLabel("Notas")); datos.add(new JScrollPane(notas));
        datos.add(MensajesUI.botonProximamente("Nuevo", this)); datos.add(MensajesUI.botonProximamente("Editar", this));
        datos.add(MensajesUI.botonProximamente("Guardar", this)); datos.add(MensajesUI.botonProximamente("Desactivar", this));
        panel.add(datos);

        JPanel horario = PanelDashboard.caja(new BorderLayout(8, 8));
        horario.add(new JLabel("<html><b>Horario semanal</b></html>"), BorderLayout.NORTH);
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Día", "Hora desde", "Hora hasta", "¿Trabaja?"}, 0);
        for (String dia : new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"}) {
            model.addRow(new Object[]{dia, "10:00", "19:00", true});
        }
        model.addRow(new Object[]{"Domingo", "--:--", "--:--", false});
        horario.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        horario.add(MensajesUI.botonProximamente("Aplicar a toda la semana", this), BorderLayout.SOUTH);
        panel.add(horario);
        return panel;
    }

    private void cargarSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        nombre.setText(String.valueOf(tabla.getValueAt(row, 1)));
        especialidad.setText(String.valueOf(tabla.getValueAt(row, 2)));
        sucursal.setText(String.valueOf(tabla.getValueAt(row, 3)));
        estado.setSelectedItem(String.valueOf(tabla.getValueAt(row, 4)));
        telefono.setText(String.valueOf(tabla.getValueAt(row, 6)));
        notas.setText("Especialista en cortes clásicos y perfilado de barba.");
    }
}
