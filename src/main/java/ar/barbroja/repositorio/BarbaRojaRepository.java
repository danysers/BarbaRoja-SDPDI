package ar.barbroja.repositorio;

import ar.barbroja.dao.*;
import ar.barbroja.excepciones.ConexionException;
import ar.barbroja.modelo.*;
import ar.barbroja.util.DatosDemo;

import java.time.LocalDate;
import java.util.List;

/**
 * Fachada de datos de la aplicacion. La UI consume esta clase y no decide si
 * los datos vienen de MySQL o de la carga demo. Esto mantiene separacion por capas.
 */
public class BarbaRojaRepository {
    private final DatosDemo demo = new DatosDemo();
    private final SucursalDAO sucursalDAO = new SucursalDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final BarberoDAO barberoDAO = new BarberoDAO();
    private final TurnoDAO turnoDAO = new TurnoDAO();
    private boolean mysqlDisponible;
    private String ultimoError = "";

    public BarbaRojaRepository() {
        verificarConexion();
    }

    public void verificarConexion() {
        try {
            sucursalDAO.contarActivas();
            mysqlDisponible = true;
            ultimoError = "";
        } catch (ConexionException e) {
            mysqlDisponible = false;
            ultimoError = e.getMessage();
        }
    }

    public boolean usaMySQL() { return mysqlDisponible; }
    public String getUltimoError() { return ultimoError; }
    public TurnoDAO getTurnoDAO() { return turnoDAO; }
    public BarberoDAO getBarberoDAO() { return barberoDAO; }

    public List<Sucursal> sucursales() {
        try { if (mysqlDisponible) return sucursalDAO.listarActivas(); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getSucursales();
    }

    public List<Cliente> clientes() {
        try { if (mysqlDisponible) return clienteDAO.listarTodos(); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getClientes();
    }

    public List<Servicio> servicios() {
        try { if (mysqlDisponible) return servicioDAO.listarActivos(); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getServicios();
    }

    public List<Barbero> barberos() {
        try { if (mysqlDisponible) return barberoDAO.listarTodos(); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getBarberos();
    }

    public List<Barbero> barberosActivos() {
        try { if (mysqlDisponible) return barberoDAO.listarActivos(); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getBarberos().stream().filter(Barbero::isActivo).toList();
    }

    public List<Turno> turnosPorFecha(LocalDate fecha) {
        try { if (mysqlDisponible) return turnoDAO.listarPorFecha(fecha); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getTurnos();
    }

    public int totalClientes() {
        try { if (mysqlDisponible) return clienteDAO.contarTodos(); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getClientes().size();
    }

    public int totalBarberosActivos() {
        try { if (mysqlDisponible) return barberoDAO.contarActivos(); } catch (ConexionException e) { marcarFallback(e); }
        return (int) demo.getBarberos().stream().filter(Barbero::isActivo).count();
    }

    public int totalSucursalesActivas() {
        try { if (mysqlDisponible) return sucursalDAO.contarActivas(); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getSucursales().size();
    }

    public int totalTurnos(LocalDate fecha) {
        try { if (mysqlDisponible) return turnoDAO.contarPorFecha(fecha); } catch (ConexionException e) { marcarFallback(e); }
        return demo.getTurnos().size();
    }

    private void marcarFallback(Exception e) {
        mysqlDisponible = false;
        ultimoError = e.getMessage();
    }
}
