package com.restaurante;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AgregarEmpleado extends JFrame {

    private static final String URL = "jdbc:sqlite:Restaurante.db";

    // Campos de DatosEmpleado
    private JTextField sueldoInicialField;
    private JTextField sueldoFinalField;
    private JComboBox<String> anosExperienciaCombo;
    private JTextField recargoField;
    private JCheckBox activoCheck;

    private JPanel datosEmpleadoPanel; // panel colapsable

    public AgregarEmpleado() {
        setTitle("Agregar Usuario");

        // Tamaño relativo a la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width * 2 / 3, screenSize.height * 2 / 3);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(mainPanel);

        // Título
        JLabel titulo = new JLabel("Agregar Empleado");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(new Color(50, 50, 50));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titulo, BorderLayout.NORTH);

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Campos generales
        JComboBox<String> rolCombo = new JComboBox<>(new String[]{"Administrador", "Empleado"});
        JTextField nombreField = new JTextField();
        JTextField apellidoField = new JTextField();
        JTextField dniField = new JTextField();
        JTextField cuilField = new JTextField();
        JTextField direccionField = new JTextField();
        JTextField localidadField = new JTextField();
        JTextField fechaIngresoField = new JTextField();
        JComboBox<String> perfilCombo = new JComboBox<>(new String[]{"null", "encargado", "mozo"});
        JPasswordField contrasenaField = new JPasswordField();

        addField(formPanel, gbc, "Rol:", rolCombo);
        addField(formPanel, gbc, "Nombre:", nombreField);
        addField(formPanel, gbc, "Apellido:", apellidoField);
        addField(formPanel, gbc, "DNI:", dniField);
        addField(formPanel, gbc, "CUIL:", cuilField);
        addField(formPanel, gbc, "Dirección:", direccionField);
        addField(formPanel, gbc, "Localidad:", localidadField);
        addField(formPanel, gbc, "Fecha Ingreso:", fechaIngresoField);
        addField(formPanel, gbc, "Perfil:", perfilCombo);
        addField(formPanel, gbc, "Contraseña:", contrasenaField);

        // Panel colapsable para DatosEmpleado
        datosEmpleadoPanel = new JPanel(new GridBagLayout());
        datosEmpleadoPanel.setBackground(new Color(230, 250, 250));
        datosEmpleadoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(60,179,113)),
                "Datos Empleado", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(60,179,113)));

        GridBagConstraints gbcDatos = new GridBagConstraints();
        gbcDatos.insets = new Insets(5,10,5,10);
        gbcDatos.fill = GridBagConstraints.HORIZONTAL;
        gbcDatos.gridx = 0;
        gbcDatos.gridy = 0;

        sueldoInicialField = new JTextField();
        sueldoFinalField = new JTextField();
        sueldoFinalField.setEditable(false); // no editable

        anosExperienciaCombo = new JComboBox<>(new String[]{"Menor o igual a 1", "Mayor a 1 y menor a 5", "Mayor a 5"});
        recargoField = new JTextField();
        recargoField.setEditable(false); // no editable
        activoCheck = new JCheckBox("Activo", true);

        addField(datosEmpleadoPanel, gbcDatos, "Sueldo Bruto Inicial:", sueldoInicialField);
        addField(datosEmpleadoPanel, gbcDatos, "Años Experiencia:", anosExperienciaCombo);
        addField(datosEmpleadoPanel, gbcDatos, "Recargo:", recargoField);
        addField(datosEmpleadoPanel, gbcDatos, "Sueldo Bruto Final:", sueldoFinalField);
        addField(datosEmpleadoPanel, gbcDatos, "", activoCheck);

        gbc.gridy++;
        formPanel.add(datosEmpleadoPanel, gbc);

        // Inicialmente ocultamos panel de DatosEmpleado
        datosEmpleadoPanel.setVisible(false);

        // Escucha cambios en rol
        rolCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                datosEmpleadoPanel.setVisible(rolCombo.getSelectedItem().toString().equals("Empleado"));
                revalidate();
                repaint();
            }
        });

        // Listener para calcular recargo y sueldo final automáticamente
        anosExperienciaCombo.addItemListener(e -> actualizarSueldoFinal());
        sueldoInicialField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() { actualizarSueldoFinal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        // Panel con scroll
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botón Crear
        JButton crearBtn = new JButton("Crear");
        crearBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        crearBtn.setBackground(new Color(60, 179, 113));
        crearBtn.setForeground(Color.WHITE);
        crearBtn.setFocusPainted(false);
        crearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(crearBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Acción del botón
        crearBtn.addActionListener(e -> insertarEmpleado(
                rolCombo.getSelectedItem().toString(),
                nombreField.getText(),
                apellidoField.getText(),
                dniField.getText(),
                cuilField.getText(),
                direccionField.getText(),
                localidadField.getText(),
                fechaIngresoField.getText(),
                perfilCombo.getSelectedItem().toString(),
                new String(contrasenaField.getPassword())
        ));

        setVisible(true);
    }

    private void actualizarSueldoFinal() {
        double sueldoInicial = parseDouble(sueldoInicialField.getText());
        double porcentaje = switch (anosExperienciaCombo.getSelectedItem().toString()) {
            case "Menor o igual a 1" -> 0;
            case "Mayor a 1 y menor a 5" -> 0.2;
            case "Mayor a 5" -> 0.3;
            default -> 0;
        };
        recargoField.setText((int)(porcentaje*100) + "%");
        sueldoFinalField.setText(String.format("%.2f", sueldoInicial * (1 + porcentaje)));
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        if (!labelText.isEmpty()) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            label.setForeground(new Color(50, 50, 50));
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            panel.add(label, gbc);
        }

        gbc.gridx = 1;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        panel.add(field, gbc);

        gbc.gridy++;
    }

    private void insertarEmpleado(String rol, String nombre, String apellido, String dni,
                                  String cuil, String direccion, String localidad,
                                  String fechaIngreso, String perfil, String contrasena) {

        String sqlUsuario = "INSERT INTO Usuario " +
                "(rol, nombre, apellido, dni, cuil, direccion, localidad, fecha_ingreso, perfil, contrasena) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, rol);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, dni);
            ps.setString(5, cuil);
            ps.setString(6, direccion);
            ps.setString(7, localidad);
            ps.setString(8, fechaIngreso);
            ps.setString(9, perfil.equals("null") ? null : perfil);
            ps.setString(10, hashPassword(contrasena));

            ps.executeUpdate();

            if (rol.equals("Empleado")) {
                var generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int usuarioId = generatedKeys.getInt(1);
                    insertarDatosEmpleado(usuarioId, conn);
                }
            }

            JOptionPane.showMessageDialog(this, "Empleado creado correctamente");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertarDatosEmpleado(int usuarioId, Connection conn) throws SQLException {
        String sql = "INSERT INTO DatosEmpleado (usuario_id, sueldo_bruto_inicial, sueldo_bruto_final, anos_experiencia, recargo, esta_activo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setDouble(2, parseDouble(sueldoInicialField.getText()));
            ps.setDouble(3, parseDouble(sueldoFinalField.getText()));
            ps.setString(4, anosExperienciaCombo.getSelectedItem().toString());
            ps.setDouble(5, parseDouble(recargoField.getText().replace("%","")) / 100.0);
            ps.setInt(6, activoCheck.isSelected() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0; }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    public static void main(String[] args) {
        new AgregarEmpleado();
    }
}
