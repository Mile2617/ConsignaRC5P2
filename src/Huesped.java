public class Huesped {
    private String nombre;
    private String cedula;

    public Huesped(String nombre, String cedula) {
        this.nombre = nombre;
        this.cedula = cedula;
    }

    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }

    @Override
    public String toString() {
        return nombre + " (C.I: " + cedula + ")";
    }
}
