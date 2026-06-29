package ar.barbroja.modelo;

import ar.barbroja.util.TextoUI;

/**
 * Clase base del dominio. Se declara abstracta para aplicar abstraccion y
 * reutilizar atributos comunes entre clientes, barberos y futuros usuarios.
 */
public abstract class Persona {
    private int id;
    private String nombre;
    private String apellido;
    private String telefono;

    protected Persona(int id, String nombre, String apellido, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public String getNombre() { return TextoUI.normalizar(nombre); }
    public String getApellido() { return TextoUI.normalizar(apellido); }
    public String getTelefono() { return TextoUI.normalizar(telefono); }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getNombreCompleto() { return getNombre() + " " + getApellido(); }

    // Metodo polimorfico: cada subclase informa su tipo de persona.
    public abstract String getTipoPersona();

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}