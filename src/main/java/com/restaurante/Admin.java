package com.restaurante;

import javax.swing.*;
import java.awt.*;

public class Admin extends JFrame {

    public Admin() {
        setTitle("Panel de Administrador");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Panel de Administrador", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        JButton agregarBtn = new JButton("Agregar Empleado");
        gbc.gridx = 0;
        panel.add(agregarBtn, gbc);

        JButton eliminarBtn = new JButton("Eliminar Empleado");
        gbc.gridx = 1;
        panel.add(eliminarBtn, gbc);

        JButton modificarBtn = new JButton("Modificar Empleado");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(modificarBtn, gbc);

        JButton estadisticasBtn = new JButton("Ver Estadísticas de Empleados");
        gbc.gridx = 1;
        panel.add(estadisticasBtn, gbc);

        add(panel);

        setVisible(true); // 🔑 necesario para mostrar la ventana
    }
}