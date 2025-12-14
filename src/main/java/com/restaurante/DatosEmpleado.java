package com.restaurante;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DatosEmpleado extends JFrame {

    private static final String URL = "jdbc:sqlite:Restaurante.db";

    private JTextField sueldoBrutoField;
    private JComboBox<String> anosExpCombo;
    private JTextField recargoField;
    private JComboBox<String> activoCombo;
    private JTextField sueldoFinalField;

    private int usuarioId;

    public DatosEmpleado(int usuarioId) {
        this.usuarioId = usuarioId;

        setTitle("Datos del Empleado");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panel);

        // Campos
        sueldoBrutoField = new JTextField();
        recargoField = new JTextField();
        recargoField.setEditable(false); // Calculado automáticamente
        sueldoFinalField = new JTextField();
        sueldoFinalField.setEditable(false); // Calculado automáticamente

        anosExpCombo = new JComboBox<>(new String[]{
                "Menor o igual a 1", "Mayor a 1 y menor a 5", "Mayor a 5"
        });
        activoCombo = new JComboBox<>(new String[]{"Activado", "Desactivado"});

        panel.add(new JLabel("Sueldo Bruto:"));
        panel.add(sueldoBrutoField);

        panel.add(new JLabel("Años de Experiencia:"));
        panel.add(anosExpCombo);

        panel.add(new JLabel("Recargo %:"));
        panel.add(recargoField);

        panel.add(new JLabel("Activo:"));
        panel.add(activoCombo);

        panel.add(new JLabel("Sueldo Final:"));
        panel.add(sueldoFinalField);

        JButton guardarBtn = new JButton("Guardar");
        panel.add(new JLabel());
        panel.add(guardarBtn);

        // Cargar datos existentes
        cargarDatos();

        // Listener para actualizar recargo y sueldo final automáticamente
        anosExpCombo.addActionListener(e -> actualizarRecargoYFinal());
        sueldoBrutoField.addActionListener(e -> actualizarRecargoYFinal());
        sueldoBrutoField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                actualizarRecargoYFinal();
            }
        });

        guardarBtn.addActionListener(e -> guardarDatos());

        setVisible(true);
    }

    private void actualizarRecargoYFinal() {
        double recargo = 0;
        double sueldoBruto = 0;

        try {
            sueldoBruto = Double.parseDouble(sueldoBrutoField.getText());
        } catch (NumberFormatException ex) {
            sueldoBruto = 0;
        }

        String seleccion = (String) anosExpCombo.getSelectedItem();
        if (seleccion.equals("Menor o igual a 1")) {
            recargo = 0;
        } else if (seleccion.equals("Mayor a 1 y menor a 5")) {
            recargo = 20;
        } else if (seleccion.equals("Mayor a 5")) {
            recargo = 30;
        }

        recargoField.setText(String.valueOf(recargo));

        double sueldoFinal = sueldoBruto + (sueldoBruto * recargo / 100);
        sueldoFinalField.setText(String.valueOf(sueldoFinal));
    }

    private void cargarDatos() {
        String sql = "SELECT * FROM DatosEmpleado WHERE usuario_id=" + usuarioId;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                sueldoBrutoField.setText(String.valueOf(rs.getDouble("sueldo_bruto")));
                double recargo = rs.getDouble("recargo");
                recargoField.setText(String.valueOf(recargo));
                sueldoFinalField.setText(String.valueOf(rs.getDouble("sueldo_final")));

                int anos = rs.getInt("anos_experiencia");
                if (anos <= 1) anosExpCombo.setSelectedIndex(0);
                else if (anos > 1 && anos < 5) anosExpCombo.setSelectedIndex(1);
                else anosExpCombo.setSelectedIndex(2);

                activoCombo.setSelectedIndex(rs.getInt("esta_activo") == 1 ? 0 : 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarDatos() {
        double sueldoBruto = 0;
        double recargo = 0;
        double sueldoFinal = 0;
        int anosExp = 0;
        int estaActivo = activoCombo.getSelectedIndex() == 0 ? 1 : 0;

        try {
            sueldoBruto = Double.parseDouble(sueldoBrutoField.getText());
            recargo = Double.parseDouble(recargoField.getText());
            sueldoFinal = Double.parseDouble(sueldoFinalField.getText());

            String seleccion = (String) anosExpCombo.getSelectedItem();
            if (seleccion.equals("Menor o igual a 1")) anosExp = 1;
            else if (seleccion.equals("Mayor a 1 y menor a 5")) anosExp = 3;
            else if (seleccion.equals("Mayor a 5")) anosExp = 6;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insertar o actualizar
        String sqlCheck = "SELECT COUNT(*) FROM DatosEmpleado WHERE usuario_id=" + usuarioId;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCheck)) {

            boolean existe = rs.getInt(1) > 0;

            if (existe) {
                String sqlUpdate = "UPDATE DatosEmpleado SET sueldo_bruto=?, anos_experiencia=?, recargo=?, esta_activo=?, sueldo_final=? WHERE usuario_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setDouble(1, sueldoBruto);
                    ps.setInt(2, anosExp);
                    ps.setDouble(3, recargo);
                    ps.setInt(4, estaActivo);
                    ps.setDouble(5, sueldoFinal);
                    ps.setInt(6, usuarioId);
                    ps.executeUpdate();
                }
            } else {
                String sqlInsert = "INSERT INTO DatosEmpleado (usuario_id, sueldo_bruto, anos_experiencia, recargo, esta_activo, sueldo_final) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    ps.setInt(1, usuarioId);
                    ps.setDouble(2, sueldoBruto);
                    ps.setInt(3, anosExp);
                    ps.setDouble(4, recargo);
                    ps.setInt(5, estaActivo);
                    ps.setDouble(6, sueldoFinal);
                    ps.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Datos guardados correctamente");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}