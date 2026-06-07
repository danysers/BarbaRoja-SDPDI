package ar.barbroja.util;

import ar.barbroja.modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Carga datos en memoria para que el prototipo funcione aunque MySQL no este
 * disponible durante la presentacion.
 */
public class DatosDemo {
    private final List<Sucursal> sucursales = new ArrayList<>();
    private final List<Servicio> servicios = new ArrayList<>();
    private final List<Barbero> barberos = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Turno> turnos = new ArrayList<>();

    public DatosDemo() {
        cargar();
    }

    private void cargar() {
        Sucursal centro = new Sucursal(1, "Centro", true);
        Sucursal norte = new Sucursal(2, "Norte", true);
        Sucursal sur = new Sucursal(3, "Sur", true);
        sucursales.addAll(List.of(centro, norte, sur));

        Servicio corte = new Servicio(1, "Corte Clásico", 45, 4500);
        Servicio barba = new Servicio(2, "Barba", 30, 3000);
        Servicio corteBarba = new Servicio(3, "Corte + Barba", 60, 6500);
        Servicio degradado = new Servicio(4, "Degradado", 60, 5500);
        servicios.addAll(List.of(corte, barba, corteBarba, degradado, new Servicio(5, "Perfilado de cejas", 20, 2500)));

        barberos.addAll(List.of(
                new Barbero(1, "Juan", "Pérez", "11 2345 6789", "Corte Clásico", centro, true),
                new Barbero(2, "Carlos", "Ramírez", "11 2233 4455", "Corte + Barba", centro, true),
                new Barbero(3, "Luis", "Gómez", "11 3344 5566", "Degradado", norte, true),
                new Barbero(4, "Pedro", "Sánchez", "11 4455 6677", "Corte + Barba", sur, true),
                new Barbero(5, "Martín", "López", "11 5566 7788", "Corte Clásico", centro, false),
                new Barbero(6, "Emiliano", "García", "11 6677 8899", "Degradado", centro, true)
        ));
        barberos.get(1).setNotas("Especialista en cortes clásicos y perfilado de barba.");

        clientes.addAll(List.of(
                new Cliente(1, "Martín", "López", "(11) 5555-1001", "", ""),
                new Cliente(2, "Emiliano", "García", "(11) 5555-1234", "", ""),
                new Cliente(3, "Luciano", "Fernández", "(11) 5555-1003", "", ""),
                new Cliente(4, "Diego", "Álvarez", "(11) 5555-1004", "", ""),
                new Cliente(5, "Santiago", "Morales", "(11) 5555-1005", "", ""),
                new Cliente(6, "Matías", "Rojas", "(11) 5555-1006", "", ""),
                new Cliente(7, "Facundo", "Herrera", "(11) 5555-1007", "", ""),
                new Cliente(8, "Nicolás", "Castro", "(11) 5555-1008", "", ""),
                new Cliente(9, "Agustín", "Díaz", "(11) 5555-1009", "", ""),
                new Cliente(10, "Joaquín", "Torres", "(11) 5555-1010", "", "")
        ));

        LocalDate fecha = LocalDate.of(2025, 5, 23);
        agregarTurno(1, 0, 0, 0, centro, fecha, "09:00", EstadoTurno.COMPLETADO);
        agregarTurno(2, 1, 0, 3, centro, fecha, "09:30", EstadoTurno.COMPLETADO);
        agregarTurno(3, 2, 1, 2, centro, fecha, "10:00", EstadoTurno.EN_CURSO);
        agregarTurno(4, 3, 1, 1, centro, fecha, "10:30", EstadoTurno.CONFIRMADO);
        agregarTurno(5, 4, 2, 3, norte, fecha, "11:00", EstadoTurno.CONFIRMADO);
        agregarTurno(6, 5, 2, 0, norte, fecha, "11:30", EstadoTurno.CONFIRMADO);
        agregarTurno(7, 6, 3, 2, sur, fecha, "12:00", EstadoTurno.CONFIRMADO);
        agregarTurno(8, 7, 3, 0, sur, fecha, "12:30", EstadoTurno.PROGRAMADO);
        agregarTurno(9, 8, 0, 3, centro, fecha, "13:00", EstadoTurno.PROGRAMADO);
        agregarTurno(10, 9, 1, 2, centro, fecha, "13:30", EstadoTurno.PROGRAMADO);
    }

    private void agregarTurno(int id, int cliente, int barbero, int servicio, Sucursal sucursal, LocalDate fecha, String hora, EstadoTurno estado) {
        LocalTime inicio = LocalTime.parse(hora);
        Servicio servicioElegido = servicios.get(servicio);
        turnos.add(new Turno(id, clientes.get(cliente), barberos.get(barbero), servicioElegido, sucursal,
                fecha, inicio, inicio.plusMinutes(servicioElegido.getDuracionMinutos()), estado, ""));
    }

    public List<Sucursal> getSucursales() { return sucursales; }
    public List<Servicio> getServicios() { return servicios; }
    public List<Barbero> getBarberos() { return barberos; }
    public List<Cliente> getClientes() { return clientes; }
    public List<Turno> getTurnos() {
        // Algoritmo de ordenacion: la agenda se muestra ordenada por hora de inicio.
        turnos.sort(Comparator.comparing(Turno::getHoraInicio));
        return turnos;
    }
}
