package ar.barbroja.servicio;

import ar.barbroja.excepciones.DisponibilidadException;
import ar.barbroja.excepciones.TurnoException;
import ar.barbroja.modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Coordina las reglas de negocio para registrar turnos.
 */
public class TurnoService {
    private final List<Turno> turnos;
    private final DisponibilidadService disponibilidadService;

    public TurnoService(List<Turno> turnos) {
        this.turnos = turnos;
        this.disponibilidadService = new DisponibilidadService(turnos);
    }

    public Turno registrar(Cliente cliente, Barbero barbero, Servicio servicio, Sucursal sucursal,
                           LocalDate fecha, LocalTime inicio, String observaciones) throws TurnoException {
        // Validaciones basicas del formulario. Si fallan, se informa con excepciones propias.
        if (cliente == null) throw new TurnoException("Debe seleccionar un cliente.");
        if (barbero == null || !barbero.isActivo()) throw new TurnoException("Debe seleccionar un barbero activo.");
        if (servicio == null) throw new TurnoException("Debe seleccionar un servicio.");
        if (sucursal == null || !sucursal.isActiva()) throw new TurnoException("Debe seleccionar una sucursal activa.");

        // La hora de fin se calcula segun la duracion del servicio elegido.
        LocalTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());
        if (!disponibilidadService.estaDisponible(barbero, fecha, inicio, fin)) {
            throw new DisponibilidadException("El horario seleccionado no se encuentra disponible.");
        }

        // En el prototipo se guarda en memoria; en la version final se reemplaza por DAO/JDBC.
        Turno turno = new Turno(turnos.size() + 1, cliente, barbero, servicio, sucursal, fecha, inicio, fin,
                EstadoTurno.PROGRAMADO, observaciones);
        turnos.add(turno);
        return turno;
    }
}
