package com.restaurante;

import com.restaurante.Empleado.Empleado;
import com.restaurante.Administrador.Admin;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.sql.*;

public class Restaurante {

    private static final String DB_URL = "jdbc:sqlite:Restaurante.db";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Restaurante::crearLogin);
    }

    private static void crearLogin() {
        JFrame frame = new JFrame("Bienvenido al Restaurante");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("¡Bienvenido al Restaurante!", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        JLabel dniLabel = new JLabel("DNI:");
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(dniLabel, gbc);

        JTextField dniField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(dniField, gbc);

        JLabel passLabel = new JLabel("Contraseña:");
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        JButton ingresarBtn = new JButton("Ingresar");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(ingresarBtn, gbc);

        ingresarBtn.addActionListener(e -> {
            String dni = dniField.getText().trim();
            String password = new String(passField.getPassword());

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT contrasena, rol FROM Usuario WHERE dni = ?")) {

                pstmt.setString(1, dni);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String hashDB = rs.getString("contrasena");
                    String rol = rs.getString("rol").trim();

                    if (hashDB.equals(hashPassword(password))) {

                        frame.dispose();

                        if (rol.equalsIgnoreCase("Administrador")) {
                            new Admin();
                        } else if (rol.equalsIgnoreCase("Empleado")) {
                            new Empleado(dni);
                        } else {
                            JOptionPane.showMessageDialog(null, "Rol desconocido");
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "DNI no registrado");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
