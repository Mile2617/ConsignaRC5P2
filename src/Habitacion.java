public class Habitacion {
    private int numero;
    private String tipo;
    private double precioPorNoche;
    private boolean disponible;
    private String requerimientoAdicional;

    public Habitacion(int numero, String tipo, double precioPorNoche, String requerimientoAdicional) {
        this.numero = numero;
        this.tipo = tipo;
        this.precioPorNoche = precioPorNoche;
        this.disponible = true;
        this.requerimientoAdicional = requerimientoAdicional;
    }

    // Constructor opcional para permitir solo 3 parámetros
    public Habitacion(int numero, String tipo, double precioPorNoche) {
        this(numero, tipo, precioPorNoche, "");
    }

    public int getNumero() { return numero; }
    public String getTipo() { return tipo; }
    public double getPrecioPorNoche() { return precioPorNoche; }
    public boolean estaDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public String getRequerimientoAdicional() { return requerimientoAdicional; }

    @Override
    public String toString() {
        return "Habitación " + numero + " (" + tipo + ", $" + precioPorNoche + "/noche, " +
                (disponible ? "Disponible" : "No disponible") +
                (requerimientoAdicional != null && !requerimientoAdicional.isEmpty()
                        ? ", Requerimiento: " + requerimientoAdicional
                        : "") + ")";
    }
}