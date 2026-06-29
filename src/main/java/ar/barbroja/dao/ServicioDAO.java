package ar.barbroja.dao;

import ar.barbroja.excepciones.ConexionException;
import ar.barbroja.modelo.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO de servicios activos usados para cargar turnos reales. */
public class ServicioDAO {
    public List<Servicio> listarActivos() throws ConexionException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre, duracion_min, precio FROM servicio WHERE estado='ACTIVO' ORDER BY nombre";
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                servicios.add(new Servicio(rs.getInt("id_servicio"), rs.getString("nombre"), rs.getInt("duracion_min"), rs.getDouble("precio")));
            }
            return servicios;
        } catch (SQLException e) {
            throw new ConexionException("No se pudieron consultar los servicios.", e);
        }
    }
}
