package ar.barbroja.modelo;

import ar.barbroja.util.TextoUI;

/**
 * Especializacion de Persona para quienes solicitan turnos. Implementa Buscable
 * para demostrar polimorfismo por interfaz en busquedas del dominio.
 */
public class Cliente extends Persona implements Buscable {
    private String email;
    private String observaciones;

    public Cliente(int id, String nombre, String apellido, String telefono, String email, String observaciones) {
        super(id, nombre, apellido, telefono);
        this.email = email;
        this.observaciones = observaciones;
    }

    public String getEmail() { return TextoUI.normalizar(email); }
    public String getObservaciones() { return TextoUI.normalizar(observaciones); }

    @Override
    public String getTipoPersona() {
        return "Cliente";
    }

    @Override
    public boolean coincideCon(String texto) {
        String valor = texto == null ? "" : texto.toLowerCase();
        return getNombreCompleto().toLowerCase().contains(valor)
                || getTelefono().toLowerCase().contains(valor);
    }
}