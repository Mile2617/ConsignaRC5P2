import java.util.Date;

public class Reserva {
    private static int contador = 1;
    private int id;
    private Huesped huesped;
    private Date fechaEntrada;
    private Date fechaSalida;
    private Habitacion habitacion;
    private Pago pago;

    public Reserva(Huesped huesped, Date fechaEntrada, Date fechaSalida, Habitacion habitacion) {
        this.id = contador++;
        this.huesped = huesped;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.habitacion = habitacion;
        this.pago = null;
    }

    public double calcularTotal() {
        long diff = fechaSalida.getTime() - fechaEntrada.getTime();
        long dias = Math.max(1, diff / (1000 * 60 * 60 * 24));
        return dias * habitacion.getPrecioPorNoche();
    }

    public void registrarPago(String metodo) {
        double total = calcularTotal();
        this.pago = new Pago(metodo, total);
    }

    public int getId() { return id; }
    public Huesped getHuesped() { return huesped; }
    public Date getFechaEntrada() { return fechaEntrada; }
    public Date getFechaSalida() { return fechaSalida; }
    public Habitacion getHabitacion() { return habitacion; }
    public Pago getPago() { return pago; }

    @Override
    public String toString() {
        return "Reserva ID: " + id + ", Huesped: " + huesped + ", Habitación: " + habitacion.getNumero() +
                ", Entrada: " + fechaEntrada + ", Salida: " + fechaSalida +
                (pago != null ? ", " + pago : ", Pago pendiente");
    }
}
