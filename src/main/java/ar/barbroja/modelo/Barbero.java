package ar.barbroja.modelo;

import ar.barbroja.util.TextoUI;

/**
 * Especializacion de Persona que agrega datos laborales propios del barbero.
 * Implementar Buscable permite tratar clientes y barberos con un mismo contrato.
 */
public class Barbero extends Persona implements Buscable {
    private String especialidad;
    private Sucursal sucursal;
    private boolean activo;
    private String notas;

    public Barbero(int id, String nombre, String apellido, String telefono, String especialidad, Sucursal sucursal, boolean activo) {
        super(id, nombre, apellido, telefono);
        this.especialidad = especialidad;
        this.sucursal = sucursal;
        this.activo = activo;
        this.notas = "";
    }

    public String getEspecialidad() { return TextoUI.normalizar(especialidad); }
    public Sucursal getSucursal() { return sucursal; }
    public boolean isActivo() { return activo; }
    public String getNotas() { return TextoUI.normalizar(notas); }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setNotas(String notas) { this.notas = notas; }

    @Override
    public String getTipoPersona() {
        return "Barbero";
    }

    @Override
    public boolean coincideCon(String texto) {
        String valor = texto == null ? "" : texto.toLowerCase();
        return getNombreCompleto().toLowerCase().contains(valor)
                || getEspecialidad().toLowerCase().contains(valor)
                || sucursal.getNombre().toLowerCase().contains(valor);
    }
}