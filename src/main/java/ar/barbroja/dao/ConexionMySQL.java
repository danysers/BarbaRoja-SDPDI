package ar.barbroja.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexion JDBC directa a MySQL. No usa frameworks de persistencia.
 */
public class ConexionMySQL {
    private static final String URL = "jdbc:mysql://localhost:3307/barba_roja_turnos";
    private static final String USER = "root";
    private static final String PASSWORD = "barba_roja_2026";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
