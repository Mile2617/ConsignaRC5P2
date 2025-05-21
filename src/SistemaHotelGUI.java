import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.jdatepicker.impl.*;

public class SistemaHotelGUI extends JFrame {
    private Hotel hotel;
    private JTable habitacionesTable, reservasTable, pagosTable;

    public SistemaHotelGUI() {
        hotel = new Hotel();
        inicializarHabitaciones();

        setTitle("Sistema de Gestión Hotelera");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Habitaciones", crearPanelHabitaciones());
        tabbedPane.addTab("Reservas", crearPanelReservas());
        tabbedPane.addTab("Ver Reservas", crearPanelVerReservas());
        tabbedPane.addTab("Pagos", crearPanelPagos());

        add(tabbedPane);
    }

    private JComboBox<String> listaReservas;

    private void inicializarHabitaciones() {
        String[] tipos = {"Suite", "Queen", "Doble", "Individual"};
        int[] precios = {200, 120, 100, 80};

        int suitesAdaptadas = 0, queenAdaptadas = 0, doblesAdaptadas = 0, individualesAdaptadas = 0;

        for (int piso = 1; piso <= 5; piso++) {
            int contadorHabitacion = 1;

            // Agregar 2 suites por piso
            for (int i = 0; i < 2; i++) {
                int numero = piso * 100 + contadorHabitacion++;
                boolean adaptada = suitesAdaptadas < 10;
                if (adaptada) suitesAdaptadas++;
                hotel.agregarHabitacion(new Habitacion(numero, tipos[0], precios[0], adaptada ? "Apta silla ruedas" : ""));
            }

            // Agregar 2 habitaciones tipo queen por piso
            for (int i = 0; i < 2; i++) {
                int numero = piso * 100 + contadorHabitacion++;
                boolean adaptada = queenAdaptadas < 4;
                if (adaptada) queenAdaptadas++;
                hotel.agregarHabitacion(new Habitacion(numero, tipos[1], precios[1], adaptada ? "Apta silla ruedas" : ""));
            }

            // Agregar 3 habitaciones dobles por piso
            for (int i = 0; i < 3; i++) {
                int numero = piso * 100 + contadorHabitacion++;
                boolean adaptada = doblesAdaptadas < 5;
                if (adaptada) doblesAdaptadas++;
                hotel.agregarHabitacion(new Habitacion(numero, tipos[2], precios[2], adaptada ? "Apta silla ruedas" : ""));
            }

            // Agregar 3 habitaciones individuales por piso
            for (int i = 0; i < 3; i++) {
                int numero = piso * 100 + contadorHabitacion++;
                boolean adaptada = individualesAdaptadas < 5;
                if (adaptada) individualesAdaptadas++;
                hotel.agregarHabitacion(new Habitacion(numero, tipos[3], precios[3], adaptada ? "Apta silla ruedas" : ""));
            }
        }
    }

    private JPanel crearPanelHabitaciones() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top: Hotel Drawing
        JPanel dibujoHotel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int roomWidth = 50, roomHeight = 30, gap = 10;
                int startX = 20, startY = 20;

                for (int piso = 5; piso >= 1; piso--) {
                    int x = startX;
                    int y = startY + (5 - piso) * (roomHeight + gap);

                    for (int i = 1; i <= 10; i++) {
                        int numero = piso * 100 + i;
                        Habitacion habitacion = hotel.getHabitaciones().stream()
                                .filter(h -> h.getNumero() == numero)
                                .findFirst()
                                .orElse(null);

                        if (habitacion != null) {
                            g.setColor(habitacion.estaDisponible() ? Color.GREEN : Color.RED);
                            g.fillRect(x, y, roomWidth, roomHeight);
                            g.setColor(Color.BLACK);
                            g.drawRect(x, y, roomWidth, roomHeight);
                            g.drawString(String.valueOf(numero), x + 15, y + 20);
                        }
                        x += roomWidth + gap;
                    }
                }
            }
        };
        dibujoHotel.setPreferredSize(new Dimension(600, 240));

        // Bottom: Room Details Table
        String[] columnas = {"Número", "Tipo", "Precio", "Disponibilidad", "Requerimiento"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        habitacionesTable = new JTable(modelo);

        actualizarTablaHabitaciones(modelo);

        panel.add(dibujoHotel, BorderLayout.NORTH);
        panel.add(new JScrollPane(habitacionesTable), BorderLayout.CENTER);
        return panel;
    }

    private void actualizarTablaHabitaciones(DefaultTableModel modelo) {
        modelo.setRowCount(0); // Clear existing rows
        for (Habitacion h : hotel.getHabitaciones()) {
            modelo.addRow(new Object[]{
                    h.getNumero(),
                    h.getTipo(),
                    h.getPrecioPorNoche(),
                    h.estaDisponible() ? "Sí" : "No",
                    h.getRequerimientoAdicional()
            });
        }
    }

    private void refreshUI() {
        // Actualizar tabla de habitaciones
        actualizarTablaHabitaciones((DefaultTableModel) habitacionesTable.getModel());

        // Actualizar tabla de reservas
        DefaultTableModel reservasModel = (DefaultTableModel) reservasTable.getModel();
        reservasModel.setRowCount(0);
        for (Reserva r : hotel.getReservas()) {
            reservasModel.addRow(new Object[]{
                    r.getId(),
                    r.getHuesped().getNombre(),
                    r.getHabitacion().getNumero(),
                    r.getFechaEntrada(),
                    r.getFechaSalida(),
                    r.getPago() != null ? r.getPago().toString() : "Pendiente"
            });
        }

        // Actualizar tabla de pagos
        DefaultTableModel pagosModel = (DefaultTableModel) pagosTable.getModel();
        pagosModel.setRowCount(0);
        for (Reserva r : hotel.getReservas()) {
            pagosModel.addRow(new Object[]{
                    r.getId(),
                    r.getHabitacion().getNumero(),
                    r.getHuesped().getNombre(),
                    String.format("$%.2f", r.calcularTotal()),
                    r.getPago() != null ? "Pagado" : "Pendiente"
            });
        }

        // Actualizar combo box de reservas pendientes
        if (listaReservas != null) {
            listaReservas.removeAllItems();
            for (Reserva r : hotel.getReservas()) {
                if (r.getPago() == null) {
                    listaReservas.addItem(r.getId());
                }
            }
        }
    }


    private JPanel crearPanelReservas() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0, 2));

        JTextField campoNombre = new JTextField();
        JTextField campoCedula = new JTextField();

        // Campo para seleccionar accesibilidad
        JComboBox<String> campoAccesible = new JComboBox<>(new String[]{"No", "Sí"});

        // Lista de habitaciones disponibles
        JComboBox<String> campoHabitacion = new JComboBox<>();

        // Calendarios para fechas de entrada y salida
        JDatePickerImpl campoEntrada = crearDatePicker();
        JDatePickerImpl campoSalida = crearDatePicker();

        // Listener para actualizar la lista de habitaciones según accesibilidad
        campoAccesible.addActionListener(e -> actualizarListaHabitaciones(campoHabitacion, campoAccesible.getSelectedItem().equals("Sí")));

        form.add(new JLabel("Nombre:")); form.add(campoNombre);
        form.add(new JLabel("Cédula:")); form.add(campoCedula);
        form.add(new JLabel("Accesible con silla de ruedas:")); form.add(campoAccesible);
        form.add(new JLabel("Nº Habitación:")); form.add(campoHabitacion);
        form.add(new JLabel("Entrada:")); form.add(campoEntrada);
        form.add(new JLabel("Salida:")); form.add(campoSalida);

        JButton btnReservar = new JButton("Reservar");
        btnReservar.addActionListener(e -> {
            try {
                // Validar que todos los campos estén llenos
                if (campoNombre.getText().isEmpty() || campoCedula.getText().isEmpty() ||
                        campoHabitacion.getSelectedItem() == null ||
                        campoEntrada.getModel().getValue() == null ||
                        campoSalida.getModel().getValue() == null) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                    return;
                }

                // Validar que la fecha de entrada no sea mayor a la de salida
                Date entrada = (Date) campoEntrada.getModel().getValue();
                Date salida = (Date) campoSalida.getModel().getValue();
                if (entrada.after(salida)) {
                    JOptionPane.showMessageDialog(this, "La fecha de entrada no puede ser mayor a la de salida.");
                    return;
                }

                // Crear la reserva
                Huesped h = new Huesped(campoNombre.getText(), campoCedula.getText());
                int nro = Integer.parseInt((String) campoHabitacion.getSelectedItem());
                hotel.crearReserva(h, entrada, salida, nro);

                // Mostrar mensaje de éxito y limpiar los campos
                JOptionPane.showMessageDialog(this, "Reserva realizada");
                campoNombre.setText("");
                campoCedula.setText("");
                campoHabitacion.removeAllItems();
                campoEntrada.getModel().setValue(null);
                campoSalida.getModel().setValue(null);
                campoAccesible.setSelectedIndex(0);

                refreshUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.CENTER);
        panel.add(btnReservar, BorderLayout.SOUTH);
        return panel;
    }



    private void actualizarListaHabitaciones(JComboBox<String> campoHabitacion, boolean accesible) {
        campoHabitacion.removeAllItems();
        for (Habitacion h : hotel.getHabitaciones()) {
            if (h.estaDisponible() && (!accesible || "Apta silla ruedas".equals(h.getRequerimientoAdicional()))) {
                campoHabitacion.addItem(String.valueOf(h.getNumero()));
            }
        }
    }

    private JDatePickerImpl crearDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Hoy");
        p.put("text.month", "Mes");
        p.put("text.year", "Año");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }



    private JPanel crearPanelVerReservas() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"ID", "Cliente", "Hab.", "Entrada", "Salida", "Pago"}; // Eliminada la columna "Cédula"
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        reservasTable = new JTable(modelo);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Reserva r : hotel.getReservas()) {
            String fechaEntrada = sdf.format(r.getFechaEntrada());
            String fechaSalida = sdf.format(r.getFechaSalida());

            modelo.addRow(new Object[]{
                    r.getId(),                            // ID
                    r.getHuesped().getNombre(),           // Cliente
                    r.getHabitacion().getNumero(),        // Nº habitación
                    fechaEntrada,                         // Entrada formateada
                    fechaSalida,                          // Salida formateada
                    r.getPago() != null ? r.getPago().toString() : "Pendiente" // Pago
            });
        }

        panel.add(new JScrollPane(reservasTable), BorderLayout.CENTER);
        return panel;
    }




    private JPanel crearPanelPagos() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnas = {"ID Reserva", "Habitación", "Nombre", "Total a Pagar", "Estado de Pago"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        pagosTable = new JTable(modelo);

        for (Reserva r : hotel.getReservas()) {
            modelo.addRow(new Object[]{
                    r.getId(),
                    r.getHabitacion().getNumero(),
                    r.getHuesped().getNombre(),
                    String.format("$%.2f", r.calcularTotal()),
                    r.getPago() != null ? "Pagado" : "Pendiente"
            });
        }

        panel.add(new JScrollPane(pagosTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(0, 2));

        listaReservas = new JComboBox<>();
        for (Reserva r : hotel.getReservas()) {
            if (r.getPago() == null) {
                listaReservas.addItem(r.getId());
            }
        }

        JComboBox<String> listaMetodos = new JComboBox<>(new String[]{
                "Tarjeta de Crédito", "Tarjeta de Débito", "Efectivo", "Transferencia"
        });

        bottomPanel.add(new JLabel("ID Reserva:"));
        bottomPanel.add(listaReservas);
        bottomPanel.add(new JLabel("Método de Pago:"));
        bottomPanel.add(listaMetodos);

        JButton btnPagar = new JButton("Registrar Pago");
        btnPagar.addActionListener(e -> {
            try {
                String id = (String) listaReservas.getSelectedItem();
                String metodo = (String) listaMetodos.getSelectedItem();
                hotel.registrarPago(id, metodo);

                JOptionPane.showMessageDialog(this, "Pago registrado");
                refreshUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en datos: " + ex.getMessage());
            }
        });

        bottomPanel.add(btnPagar);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaHotelGUI().setVisible(true));
    }
}



class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private final String datePattern = "yyyy-MM-dd";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parse(text);
    }

    @Override
    public String valueToString(Object value) {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }
        return "";
    }
}

