package ar.barbroja.dao;

import ar.barbroja.excepciones.ConexionException;
import ar.barbroja.modelo.Barbero;
import ar.barbroja.modelo.Sucursal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de barberos. Aplica el patron DAO para separar SQL de la interfaz Swing.
 * La UI solo consume objetos Barbero, no conoce consultas ni ResultSet.
 */
public class BarberoDAO {
    public List<Barbero> listarTodos() throws ConexionException {
        List<Barbero> barberos = new ArrayList<>();
        String sql = """
                SELECT b.id_barbero, b.nombre, b.apellido, b.telefono, b.especialidad, b.estado,
                       s.id_sucursal, s.nombre AS sucursal, s.estado AS estado_sucursal
                FROM barbero b
                INNER JOIN sucursal s ON s.id_sucursal = b.id_sucursal
                ORDER BY b.apellido, b.nombre
                """;
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Sucursal sucursal = new Sucursal(rs.getInt("id_sucursal"), rs.getString("sucursal"), "ACTIVA".equals(rs.getString("estado_sucursal")));
                Barbero barbero = new Barbero(rs.getInt("id_barbero"), rs.getString("nombre"), rs.getString("apellido"), rs.getString("telefono"), rs.getString("especialidad"), sucursal, "ACTIVO".equals(rs.getString("estado")));
                barbero.setNotas("Datos cargados desde MySQL mediante BarberoDAO.");
                barberos.add(barbero);
            }
            return barberos;
        } catch (SQLException e) {
            throw new ConexionException("No se pudieron consultar los barberos.", e);
        }
    }

    public List<Barbero> listarActivos() throws ConexionException {
        List<Barbero> activos = new ArrayList<>();
        for (Barbero barbero : listarTodos()) {
            if (barbero.isActivo()) {
                activos.add(barbero);
            }
        }
        return activos;
    }

    public int contarActivos() throws ConexionException {
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM barbero WHERE estado='ACTIVO'"); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new ConexionException("No se pudo contar barberos activos.", e);
        }
    }

    public Object[][] listarHorarioSemanal(int idBarbero) throws ConexionException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT dia_semana, hora_desde, hora_hasta, trabaja FROM horario_barbero WHERE id_barbero=? ORDER BY FIELD(dia_semana,'LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO')";
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idBarbero);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{formatearDia(rs.getString("dia_semana")), rs.getTime("hora_desde").toLocalTime().toString(), rs.getTime("hora_hasta").toLocalTime().toString(), rs.getBoolean("trabaja")});
                }
            }
            return filas.toArray(new Object[0][]);
        } catch (SQLException e) {
            throw new ConexionException("No se pudo consultar el horario del barbero.", e);
        }
    }

    private String formatearDia(String dia) {
        return dia.substring(0, 1) + dia.substring(1).toLowerCase();
    }
}
