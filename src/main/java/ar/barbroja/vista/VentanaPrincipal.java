package ar.barbroja.vista;

import ar.barbroja.util.DatosDemo;
import ar.barbroja.util.EstiloUI;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del prototipo. Organiza menu superior, toolbar,
 * sidebar, panel central y barra de estado usando Java Swing.
 */
public class VentanaPrincipal extends JFrame {
    private final DatosDemo datos = new DatosDemo();
    private final JPanel contenido = new JPanel(new CardLayout());
    private final DefaultListModel<String> menuModel = new DefaultListModel<>();
    private final JList<String> sidebar = new JList<>(menuModel);

    public VentanaPrincipal() {
        setTitle("Barba Roja Turnos - Sistema de gestión de turnos y agenda operativa");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setJMenuBar(crearMenu());
        add(crearToolbar(), BorderLayout.NORTH);
        add(crearSidebar(), BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);
        add(crearEstado(), BorderLayout.SOUTH);

        // CardLayout permite cambiar de pantalla sin abrir nuevas ventanas.
        contenido.add(new PanelDashboard(datos, () -> mostrar("Turnos")), "Dashboard");
        contenido.add(new PanelRegistrarTurno(datos, () -> mostrar("Dashboard")), "Turnos");
        contenido.add(new PanelBarberos(datos), "Barberos");
        mostrar("Dashboard");
    }

    private JMenuBar crearMenu() {
        JMenuBar bar = new JMenuBar();
        String[] menus = {"Archivo", "Turnos", "Clientes", "Barberos", "Servicios", "Sucursales", "Reportes", "Configuración", "Ayuda"};
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
        // El parametro implementado distingue acciones reales de opciones futuras del sistema.
        agregarTool(toolBar, "Agenda", "Dashboard", true);
        agregarTool(toolBar, "Nuevo turno", "Turnos", true);
        agregarTool(toolBar, "Clientes", "Dashboard", false);
        agregarTool(toolBar, "Barberos", "Barberos", true);
        agregarTool(toolBar, "Servicios", "Dashboard", false);
        agregarTool(toolBar, "Sucursales", "Dashboard", false);
        agregarTool(toolBar, "Reportes", "Dashboard", false);
        agregarTool(toolBar, "Actualizar", "Dashboard", false);
        return toolBar;
    }

    private void agregarTool(JToolBar toolBar, String texto, String destino, boolean implementado) {
        JButton boton = new JButton(texto);
        if (implementado) {
            boton.addActionListener(e -> mostrar(destino));
        } else {
            boton.addActionListener(e -> MensajesUI.mostrarProximamente(this));
        }
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

        for (String item : new String[]{"Dashboard", "Agenda", "Turnos", "Clientes", "Barberos", "Servicios", "Sucursales", "Reportes", "Configuración"}) {
            menuModel.addElement(item);
        }
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
                else MensajesUI.mostrarProximamente(this);
            }
        });
        panel.add(sidebar, BorderLayout.CENTER);

        JLabel usuario = new JLabel("<html>Administrador<br><span style='color:#B8C0C8'>admin</span><br><br>Versión 1.0.0</html>");
        usuario.setForeground(Color.WHITE);
        usuario.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));
        panel.add(usuario, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel crearEstado() {
        JLabel estado = new JLabel("  Sucursal actual: Centro                                      ● Conexión: Activa");
        estado.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        return estado;
    }

    private void mostrar(String nombre) {
        // Metodo centralizado de navegacion entre pantallas del prototipo.
        ((CardLayout) contenido.getLayout()).show(contenido, nombre);
        sidebar.setSelectedValue(nombre, true);
    }
}
