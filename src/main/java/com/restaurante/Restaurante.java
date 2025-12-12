package com.restaurante;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class Restaurante {

    private static final String DB_URL = "jdbc:sqlite:Restaurante.db";
    private static final String SECRET_KEY = "EstaEsUnaClaveMuyLargaDeAlMenos32Bytes!!";

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
        ingresarBtn.setBackground(new Color(70, 130, 180));
        ingresarBtn.setForeground(Color.WHITE);
        ingresarBtn.setFocusPainted(false);
        ingresarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(ingresarBtn, gbc);

        ingresarBtn.addActionListener(e -> {
            String dni = dniField.getText();
            String password = new String(passField.getPassword());

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Usuario WHERE dni = ?")) {

                pstmt.setString(1, dni);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String hashedPasswordDB = rs.getString("contrasena");
                    String rol = rs.getString("rol").trim();

                    if (hashedPasswordDB.equals(hashPassword(password))) {
                        JOptionPane.showMessageDialog(null, "Login correcto.");

                        // Detectar rol y abrir ventana correspondiente
                        if ("Administrador".equalsIgnoreCase(rol)) {
                            frame.dispose();
                            new Admin();  // abrir ventana Admin
                        } else if ("Empleado".equalsIgnoreCase(rol)) {
                            frame.dispose();
                            new Empleado(dni); // abrir ventana Empleado
                        } else {
                            JOptionPane.showMessageDialog(null, "Rol desconocido: " + rol);
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "DNI no registrado");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al iniciar sesión: " + ex.getMessage());
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
