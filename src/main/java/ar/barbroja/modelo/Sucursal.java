package ar.barbroja.modelo;

import ar.barbroja.util.TextoUI;

public class Sucursal {
    private int id;
    private String nombre;
    private boolean activa;

    public Sucursal(int id, String nombre, boolean activa) {
        this.id = id;
        this.nombre = nombre;
        this.activa = activa;
    }

    public int getId() { return id; }
    public String getNombre() { return TextoUI.normalizar(nombre); }
    public boolean isActiva() { return activa; }

    @Override
    public String toString() {
        return getNombre();
    }
}
