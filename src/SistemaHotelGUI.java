// SistemaHotelGUI.java
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
        dibujoHotel.setPreferredSize(new Dimension(600, 250));

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
        // Refresh the habitaciones table
        actualizarTablaHabitaciones((DefaultTableModel) habitacionesTable.getModel());

        // Refresh the hotel drawing
        habitacionesTable.getParent().repaint();

        // Refresh other tabs (e.g., reservations)
        DefaultTableModel reservasModel = (DefaultTableModel) reservasTable.getModel();
        reservasModel.setRowCount(0);
        for (Reserva r : hotel.getReservas()) {
            reservasModel.addRow(new Object[]{
                    r.getId(), r.getHuesped(), r.getHabitacion().getNumero(),
                    r.getFechaEntrada(), r.getFechaSalida(),
                    r.getPago() != null ? r.getPago().toString() : "Pendiente"
            });
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
                Huesped h = new Huesped(campoNombre.getText(), campoCedula.getText());
                int nro = Integer.parseInt((String) campoHabitacion.getSelectedItem());
                Date entrada = (Date) campoEntrada.getModel().getValue();
                Date salida = (Date) campoSalida.getModel().getValue();
                hotel.crearReserva(h, entrada, salida, nro);
                JOptionPane.showMessageDialog(this, "Reserva realizada");
                refreshUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en datos");
            }
        });

        panel.add(form, BorderLayout.CENTER);
        panel.add(btnReservar, BorderLayout.SOUTH);
        return panel;
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

    private void actualizarListaHabitaciones(JComboBox<String> campoHabitacion, boolean accesible) {
        campoHabitacion.removeAllItems();
        for (Habitacion h : hotel.getHabitaciones()) {
            if (h.estaDisponible() && (!accesible || "Apta silla ruedas".equals(h.getRequerimientoAdicional()))) {
                campoHabitacion.addItem(String.valueOf(h.getNumero()));
            }
        }
    }

    private JPanel crearPanelVerReservas() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"ID", "Cliente", "Hab.", "Entrada", "Salida", "Pago"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        reservasTable = new JTable(modelo);

        for (Reserva r : hotel.getReservas()) {
            modelo.addRow(new Object[]{
                    r.getId(), r.getHuesped(), r.getHabitacion().getNumero(),
                    r.getFechaEntrada(), r.getFechaSalida(),
                    r.getPago() != null ? r.getPago().toString() : "Pendiente"
            });
        }

        panel.add(new JScrollPane(reservasTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPagos() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField campoId = new JTextField();
        JTextField campoMetodo = new JTextField();

        panel.add(new JLabel("ID Reserva:")); panel.add(campoId);
        panel.add(new JLabel("Método de pago:")); panel.add(campoMetodo);
        JButton btnPagar = new JButton("Registrar Pago");
        btnPagar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(campoId.getText());
                String metodo = campoMetodo.getText();
                hotel.registrarPago(id, metodo);
                JOptionPane.showMessageDialog(this, "Pago registrado");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en datos");
            }
        });

        panel.add(btnPagar);
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

