package com.restaurante.Administrador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class BajaEmpleado extends JFrame {

    private static final String URL = "jdbc:sqlite:Restaurante.db";
    private JTextField buscarDniField;
    private JPanel listaUsuariosPanel;

    public BajaEmpleado() {
        setTitle("Baja de Empleados");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));
        add(mainPanel);

        // Título
        JLabel titulo = new JLabel("Baja de Empleados", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(50, 50, 50));
        mainPanel.add(titulo, BorderLayout.NORTH);

        // Campo de búsqueda
        JPanel buscarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buscarPanel.setBackground(new Color(245, 245, 245));
        buscarPanel.add(new JLabel("Buscar por DNI:"));
        buscarDniField = new JTextField(15);
        buscarPanel.add(buscarDniField);
        JButton buscarBtn = new JButton("Buscar");
        buscarBtn.setBackground(new Color(70, 130, 180));
        buscarBtn.setForeground(Color.WHITE);
        buscarBtn.setFocusPainted(false);
        buscarPanel.add(buscarBtn);
        mainPanel.add(buscarPanel, BorderLayout.SOUTH);

        // Panel de lista de usuarios
        listaUsuariosPanel = new JPanel();
        listaUsuariosPanel.setLayout(new BoxLayout(listaUsuariosPanel, BoxLayout.Y_AXIS));
        listaUsuariosPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scroll = new JScrollPane(listaUsuariosPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Acción botón buscar
        buscarBtn.addActionListener(e -> cargarUsuarios(buscarDniField.getText().trim()));

        // Carga inicial
        cargarUsuarios("");
        setVisible(true);
    }

    private void cargarUsuarios(String dniFiltro) {
        listaUsuariosPanel.removeAll();

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT u.id, u.nombre, u.apellido, u.dni, u.perfil, d.esta_activo " +
                             "FROM Usuario u LEFT JOIN DatosEmpleado d ON u.id = d.usuario_id " +
                             "WHERE u.rol = 'Empleado' AND u.dni LIKE ?")) {
            ps.setString(1, "%" + dniFiltro + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int usuarioId = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String dni = rs.getString("dni");
                String perfil = rs.getString("perfil");
                int estaActivo = rs.getInt("esta_activo");

                JPanel usuarioPanel = crearUsuarioPanel(usuarioId, nombre, apellido, dni, perfil, estaActivo);
                listaUsuariosPanel.add(usuarioPanel);
                listaUsuariosPanel.add(Box.createVerticalStrut(10));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }

        listaUsuariosPanel.revalidate();
        listaUsuariosPanel.repaint();
    }

    private JPanel crearUsuarioPanel(int id, String nombre, String apellido, String dni, String perfil, int estaActivo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(500, 180));
        panel.setBackground(estaActivo == 1 ? new Color(144, 238, 144) : Color.LIGHT_GRAY);

        JLabel nombreLabel = new JLabel(nombre + " " + apellido);
        JLabel dniLabel = new JLabel("DNI: " + dni);
        JLabel perfilLabel = new JLabel("Perfil: " + perfil);
        JLabel estadoLabel = new JLabel("Activo: " + (estaActivo == 1 ? "Sí" : "No"));

        nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nombreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dniLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        perfilLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        estadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(nombreLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(dniLabel);
        panel.add(perfilLabel);
        panel.add(estadoLabel);
        panel.add(Box.createVerticalStrut(10));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        botonesPanel.setBackground(panel.getBackground());
        JButton eliminarBtn = new JButton("Eliminar");
        eliminarBtn.setBackground(new Color(220, 20, 60));
        eliminarBtn.setForeground(Color.WHITE);
        JButton bajaLogicaBtn = new JButton("Baja Lógica");
        bajaLogicaBtn.setBackground(new Color(255, 165, 0));
        bajaLogicaBtn.setForeground(Color.WHITE);

        botonesPanel.add(eliminarBtn);
        botonesPanel.add(bajaLogicaBtn);
        panel.add(botonesPanel);

        eliminarBtn.addActionListener(e -> eliminarUsuario(id, panel));
        bajaLogicaBtn.addActionListener(e -> bajaLogicaUsuario(id, panel));

        return panel;
    }

    private void eliminarUsuario(int id, JPanel panel) {
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Eliminar usuario definitivamente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(URL)) {
                conn.setAutoCommit(false);
                try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM DatosEmpleado WHERE usuario_id = ?");
                     PreparedStatement ps2 = conn.prepareStatement("DELETE FROM Usuario WHERE id = ?")) {
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                    conn.commit();
                    listaUsuariosPanel.remove(panel);
                    listaUsuariosPanel.revalidate();
                    listaUsuariosPanel.repaint();
                } catch (SQLException ex) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al conectar a BD: " + e.getMessage());
            }
        }
    }

    private void bajaLogicaUsuario(int id, JPanel panel) {
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement("UPDATE DatosEmpleado SET esta_activo = 0 WHERE usuario_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            panel.setBackground(Color.LIGHT_GRAY);
            for (Component c : panel.getComponents()) {
                c.setBackground(Color.LIGHT_GRAY);
                if (c instanceof JLabel && ((JLabel) c).getText().startsWith("Activo:")) {
                    ((JLabel) c).setText("Activo: No");
                }
            }
            panel.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al dar baja lógica: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new BajaEmpleado();
    }
}
