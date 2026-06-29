package ar.barbroja.servicio;

import ar.barbroja.dao.TurnoDAO;
import ar.barbroja.excepciones.ConexionException;
import ar.barbroja.excepciones.DisponibilidadException;
import ar.barbroja.excepciones.TurnoException;
import ar.barbroja.modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Coordina las reglas de negocio para registrar y cancelar turnos. Si recibe
 * TurnoDAO trabaja contra MySQL; si recibe una lista funciona como fallback demo.
 */
public class TurnoService {
    private final List<Turno> turnosDemo;
    private final TurnoDAO turnoDAO;
    private final DisponibilidadService disponibilidadService;

    public TurnoService(List<Turno> turnos) {
        this.turnosDemo = turnos;
        this.turnoDAO = null;
        this.disponibilidadService = new DisponibilidadService(turnos);
    }

    public TurnoService(TurnoDAO turnoDAO) {
        this.turnosDemo = null;
        this.turnoDAO = turnoDAO;
        this.disponibilidadService = null;
    }

    public Turno registrar(Cliente cliente, Barbero barbero, Servicio servicio, Sucursal sucursal,
                           LocalDate fecha, LocalTime inicio, String observaciones) throws TurnoException {
        validarCampos(cliente, barbero, servicio, sucursal, fecha, inicio);
        LocalTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());
        if (turnoDAO != null) {
            try {
                if (turnoDAO.existeSuperposicion(barbero.getId(), fecha, inicio, fin)) {
                    throw new DisponibilidadException("El horario seleccionado se superpone con otro turno del barbero.");
                }
                Turno turno = new Turno(0, cliente, barbero, servicio, sucursal, fecha, inicio, fin, EstadoTurno.PROGRAMADO, observaciones);
                return turnoDAO.insertar(turno);
            } catch (ConexionException e) {
                throw new TurnoException("Error al guardar el turno en MySQL: " + e.getMessage());
            }
        }
        if (!disponibilidadService.estaDisponible(barbero, fecha, inicio, fin)) {
            throw new DisponibilidadException("El horario seleccionado no se encuentra disponible.");
        }
        Turno turno = new Turno(turnosDemo.size() + 1, cliente, barbero, servicio, sucursal, fecha, inicio, fin,
                EstadoTurno.PROGRAMADO, observaciones);
        turnosDemo.add(turno);
        return turno;
    }

    public void cancelar(int idTurno) throws TurnoException {
        if (idTurno <= 0) throw new TurnoException("Debe seleccionar un turno valido para cancelar.");
        if (turnoDAO != null) {
            try {
                turnoDAO.cancelar(idTurno);
                return;
            } catch (ConexionException e) {
                throw new TurnoException("Error al cancelar el turno en MySQL: " + e.getMessage());
            }
        }
        for (Turno turno : turnosDemo) {
            if (turno.getId() == idTurno) {
                turno.setEstado(EstadoTurno.CANCELADO);
                return;
            }
        }
        throw new TurnoException("No se encontro el turno seleccionado.");
    }

    private void validarCampos(Cliente cliente, Barbero barbero, Servicio servicio, Sucursal sucursal,
                               LocalDate fecha, LocalTime inicio) throws TurnoException {
        if (cliente == null) throw new TurnoException("Debe seleccionar un cliente.");
        if (barbero == null || !barbero.isActivo()) throw new TurnoException("Debe seleccionar un barbero activo.");
        if (servicio == null) throw new TurnoException("Debe seleccionar un servicio.");
        if (sucursal == null || !sucursal.isActiva()) throw new TurnoException("Debe seleccionar una sucursal activa.");
        if (fecha == null) throw new TurnoException("Debe ingresar una fecha valida.");
        if (inicio == null) throw new TurnoException("Debe ingresar una hora valida.");
    }
}
