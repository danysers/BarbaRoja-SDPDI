package ar.barbroja.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa una reserva de la agenda. Integra cliente, barbero, servicio,
 * sucursal, fecha, horario y estado del turno.
 */
public class Turno {
    // Atributos privados para cumplir encapsulamiento.
    private int id;
    private Cliente cliente;
    private Barbero barbero;
    private Servicio servicio;
    private Sucursal sucursal;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoTurno estado;
    private String observaciones;

    // Constructor usado para crear objetos Turno con todos sus datos iniciales.
    public Turno(int id, Cliente cliente, Barbero barbero, Servicio servicio, Sucursal sucursal,
                 LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, EstadoTurno estado, String observaciones) {
        this.id = id;
        this.cliente = cliente;
        this.barbero = barbero;
        this.servicio = servicio;
        this.sucursal = sucursal;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Barbero getBarbero() { return barbero; }
    public Servicio getServicio() { return servicio; }
    public Sucursal getSucursal() { return sucursal; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public EstadoTurno getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }
}
