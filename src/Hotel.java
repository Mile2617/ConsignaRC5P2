import java.util.*;

public class Hotel {
    private List<Habitacion> habitaciones = new ArrayList<>();
    private List<Reserva> reservas = new ArrayList<>();

    // Agrega una nueva habitación a la lista
    public void agregarHabitacion(Habitacion h) {
        habitaciones.add(h);
    }

    // Verifica si una habitación está disponible
    public boolean estaDisponible(int numero) {
        for (Habitacion h : habitaciones) {
            if (h.getNumero() == numero) {
                return h.estaDisponible();
            }
        }
        return false;
    }

    // Crea una nueva reserva si la habitación está disponible
    public void crearReserva(Huesped huesped, Date entrada, Date salida, int numeroHabitacion) {
        for (Habitacion h : habitaciones) {
            if (h.getNumero() == numeroHabitacion && h.estaDisponible()) {
                Reserva r = new Reserva(huesped, entrada, salida, h);
                reservas.add(r);
                h.setDisponible(false);
                return;
            }
        }
        throw new IllegalArgumentException("Habitación no disponible o inexistente.");
    }

    public void registrarPago(String idReserva, String metodo) {
        for (Reserva r : reservas) {
            if (r.getId().equals(idReserva)) {
                r.registrarPago(metodo);
                return;
            }
        }
        throw new IllegalArgumentException("Reserva no encontrada.");
    }



    // Muestra todas las reservas en consola
    public void mostrarReservas() {
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
        } else {
            for (Reserva r : reservas) {
                System.out.println(r);
            }
        }
    }

    // Retorna la lista de habitaciones
    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    // Retorna la lista de reservas
    public List<Reserva> getReservas() {
        return reservas;
    }
}
