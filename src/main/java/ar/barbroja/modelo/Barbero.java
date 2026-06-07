package ar.barbroja.modelo;

/**
 * Especializacion de Persona que agrega datos laborales propios del barbero.
 */
public class Barbero extends Persona {
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

    public String getEspecialidad() { return especialidad; }
    public Sucursal getSucursal() { return sucursal; }
    public boolean isActivo() { return activo; }
    public String getNotas() { return notas; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setNotas(String notas) { this.notas = notas; }

    @Override
    public String getTipoPersona() {
        return "Barbero";
    }
}
