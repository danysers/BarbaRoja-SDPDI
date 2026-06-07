package ar.barbroja.servicio;

import ar.barbroja.modelo.Barbero;
import ar.barbroja.modelo.EstadoTurno;
import ar.barbroja.modelo.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Servicio responsable de validar que un barbero no tenga dos turnos
 * superpuestos en la misma fecha.
 */
public class DisponibilidadService {
    private final List<Turno> turnos;

    public DisponibilidadService(List<Turno> turnos) {
        this.turnos = turnos;
    }

    public boolean estaDisponible(Barbero barbero, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Turno turno : turnos) {
            boolean mismoBarbero = turno.getBarbero().equals(barbero);
            boolean mismaFecha = turno.getFecha().equals(fecha);
            boolean noCancelado = turno.getEstado() != EstadoTurno.CANCELADO;
            // Regla de superposicion: el nuevo inicio cae antes del fin existente
            // y el nuevo fin cae despues del inicio existente.
            boolean seSuperpone = inicio.isBefore(turno.getHoraFin()) && fin.isAfter(turno.getHoraInicio());
            if (mismoBarbero && mismaFecha && noCancelado && seSuperpone) {
                return false;
            }
        }
        return true;
    }
}
