
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.jdatepicker.impl.*;

public class SistemaHotelGUI extends JFrame {
    private Hotel hotel;
    private JTable habitacionesTable, reservasTable, pagosTable;
    private JComboBox<String> listaReservas;
    private java.util.List<JButton> botonesHabitaciones = new java.util.ArrayList<>();

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
        tabbedPane.addTab("Pagos", crearPanelPagos());
        tabbedPane.addTab("Ver Reservas", crearPanelVerReservas());
        add(tabbedPane);
    }

    private void inicializarHabitaciones() {
        String[] tipos = {"Suite", "Queen", "Doble", "Individual"};
        int[] precios = {200, 120, 100, 80};

        int suitesAdaptadas = 0, queenAdaptadas = 0, doblesAdaptadas = 0, individualesAdaptadas = 0;

        for (int piso = 1; piso <= 5; piso++) {
            int contadorHabitacion = 1;

            for (int i = 0; i < 2; i++) {
                int numero = piso * 100 + contadorHabitacion++;
                boolean adaptada = suitesAdaptadas < 10;
                if (adaptada) suitesAdaptadas++;
                hotel.agregarHabitacion(new Habitacion(numero, tipos[0], precios[0], adaptada ? "Apta silla ruedas" : ""));
            }

            for (int i = 0; i < 2; i++) {
                int numero = piso * 100 + contadorHabitacion++;
                boolean adaptada = queenAdaptadas < 4;
                if (adaptada) queenAdaptadas++;
                hotel.agregarHabitacion(new Habitacion(numero, tipos[1], precios[1], adaptada ? "Apta silla ruedas" : ""));
            }

            for (int i = 0; i < 3; i++) {
                int numero = piso * 100 + contadorHabitacion++;
                boolean adaptada = doblesAdaptadas < 5;
                if (adaptada) doblesAdaptadas++;
                hotel.agregarHabitacion(new Habitacion(numero, tipos[2], precios[2], adaptada ? "Apta silla ruedas" : ""));
            }

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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel botonesPanel = new JPanel(new GridLayout(5, 10, 10, 10));
        botonesPanel.setBorder(BorderFactory.createTitledBorder("Habitaciones"));

        botonesHabitaciones.clear(); // Asegúrate de limpiar la lista antes de poblarla

        for (Habitacion habitacion : hotel.getHabitaciones()) {
            JButton boton = new JButton(String.valueOf(habitacion.getNumero()));
            boton.setPreferredSize(new Dimension(60, 40));
            boton.setFocusPainted(false);
            boton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            boton.setBackground(habitacion.estaDisponible() ? new Color(144, 238, 144) : new Color(255, 102, 102));
            boton.setForeground(Color.BLACK);
            boton.setFont(new Font("Arial", Font.BOLD, 12));
            boton.setToolTipText(habitacion.getTipo() + " - $" + habitacion.getPrecioPorNoche());

            boton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    boton.setBackground(habitacion.estaDisponible() ? new Color(102, 205, 102) : new Color(255, 77, 77));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    boton.setBackground(habitacion.estaDisponible() ? new Color(144, 238, 144) : new Color(255, 102, 102));
                }
            });

            botonesHabitaciones.add(boton); // Guardamos el botón para actualizar luego
            botonesPanel.add(boton);
        }

        // Tabla de detalles
        String[] columnas = {"Número", "Tipo", "Precio", "Disponibilidad", "Requerimiento"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Solo el precio editable si deseas
            }
        };

        habitacionesTable = new JTable(modelo);
        habitacionesTable.setRowHeight(25);
        habitacionesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        habitacionesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        habitacionesTable.getTableHeader().setBackground(new Color(220, 220, 220));
        habitacionesTable.setSelectionBackground(new Color(173, 216, 230));
        habitacionesTable.setGridColor(new Color(200, 200, 200));

        habitacionesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        });

        actualizarTablaHabitaciones(modelo);

        JScrollPane tablaScroll = new JScrollPane(habitacionesTable);
        tablaScroll.setBorder(BorderFactory.createTitledBorder("Detalles de Habitaciones"));

        panel.add(botonesPanel, BorderLayout.NORTH);
        panel.add(tablaScroll, BorderLayout.CENTER);

        return panel;
    }


    private void actualizarTablaHabitaciones(DefaultTableModel modelo) {
        modelo.setRowCount(0);
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
        // Actualizar la tabla de habitaciones
        DefaultTableModel modeloHabitaciones = (DefaultTableModel) habitacionesTable.getModel();
        actualizarTablaHabitaciones(modeloHabitaciones);

        for (int i = 0; i < hotel.getHabitaciones().size(); i++) {
            Habitacion h = hotel.getHabitaciones().get(i);
            JButton btn = botonesHabitaciones.get(i);
            Color color = h.estaDisponible() ? new Color(144, 238, 144) : new Color(255, 102, 102);
            btn.setBackground(color);
        }

        // Actualizar la tabla de reservas
        DefaultTableModel modeloReservas = (DefaultTableModel) reservasTable.getModel();
        modeloReservas.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Reserva r : hotel.getReservas()) {
            modeloReservas.addRow(new Object[]{
                    r.getId(),
                    r.getHuesped().getNombre(),
                    r.getHabitacion().getNumero(),
                    sdf.format(r.getFechaEntrada()),
                    sdf.format(r.getFechaSalida()),
                    r.getPago() != null ? r.getPago().toString() : "Pendiente"
            });
        }

        // Actualizar la tabla de pagos
        DefaultTableModel modeloPagos = (DefaultTableModel) pagosTable.getModel();
        modeloPagos.setRowCount(0);
        for (Reserva r : hotel.getReservas()) {
            modeloPagos.addRow(new Object[]{
                    r.getId(),
                    r.getHabitacion().getNumero(),
                    r.getHuesped().getNombre(),
                    String.format("$%.2f", r.calcularTotal()),
                    r.getPago() != null ? "Pagado" : "Pendiente"
            });
        }

        // Actualizar la lista de reservas pendientes de pago
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel botonesPanel = new JPanel(new GridLayout(5, 10, 10, 10));
        botonesPanel.setBorder(BorderFactory.createTitledBorder("Habitaciones Disponibles"));

        JComboBox<String> campoTipoHabitacion = new JComboBox<>(new String[]{"Individual", "Doble", "Queen", "Suite"});
        JComboBox<String> campoAccesible = new JComboBox<>(new String[]{"No", "Sí"});
        JComboBox<String> campoHabitacion = new JComboBox<>();
        JTextField campoNombre = new JTextField();
        JTextField campoCedula = new JTextField();
        JDatePickerImpl campoEntrada = crearDatePicker();
        JDatePickerImpl campoSalida = crearDatePicker();

        // Actualizar lista de habitaciones según tipo y accesibilidad
        ActionListener actualizarHabitaciones = e -> actualizarListaHabitaciones(
                campoHabitacion,
                campoTipoHabitacion.getSelectedItem().toString(),
                campoAccesible.getSelectedItem().equals("Sí")
        );
        campoTipoHabitacion.addActionListener(actualizarHabitaciones);
        campoAccesible.addActionListener(actualizarHabitaciones);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nueva Reserva"));
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(campoNombre);
        formPanel.add(new JLabel("Cédula:"));
        formPanel.add(campoCedula);
        formPanel.add(new JLabel("Tipo de Habitación:"));
        formPanel.add(campoTipoHabitacion);
        formPanel.add(new JLabel("Accesible con silla de ruedas:"));
        formPanel.add(campoAccesible);
        formPanel.add(new JLabel("Nº Habitación:"));
        formPanel.add(campoHabitacion);
        formPanel.add(new JLabel("Entrada:"));
        formPanel.add(campoEntrada);
        formPanel.add(new JLabel("Salida:"));
        formPanel.add(campoSalida);

        JButton btnReservar = new JButton("Reservar");
        btnReservar.addActionListener(e -> {
            try {
                if (campoNombre.getText().isEmpty() || campoCedula.getText().isEmpty() ||
                        campoHabitacion.getSelectedItem() == null ||
                        campoEntrada.getModel().getValue() == null ||
                        campoSalida.getModel().getValue() == null) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                    return;
                }

                Date entrada = (Date) campoEntrada.getModel().getValue();
                Date salida = (Date) campoSalida.getModel().getValue();
                if (entrada.after(salida)) {
                    JOptionPane.showMessageDialog(this, "La fecha de entrada no puede ser mayor a la de salida.");
                    return;
                }

                Huesped h = new Huesped(campoNombre.getText(), campoCedula.getText());
                int nro = Integer.parseInt((String) campoHabitacion.getSelectedItem());
                hotel.crearReserva(h, entrada, salida, nro);

                JOptionPane.showMessageDialog(this, "Reserva realizada");
                campoNombre.setText("");
                campoCedula.setText("");
                campoHabitacion.removeAllItems();
                campoEntrada.getModel().setValue(null);
                campoSalida.getModel().setValue(null);
                campoTipoHabitacion.setSelectedIndex(0);
                campoAccesible.setSelectedIndex(0);

                refreshUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        /*panel.add(botonesPanel, BorderLayout.CENTER);*/
        panel.add(btnReservar, BorderLayout.SOUTH);

        return panel;
    }

    private void actualizarListaHabitaciones(JComboBox<String> campoHabitacion, String tipo, boolean accesible) {
        campoHabitacion.removeAllItems();
        for (Habitacion h : hotel.getHabitaciones()) {
            if (h.estaDisponible() && h.getTipo().equals(tipo) &&
                    (!accesible || "Apta silla ruedas".equals(h.getRequerimientoAdicional()))) {
                campoHabitacion.addItem(String.valueOf(h.getNumero()));
            }
        }
    }

    private JPanel crearPanelPagos() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnas = {"ID Reserva", "Habitación", "Nombre", "Total a Pagar", "Estado de Pago"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

        JLabel labelDiferido = new JLabel("Diferir a:");
        JComboBox<String> listaDiferido = new JComboBox<>(new String[]{"3 meses", "6 meses", "12 meses"});
        labelDiferido.setVisible(false);
        listaDiferido.setVisible(false);

        listaMetodos.addActionListener(e -> {
            boolean esTarjetaCredito = "Tarjeta de Crédito".equals(listaMetodos.getSelectedItem());
            labelDiferido.setVisible(esTarjetaCredito);
            listaDiferido.setVisible(esTarjetaCredito);
        });

        bottomPanel.add(new JLabel("ID Reserva:"));
        bottomPanel.add(listaReservas);
        bottomPanel.add(new JLabel("Método de Pago:"));
        bottomPanel.add(listaMetodos);
        bottomPanel.add(labelDiferido);
        bottomPanel.add(listaDiferido);

        JButton btnPagar = new JButton("Registrar Pago");
        btnPagar.addActionListener(e -> {
            try {
                String id = (String) listaReservas.getSelectedItem();
                String metodo = (String) listaMetodos.getSelectedItem();
                String diferido = listaDiferido.isVisible() ? (String) listaDiferido.getSelectedItem() : "Sin diferir";

                hotel.registrarPago(id, metodo + (listaDiferido.isVisible() ? " - " + diferido : ""));

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

    private JPanel crearPanelVerReservas() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"ID", "Cliente", "Hab.", "Entrada", "Salida", "Pago"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservasTable = new JTable(modelo);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Reserva r : hotel.getReservas()) {
            String fechaEntrada = sdf.format(r.getFechaEntrada());
            String fechaSalida = sdf.format(r.getFechaSalida());

            modelo.addRow(new Object[]{
                    r.getId(),
                    r.getHuesped().getNombre(),
                    r.getHabitacion().getNumero(),
                    fechaEntrada,
                    fechaSalida,
                    r.getPago() != null ? r.getPago().toString() : "Pendiente"
            });
        }

        panel.add(new JScrollPane(reservasTable), BorderLayout.CENTER);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaHotelGUI().setVisible(true));
    }
}

