package ar.barbroja.modelo;

public class Servicio {
    private int id;
    private String nombre;
    private int duracionMinutos;
    private double precio;

    public Servicio(int id, String nombre, int duracionMinutos, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public double getPrecio() { return precio; }

    @Override
    public String toString() {
        return nombre;
    }
}
