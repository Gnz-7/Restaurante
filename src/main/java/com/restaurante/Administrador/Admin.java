package com.restaurante.Administrador;

import javax.swing.*;
import java.awt.*;

public class Admin extends JFrame {

    private String token;

    public Admin(String token) {
        this.token = token;

        setTitle("Panel de Administrador");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        add(panel);

        JLabel titulo = new JLabel("Panel de Administrador", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(50, 50, 50));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel botonesPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        botonesPanel.setBackground(new Color(245, 245, 245));
        panel.add(botonesPanel, BorderLayout.CENTER);

        JButton agregarBtn = crearBoton("Agregar Empleado", new Color(60, 179, 113));
        JButton eliminarBtn = crearBoton("Eliminar Empleado", new Color(220, 20, 60));
        JButton modificarBtn = crearBoton("Modificar Empleado", new Color(255, 165, 0));
        JButton estadisticasBtn = crearBoton("Ver Estadísticas", new Color(70, 130, 180));

        // Ahora sí se pasa token aunque no se use
        agregarBtn.addActionListener(e -> new AgregarEmpleado(token));
        modificarBtn.addActionListener(e -> new ModificarEmpleado(token));
        eliminarBtn.addActionListener(e -> new BajaEmpleado(token));
        estadisticasBtn.addActionListener(e -> new VerEstadistica(token));

        botonesPanel.add(agregarBtn);
        botonesPanel.add(eliminarBtn);
        botonesPanel.add(modificarBtn);
        botonesPanel.add(estadisticasBtn);

        setVisible(true);
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
