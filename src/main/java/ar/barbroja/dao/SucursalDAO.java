package ar.barbroja.dao;

import ar.barbroja.excepciones.ConexionException;
import ar.barbroja.modelo.Sucursal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO de sucursales: centraliza las consultas JDBC sobre la tabla sucursal. */
public class SucursalDAO {
    public List<Sucursal> listarActivas() throws ConexionException {
        List<Sucursal> sucursales = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre, estado FROM sucursal WHERE estado='ACTIVA' ORDER BY nombre";
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sucursales.add(new Sucursal(rs.getInt("id_sucursal"), rs.getString("nombre"), "ACTIVA".equals(rs.getString("estado"))));
            }
            return sucursales;
        } catch (SQLException e) {
            throw new ConexionException("No se pudieron consultar las sucursales.", e);
        }
    }

    public int contarActivas() throws ConexionException {
        return contar("SELECT COUNT(*) FROM sucursal WHERE estado='ACTIVA'");
    }

    private int contar(String sql) throws ConexionException {
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new ConexionException("No se pudo contar sucursales.", e);
        }
    }
}
