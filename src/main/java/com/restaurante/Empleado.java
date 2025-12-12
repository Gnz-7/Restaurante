package com.restaurante;

import javax.swing.*;
import java.awt.*;

public class Empleado {
    public Empleado(String dni) {
        JFrame frame = new JFrame("Panel de Empleado");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Bienvenido Empleado: " + dni, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }
}
