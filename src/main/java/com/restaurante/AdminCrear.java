package com.restaurante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class AdminCrear extends JFrame {

    private static final String DB_URL = "jdbc:sqlite:Restaurante.db";

    public AdminCrear() {
        setTitle("Crear Administrador");
        setSize(400, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de la tabla Usuario
        JLabel rolLabel = new JLabel("Rol:");
        JTextField rolField = new JTextField("admin", 15);
        JLabel nombreLabel = new JLabel("Nombre:");
        JTextField nombreField = new JTextField(15);
        JLabel apellidoLabel = new JLabel("Apellido:");
        JTextField apellidoField = new JTextField(15);
        JLabel dniLabel = new JLabel("DNI:");
        JTextField dniField = new JTextField(15);
        JLabel cuilLabel = new JLabel("CUIL:");
        JTextField cuilField = new JTextField(15);
        JLabel direccionLabel = new JLabel("Dirección:");
        JTextField direccionField = new JTextField(15);
        JLabel localidadLabel = new JLabel("Localidad:");
        JTextField localidadField = new JTextField(15);
        JLabel fechaLabel = new JLabel("Fecha de ingreso:");
        JTextField fechaField = new JTextField(15);
        JLabel perfilLabel = new JLabel("Perfil:");
        JTextField perfilField = new JTextField(15);
        JLabel contrasenaLabel = new JLabel("Contraseña:");
        JPasswordField contrasenaField = new JPasswordField(15);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; panel.add(rolLabel, gbc);
        gbc.gridx = 1; panel.add(rolField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(nombreLabel, gbc);
        gbc.gridx = 1; panel.add(nombreField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(apellidoLabel, gbc);
        gbc.gridx = 1; panel.add(apellidoField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(dniLabel, gbc);
        gbc.gridx = 1; panel.add(dniField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(cuilLabel, gbc);
        gbc.gridx = 1; panel.add(cuilField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(direccionLabel, gbc);
        gbc.gridx = 1; panel.add(direccionField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(localidadLabel, gbc);
        gbc.gridx = 1; panel.add(localidadField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(fechaLabel, gbc);
        gbc.gridx = 1; panel.add(fechaField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(perfilLabel, gbc);
        gbc.gridx = 1; panel.add(perfilField, gbc); y++;
        gbc.gridx = 0; gbc.gridy = y; panel.add(contrasenaLabel, gbc);
        gbc.gridx = 1; panel.add(contrasenaField, gbc); y++;

        // Botón Guardar
        JButton guardarBtn = new JButton("Guardar");
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(guardarBtn, gbc);

        // Acción del botón Guardar
        guardarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rol = rolField.getText();
                String nombre = nombreField.getText();
                String apellido = apellidoField.getText();
                String dni = dniField.getText();
                String cuil = cuilField.getText();
                String direccion = direccionField.getText();
                String localidad = localidadField.getText();
                String fecha = fechaField.getText();
                String perfil = perfilField.getText();
                String contrasena = new String(contrasenaField.getPassword());

                if(nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nombre, Apellido, DNI y Contraseña son obligatorios.");
                    return;
                }

                String hashedPassword = hashPassword(contrasena);

                // Insertar en la base de datos
                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement(
                             "INSERT INTO Usuario (rol, nombre, apellido, dni, cuil, direccion, localidad, fecha_ingreso, perfil, contrasena) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                    pstmt.setString(1, rol);
                    pstmt.setString(2, nombre);
                    pstmt.setString(3, apellido);
                    pstmt.setString(4, dni);
                    pstmt.setString(5, cuil);
                    pstmt.setString(6, direccion);
                    pstmt.setString(7, localidad);
                    pstmt.setString(8, fecha);
                    pstmt.setString(9, perfil);
                    pstmt.setString(10, hashedPassword);

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Administrador creado correctamente.");
                    dispose();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al guardar: " + ex.getMessage());
                }
            }
        });

        add(panel);
    }

    // Método para hashear contraseña con SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
