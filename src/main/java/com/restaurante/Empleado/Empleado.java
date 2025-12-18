package com.restaurante.Empleado;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Empleado {

    private static final String DB_URL = "jdbc:sqlite:Restaurante.db";
    private JFrame frame;
    private String token;
    private String dni;
    private Map<Integer, JButton> botonesMesas = new HashMap<>();

    public Empleado(String token, String dni) {
        this.token = token;
        this.dni = dni;

        frame = new JFrame("Panel de Empleado");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        mostrarPanelMesas();

        frame.setVisible(true);
    }

    private void mostrarPanelMesas() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Bienvenido", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel mesasPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        mesasPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        Color verdeMesa = new Color(46, 125, 50);
        Dimension botonSize = new Dimension(110, 110);

        for (Integer mesa : obtenerMesas()) {
            JButton btnMesa = new JButton("Mesa " + mesa);
            btnMesa.setPreferredSize(botonSize);
            btnMesa.setBackground(verdeMesa);
            btnMesa.setForeground(Color.WHITE);
            btnMesa.setFont(new Font("Arial", Font.BOLD, 14));
            btnMesa.setFocusPainted(false);

            botonesMesas.put(mesa, btnMesa);
            btnMesa.addActionListener(e -> new Mesa(mesa, btnMesa));
            mesasPanel.add(btnMesa);
        }

        JPanel contenedorCentrado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenedorCentrado.add(mesasPanel);

        mainPanel.add(new JScrollPane(contenedorCentrado), BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    private List<Integer> obtenerMesas() {
        List<Integer> mesas = new ArrayList<>();
        String sql = "SELECT numero_mesa FROM Mesa ORDER BY numero_mesa";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mesas.add(rs.getInt("numero_mesa"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "Error cargando mesas: " + e.getMessage());
        }
        return mesas;
    }
}
