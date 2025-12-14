package com.restaurante;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VerEstadisticas extends JFrame {

    private static final String URL = "jdbc:sqlite:Restaurante.db";
    private JPanel mainPanel;

    public VerEstadisticas() {
        setTitle("Estadísticas de Empleados");
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // scroll más suave
        add(scrollPane);

        JLabel titulo = new JLabel("Empleados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titulo);

        // Carga inicial de empleados
        cargarEmpleados();

        // Recargar datos cada vez que la ventana gana foco
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                cargarEmpleados();
            }
        });

        setVisible(true);
    }

    private void cargarEmpleados() {
        // Limpiar panel antes de recargar
        if (mainPanel.getComponentCount() > 1) {
            mainPanel.removeAll();
        }

        JLabel titulo = new JLabel("Empleados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titulo);

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT u.id, u.nombre, u.apellido, d.sueldo_bruto, " +
                     "d.anos_experiencia, d.recargo, d.esta_activo, d.sueldo_final " +
                     "FROM Usuario u LEFT JOIN DatosEmpleado d ON u.id = d.usuario_id " +
                     "WHERE u.rol='Empleado'")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                Double sueldoBruto = rs.getDouble("sueldo_bruto");
                Integer anosExp = rs.getInt("anos_experiencia");
                Double recargo = rs.getDouble("recargo");
                Integer estaActivo = rs.getInt("esta_activo");
                Double sueldoFinal = rs.getDouble("sueldo_final");

                JPanel empleadoPanel = new JPanel();
                empleadoPanel.setLayout(new BoxLayout(empleadoPanel, BoxLayout.Y_AXIS));
                empleadoPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                // Color según activo / desactivado
                if (estaActivo != null && estaActivo == 1) {
                    empleadoPanel.setBackground(new Color(198, 239, 206)); // verde suave
                } else {
                    empleadoPanel.setBackground(new Color(255, 199, 206)); // rojo suave
                }

                // Datos del empleado
                JLabel nombreLabel = new JLabel(nombreCompleto);
                nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                empleadoPanel.add(nombreLabel);

                empleadoPanel.add(new JLabel("Sueldo Bruto: " + (sueldoBruto != 0 ? sueldoBruto : "-")));
                empleadoPanel.add(new JLabel("Años de Experiencia: " + (anosExp != 0 ? anosExp : "-")));
                empleadoPanel.add(new JLabel("Recargo %: " + (recargo != 0 ? recargo : "-")));
                empleadoPanel.add(new JLabel("Activo: " + (estaActivo != 0 ? "Activado" : "Desactivado")));
                empleadoPanel.add(new JLabel("Sueldo Final: " + (sueldoFinal != 0 ? sueldoFinal : "-")));

                JButton agregarBtn = new JButton("Añadir / Editar");
                agregarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                agregarBtn.setBackground(new Color(60, 179, 113));
                agregarBtn.setForeground(Color.WHITE);
                agregarBtn.setFocusPainted(false);
                agregarBtn.addActionListener(e -> new DatosEmpleado(id));
                agregarBtn.setMaximumSize(new Dimension(200, 30));
                agregarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                empleadoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                empleadoPanel.add(agregarBtn);

                empleadoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                empleadoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

                mainPanel.add(empleadoPanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar empleados:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        new VerEstadisticas();
    }
}
