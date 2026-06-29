package ar.barbroja.vista;

import ar.barbroja.repositorio.BarbaRojaRepository;
import ar.barbroja.util.EstiloUI;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del TP4. Consume una fachada de datos que usa MySQL por DAO
 * y conserva fallback demo solamente si falla la conexion.
 */
public class VentanaPrincipal extends JFrame {
    private final BarbaRojaRepository repository = new BarbaRojaRepository();
    private final JPanel contenido = new JPanel(new CardLayout());
    private final DefaultListModel<String> menuModel = new DefaultListModel<>();
    private final JList<String> sidebar = new JList<>(menuModel);
    private JLabel estado;

    public VentanaPrincipal() {
        setTitle("Barba Roja Turnos - Sistema de gesti\u00f3n de turnos y agenda operativa");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setJMenuBar(crearMenu());
        add(crearToolbar(), BorderLayout.NORTH);
        add(crearSidebar(), BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);
        add(crearEstado(), BorderLayout.SOUTH);
        reconstruirPantallas("Dashboard");
    }

    private void reconstruirPantallas(String pantalla) {
        contenido.removeAll();
        contenido.add(new PanelDashboard(repository, () -> mostrar("Turnos"), () -> reconstruirPantallas("Dashboard")), "Dashboard");
        contenido.add(new PanelRegistrarTurno(repository, () -> reconstruirPantallas("Dashboard")), "Turnos");
        contenido.add(new PanelBarberos(repository), "Barberos");
        contenido.add(new PanelReportes(repository), "Reportes");
        actualizarEstado();
        mostrar(pantalla);
        contenido.revalidate();
        contenido.repaint();
    }

    private JMenuBar crearMenu() {
        JMenuBar bar = new JMenuBar();
        String[] menus = {"Archivo", "Turnos", "Clientes", "Barberos", "Servicios", "Sucursales", "Reportes", "Configuraci\u00f3n", "Ayuda"};
        for (String nombre : menus) {
            JMenu menu = new JMenu(nombre);
            if ("Archivo".equals(nombre)) {
                JMenuItem salir = new JMenuItem("Salir");
                salir.addActionListener(e -> dispose());
                menu.add(salir);
            } else if ("Turnos".equals(nombre)) {
                JMenuItem nuevo = new JMenuItem("Nuevo turno");
                nuevo.addActionListener(e -> mostrar("Turnos"));
                menu.add(nuevo);
            } else if ("Reportes".equals(nombre)) {
                JMenuItem exportar = new JMenuItem("Exportar turnero a Excel");
                exportar.addActionListener(e -> mostrar("Reportes"));
                menu.add(exportar);
            } else if ("Barberos".equals(nombre)) {
                JMenuItem administrar = new JMenuItem("Administrar barberos");
                administrar.addActionListener(e -> mostrar("Barberos"));
                menu.add(administrar);
            }
            bar.add(menu);
        }
        return bar;
    }

    private JToolBar crearToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        agregarTool(toolBar, "Agenda", "Dashboard", true);
        agregarTool(toolBar, "Nuevo turno", "Turnos", true);
        agregarTool(toolBar, "Clientes", "Dashboard", false);
        agregarTool(toolBar, "Barberos", "Barberos", true);
        agregarTool(toolBar, "Servicios", "Dashboard", false);
        agregarTool(toolBar, "Sucursales", "Dashboard", false);
        agregarTool(toolBar, "Reportes", "Reportes", true);
        JButton actualizar = new JButton("Actualizar");
        actualizar.addActionListener(e -> reconstruirPantallas("Dashboard"));
        toolBar.add(actualizar);
        return toolBar;
    }

    private void agregarTool(JToolBar toolBar, String texto, String destino, boolean implementado) {
        JButton boton = new JButton(texto);
        if (implementado) boton.addActionListener(e -> mostrar(destino));
        else boton.addActionListener(e -> MensajesUI.mostrarProximamente(this));
        toolBar.add(boton);
    }

    private JComponent crearSidebar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(210, 0));
        panel.setBackground(Color.decode(EstiloUI.SIDEBAR));
        JLabel marca = new JLabel("<html><b>BARBA ROJA</b><br>TURNOS</html>");
        marca.setForeground(Color.WHITE);
        marca.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 22f));
        panel.add(marca, BorderLayout.NORTH);
        for (String item : new String[]{"Dashboard", "Agenda", "Turnos", "Clientes", "Barberos", "Servicios", "Sucursales", "Reportes", "Configuraci\u00f3n"}) menuModel.addElement(item);
        sidebar.setBackground(Color.decode(EstiloUI.SIDEBAR));
        sidebar.setForeground(Color.WHITE);
        sidebar.setFixedCellHeight(42);
        sidebar.setSelectionBackground(Color.decode(EstiloUI.ROJO));
        sidebar.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        sidebar.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String item = sidebar.getSelectedValue();
                if ("Turnos".equals(item)) mostrar("Turnos");
                else if ("Barberos".equals(item)) mostrar("Barberos");
                else if ("Dashboard".equals(item) || "Agenda".equals(item)) mostrar("Dashboard");
                else if ("Reportes".equals(item)) mostrar("Reportes");
                else MensajesUI.mostrarProximamente(this);
            }
        });
        panel.add(sidebar, BorderLayout.CENTER);
        JLabel usuario = new JLabel("<html>Administrador<br><span style='color:#B8C0C8'>admin</span><br><br>Version 1.0.0</html>");
        usuario.setForeground(Color.WHITE);
        usuario.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));
        panel.add(usuario, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel crearEstado() {
        estado = new JLabel();
        estado.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        actualizarEstado();
        return estado;
    }

    private void actualizarEstado() {
        if (estado != null) {
            String origen = repository.usaMySQL() ? "MySQL/JDBC" : "Datos demo (fallback)";
            estado.setText("  Sucursal actual: Centro                                      \u25cf Conexi\u00f3n: " + origen);
        }
    }

    private void mostrar(String nombre) {
        ((CardLayout) contenido.getLayout()).show(contenido, nombre);
        sidebar.setSelectedValue(nombre, true);
    }
}