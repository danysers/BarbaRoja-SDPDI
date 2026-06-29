package ar.barbroja.excepciones;

/**
 * Excepcion propia para encapsular errores de conexion, consulta o escritura SQL.
 * Permite que la vista muestre mensajes controlados sin exponer SQLException.
 */
public class ConexionException extends Exception {
    public ConexionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConexionException(String message) {
        super(message);
    }
}
