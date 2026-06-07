package ar.barbroja.modelo;

/**
 * Estados posibles de un turno. El enum evita usar textos sueltos en la logica.
 */
public enum EstadoTurno {
    PROGRAMADO("Programado"),
    CONFIRMADO("Confirmado"),
    EN_CURSO("En curso"),
    COMPLETADO("Completado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoTurno(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
