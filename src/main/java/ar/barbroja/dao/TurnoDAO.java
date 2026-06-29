package ar.barbroja.dao;

import ar.barbroja.excepciones.ConexionException;
import ar.barbroja.modelo.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de turnos. Toda operacion persistente sobre la agenda pasa por esta clase:
 * consultas del dashboard, altas, cancelaciones y validacion de superposicion.
 */
public class TurnoDAO {
    public List<Turno> listarPorFecha(LocalDate fecha) throws ConexionException {
        List<Turno> turnos = new ArrayList<>();
        String sql = """
                SELECT t.id_turno, t.fecha, t.hora_inicio, t.hora_fin, t.estado, t.observaciones,
                       c.id_cliente, c.nombre AS cliente_nombre, c.apellido AS cliente_apellido, c.telefono AS cliente_tel, c.email, c.observaciones AS cliente_obs,
                       b.id_barbero, b.nombre AS barbero_nombre, b.apellido AS barbero_apellido, b.telefono AS barbero_tel, b.especialidad, b.estado AS barbero_estado,
                       sv.id_servicio, sv.nombre AS servicio, sv.duracion_min, sv.precio,
                       s.id_sucursal, s.nombre AS sucursal, s.estado AS sucursal_estado
                FROM turno t
                INNER JOIN cliente c ON c.id_cliente = t.id_cliente
                INNER JOIN barbero b ON b.id_barbero = t.id_barbero
                INNER JOIN servicio sv ON sv.id_servicio = t.id_servicio
                INNER JOIN sucursal s ON s.id_sucursal = t.id_sucursal
                WHERE t.fecha = ?
                ORDER BY t.hora_inicio
                """;
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mapearTurno(rs));
                }
            }
            return turnos;
        } catch (SQLException e) {
            throw new ConexionException("No se pudo consultar la agenda del dia.", e);
        }
    }

    public int contarPorFecha(LocalDate fecha) throws ConexionException {
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM turno WHERE fecha=?")) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ConexionException("No se pudo contar turnos del dia.", e);
        }
    }

    public Turno insertar(Turno turno) throws ConexionException {
        String sql = """
                INSERT INTO turno (id_cliente, id_barbero, id_servicio, id_sucursal, fecha, hora_inicio, hora_fin, estado, observaciones)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, turno.getCliente().getId());
            ps.setInt(2, turno.getBarbero().getId());
            ps.setInt(3, turno.getServicio().getId());
            ps.setInt(4, turno.getSucursal().getId());
            ps.setDate(5, Date.valueOf(turno.getFecha()));
            ps.setTime(6, Time.valueOf(turno.getHoraInicio()));
            ps.setTime(7, Time.valueOf(turno.getHoraFin()));
            ps.setString(8, turno.getEstado().name());
            ps.setString(9, turno.getObservaciones());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = turno.getId();
                if (keys.next()) {
                    id = keys.getInt(1);
                }
                return new Turno(id, turno.getCliente(), turno.getBarbero(), turno.getServicio(), turno.getSucursal(), turno.getFecha(), turno.getHoraInicio(), turno.getHoraFin(), turno.getEstado(), turno.getObservaciones());
            }
        } catch (SQLException e) {
            throw new ConexionException("No se pudo insertar el turno en MySQL.", e);
        }
    }

    public void cancelar(int idTurno) throws ConexionException {
        String sql = "UPDATE turno SET estado='CANCELADO', observaciones=CONCAT(COALESCE(observaciones,''), ' | Cancelado desde la aplicacion') WHERE id_turno=?";
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idTurno);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new ConexionException("No se encontro el turno a cancelar.");
            }
        } catch (SQLException e) {
            throw new ConexionException("No se pudo cancelar el turno en MySQL.", e);
        }
    }

    public boolean existeSuperposicion(int idBarbero, LocalDate fecha, LocalTime inicio, LocalTime fin) throws ConexionException {
        String sql = """
                SELECT COUNT(*) FROM turno
                WHERE id_barbero=? AND fecha=? AND estado <> 'CANCELADO'
                  AND (? < hora_fin AND ? > hora_inicio)
                """;
        try (Connection c = ConexionMySQL.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idBarbero);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(inicio));
            ps.setTime(4, Time.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new ConexionException("No se pudo validar la disponibilidad en MySQL.", e);
        }
    }

    private Turno mapearTurno(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(rs.getInt("id_cliente"), rs.getString("cliente_nombre"), rs.getString("cliente_apellido"), rs.getString("cliente_tel"), rs.getString("email"), rs.getString("cliente_obs"));
        Sucursal sucursal = new Sucursal(rs.getInt("id_sucursal"), rs.getString("sucursal"), "ACTIVA".equals(rs.getString("sucursal_estado")));
        Barbero barbero = new Barbero(rs.getInt("id_barbero"), rs.getString("barbero_nombre"), rs.getString("barbero_apellido"), rs.getString("barbero_tel"), rs.getString("especialidad"), sucursal, "ACTIVO".equals(rs.getString("barbero_estado")));
        Servicio servicio = new Servicio(rs.getInt("id_servicio"), rs.getString("servicio"), rs.getInt("duracion_min"), rs.getDouble("precio"));
        EstadoTurno estado = EstadoTurno.valueOf(rs.getString("estado"));
        return new Turno(rs.getInt("id_turno"), cliente, barbero, servicio, sucursal, rs.getDate("fecha").toLocalDate(), rs.getTime("hora_inicio").toLocalTime(), rs.getTime("hora_fin").toLocalTime(), estado, rs.getString("observaciones"));
    }
}
