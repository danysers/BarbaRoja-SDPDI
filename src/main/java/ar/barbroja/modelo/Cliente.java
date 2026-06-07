package ar.barbroja.modelo;

/**
 * Especializacion de Persona para quienes solicitan turnos.
 */
public class Cliente extends Persona {
    private String email;
    private String observaciones;

    public Cliente(int id, String nombre, String apellido, String telefono, String email, String observaciones) {
        super(id, nombre, apellido, telefono);
        this.email = email;
        this.observaciones = observaciones;
    }

    public String getEmail() { return email; }
    public String getObservaciones() { return observaciones; }

    @Override
    public String getTipoPersona() {
        return "Cliente";
    }
}
