package ar.barbroja.dao;

import ar.barbroja.excepciones.ConexionException;
import ar.barbroja.modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO de clientes: obtiene clientes reales desde MySQL para combos y reportes. */
public class ClienteDAO {
    public List<Cliente> listarTodos() throws ConexionException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, apellido, telefono, email, observaciones FROM cliente ORDER BY apellido, nombre";
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"), rs.getString("apellido"), rs.getString("telefono"), rs.getString("email"), rs.getString("observaciones")));
            }
            return clientes;
        } catch (SQLException e) {
            throw new ConexionException("No se pudieron consultar los clientes.", e);
        }
    }

    public int contarTodos() throws ConexionException {
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM cliente"); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new ConexionException("No se pudo contar clientes.", e);
        }
    }
}
