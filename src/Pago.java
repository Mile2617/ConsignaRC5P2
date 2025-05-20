import java.util.Date;

public class Pago {
    private String metodo;
    private double monto;
    private Date fecha;

    public Pago(String metodo, double monto) {
        this.metodo = metodo;
        this.monto = monto;
        this.fecha = new Date();
    }

    @Override
    public String toString() {
        return "Pago de $" + monto + " mediante " + metodo + " el " + fecha;
    }
}
