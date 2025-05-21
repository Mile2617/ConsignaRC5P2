import java.sql.Date;
import java.util.Scanner;



public class SistemaHotel {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();

        hotel.agregarHabitacion(new Habitacion(101, "Individual", 50));
        hotel.agregarHabitacion(new Habitacion(102, "Doble", 80));
        hotel.agregarHabitacion(new Habitacion(103, "Suite", 120));

        int opcion;
        do {
            System.out.println("\n--- Menú del Sistema de Operación Hotelera ---");
            System.out.println("1. Crear nueva reserva");
            System.out.println("2. Consultar disponibilidad de una habitación");
            System.out.println("3. Registrar pago de una reserva");
            System.out.println("4. Mostrar todas las reservas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Nombre del huésped: ");
                    String nombre = sc.nextLine();
                    System.out.print("Cédula del huésped: ");
                    String cedula = sc.nextLine();
                    Huesped huesped = new Huesped(nombre, cedula);

                    System.out.print("Número de habitación: ");
                    int numero = sc.nextInt();
                    sc.nextLine();
                    try {
                        System.out.print("Fecha de entrada (yyyy-mm-dd): ");
                        Date entrada = Date.valueOf(sc.nextLine());
                        System.out.print("Fecha de salida (yyyy-mm-dd): ");
                        Date salida = Date.valueOf(sc.nextLine());
                        hotel.crearReserva(huesped, entrada, salida, numero);
                    } catch (Exception e) {
                        System.out.println("Error en formato de fecha.");
                    }
                    break;
                case 2:
                    System.out.print("Número de habitación: ");
                    int num = sc.nextInt();
                    System.out.println(hotel.estaDisponible(num) ? "Disponible" : "No disponible o inexistente");
                    break;
                case 3:
                    System.out.print("ID de la reserva: ");
                    String id = sc.nextLine();  // ✅ ahora id es un String
                    System.out.print("Método de pago: ");
                    String metodo = sc.nextLine();
                    hotel.registrarPago(id, metodo);  // ✅ sin errores
                    break;

                case 4:
                    hotel.mostrarReservas();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
        sc.close();
    }
}
